package com.charging.payment.service;

import com.charging.payment.entity.PaymentRecord;
import java.util.List;

public interface PaymentRecordService {
    PaymentRecord getById(Long paymentId);
    PaymentRecord getByOrderId(Long orderId);
    List<PaymentRecord> getListByUserId(Long userId);
    boolean save(PaymentRecord record);
    boolean update(PaymentRecord record);
}
