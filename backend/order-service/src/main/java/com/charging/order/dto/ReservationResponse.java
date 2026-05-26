package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReservationResponse {
    private boolean success;
    private Long reservationId;
    private String message;
    private Long pileId;
    private Integer slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal estimatedFee;
}
