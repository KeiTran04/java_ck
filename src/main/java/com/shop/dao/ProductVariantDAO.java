package com.shop.dao;

import com.shop.model.ProductVariant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductVariantDAO {
    private static final Logger log = LoggerFactory.getLogger(ProductVariantDAO.class);

    public List<ProductVariant> findByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? ORDER BY variant_type, id";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapVariant(rs)); }
        } catch (SQLException e) { log.error("findByProductId({}) failed", productId, e); }
        return list;
    }

    public ProductVariant findById(int id) {
        String sql = "SELECT * FROM product_variants WHERE id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return mapVariant(rs); }
        } catch (SQLException e) { log.error("findById({}) failed", id, e); }
        return null;
    }

    private ProductVariant mapVariant(ResultSet rs) throws SQLException {
        ProductVariant v = new ProductVariant();
        v.setId(rs.getInt("id"));
        v.setProductId(rs.getInt("product_id"));
        v.setVariantType(rs.getString("variant_type"));
        v.setVariantName(rs.getString("variant_name"));
        v.setPriceAdjustment(rs.getDouble("price_adjustment"));
        v.setStock(rs.getInt("stock"));
        return v;
    }
}
