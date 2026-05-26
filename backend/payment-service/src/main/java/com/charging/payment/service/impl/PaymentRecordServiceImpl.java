package com.charging.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.payment.entity.PaymentRecord;
import com.charging.payment.mapper.PaymentRecordMapper;
import com.charging.payment.service.PaymentRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentRecordServiceImpl implements PaymentRecordService {

    @Resource
    private PaymentRecordMapper paymentRecordMapper;

    @Override
    public List<PaymentRecord> getList(Long userId, Integer status) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(PaymentRecord::getUserId, userId);
        }
        if (status != null) {
            wrapper.eq(PaymentRecord::getPaymentStatus, status);
        }
        wrapper.orderByDesc(PaymentRecord::getCreatedAt);
        return paymentRecordMapper.selectList(wrapper);
    }

    @Override
    public PaymentRecord getByOrderNumber(String orderNumber) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderNumber, orderNumber);
        return paymentRecordMapper.selectOne(wrapper);
    }

    @Override
    public PaymentRecord getByOrderId(Long orderId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getOrderId, orderId);
        return paymentRecordMapper.selectOne(wrapper);
    }

    @Override
    public List<PaymentRecord> getListByUserId(Long userId) {
        LambdaQueryWrapper<PaymentRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaymentRecord::getUserId, userId);
        return paymentRecordMapper.selectList(wrapper);
    }

    @Override
    public boolean save(PaymentRecord record) {
        record.setPaymentNumber(generatePaymentNumber());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return paymentRecordMapper.insert(record) > 0;
    }

    @Override
    public boolean update(PaymentRecord record) {
        record.setUpdatedAt(LocalDateTime.now());
        return paymentRecordMapper.updateById(record) > 0;
    }

    private String generatePaymentNumber() {
        return "PAY" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + 
               IdUtil.getSnowflakeNextIdStr();
    }
}
