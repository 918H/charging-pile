package com.charging.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("user_coupon")
public class UserCoupon implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_coupon_id", type = IdType.AUTO)
    private Long userCouponId;

    @TableField("user_id")
    private Long userId;

    @TableField("coupon_id")
    private Long couponId;

    @TableField("coupon_code")
    private String couponCode;

    @TableField("status")
    private Integer status;

    @TableField("order_id")
    private Long orderId;

    @TableField("used_time")
    private LocalDateTime usedTime;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
