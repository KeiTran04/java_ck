package com.shop.dao;

import com.shop.model.Coupon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CouponDAO {
    private static final Logger log = LoggerFactory.getLogger(CouponDAO.class);

    public Coupon findByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code = ? AND active = TRUE";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapCoupon(rs); }
        } catch (SQLException e) { log.error("findByCode({}) failed", code, e); }
        return null;
    }

    public Coupon findById(int id) {
        String sql = "SELECT * FROM coupons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapCoupon(rs); }
        } catch (SQLException e) { log.error("findById({}) failed", id, e); }
        return null;
    }

    public List<Coupon> findAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM coupons ORDER BY id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapCoupon(rs));
        } catch (SQLException e) { log.error("findAll failed", e); }
        return list;
    }

    public boolean incrementUsage(int id) {
        String sql = "UPDATE coupons SET used_count = used_count + 1 WHERE id = ? AND (max_usage = 0 OR used_count < max_usage)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { log.error("incrementUsage({}) failed", id, e); }
        return false;
    }

    public boolean save(Coupon c) {
        String sql = "INSERT INTO coupons (code, discount_type, discount_value, min_order_amount, max_usage, expiry_date, active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getDiscountType());
            ps.setDouble(3, c.getDiscountValue());
            ps.setDouble(4, c.getMinOrderAmount());
            ps.setInt(5, c.getMaxUsage());
            ps.setTimestamp(6, c.getExpiryDate() != null ? new Timestamp(c.getExpiryDate().getTime()) : null);
            ps.setBoolean(7, c.isActive());
            ps.executeUpdate();
            log.info("Coupon '{}' saved", c.getCode());
            return true;
        } catch (SQLException e) { log.error("save coupon failed", e); }
        return false;
    }

    public boolean update(Coupon c) {
        String sql = "UPDATE coupons SET code=?, discount_type=?, discount_value=?, min_order_amount=?, max_usage=?, expiry_date=?, active=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setString(2, c.getDiscountType());
            ps.setDouble(3, c.getDiscountValue());
            ps.setDouble(4, c.getMinOrderAmount());
            ps.setInt(5, c.getMaxUsage());
            ps.setTimestamp(6, c.getExpiryDate() != null ? new Timestamp(c.getExpiryDate().getTime()) : null);
            ps.setBoolean(7, c.isActive());
            ps.setInt(8, c.getId());
            ps.executeUpdate();
            log.info("Coupon {} updated", c.getId());
            return true;
        } catch (SQLException e) { log.error("update coupon {} failed", c.getId(), e); }
        return false;
    }

    public boolean toggleActive(int id) {
        String sql = "UPDATE coupons SET active = NOT active WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { log.error("toggleActive({}) failed", id, e); }
        return false;
    }

    private Coupon mapCoupon(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setDiscountType(rs.getString("discount_type"));
        c.setDiscountValue(rs.getDouble("discount_value"));
        c.setMinOrderAmount(rs.getDouble("min_order_amount"));
        c.setMaxUsage(rs.getInt("max_usage"));
        c.setUsedCount(rs.getInt("used_count"));
        c.setExpiryDate(rs.getTimestamp("expiry_date"));
        c.setActive(rs.getBoolean("active"));
        return c;
    }
}
