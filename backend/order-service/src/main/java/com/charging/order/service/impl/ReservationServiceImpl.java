package com.charging.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.order.dto.ReservationRequest;
import com.charging.order.dto.ReservationResponse;
import com.charging.order.entity.ChargingReservation;
import com.charging.order.mapper.ChargingReservationMapper;
import com.charging.order.service.ReservationService;
import com.charging.order.service.TimeOfUsePriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Resource
    private ChargingReservationMapper chargingReservationMapper;

    @Resource
    private TimeOfUsePriceService timeOfUsePriceService;

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        ReservationResponse response = new ReservationResponse();
        
        try {
            boolean available = checkAvailability(
                request.getPileId(),
                request.getSlotId(),
                request.getStartTime(),
                request.getStartTime().plusMinutes(request.getDurationMinutes())
            );
            
            if (!available) {
                response.setSuccess(false);
                response.setMessage("该时段不可预约");
                return response;
            }
            
            ChargingReservation reservation = new ChargingReservation();
            reservation.setUserId(request.getUserId());
            reservation.setPileId(request.getPileId());
            reservation.setSlotId(request.getSlotId());
            reservation.setReservationTime(LocalDateTime.now());
            reservation.setStartTime(request.getStartTime());
            reservation.setEndTime(request.getStartTime().plusMinutes(request.getDurationMinutes()));
            reservation.setDurationMinutes(request.getDurationMinutes());
            reservation.setStatus(0);
            reservation.setCreatedAt(LocalDateTime.now());
            reservation.setUpdatedAt(LocalDateTime.now());
            
            BigDecimal estimatedFee = timeOfUsePriceService.calculateFee(
                request.getPileId(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                BigDecimal.valueOf(request.getDurationMinutes()).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP)
            );
            reservation.setEstimatedFee(estimatedFee);
            
            chargingReservationMapper.insert(reservation);
            
            response.setSuccess(true);
            response.setReservationId(reservation.getReservationId());
            response.setMessage("预约成功");
            response.setPileId(request.getPileId());
            response.setSlotId(request.getSlotId());
            response.setStartTime(reservation.getStartTime());
            response.setEndTime(reservation.getEndTime());
            response.setEstimatedFee(estimatedFee);
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("预约失败：" + e.getMessage());
        }
        
        return response;
    }

    @Override
    public boolean cancelReservation(Long reservationId, String reason) {
        ChargingReservation reservation = chargingReservationMapper.selectById(reservationId);
        if (reservation == null || reservation.getStatus() != 0) {
            return false;
        }
        
        if (LocalDateTime.now().isAfter(reservation.getStartTime().minusMinutes(30))) {
            return false;
        }
        
        reservation.setStatus(1);
        reservation.setCancelReason(reason);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        return chargingReservationMapper.updateById(reservation) > 0;
    }

    @Override
    public ChargingReservation getReservation(Long reservationId) {
        return chargingReservationMapper.selectById(reservationId);
    }

    @Override
    public List<ChargingReservation> getUserReservations(Long userId, Integer status) {
        LambdaQueryWrapper<ChargingReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReservation::getUserId, userId);
        if (status != null) {
            wrapper.eq(ChargingReservation::getStatus, status);
        }
        wrapper.orderByDesc(ChargingReservation::getCreatedAt);
        return chargingReservationMapper.selectList(wrapper);
    }

    @Override
    public boolean checkAvailability(Long pileId, Integer slotId, LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ChargingReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReservation::getPileId, pileId)
               .eq(ChargingReservation::getSlotId, slotId)
               .eq(ChargingReservation::getStatus, 0)
               .and(w -> w.and(
                   ww -> ww.le(ChargingReservation::getStartTime, endTime)
                          .ge(ChargingReservation::getEndTime, startTime)
               ));
        
        long count = chargingReservationMapper.selectCount(wrapper);
        return count == 0;
    }
}
