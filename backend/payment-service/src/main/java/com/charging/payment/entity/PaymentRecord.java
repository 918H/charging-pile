package com.charging.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("payment_record")
public class PaymentRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long paymentId;

    @TableField("payment_number")
    private String paymentNumber;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("amount")
    private BigDecimal amount;

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("third_party_id")
    private String thirdPartyId;

    @TableField("status")
    private Integer status;

    @TableField("paid_at")
    private LocalDateTime paidAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
