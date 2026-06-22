package com.shop.controller;

import com.shop.dao.UserDAO;
import com.shop.model.User;
import com.shop.util.PasswordUtil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect("login.jsp"); return; }
        String action = req.getParameter("action");
        if ("change-password".equals(action)) {
            req.getRequestDispatcher("change-password.jsp").forward(req, resp);
            return;
        }
        req.getRequestDispatcher("profile.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect("login.jsp"); return; }
        String action = req.getParameter("action");
        if ("update-profile".equals(action)) {
            String fullName = req.getParameter("fullName");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            user.setFullName(fullName);
            user.setPhone(phone);
            user.setAddress(address);
            if (userDAO.updateProfile(user)) {
                req.getSession().setAttribute("user", user);
                req.setAttribute("success", "Cập nhật thông tin thành công");
            } else {
                req.setAttribute("error", "Cập nhật thất bại");
            }
            req.getRequestDispatcher("profile.jsp").forward(req, resp);
        } else if ("change-password".equals(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");
            if (!PasswordUtil.verifyPassword(currentPassword, user.getPassword())) {
                req.setAttribute("error", "Mật khẩu hiện tại không đúng");
                req.getRequestDispatcher("change-password.jsp").forward(req, resp);
                return;
            }
            if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "Mật khẩu xác nhận không khớp");
                req.getRequestDispatcher("change-password.jsp").forward(req, resp);
                return;
            }
            if (newPassword.length() < 6) {
                req.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự");
                req.getRequestDispatcher("change-password.jsp").forward(req, resp);
                return;
            }
            String hashed = PasswordUtil.hashPassword(newPassword);
            if (userDAO.updatePassword(user.getId(), hashed)) {
                req.setAttribute("success", "Đổi mật khẩu thành công");
            } else {
                req.setAttribute("error", "Đổi mật khẩu thất bại");
            }
            req.getRequestDispatcher("change-password.jsp").forward(req, resp);
        }
    }
}
