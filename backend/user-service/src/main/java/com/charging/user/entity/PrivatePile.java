package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("private_pile")
public class PrivatePile {

    @TableId(type = IdType.AUTO)
    private Long pileId;

    private Long userId;

    private String pileName;

    private String address;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Integer powerType;

    private Integer connectorType;

    private BigDecimal chargingSpeed;

    private String availableTime;

    private BigDecimal pricePerKwh;

    private BigDecimal serviceFee;

    private Integer status;

    private BigDecimal totalEnergy;

    private Integer totalSessions;

    private BigDecimal rating;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
