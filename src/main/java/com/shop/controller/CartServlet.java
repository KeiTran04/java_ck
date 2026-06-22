package com.shop.controller;

import com.shop.dao.CartDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.CartItem;
import com.shop.model.Product;
import com.shop.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    private ProductDAO productDAO = new ProductDAO();
    private CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String action = req.getParameter("action");
        if ("remove".equals(action)) {
            int productId = Integer.parseInt(req.getParameter("id"));
            removeFromCart(req, productId);
            if (isAjax(req)) {
                writeJson(resp, "{\"success\":true,\"count\":" + getCartCount(req) + "}");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/cart");
            return;
        }
        if ("count".equals(action)) {
            writeJson(resp, "{\"count\":" + getCartCount(req) + "}");
            return;
        }

        HttpSession session = req.getSession();
        loadCartFromDbIfNeeded(session);
        List<CartItem> cart = getCart(session);
        req.setAttribute("cart", cart);
        req.getRequestDispatcher("cart.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            String action = req.getParameter("action");

            if ("update".equals(action)) {
                String pidParam = req.getParameter("productId");
                if (pidParam == null) pidParam = req.getParameter("id");
                int productId = Integer.parseInt(pidParam);
                int quantity = Integer.parseInt(req.getParameter("quantity"));
                double subtotal = 0;
                if (quantity <= 0) {
                    removeFromCart(req, productId);
                } else {
                    HttpSession session = req.getSession();
                    List<CartItem> cart = getCart(session);
                    for (CartItem item : cart) {
                        if (item.getProduct().getId() == productId) {
                            Product product = productDAO.findById(productId);
                            if (quantity > product.getStock()) quantity = product.getStock();
                            item.setQuantity(quantity);
                            subtotal = item.getSubtotal();
                            break;
                        }
                    }
                }
                syncToDb(req);
                double total = getCartTotal(req);
                writeJson(resp, "{\"success\":true,\"count\":" + getCartCount(req) + ",\"subtotal\":" + subtotal + ",\"total\":" + total + "}");
                return;
            }

            String pid = req.getParameter("productId");
            String qty = req.getParameter("quantity");
            if (pid == null || qty == null) {
                if (isAjax(req)) {
                    writeJson(resp, "{\"success\":false,\"error\":\"Thiếu thông tin sản phẩm\"}");
                    return;
                }
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            int productId = Integer.parseInt(pid);
            int quantity = Integer.parseInt(qty);
            if (quantity <= 0) quantity = 1;

            Product product = productDAO.findById(productId);
            if (product == null) {
                if (isAjax(req)) {
                    writeJson(resp, "{\"success\":false,\"error\":\"Sản phẩm không tồn tại\"}");
                    return;
                }
                resp.sendRedirect(req.getContextPath() + "/home");
                return;
            }
            if (quantity > product.getStock()) quantity = product.getStock();

            HttpSession session = req.getSession();
            List<CartItem> cart = getCart(session);

            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProduct().getId() == productId) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) cart.add(new CartItem(product, quantity));

            session.setAttribute("cart", cart);
            syncToDb(req);

            if (isAjax(req)) {
                writeJson(resp, "{\"success\":true,\"count\":" + getCartCount(req) + "}");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/cart");
        } catch (NumberFormatException e) {
            if (isAjax(req)) {
                writeJson(resp, "{\"success\":false,\"error\":\"Dữ liệu không hợp lệ\"}");
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/home");
        }
    }

    private void syncToDb(HttpServletRequest req) {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user != null) {
            List<CartItem> cart = getCart(session);
            cartDAO.syncFromSession(user.getId(), cart);
        }
    }

    private void loadCartFromDbIfNeeded(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return;
        List<CartItem> sessionCart = (List<CartItem>) session.getAttribute("cart");
        if (sessionCart == null || sessionCart.isEmpty()) {
            List<CartItem> dbCart = cartDAO.findByUserId(user.getId());
            if (!dbCart.isEmpty()) {
                session.setAttribute("cart", dbCart);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpSession session) {
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    private void removeFromCart(HttpServletRequest req, int productId) {
        HttpSession session = req.getSession();
        List<CartItem> cart = getCart(session);
        cart.removeIf(item -> item.getProduct().getId() == productId);
        syncToDb(req);
    }

    private int getCartCount(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return getCart(session).stream().mapToInt(CartItem::getQuantity).sum();
    }

    private double getCartTotal(HttpServletRequest req) {
        HttpSession session = req.getSession();
        return getCart(session).stream().mapToDouble(CartItem::getSubtotal).sum();
    }

    private boolean isAjax(HttpServletRequest req) {
        return "XMLHttpRequest".equals(req.getHeader("X-Requested-With"));
    }

    private void writeJson(HttpServletResponse resp, String json) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }
}
