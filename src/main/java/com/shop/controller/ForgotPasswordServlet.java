package com.shop.controller;

import com.shop.dao.UserDAO;
import com.shop.model.User;
import com.shop.util.EmailUtil;
import com.shop.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.*;
import java.util.HexFormat;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token != null && !token.isEmpty()) {
            req.setAttribute("token", token);
            req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("send-reset".equals(action)) {
            String email = req.getParameter("email");
            User user = userDAO.findByEmail(email);
            if (user == null) {
                req.setAttribute("error", "Email không tồn tại trong hệ thống");
                req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
                return;
            }
            String token = generateToken();
            try (Connection conn = com.shop.dao.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement("INSERT INTO password_resets (user_id, token, expires_at) VALUES (?, ?, NOW() + INTERVAL '1 hour')")) {
                ps.setInt(1, user.getId());
                ps.setString(2, token);
                ps.executeUpdate();
            } catch (SQLException e) {
                req.setAttribute("error", "Lỗi hệ thống, vui lòng thử lại");
                req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
                return;
            }
            String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
                + req.getContextPath() + "/forgot-password?token=" + token;
            EmailUtil.sendOrderConfirmation(email, "Đặt lại mật khẩu - Shop",
                "Chào bạn,\n\nVui lòng click vào link sau để đặt lại mật khẩu:\n" + resetLink + "\n\nLink có hiệu lực trong 1 giờ.\n\nNếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.", null);
            if (!EmailUtil.isConfigured()) {
                req.setAttribute("info", "Yêu cầu đặt lại mật khẩu đã được ghi nhận. Token: " + token + " (email chưa được cấu hình SMTP)");
            } else {
                req.setAttribute("success", "Email đặt lại mật khẩu đã được gửi đến " + email);
            }
            req.getRequestDispatcher("forgot-password.jsp").forward(req, resp);
        } else if ("reset-password".equals(action)) {
            String token = req.getParameter("token");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");
            if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "Mật khẩu xác nhận không khớp");
                req.setAttribute("token", token);
                req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
                return;
            }
            if (newPassword.length() < 6) {
                req.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
                req.setAttribute("token", token);
                req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
                return;
            }
            try (Connection conn = com.shop.dao.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "SELECT user_id FROM password_resets WHERE token = ? AND used = FALSE AND expires_at > NOW()")) {
                ps.setString(1, token);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("user_id");
                        String hashed = PasswordUtil.hashPassword(newPassword);
                        userDAO.updatePassword(userId, hashed);
                        try (PreparedStatement ps2 = conn.prepareStatement("UPDATE password_resets SET used = TRUE WHERE token = ?")) {
                            ps2.setString(1, token);
                            ps2.executeUpdate();
                        }
                        req.setAttribute("success", "Đặt lại mật khẩu thành công. Vui lòng đăng nhập.");
                        req.getRequestDispatcher("login.jsp").forward(req, resp);
                        return;
                    }
                }
            } catch (SQLException e) {
                req.setAttribute("error", "Lỗi hệ thống");
                req.setAttribute("token", token);
                req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
                return;
            }
            req.setAttribute("error", "Token không hợp lệ hoặc đã hết hạn");
            req.getRequestDispatcher("reset-password.jsp").forward(req, resp);
        }
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
