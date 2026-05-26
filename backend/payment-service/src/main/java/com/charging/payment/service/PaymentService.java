package com.charging.payment.service;

import com.charging.payment.dto.PaymentRequest;
import com.charging.payment.dto.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPayment(PaymentRequest request);
    boolean verifyPayment(String orderNumber, String transactionId);
    boolean updatePaymentStatus(String orderNumber, int status);
}
