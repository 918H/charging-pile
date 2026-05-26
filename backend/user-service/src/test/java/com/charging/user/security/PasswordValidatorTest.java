package com.charging.user.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void testValidPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test123!");
        assertTrue(result.isValid());
        assertNull(result.getMessage());
    }

    @Test
    void testPasswordTooShort() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("T1!");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("长度"));
    }

    @Test
    void testPasswordTooLong() {
        String longPassword = "Test1234567890123456789!";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(longPassword);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("长度"));
    }

    @Test
    void testPasswordNoUpperCase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("test123!");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("大写字母"));
    }

    @Test
    void testPasswordNoLowerCase() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("TEST123!");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("小写字母"));
    }

    @Test
    void testPasswordNoDigit() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("TestTest!");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("数字"));
    }

    @Test
    void testPasswordNoSpecialChar() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("Test1234");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("特殊字符"));
    }

    @Test
    void testNullPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(null);
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("不能为空"));
    }

    @Test
    void testEmptyPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("");
        assertFalse(result.isValid());
        assertTrue(result.getMessage().contains("不能为空"));
    }
}
