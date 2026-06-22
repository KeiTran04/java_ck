package com.shop.dao;

import com.shop.model.Order;
import com.shop.model.OrderDetail;
import org.junit.jupiter.api.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderDAOTest {

    private static OrderDAO orderDAO;
    private static int testUserId;
    private static int createdOrderId;

    @BeforeAll
    static void setup() {
        orderDAO = new OrderDAO();
        UserDAO userDAO = new UserDAO();
        String uniqueUser = "ordertest_" + System.currentTimeMillis();
        com.shop.model.User u = new com.shop.model.User();
        u.setUsername(uniqueUser);
        u.setEmail(uniqueUser + "@test.com");
        u.setPassword(com.shop.util.PasswordUtil.hashPassword("test123"));
        userDAO.register(u);

        com.shop.model.User created = userDAO.findByUsername(uniqueUser);
        testUserId = created.getId();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testCreateOrder() throws Exception {
        Order order = new Order();
        order.setUserId(testUserId);
        order.setTotalAmount(150000);

        List<OrderDetail> details = new ArrayList<>();
        OrderDetail d1 = new OrderDetail();
        d1.setProductId(1);
        d1.setQuantity(2);
        d1.setPrice(50000);
        details.add(d1);

        OrderDetail d2 = new OrderDetail();
        d2.setProductId(2);
        d2.setQuantity(1);
        d2.setPrice(50000);
        details.add(d2);

        int orderId = orderDAO.createOrder(order, details);
        assertTrue(orderId > 0);
        createdOrderId = orderId;
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testFindByUserId() {
        List<Order> orders = orderDAO.findByUserId(testUserId);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        assertEquals(createdOrderId, orders.get(0).getId());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testFindDetailsByOrderId() {
        List<OrderDetail> details = orderDAO.findDetailsByOrderId(createdOrderId);
        assertNotNull(details);
        assertEquals(2, details.size());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testUpdateInvoicePath() {
        orderDAO.updateInvoicePath(createdOrderId, "/invoices/invoice_" + createdOrderId + ".xml");
        List<Order> orders = orderDAO.findByUserId(testUserId);
        assertEquals("/invoices/invoice_" + createdOrderId + ".xml", orders.get(0).getInvoicePath());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testFindAll() {
        List<Order> all = orderDAO.findAll();
        assertNotNull(all);
        assertTrue(all.size() > 0);
    }
}
