package com.charging.payment.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Random;

@Slf4j
@Service
public class MockPaymentGatewayService implements PaymentGatewayService {

    private static final String MOCK_GATEWAY = "MOCK_GATEWAY";

    @Override
    public String createOrder(String orderNumber, BigDecimal amount, String userId) {
        log.info("Mock payment gateway: create order {} for user {} with amount {}", 
            orderNumber, userId, amount);
        
        String transactionId = "MOCK" + System.currentTimeMillis();
        log.info("Mock payment gateway: generated transaction id {}", transactionId);
        
        return transactionId;
    }

    @Override
    public boolean verifyOrder(String orderNumber, String transactionId) {
        log.info("Mock payment gateway: verify order {} with transaction {}", 
            orderNumber, transactionId);
        return transactionId != null && transactionId.startsWith("MOCK");
    }

    @Override
    public boolean refundOrder(String orderNumber, String transactionId, BigDecimal amount) {
        log.info("Mock payment gateway: refund order {} with transaction {} for amount {}", 
            orderNumber, transactionId, amount);
        return true;
    }

    @Override
    public String queryOrderStatus(String orderNumber) {
        log.info("Mock payment gateway: query order {} status", orderNumber);
        return "SUCCESS";
    }
}
