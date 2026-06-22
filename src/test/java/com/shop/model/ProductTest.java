package com.shop.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void testSettersAndGetters() {
        Product p = new Product();
        p.setId(1);
        p.setName("Laptop");
        p.setPrice(15000000);
        p.setStock(5);
        p.setImageUrl("laptop.jpg");
        assertEquals(1, p.getId());
        assertEquals("Laptop", p.getName());
        assertEquals(15000000, p.getPrice(), 0.001);
        assertEquals(5, p.getStock());
        assertEquals("laptop.jpg", p.getImageUrl());
    }

    @Test
    void testDefaultConstructor() {
        Product p = new Product();
        assertNotNull(p);
    }

    @Test
    void testNegativeStock() {
        Product p = new Product();
        p.setStock(-1);
        assertEquals(-1, p.getStock());
    }
}
