package com.charging.payment.service;

import com.charging.payment.entity.PaymentRecord;

import java.util.List;

public interface PaymentRecordService {
    List<PaymentRecord> getList(Long userId, Integer status);
    PaymentRecord getById(Long paymentId);
    PaymentRecord getByOrderNumber(String orderNumber);
    boolean save(PaymentRecord record);
    boolean update(PaymentRecord record);
}
