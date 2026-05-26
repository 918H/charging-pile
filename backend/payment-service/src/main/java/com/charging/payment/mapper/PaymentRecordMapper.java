package com.charging.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.charging.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {
    
    @Select("SELECT * FROM payment_record WHERE payment_number = #{paymentNumber}")
    PaymentRecord selectByPaymentNumber(String paymentNumber);
    
    @Select("SELECT * FROM payment_record WHERE third_party_id = #{outTradeNo}")
    PaymentRecord selectByOutTradeNo(String outTradeNo);
}
