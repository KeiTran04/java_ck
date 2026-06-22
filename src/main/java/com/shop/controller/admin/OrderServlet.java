package com.shop.controller.admin;

import com.shop.dao.OrderDAO;
import com.shop.model.Order;
import com.shop.model.OrderDetail;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/orders")
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("view".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            Order order = orderDAO.findById(id);
            req.setAttribute("order", order);
            req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
            return;
        }

        List<Order> orders = orderDAO.findAll();
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/admin/orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("updateStatus".equals(action)) {
            int id = Integer.parseInt(req.getParameter("id"));
            String status = req.getParameter("status");
            if ("CANCELLED".equals(status)) {
                orderDAO.cancelOrder(id);
            } else {
                orderDAO.updateStatus(id, status);
            }
        }
        resp.sendRedirect(req.getContextPath() + "/admin/orders");
    }
}
