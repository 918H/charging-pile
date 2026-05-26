package com.charging.common.service;

public interface NotificationService {
    boolean sendSms(String phone, String templateCode, String... params);
    boolean sendEmail(String to, String subject, String content);
    boolean sendTemplateEmail(String to, String templateCode, Object data);
}
