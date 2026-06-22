package com.shop.controller;

import com.shop.dao.CouponDAO;
import com.shop.model.CartItem;
import com.shop.model.Coupon;
import com.shop.model.Order;
import com.shop.model.User;
import com.shop.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private OrderService orderService = new OrderService();
    private CouponDAO couponDAO = new CouponDAO();

    @Override
    public void destroy() {
        orderService.shutdown();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath() + "/cart"); return; }

        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();

        Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
        Double discountAmount = (Double) session.getAttribute("discountAmount");
        if (appliedCoupon != null && discountAmount != null) {
            req.setAttribute("appliedCoupon", appliedCoupon);
            req.setAttribute("discountAmount", discountAmount);
            req.setAttribute("totalAfterDiscount", total - discountAmount);
        }

        req.setAttribute("cart", cart);
        req.setAttribute("total", total);
        req.getRequestDispatcher("checkout.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) { resp.sendRedirect(req.getContextPath() + "/login.jsp"); return; }

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) { resp.sendRedirect(req.getContextPath() + "/cart"); return; }

        String action = req.getParameter("action");
        if ("apply-coupon".equals(action)) {
            String code = req.getParameter("couponCode");
            double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
            if (code != null && !code.isEmpty()) {
                Coupon coupon = couponDAO.findByCode(code);
                if (coupon == null) {
                    req.setAttribute("error", "Mã giảm giá không hợp lệ");
                } else if (coupon.getExpiryDate() != null && coupon.getExpiryDate().before(new Date())) {
                    req.setAttribute("error", "Mã giảm giá đã hết hạn");
                } else if (total < coupon.getMinOrderAmount()) {
                    req.setAttribute("error", "Đơn hàng tối thiểu " + coupon.getMinOrderAmount() + " VND");
                } else {
                    double discount = coupon.calculateDiscount(total);
                    session.setAttribute("appliedCoupon", coupon);
                    session.setAttribute("discountAmount", discount);
                    req.setAttribute("appliedCoupon", coupon);
                    req.setAttribute("discountAmount", discount);
                    req.setAttribute("totalAfterDiscount", total - discount);
                    req.setAttribute("success", "Áp dụng mã giảm giá thành công!");
                }
            }
            req.setAttribute("cart", cart);
            req.setAttribute("total", total);
            req.getRequestDispatcher("checkout.jsp").forward(req, resp);
            return;
        }

        String paymentMethod = req.getParameter("paymentMethod");
        if (paymentMethod == null) paymentMethod = "COD";

        double total = cart.stream().mapToDouble(CartItem::getSubtotal).sum();
        Coupon appliedCoupon = (Coupon) session.getAttribute("appliedCoupon");
        Double discountAmount = (Double) session.getAttribute("discountAmount");
        int couponId = 0;
        double discount = 0;
        if (appliedCoupon != null && discountAmount != null) {
            couponId = appliedCoupon.getId();
            discount = discountAmount;
        }

        OrderService.CheckoutResult result = orderService.processCheckout(user, cart, getServletContext(), paymentMethod, couponId, discount);

        if (result.success) {
            if (couponId > 0) {
                couponDAO.incrementUsage(couponId);
                session.removeAttribute("appliedCoupon");
                session.removeAttribute("discountAmount");
            }
            session.removeAttribute("cart");
            req.setAttribute("orderId", result.orderId);
            req.setAttribute("total", result.total);
            req.setAttribute("paymentMethod", paymentMethod);
            req.getRequestDispatcher("order-success.jsp").forward(req, resp);
        } else {
            req.setAttribute("error", result.error);
            req.getRequestDispatcher("checkout.jsp").forward(req, resp);
        }
    }
}
