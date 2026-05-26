package com.charging.payment.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.payment.dto.PaymentRequest;
import com.charging.payment.dto.PaymentResponse;
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
    public PaymentResponse createPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();
        
        try {
            LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentRecord::getOrderNumber, request.getOrderNumber());
            PaymentRecord existingPayment = paymentRecordMapper.selectOne(wrapper);
            
            if (existingPayment != null && existingPayment.getPaymentStatus() == 1) {
                response.setSuccess(false);
                response.setMessage("订单已支付");
                return response;
            }
            
            String paymentNumber = generatePaymentNumber();
            String transactionId = request.getTransactionId() != null 
                ? request.getTransactionId() 
                : "WX" + IdUtil.getSnowflakeNextIdStr();
            
            int paymentStatus = request.getPaymentStatus() != null 
                ? request.getPaymentStatus() 
                : 1;
            
            if (existingPayment != null) {
                existingPayment.setPaymentNumber(paymentNumber);
                existingPayment.setTransactionId(transactionId);
                existingPayment.setPaymentMethod(request.getPaymentMethod());
                existingPayment.setPaymentTime(LocalDateTime.now());
                existingPayment.setPaymentStatus(paymentStatus);
                existingPayment.setRemarks(request.getRemarks());
                existingPayment.setUpdatedAt(LocalDateTime.now());
                
                paymentRecordMapper.updateById(existingPayment);
                
                response.setSuccess(true);
                response.setPaymentId(existingPayment.getPaymentId());
                response.setPaymentNumber(paymentNumber);
                response.setTransactionId(transactionId);
                response.setMessage("支付成功");
            } else {
                PaymentRecord payment = new PaymentRecord();
                payment.setPaymentNumber(paymentNumber);
                payment.setOrderNumber(request.getOrderNumber());
                payment.setUserId(request.getUserId());
                payment.setAmount(request.getAmount());
                payment.setPaymentMethod(request.getPaymentMethod());
                payment.setTransactionId(transactionId);
                payment.setPaymentTime(LocalDateTime.now());
                payment.setPaymentStatus(paymentStatus);
                payment.setRefundStatus(0);
                payment.setRemarks(request.getRemarks());
                payment.setCreatedAt(LocalDateTime.now());
                payment.setUpdatedAt(LocalDateTime.now());
                
                paymentRecordMapper.insert(payment);
                
                response.setSuccess(true);
                response.setPaymentId(payment.getPaymentId());
                response.setPaymentNumber(paymentNumber);
                response.setTransactionId(transactionId);
                response.setMessage("支付成功");
            }
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("支付失败：" + e.getMessage());
        }
        
        return response;
    }

    @Override
    public boolean verifyPayment(String orderNumber, String transactionId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderNumber, orderNumber)
               .eq(PaymentRecord::getTransactionId, transactionId)
               .eq(PaymentRecord::getPaymentStatus, 1);
        
        long count = paymentRecordMapper.selectCount(wrapper);
        return count > 0;
    }

    @Override
    public boolean updatePaymentStatus(String orderNumber, int status) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderNumber, orderNumber);
        
        PaymentRecord payment = paymentRecordMapper.selectOne(wrapper);
        if (payment == null) {
            return false;
        }
        
        payment.setPaymentStatus(status);
        payment.setUpdatedAt(LocalDateTime.now());
        
        return paymentRecordMapper.updateById(payment) > 0;
    }

    private String generatePaymentNumber() {
        return "PAY" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") +
               IdUtil.getSnowflakeNextIdStr();
    }
}
