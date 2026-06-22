package com.shop.dao;

import com.shop.model.User;
import com.shop.util.PasswordUtil;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDAOTest {

    private static UserDAO userDAO;
    private static final String TEST_USER = "testuser_" + System.currentTimeMillis();
    private static final String TEST_EMAIL = "test_" + System.currentTimeMillis() + "@test.com";

    @BeforeAll
    static void setup() {
        userDAO = new UserDAO();
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testRegister() {
        User u = new User();
        u.setUsername(TEST_USER);
        u.setEmail(TEST_EMAIL);
        u.setPassword(PasswordUtil.hashPassword("test123"));

        boolean result = userDAO.register(u);
        assertTrue(result);
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testFindByUsername() {
        User u = userDAO.findByUsername(TEST_USER);
        assertNotNull(u);
        assertEquals(TEST_USER, u.getUsername());
        assertEquals(TEST_EMAIL, u.getEmail());
        assertEquals("CUSTOMER", u.getRole());
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testFindByEmail() {
        User u = userDAO.findByEmail(TEST_EMAIL);
        assertNotNull(u);
        assertEquals(TEST_USER, u.getUsername());
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testFindById() {
        User found = userDAO.findByUsername(TEST_USER);
        User u = userDAO.findById(found.getId());
        assertNotNull(u);
        assertEquals(TEST_USER, u.getUsername());
    }

    @Test
    @org.junit.jupiter.api.Order(5)
    void testFindByUsername_notFound() {
        User u = userDAO.findByUsername("nonexistent_user_xyz");
        assertNull(u);
    }

    @Test
    @org.junit.jupiter.api.Order(6)
    void testRegister_duplicateUsername() {
        User u = new User();
        u.setUsername(TEST_USER);
        u.setEmail("another_" + System.currentTimeMillis() + "@test.com");
        u.setPassword(PasswordUtil.hashPassword("test123"));

        boolean result = userDAO.register(u);
        assertFalse(result);
    }
}
