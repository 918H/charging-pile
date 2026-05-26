package com.charging.common.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationServiceImpl implements com.charging.common.service.NotificationService {

    @Value("${sms.aliyun.enabled:false}")
    private boolean smsEnabled;

    @Value("${email.enabled:false}")
    private boolean emailEnabled;

    @Override
    public boolean sendSms(String phone, String templateCode, String... params) {
        if (!smsEnabled) {
            log.warn("短信功能未启用，手机号：{}, 模板：{}", phone, templateCode);
            return false;
        }

        try {
            log.info("发送短信：手机号={}, 模板={}, 参数={}", phone, templateCode, params);
            
            // TODO: 集成阿里云短信服务
            // AliyunSmsClient.sendSms(phone, templateCode, params);
            
            return true;
        } catch (Exception e) {
            log.error("发送短信失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendEmail(String to, String subject, String content) {
        if (!emailEnabled) {
            log.warn("邮件功能未启用，收件人：{}", to);
            return false;
        }

        try {
            log.info("发送邮件：收件人={}, 主题={}", to, subject);
            
            // TODO: 集成邮件发送服务
            // JavaMailSender.send(to, subject, content);
            
            return true;
        } catch (Exception e) {
            log.error("发送邮件失败：{}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean sendTemplateEmail(String to, String templateCode, Object data) {
        return sendEmail(to, getTemplateSubject(templateCode), renderTemplate(templateCode, data));
    }

    private String getTemplateSubject(String templateCode) {
        // 根据模板代码获取主题
        switch (templateCode) {
            case "ORDER_CREATED": return "订单创建成功";
            case "PAYMENT_SUCCESS": return "支付成功通知";
            case "ORDER_CANCELLED": return "订单取消通知";
            default: return "系统通知";
        }
    }

    private String renderTemplate(String templateCode, Object data) {
        // TODO: 实现模板渲染
        return "通知内容";
    }

    public boolean sendOrderCreatedSms(String phone, String orderNumber) {
        return sendSms(phone, "SMS_123456789", orderNumber);
    }

    public boolean sendPaymentSuccessSms(String phone, String orderNumber, String amount) {
        return sendSms(phone, "SMS_987654321", orderNumber, amount);
    }
}
