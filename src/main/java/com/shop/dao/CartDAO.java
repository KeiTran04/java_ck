package com.shop.dao;

import com.shop.model.CartItem;
import com.shop.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartDAO {
    private static final Logger log = LoggerFactory.getLogger(CartDAO.class);
    private ProductDAO productDAO = new ProductDAO();

    public List<CartItem> findByUserId(int userId) {
        List<CartItem> list = new ArrayList<>();
        String sql = "SELECT c.product_id, c.quantity FROM cart c WHERE c.user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = productDAO.findById(rs.getInt("product_id"));
                    if (product != null) {
                        list.add(new CartItem(product, rs.getInt("quantity")));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("findByUserId({}) failed", userId, e);
        }
        return list;
    }

    public void save(int userId, int productId, int quantity) {
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) ON CONFLICT (user_id, product_id) DO UPDATE SET quantity = EXCLUDED.quantity";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("save cart({},{}) failed", userId, productId, e);
        }
    }

    public void remove(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("remove cart({},{}) failed", userId, productId, e);
        }
    }

    public void clear(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("clear cart({}) failed", userId, e);
        }
    }

    public void syncFromSession(int userId, List<CartItem> cart) {
        clear(userId);
        String sql = "INSERT INTO cart (user_id, product_id, quantity) VALUES (?, ?, ?) ON CONFLICT (user_id, product_id) DO UPDATE SET quantity = EXCLUDED.quantity";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            for (CartItem item : cart) {
                ps.setInt(1, userId);
                ps.setInt(2, item.getProduct().getId());
                ps.setInt(3, item.getQuantity());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException e) {
            log.error("syncFromSession({}) failed", userId, e);
        }
    }
}
