package com.shop.service;

import com.shop.dao.OrderDAO;
import com.shop.dao.ProductDAO;
import com.shop.model.*;
import com.shop.task.OrderProcessingTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private OrderDAO orderDAO = new OrderDAO();
    private ProductDAO productDAO = new ProductDAO();
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void shutdown() {
        executorService.shutdown();
    }

    private String getInvoiceDir() {
        try (InputStream is = OrderService.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                String dir = props.getProperty("invoice.dir");
                if (dir != null && !dir.trim().isEmpty()) {
                    return dir.trim();
                }
            }
        } catch (Exception e) {
            log.warn("Could not read invoice.dir from db.properties", e);
        }
        String defaultDir = System.getProperty("user.home") + java.io.File.separator + "shop-invoices";
        log.info("Using default invoice directory: {}", defaultDir);
        return defaultDir;
    }

    public static class CheckoutResult {
        public boolean success;
        public String error;
        public int orderId;
        public double total;
    }

    public CheckoutResult processCheckout(User user, List<CartItem> cart, ServletContext context) {
        return processCheckout(user, cart, context, "COD", 0, 0);
    }

    public CheckoutResult processCheckout(User user, List<CartItem> cart, ServletContext context, String paymentMethod, int couponId, double discountAmount) {
        CheckoutResult result = new CheckoutResult();

        if (user == null) { result.error = "Vui lòng đăng nhập"; return result; }
        if (cart == null || cart.isEmpty()) { result.error = "Giỏ hàng trống"; return result; }

        double total = 0;
        List<OrderDetail> details = new ArrayList<>();

        for (CartItem item : cart) {
            Product product = productDAO.findById(item.getProduct().getId());
            if (product == null) { result.error = "Sản phẩm không tồn tại"; return result; }
            if (product.getStock() < item.getQuantity()) {
                result.error = "Sản phẩm \"" + product.getName() + "\" không đủ hàng!";
                return result;
            }
            total += item.getSubtotal();

            OrderDetail detail = new OrderDetail();
            detail.setProductId(product.getId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(product.getPrice());
            details.add(detail);
        }

        Order order = new Order();
        order.setUserId(user.getId());
        order.setOrderDate(new Date());
        order.setTotalAmount(total);
        order.setPaymentMethod(paymentMethod);
        order.setCouponId(couponId);
        order.setDiscountAmount(discountAmount);

        try {
            int orderId = orderDAO.createOrder(order, details);
            order.setId(orderId);

            String invoiceDir = getInvoiceDir();
            OrderProcessingTask task = new OrderProcessingTask(order, details, user.getEmail(), invoiceDir);
            executorService.submit(task);

            log.info("Order #{} created for user '{}'", orderId, user.getUsername());

            result.success = true;
            result.orderId = orderId;
            result.total = total;
            return result;

        } catch (Exception e) {
            log.error("Failed to create order", e);
            result.error = "Đặt hàng thất bại, vui lòng thử lại!";
            return result;
        }
    }
}
