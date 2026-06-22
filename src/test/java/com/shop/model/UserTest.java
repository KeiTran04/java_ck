package com.shop.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testDefaultRoleIsNull() {
        User u = new User();
        assertNull(u.getRole());
    }

    @Test
    void testSetAndGetFields() {
        User u = new User();
        u.setId(1);
        u.setUsername("john");
        u.setEmail("john@test.com");
        u.setPassword("hash123");
        u.setRole("ADMIN");

        assertEquals(1, u.getId());
        assertEquals("john", u.getUsername());
        assertEquals("john@test.com", u.getEmail());
        assertEquals("hash123", u.getPassword());
        assertEquals("ADMIN", u.getRole());
    }

    @Test
    void testConstructor() {
        User u = new User(1, "alice", "alice@test.com", "abc123", "CUSTOMER");
        assertEquals("alice", u.getUsername());
        assertEquals("CUSTOMER", u.getRole());
    }
}
