package com.charging.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRequest {
    private Long userId;
    private Long pileId;
    private Integer slotId;
    private LocalDateTime startTime;
    private Integer durationMinutes;
}
