package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCalculateRequest {

    private Long pileId;

    private Integer durationMinutes;

    private BigDecimal powerConsumed;

    private Long couponId;
}
