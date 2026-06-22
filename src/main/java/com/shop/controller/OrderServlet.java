package com.shop.controller;

import com.shop.dao.OrderDAO;
import com.shop.model.Order;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@WebServlet("/orders")
public class OrderServlet extends HttpServlet {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp");
            return;
        }

        String action = req.getParameter("action");
        if ("cancel".equals(action)) {
            int orderId = Integer.parseInt(req.getParameter("id"));
            boolean ok = orderDAO.cancelOrder(orderId);
            if ("XMLHttpRequest".equals(req.getHeader("X-Requested-With"))) {
                resp.setContentType("application/json");
                resp.getWriter().write("{\"success\":" + ok + "}");
                return;
            }
            req.setAttribute(ok ? "success" : "error", ok ? "Đã hủy đơn hàng" : "Không thể hủy (đơn hàng không ở trạng thái PENDING)");
        } else if ("download-invoice".equals(action)) {
            int orderId = Integer.parseInt(req.getParameter("id"));
            Order o = orderDAO.findById(orderId);
            if (o == null || o.getUserId() != user.getId()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            String path = o.getInvoicePath();
            if (path == null || path.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            File f = new File(path);
            if (!f.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            resp.setContentType("application/xml");
            resp.setHeader("Content-Disposition", "attachment; filename=invoice_" + orderId + ".xml");
            resp.setContentLength((int) f.length());
            try (FileInputStream fis = new FileInputStream(f); OutputStream os = resp.getOutputStream()) {
                byte[] buf = new byte[4096];
                int len;
                while ((len = fis.read(buf)) != -1) os.write(buf, 0, len);
            }
            return;
        }

        List<Order> orders = orderDAO.findByUserId(user.getId());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("orders.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doGet(req, resp);
    }
}
