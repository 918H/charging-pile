package com.charging.order.service;

import com.charging.order.dto.ReservationRequest;
import com.charging.order.dto.ReservationResponse;
import com.charging.order.entity.ChargingReservation;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest request);
    boolean cancelReservation(Long reservationId, String reason);
    ChargingReservation getReservation(Long reservationId);
    List<ChargingReservation> getUserReservations(Long userId, Integer status);
    boolean checkAvailability(Long pileId, Integer slotId, LocalDateTime startTime, LocalDateTime endTime);
}
