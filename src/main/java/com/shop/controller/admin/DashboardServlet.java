package com.shop.controller.admin;

import com.shop.dao.OrderDAO;
import com.shop.dao.ProductDAO;
import com.shop.dao.UserDAO;
import com.shop.model.Order;
import com.shop.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/admin/dashboard")
public class DashboardServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        List<Product> products = productDAO.findAll();
        List<Order> orders = orderDAO.findAll();

        int totalProducts = products.size();
        int totalOrders = orders.size();
        double totalRevenue = 0;
        int totalStock = 0;
        for (Product p : products) totalStock += p.getStock();
        for (Order o : orders) totalRevenue += o.getTotalAmount();

        req.setAttribute("totalUsers", userDAO.countAll());
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("totalStock", totalStock);
        req.setAttribute("recentOrders", orders.size() > 5 ? orders.subList(0, 5) : orders);

        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
