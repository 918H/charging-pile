package com.charging.payment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_refund")
public class PaymentRefund {

    @TableId(type = IdType.AUTO)
    private Long refundId;

    private String refundNumber;

    private Long paymentId;

    private String orderNumber;

    private Long userId;

    private BigDecimal refundAmount;

    private Integer refundType;

    private String refundReason;

    private String images;

    private Integer status;

    private String rejectReason;

    private Long auditorId;

    private LocalDateTime auditedAt;

    private LocalDateTime payedAt;

    private String paymentTransactionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
