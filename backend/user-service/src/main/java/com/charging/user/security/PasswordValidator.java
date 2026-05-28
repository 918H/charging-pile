package com.charging.user.security;

import java.util.regex.Pattern;

public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 20;
    private static final Pattern UPPER_CASE = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWER_CASE = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT = Pattern.compile(".*\\d.*");
    private static final Pattern SPECIAL_CHAR = Pattern.compile(".*[!@#$%^&*(),.?\":{}|<>].*");

    public static ValidationResult validate(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationR.fail("密码不能为空");
        }

        if (password.length() < MIN_LENGTH || password.length() > MAX_LENGTH) {
            return ValidationR.fail("密码长度必须在 8-20 之间");
        }

        if (!UPPER_CASE.matcher(password).matches()) {
            return ValidationR.fail("密码必须包含至少一个大写字母");
        }

        if (!LOWER_CASE.matcher(password).matches()) {
            return ValidationR.fail("密码必须包含至少一个小写字母");
        }

        if (!DIGIT.matcher(password).matches()) {
            return ValidationR.fail("密码必须包含至少一个数字");
        }

        if (!SPECIAL_CHAR.matcher(password).matches()) {
            return ValidationR.fail("密码必须包含至少一个特殊字符");
        }

        return ValidationR.ok();
    }

    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult fail(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
