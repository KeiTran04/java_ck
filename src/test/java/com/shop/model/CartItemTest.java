package com.shop.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private Product createProduct(int id, String name, double price, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName(name);
        p.setPrice(price);
        p.setStock(stock);
        return p;
    }

    @Test
    void testGetSubtotal() {
        Product p = createProduct(1, "Test", 1000, 10);
        CartItem item = new CartItem(p, 3);
        assertEquals(3000, item.getSubtotal(), 0.001);
    }

    @Test
    void testGetSubtotal_zeroQuantity() {
        Product p = createProduct(1, "Test", 500, 10);
        CartItem item = new CartItem(p, 0);
        assertEquals(0, item.getSubtotal(), 0.001);
    }

    @Test
    void testGetSubtotal_singleItem() {
        Product p = createProduct(1, "Test", 9999, 10);
        CartItem item = new CartItem(p, 1);
        assertEquals(9999, item.getSubtotal(), 0.001);
    }
}
