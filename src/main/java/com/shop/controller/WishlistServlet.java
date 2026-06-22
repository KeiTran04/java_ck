package com.shop.controller;

import com.shop.dao.WishlistDAO;
import com.shop.model.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/wishlist")
public class WishlistServlet extends HttpServlet {
    private WishlistDAO wishlistDAO = new WishlistDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect("login.jsp"); return; }
        String action = req.getParameter("action");
        if ("check".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            boolean wished = wishlistDAO.isWishlisted(user.getId(), productId);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"wishlisted\":" + wished + "}");
            return;
        }
        if ("remove".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("productId"));
            wishlistDAO.remove(user.getId(), productId);
            if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\":true,\"count\":" + wishlistDAO.countByUserId(user.getId()) + "}");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/wishlist");
            return;
        }
        req.setAttribute("wishlist", wishlistDAO.findByUserId(user.getId()));
        req.getRequestDispatcher("wishlist.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null) { resp.sendRedirect("login.jsp"); return; }
        String action = req.getParameter("action");
        int productId = Integer.parseInt(req.getParameter("productId"));
        boolean success;
        boolean added = false;
        if ("remove".equals(action)) {
            success = wishlistDAO.remove(user.getId(), productId);
        } else if ("toggle".equals(action)) {
            boolean alreadyWished = wishlistDAO.isWishlisted(user.getId(), productId);
            if (alreadyWished) {
                success = wishlistDAO.remove(user.getId(), productId);
            } else {
                success = wishlistDAO.add(user.getId(), productId);
                added = true;
            }
        } else {
            success = wishlistDAO.add(user.getId(), productId);
            added = true;
        }
        if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
            resp.setContentType("application/json");
            resp.getWriter().write("{\"success\":" + success + ",\"action\":\"" + (added ? "added" : "removed") + "\",\"count\":" + wishlistDAO.countByUserId(user.getId()) + "}");
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/wishlist");
    }
}
