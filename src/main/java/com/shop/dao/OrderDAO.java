package com.shop.dao;

import com.shop.model.Order;
import com.shop.model.OrderDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private static final Logger log = LoggerFactory.getLogger(OrderDAO.class);

    public int createOrder(Order order, List<OrderDetail> details) throws SQLException {
        Connection conn = null;
        PreparedStatement psOrder = null;
        PreparedStatement psDetail = null;
        PreparedStatement psUpdateStock = null;
        ResultSet rs = null;
        int orderId = -1;

        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlOrder = "INSERT INTO orders (user_id, total_amount, status, coupon_id, discount_amount, payment_method) VALUES (?, ?, ?, ?, ?, ?)";
            psOrder = conn.prepareStatement(sqlOrder, new String[]{"id"});
            psOrder.setInt(1, order.getUserId());
            psOrder.setDouble(2, order.getTotalAmount());
            psOrder.setString(3, "PENDING");
            if (order.getCouponId() > 0) psOrder.setInt(4, order.getCouponId());
            else psOrder.setNull(4, Types.INTEGER);
            psOrder.setDouble(5, order.getDiscountAmount());
            psOrder.setString(6, order.getPaymentMethod() != null ? order.getPaymentMethod() : "COD");
            psOrder.executeUpdate();

            rs = psOrder.getGeneratedKeys();
            if (rs.next()) orderId = rs.getInt(1);

            String sqlDetail = "INSERT INTO order_details (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            psDetail = conn.prepareStatement(sqlDetail);
            String sqlUpdateStock = "UPDATE products SET stock = stock - ? WHERE id = ?";
            psUpdateStock = conn.prepareStatement(sqlUpdateStock);

            for (OrderDetail d : details) {
                psDetail.setInt(1, orderId);
                psDetail.setInt(2, d.getProductId());
                psDetail.setInt(3, d.getQuantity());
                psDetail.setDouble(4, d.getPrice());
                psDetail.addBatch();

                psUpdateStock.setInt(1, d.getQuantity());
                psUpdateStock.setInt(2, d.getProductId());
                psUpdateStock.addBatch();
            }
            psDetail.executeBatch();
            psUpdateStock.executeBatch();
            conn.commit();
            log.info("Order {} created with {} items", orderId, details.size());
            return orderId;

        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { log.error("rollback failed", ex); } }
            log.error("createOrder failed", e);
            throw e;
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { log.error("rs close", e); }
            if (psOrder != null) try { psOrder.close(); } catch (SQLException e) { log.error("ps close", e); }
            if (psDetail != null) try { psDetail.close(); } catch (SQLException e) { log.error("ps detail close", e); }
            if (psUpdateStock != null) try { psUpdateStock.close(); } catch (SQLException e) { log.error("ps stock close", e); }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { log.error("conn close", e); }
        }
    }

    public boolean cancelOrder(int orderId) {
        Connection conn = null;
        PreparedStatement psUpdate = null;
        PreparedStatement psDetails = null;
        PreparedStatement psRestoreStock = null;
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            psUpdate = conn.prepareStatement("UPDATE orders SET status = 'CANCELLED' WHERE id = ? AND status = 'PENDING'");
            psUpdate.setInt(1, orderId);
            int rows = psUpdate.executeUpdate();
            if (rows == 0) { conn.rollback(); return false; }
            psDetails = conn.prepareStatement("SELECT product_id, quantity FROM order_details WHERE order_id = ?");
            psDetails.setInt(1, orderId);
            rs = psDetails.executeQuery();
            psRestoreStock = conn.prepareStatement("UPDATE products SET stock = stock + ? WHERE id = ?");
            while (rs.next()) {
                psRestoreStock.setInt(1, rs.getInt("quantity"));
                psRestoreStock.setInt(2, rs.getInt("product_id"));
                psRestoreStock.addBatch();
            }
            psRestoreStock.executeBatch();
            conn.commit();
            log.info("Order {} cancelled, stock restored", orderId);
            return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { log.error("rollback failed", ex); } }
            log.error("cancelOrder({}) failed", orderId, e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { }
            if (psUpdate != null) try { psUpdate.close(); } catch (SQLException e) { }
            if (psDetails != null) try { psDetails.close(); } catch (SQLException e) { }
            if (psRestoreStock != null) try { psRestoreStock.close(); } catch (SQLException e) { }
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { }
        }
        return false;
    }

    public void updateInvoicePath(int orderId, String path) {
        String sql = "UPDATE orders SET invoice_path = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            log.info("Invoice path updated for order {}", orderId);
        } catch (SQLException e) {
            log.error("updateInvoicePath({}) failed", orderId, e);
        }
    }

    public void updateStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            ps.executeUpdate();
            log.info("Order {} status updated to {}", orderId, status);
        } catch (SQLException e) {
            log.error("updateStatus({}) failed", orderId, e);
        }
    }

    public Order findById(int orderId) {
        String sql = "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id WHERE o.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setUsername(rs.getString("username"));
                    o.setDetails(findDetailsByOrderId(orderId));
                    return o;
                }
            }
        } catch (SQLException e) {
            log.error("findById({}) failed", orderId, e);
        }
        return null;
    }

    public List<Order> findByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order o = mapOrder(rs);
                    o.setDetails(findDetailsByOrderId(o.getId()));
                    list.add(o);
                }
            }
        } catch (SQLException e) {
            log.error("findByUserId({}) failed", userId, e);
        }
        return list;
    }

    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, u.username FROM orders o JOIN users u ON o.user_id = u.id ORDER BY o.order_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order o = mapOrder(rs);
                o.setUsername(rs.getString("username"));
                list.add(o);
            }
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    public List<OrderDetail> findDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = "SELECT od.*, p.name as product_name FROM order_details od JOIN products p ON od.product_id = p.id WHERE od.order_id = ? ORDER BY od.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderDetail d = new OrderDetail();
                    d.setId(rs.getInt("id"));
                    d.setOrderId(rs.getInt("order_id"));
                    d.setProductId(rs.getInt("product_id"));
                    d.setQuantity(rs.getInt("quantity"));
                    d.setPrice(rs.getDouble("price"));
                    d.setProductName(rs.getString("product_name"));
                    list.add(d);
                }
            }
        } catch (SQLException e) {
            log.error("findDetailsByOrderId({}) failed", orderId, e);
        }
        return list;
    }

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setInvoicePath(rs.getString("invoice_path"));
        try { o.setStatus(rs.getString("status")); } catch (SQLException e) { o.setStatus("PENDING"); }
        try { o.setCouponId(rs.getInt("coupon_id")); } catch (SQLException e) { o.setCouponId(0); }
        try { o.setDiscountAmount(rs.getDouble("discount_amount")); } catch (SQLException e) { o.setDiscountAmount(0); }
        try { o.setPaymentMethod(rs.getString("payment_method")); } catch (SQLException e) { o.setPaymentMethod("COD"); }
        return o;
    }
}
