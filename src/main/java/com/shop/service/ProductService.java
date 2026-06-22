package com.shop.service;

import com.shop.dao.ProductDAO;
import com.shop.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private ProductDAO productDAO = new ProductDAO();

    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }

    public Product getProductById(int id) {
        return productDAO.findById(id);
    }

    public String saveProduct(String name, double price, int stock, String imageUrl, String description, int categoryId) {
        if (name == null || name.trim().isEmpty()) return "Tên sản phẩm không được để trống";
        if (price < 0) return "Giá không được âm";
        if (stock < 0) return "Tồn kho không được âm";

        Product p = new Product();
        p.setName(name.trim());
        p.setPrice(price);
        p.setStock(stock);
        p.setImageUrl(imageUrl);
        p.setDescription(description != null ? description : "");
        p.setCategoryId(categoryId);
        productDAO.save(p);
        log.info("Product '{}' created", name);
        return null;
    }

    public String updateProduct(int id, String name, double price, int stock, String imageUrl, String description, int categoryId) {
        Product existing = productDAO.findById(id);
        if (existing == null) return "Sản phẩm không tồn tại";
        if (name == null || name.trim().isEmpty()) return "Tên sản phẩm không được để trống";

        existing.setName(name.trim());
        existing.setPrice(price);
        existing.setStock(stock);
        if (imageUrl != null) existing.setImageUrl(imageUrl);
        existing.setDescription(description != null ? description : "");
        existing.setCategoryId(categoryId);
        productDAO.update(existing);
        log.info("Product '{}' updated", name);
        return null;
    }

    public boolean deleteProduct(int id) {
        boolean ok = productDAO.delete(id);
        if (ok) log.info("Product {} deleted", id);
        return ok;
    }
}
