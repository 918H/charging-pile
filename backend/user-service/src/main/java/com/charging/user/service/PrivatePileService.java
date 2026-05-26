package com.charging.user.service;

import com.charging.user.dto.PileDTO;
import com.charging.user.entity.PileIncome;
import com.charging.user.entity.PrivatePile;
import com.charging.user.entity.PileReservation;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PrivatePileService {
    PrivatePile createPile(Long userId, PileDTO dto);
    PrivatePile updatePile(Long userId, Long pileId, PileDTO dto);
    List<PrivatePile> getUserPiles(Long userId);
    PrivatePile getPileDetail(Long pileId);
    List<PrivatePile> getNearbyPiles(BigDecimal latitude, BigDecimal longitude, Double radius);
    PileReservation reservePile(Long userId, Long pileId, java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);
    void cancelReservation(Long userId, Long reservationId);
    void startCharging(Long userId, Long reservationId);
    void stopCharging(Long userId, Long reservationId, BigDecimal energy);
    List<PileIncome> getIncomeRecords(Long userId, Integer status);
    Map<String, Object> getIncomeStats(Long userId);
}
