package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pile_reservation")
public class PileReservation {

    @TableId(type = IdType.AUTO)
    private Long reservationId;

    private Long pileId;

    private Long userId;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime actualStartTime;

    private LocalDateTime actualEndTime;

    private BigDecimal actualEnergy;

    private BigDecimal totalFee;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
