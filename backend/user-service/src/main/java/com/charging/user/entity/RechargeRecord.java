package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recharge_record")
public class RechargeRecord {

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long cardId;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal bonusAmount;

    private Integer paymentMethod;

    private String transactionId;

    private Integer status;

    private LocalDateTime payTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
