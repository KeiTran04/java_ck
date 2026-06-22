package com.shop.dao;

import com.shop.model.WishlistItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    private static final Logger log = LoggerFactory.getLogger(WishlistDAO.class);

    public List<WishlistItem> findByUserId(int userId) {
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT w.*, p.name as product_name, p.price as product_price, p.image_url as product_image FROM wishlists w JOIN products p ON w.product_id = p.id WHERE w.user_id = ? ORDER BY w.created_at DESC";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapItem(rs));
            }
        } catch (SQLException e) { log.error("findByUserId({}) failed", userId, e); }
        return list;
    }

    public boolean isWishlisted(int userId, int productId) {
        String sql = "SELECT 1 FROM wishlists WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { log.error("isWishlisted failed", e); }
        return false;
    }

    public boolean add(int userId, int productId) {
        String sql = "INSERT INTO wishlists (user_id, product_id) VALUES (?, ?) ON CONFLICT DO NOTHING";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { log.error("add wishlist failed", e); }
        return false;
    }

    public boolean remove(int userId, int productId) {
        String sql = "DELETE FROM wishlists WHERE user_id = ? AND product_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId); ps.setInt(2, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { log.error("remove wishlist failed", e); }
        return false;
    }

    public int countByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM wishlists WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { log.error("countByUserId failed", e); }
        return 0;
    }

    private WishlistItem mapItem(ResultSet rs) throws SQLException {
        WishlistItem item = new WishlistItem();
        item.setId(rs.getInt("id"));
        item.setUserId(rs.getInt("user_id"));
        item.setProductId(rs.getInt("product_id"));
        item.setProductName(rs.getString("product_name"));
        item.setProductPrice(rs.getDouble("product_price"));
        item.setProductImage(rs.getString("product_image"));
        return item;
    }
}
