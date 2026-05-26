package com.charging.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon")
public class Coupon implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "coupon_id", type = IdType.AUTO)
    private Long couponId;

    @TableField("coupon_name")
    private String couponName;

    @TableField("coupon_type")
    private Integer couponType;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("min_purchase_amount")
    private BigDecimal minPurchaseAmount;

    @TableField("max_discount_amount")
    private BigDecimal maxDiscountAmount;

    @TableField("total_count")
    private Integer totalCount;

    @TableField("issued_count")
    private Integer issuedCount;

    @TableField("valid_days")
    private Integer validDays;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("status")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
