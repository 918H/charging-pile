package com.charging.payment.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.charging.payment.dto.PaymentRequest;
import com.charging.payment.entity.PaymentRecord;
import com.charging.payment.mapper.PaymentRecordMapper;
import com.charging.payment.service.PaymentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public PaymentRecord createPayment(PaymentRequest request) {
        PaymentRecord record = new PaymentRecord();
        record.setOrderId(request.getOrderId());
        record.setAmount(request.getAmount());
        record.setPaymentMethod(request.getPaymentMethod());
        record.setStatus(0);
        record.setPaymentNumber(generatePaymentNumber());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        paymentRecordMapper.insert(record);
        return record;
    }

    @Override
    public PaymentRecord queryPayment(Long paymentId) {
        return paymentRecordMapper.selectById(paymentId);
    }

    @Override
    public boolean refund(Long paymentId, String reason) {
        PaymentRecord record = paymentRecordMapper.selectById(paymentId);
        if (record == null || record.getStatus() != 1) {
            return false;
        }
        record.setStatus(2);
        record.setUpdatedAt(LocalDateTime.now());
        return paymentRecordMapper.updateById(record) > 0;
    }

    @Override
    public void handleNotify(String transactionId, String outTradeNo) {
        PaymentRecord record = paymentRecordMapper.selectByOutTradeNo(outTradeNo);
        if (record != null && record.getStatus() == 0) {
            record.setStatus(1);
            record.setThirdPartyId(transactionId);
            record.setPaidAt(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
        }
    }

    private String generatePaymentNumber() {
        return "PAY" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + 
               IdUtil.getSnowflakeNextIdStr();
    }
}
