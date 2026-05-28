package com.charging.common.core.util;

/**
 * 敏感信息脱敏工具类
 */
public class SensitiveUtils {
    
    /**
     * 脱敏手机号
     * 13812345678 -> 138****5678
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
    
    /**
     * 脱敏身份证号
     * 110101199003071234 -> 110101********1234
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return idCard.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1********$2");
    }
    
    /**
     * 脱敏银行卡号
     * 6222021234567890123 -> 6222 **** **** 7890123
     */
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 16) {
            return bankCard;
        }
        return bankCard.replaceAll("(\\d{4})\\d+(\\d{7})", "$1 **** **** $2");
    }
    
    /**
     * 脱敏邮箱
     * test@example.com -> t**t@example.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username + "@" + domain;
        }
        
        return username.charAt(0) + "**" + 
               username.charAt(username.length() - 1) + 
               "@" + domain;
    }
    
    /**
     * 脱敏真实姓名
     * 张三 -> 张*，欧阳小明 -> 欧阳*
     */
    public static String maskName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        return name.substring(0, name.length() - 1) + "*";
    }
}
