package com.shop.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    @Test
    void testHashPassword_returns64CharHex() {
        String hash = PasswordUtil.hashPassword("hello123");
        assertNotNull(hash);
        assertEquals(64, hash.length());
        assertTrue(hash.matches("[0-9a-f]{64}"));
    }

    @Test
    void testHashPassword_differentPasswords_differentHashes() {
        String hash1 = PasswordUtil.hashPassword("pass1");
        String hash2 = PasswordUtil.hashPassword("pass2");
        assertNotEquals(hash1, hash2);
    }

    @Test
    void testHashPassword_samePassword_sameHash() {
        String hash1 = PasswordUtil.hashPassword("test123");
        String hash2 = PasswordUtil.hashPassword("test123");
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashPassword_emptyString() {
        String hash = PasswordUtil.hashPassword("");
        assertNotNull(hash);
        assertEquals(64, hash.length());
    }

    @Test
    void testVerifyPassword_correctPassword_returnsTrue() {
        String raw = "myPassword";
        String hash = PasswordUtil.hashPassword(raw);
        assertTrue(PasswordUtil.verifyPassword(raw, hash));
    }

    @Test
    void testVerifyPassword_wrongPassword_returnsFalse() {
        String hash = PasswordUtil.hashPassword("correct");
        assertFalse(PasswordUtil.verifyPassword("wrong", hash));
    }

    @Test
    void testVerifyPassword_nullInput() {
        String hash = PasswordUtil.hashPassword("test");
        assertFalse(PasswordUtil.verifyPassword(null, hash));
    }
}
