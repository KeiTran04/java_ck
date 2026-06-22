package com.shop.dao;

import com.shop.model.Product;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductDAOTest {

    private static ProductDAO productDAO;
    private static int createdId;

    @BeforeAll
    static void setup() {
        productDAO = new ProductDAO();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testFindAll_returnsSampleData() {
        List<Product> list = productDAO.findAll();
        assertNotNull(list);
        assertTrue(list.size() >= 5);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testSave() {
        Product p = new Product();
        p.setName("Test Product");
        p.setPrice(99999);
        p.setStock(100);
        p.setImageUrl("test.jpg");

        boolean result = productDAO.save(p);
        assertTrue(result);
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testFindById() {
        List<Product> list = productDAO.findAll();
        Product last = list.get(list.size() - 1);
        createdId = last.getId();

        Product found = productDAO.findById(createdId);
        assertNotNull(found);
        assertEquals("Test Product", found.getName());
        assertEquals(99999, found.getPrice(), 0.001);
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testUpdate() {
        Product p = productDAO.findById(createdId);
        p.setName("Updated Product");
        p.setPrice(88888);
        p.setStock(50);

        boolean result = productDAO.update(p);
        assertTrue(result);

        Product updated = productDAO.findById(createdId);
        assertEquals("Updated Product", updated.getName());
        assertEquals(88888, updated.getPrice(), 0.001);
        assertEquals(50, updated.getStock());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testUpdateStock_sufficientStock() {
        boolean result = productDAO.updateStock(createdId, 10);
        assertTrue(result);

        Product p = productDAO.findById(createdId);
        assertEquals(40, p.getStock());
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testUpdateStock_insufficientStock() {
        boolean result = productDAO.updateStock(createdId, 999);
        assertFalse(result);
    }

    @Test
    @org.junit.jupiter.api.Order(7)
    void testFindById_notFound() {
        Product p = productDAO.findById(-1);
        assertNull(p);
    }

    @Test
    @org.junit.jupiter.api.Order(8)
    void testDelete() {
        boolean result = productDAO.delete(createdId);
        assertTrue(result);

        Product p = productDAO.findById(createdId);
        assertNull(p);
    }
}
