package com.charging.payment.service;

import com.charging.payment.dto.PaymentRequest;
import com.charging.payment.entity.PaymentRecord;

public interface PaymentService {
    PaymentRecord createPayment(PaymentRequest request);
    PaymentRecord queryPayment(Long paymentId);
    boolean refund(Long paymentId, String reason);
    void handleNotify(String transactionId, String outTradeNo);
}
