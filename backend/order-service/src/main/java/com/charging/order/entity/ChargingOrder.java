package com.charging.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_order")
public class ChargingOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    @TableField("order_number")
    private String orderNumber;

    @TableField("user_id")
    private Long userId;

    @TableField("pile_id")
    private Long pileId;

    @TableField("slot_id")
    private Long slotId;

    @TableField("charging_mode")
    private Integer chargingMode;

    @TableField("status")
    private Integer status;

    @TableField("reserve_start")
    private LocalDateTime reserveStart;

    @TableField("reserve_end")
    private LocalDateTime reserveEnd;

    @TableField("actual_start")
    private LocalDateTime actualStart;

    @TableField("actual_end")
    private LocalDateTime actualEnd;

    @TableField("duration_minutes")
    private Integer durationMinutes;

    @TableField("power_consumed")
    private BigDecimal powerConsumed;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("electricity_fee")
    private BigDecimal electricityFee;

    @TableField("service_fee")
    private BigDecimal serviceFee;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("final_amount")
    private BigDecimal finalAmount;

    @TableField("payment_status")
    private Integer paymentStatus;

    @TableField("start_soc")
    private Integer startSoc;

    @TableField("end_soc")
    private Integer endSoc;

    @TableField("stop_reason")
    private String stopReason;

    @TableField("remark")
    private String remark;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
