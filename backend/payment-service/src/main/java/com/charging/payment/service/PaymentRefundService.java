package com.charging.payment.service;

import com.charging.payment.dto.RefundRequest;
import com.charging.payment.entity.PaymentRefund;

import java.util.List;

public interface PaymentRefundService {
    String applyRefund(RefundRequest request);
    boolean approveRefund(Long refundId, Long auditorId);
    boolean rejectRefund(Long refundId, Long auditorId, String reason);
    PaymentRefund getRefundDetail(Long refundId);
    List<PaymentRefund> getUserRefunds(Long userId);
    List<PaymentRefund> getPendingRefunds();
    boolean processRefundPayment(Long refundId, String transactionId);
}
