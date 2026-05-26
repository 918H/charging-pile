package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChargingProgressDTO {
    private Long orderId;
    private Integer status;
    
    private LocalDateTime startTime;
    private Integer elapsedMinutes;
    
    private BigDecimal powerConsumed;
    private BigDecimal currentPower;
    private BigDecimal avgPower;
    
    private Integer startSoc;
    private Integer currentSoc;
    private Integer targetSoc;
    
    private BigDecimal currentVoltage;
    private BigDecimal currentCurrent;
    private BigDecimal batteryTemp;
    
    private BigDecimal currentAmount;
    private BigDecimal estimatedTotalAmount;
    private LocalDateTime estimatedEndTime;
}
