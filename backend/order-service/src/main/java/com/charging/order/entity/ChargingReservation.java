package com.charging.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_reservation")
public class ChargingReservation {

    @TableId(type = IdType.AUTO)
    private Long reservationId;

    private Long userId;

    private Long pileId;

    private Integer slotId;

    private LocalDateTime reservationTime;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer durationMinutes;

    private BigDecimal estimatedFee;

    private Integer status;

    private String cancelReason;

    private LocalDateTime cancelledAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
