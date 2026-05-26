package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChargingStartRequest {
    private Long userId;
    private Long pileId;
    private Long slotId;
    private Integer chargingMode;
    private Integer targetSoc;
    private BigDecimal maxAmount;
    private String scanCode;
}
