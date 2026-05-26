package com.charging.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_session")
public class ChargingSession implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "session_id", type = IdType.AUTO)
    private Long sessionId;

    @TableField("order_id")
    private Long orderId;

    @TableField("user_id")
    private Long userId;

    @TableField("pile_id")
    private Long pileId;

    @TableField("slot_id")
    private Long slotId;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("start_soc")
    private Integer startSoc;

    @TableField("end_soc")
    private Integer endSoc;

    @TableField("start_voltage")
    private BigDecimal startVoltage;

    @TableField("end_voltage")
    private BigDecimal endVoltage;

    @TableField("start_current")
    private BigDecimal startCurrent;

    @TableField("end_current")
    private BigDecimal endCurrent;

    @TableField("max_power")
    private BigDecimal maxPower;

    @TableField("avg_power")
    private BigDecimal avgPower;

    @TableField("power_consumed")
    private BigDecimal powerConsumed;

    @TableField("charging_duration")
    private Integer chargingDuration;

    @TableField("charging_efficiency")
    private BigDecimal chargingEfficiency;

    @TableField("battery_temp")
    private BigDecimal batteryTemp;

    @TableField("status")
    private Integer status;

    @TableField("stop_reason")
    private String stopReason;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
