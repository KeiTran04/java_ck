package com.shop.controller;

import com.shop.dao.CartDAO;
import com.shop.model.CartItem;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                CartDAO cartDAO = new CartDAO();
                @SuppressWarnings("unchecked")
                List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
                if (cart != null && !cart.isEmpty()) {
                    cartDAO.syncFromSession(user.getId(), cart);
                }
            }
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}
