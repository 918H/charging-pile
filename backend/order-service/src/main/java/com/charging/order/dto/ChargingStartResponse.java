package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargingStartResponse {
    private Long orderId;
    private String orderNumber;
    private Long pileId;
    private Long slotId;
    private LocalDateTime startTime;
    private BigDecimal currentPrice;
    private boolean success;
    private String message;
}
