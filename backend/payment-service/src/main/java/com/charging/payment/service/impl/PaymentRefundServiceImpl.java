package com.charging.payment.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.payment.dto.RefundRequest;
import com.charging.payment.entity.PaymentRefund;
import com.charging.payment.entity.PaymentRecord;
import com.charging.payment.mapper.PaymentRefundMapper;
import com.charging.payment.mapper.PaymentRecordMapper;
import com.charging.payment.service.PaymentRefundService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentRefundServiceImpl implements PaymentRefundService {

    @Resource
    private PaymentRefundMapper paymentRefundMapper;

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public String applyRefund(RefundRequest request) {
        PaymentRecord payment = null;
        if (request.getPaymentId() != null) {
            payment = paymentRecordMapper.selectById(request.getPaymentId());
        } else if (request.getOrderNumber() != null) {
            LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(PaymentRecord::getOrderNumber, request.getOrderNumber());
            payment = paymentRecordMapper.selectOne(wrapper);
        }

        if (payment == null) {
            return null;
        }

        if (payment.getPaymentStatus() != 1) {
            return null;
        }

        PaymentRefund refund = new PaymentRefund();
        refund.setRefundNumber(generateRefundNumber());
        refund.setPaymentId(payment.getPaymentId());
        refund.setOrderNumber(payment.getOrderNumber());
        refund.setUserId(request.getUserId() != null ? request.getUserId() : payment.getUserId());
        refund.setRefundAmount(request.getRefundAmount() != null ? request.getRefundAmount() : payment.getAmount());
        refund.setRefundType(request.getRefundType() != null ? request.getRefundType() : 0);
        refund.setRefundReason(request.getRefundReason());
        refund.setImages(request.getImages());
        refund.setStatus(0);
        refund.setCreatedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        paymentRefundMapper.insert(refund);
        return refund.getRefundNumber();
    }

    @Override
    public boolean approveRefund(Long refundId, Long auditorId) {
        PaymentRefund refund = paymentRefundMapper.selectById(refundId);
        if (refund == null || refund.getStatus() != 0) {
            return false;
        }

        refund.setStatus(1);
        refund.setAuditorId(auditorId);
        refund.setAuditedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        return paymentRefundMapper.updateById(refund) > 0;
    }

    @Override
    public boolean rejectRefund(Long refundId, Long auditorId, String reason) {
        PaymentRefund refund = paymentRefundMapper.selectById(refundId);
        if (refund == null || refund.getStatus() != 0) {
            return false;
        }

        refund.setStatus(2);
        refund.setAuditorId(auditorId);
        refund.setRejectReason(reason);
        refund.setAuditedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        return paymentRefundMapper.updateById(refund) > 0;
    }

    @Override
    public PaymentRefund getRefundDetail(Long refundId) {
        return paymentRefundMapper.selectById(refundId);
    }

    @Override
    public List<PaymentRefund> getUserRefunds(Long userId) {
        LambdaQueryWrapper<PaymentRefund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRefund::getUserId, userId)
               .orderByDesc(PaymentRefund::getCreatedAt);
        return paymentRefundMapper.selectList(wrapper);
    }

    @Override
    public List<PaymentRefund> getPendingRefunds() {
        LambdaQueryWrapper<PaymentRefund> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRefund::getStatus, 0)
               .orderByAsc(PaymentRefund::getCreatedAt);
        return paymentRefundMapper.selectList(wrapper);
    }

    @Override
    public boolean processRefundPayment(Long refundId, String transactionId) {
        PaymentRefund refund = paymentRefundMapper.selectById(refundId);
        if (refund == null || refund.getStatus() != 1) {
            return false;
        }

        refund.setStatus(3);
        refund.setPaymentTransactionId(transactionId);
        refund.setPayedAt(LocalDateTime.now());
        refund.setUpdatedAt(LocalDateTime.now());

        if (refund.getPaymentId() != null) {
            PaymentRecord payment = paymentRecordMapper.selectById(refund.getPaymentId());
            if (payment != null) {
                payment.setRefundStatus(1);
                payment.setRefundAmount(refund.getRefundAmount());
                paymentRecordMapper.updateById(payment);
            }
        }

        return paymentRefundMapper.updateById(refund) > 0;
    }

    private String generateRefundNumber() {
        return "REF" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") +
               IdUtil.getSnowflakeNextIdStr();
    }
}
