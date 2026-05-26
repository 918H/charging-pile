package com.charging.payment.service;

import java.math.BigDecimal;

public interface PaymentGatewayService {
    String createOrder(String orderNumber, BigDecimal amount, String userId);
    boolean verifyOrder(String orderNumber, String transactionId);
    boolean refundOrder(String orderNumber, String transactionId, BigDecimal amount);
    String queryOrderStatus(String orderNumber);
}
