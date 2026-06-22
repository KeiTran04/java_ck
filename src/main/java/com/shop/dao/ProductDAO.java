package com.shop.dao;

import com.shop.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final Logger log = LoggerFactory.getLogger(ProductDAO.class);

    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapProduct(rs));
        } catch (SQLException e) {
            log.error("findAll failed", e);
        }
        return list;
    }

    public List<Product> findByCategory(int categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.category_id = ? ORDER BY p.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            log.error("findByCategory({}) failed", categoryId, e);
        }
        return list;
    }

    public List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE LOWER(p.name) LIKE LOWER(?) ORDER BY p.id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            log.error("search({}) failed", keyword, e);
        }
        return list;
    }

    public List<Product> findPage(int page, int size) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id ORDER BY p.id LIMIT ? OFFSET ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, size);
            ps.setInt(2, (page - 1) * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapProduct(rs));
            }
        } catch (SQLException e) {
            log.error("findPage({},{}) failed", page, size, e);
        }
        return list;
    }

    public int countAll() {
        String sql = "SELECT COUNT(*) FROM products";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            log.error("countAll failed", e);
        }
        return 0;
    }

    public Product findById(int id) {
        String sql = "SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE p.id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapProduct(rs);
            }
        } catch (SQLException e) {
            log.error("findById({}) failed", id, e);
        }
        return null;
    }

    public boolean save(Product p) {
        String sql = "INSERT INTO products (name, price, stock, image_url, description, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getImageUrl());
            ps.setString(5, p.getDescription());
            if (p.getCategoryId() > 0) ps.setInt(6, p.getCategoryId());
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            log.info("Product '{}' saved", p.getName());
            return true;
        } catch (SQLException e) {
            log.error("save failed", e);
        }
        return false;
    }

    public boolean update(Product p) {
        String sql = "UPDATE products SET name=?, price=?, stock=?, image_url=?, description=?, category_id=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getName());
            ps.setDouble(2, p.getPrice());
            ps.setInt(3, p.getStock());
            ps.setString(4, p.getImageUrl());
            ps.setString(5, p.getDescription());
            if (p.getCategoryId() > 0) ps.setInt(6, p.getCategoryId());
            else ps.setNull(6, Types.INTEGER);
            ps.setInt(7, p.getId());
            ps.executeUpdate();
            log.info("Product {} updated", p.getId());
            return true;
        } catch (SQLException e) {
            log.error("update({}) failed", p.getId(), e);
        }
        return false;
    }

    public List<Product> findFiltered(String search, int categoryId, Double minPrice, Double maxPrice, String sort, int page, int size) {
        List<Product> list = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT p.*, c.name as category_name FROM products p LEFT JOIN categories c ON p.category_id = c.id WHERE 1=1");
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND LOWER(p.name) LIKE LOWER(?)");
            params.add("%" + search.trim() + "%");
        }
        if (categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (minPrice != null) {
            sql.append(" AND p.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.price <= ?");
            params.add(maxPrice);
        }
        String orderBy = "p.id";
        if ("price_asc".equals(sort)) orderBy = "p.price ASC";
        else if ("price_desc".equals(sort)) orderBy = "p.price DESC";
        else if ("name_asc".equals(sort)) orderBy = "p.name ASC";
        else if ("name_desc".equals(sort)) orderBy = "p.name DESC";
        sql.append(" ORDER BY ").append(orderBy).append(" LIMIT ? OFFSET ?");
        params.add(size);
        params.add((page - 1) * size);
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Double) ps.setDouble(i + 1, (Double) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) { while (rs.next()) list.add(mapProduct(rs)); }
        } catch (SQLException e) { log.error("findFiltered failed", e); }
        return list;
    }

    public int countFiltered(String search, int categoryId, Double minPrice, Double maxPrice) {
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM products p WHERE 1=1");
        if (search != null && !search.trim().isEmpty()) {
            sql.append(" AND LOWER(p.name) LIKE LOWER(?)");
            params.add("%" + search.trim() + "%");
        }
        if (categoryId > 0) {
            sql.append(" AND p.category_id = ?");
            params.add(categoryId);
        }
        if (minPrice != null) {
            sql.append(" AND p.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sql.append(" AND p.price <= ?");
            params.add(maxPrice);
        }
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Double) ps.setDouble(i + 1, (Double) p);
                else if (p instanceof Integer) ps.setInt(i + 1, (Integer) p);
                else ps.setString(i + 1, (String) p);
            }
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getInt(1); }
        } catch (SQLException e) { log.error("countFiltered failed", e); }
        return 0;
    }

    public boolean updateStock(int productId, int quantity) {
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ? AND stock >= ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) log.info("Stock updated for product {}: -{}", productId, quantity);
            else log.warn("Insufficient stock for product {}", productId);
            return ok;
        } catch (SQLException e) {
            log.error("updateStock({},{}) failed", productId, quantity, e);
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            boolean ok = ps.executeUpdate() > 0;
            if (ok) log.info("Product {} deleted", id);
            return ok;
        } catch (SQLException e) {
            log.error("delete({}) failed", id, e);
        }
        return false;
    }

    private Product mapProduct(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setStock(rs.getInt("stock"));
        p.setImageUrl(rs.getString("image_url"));
        try { p.setDescription(rs.getString("description")); } catch (SQLException e) { p.setDescription(""); }
        try { p.setCategoryId(rs.getInt("category_id")); } catch (SQLException e) { p.setCategoryId(0); }
        try { p.setCategoryName(rs.getString("category_name")); } catch (SQLException e) { p.setCategoryName(""); }
        return p;
    }
}
