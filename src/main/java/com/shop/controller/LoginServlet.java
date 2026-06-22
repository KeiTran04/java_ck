package com.shop.controller;

import com.shop.dao.CartDAO;
import com.shop.model.CartItem;
import com.shop.model.User;
import com.shop.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            req.setAttribute("error", "Vui lòng nhập tên đăng nhập và mật khẩu");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        User user = userService.login(username, password);
        if (user == null) {
            req.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng");
            req.setAttribute("username", username);
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        HttpSession session = req.getSession();
        session.setAttribute("user", user);

        CartDAO cartDAO = new CartDAO();
        List<CartItem> dbCart = cartDAO.findByUserId(user.getId());
        if (!dbCart.isEmpty()) {
            session.setAttribute("cart", dbCart);
        }

        if ("ADMIN".equals(user.getRole())) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }
}
