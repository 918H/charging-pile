package com.charging.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.PileDTO;
import com.charging.user.entity.PileIncome;
import com.charging.user.entity.PileReservation;
import com.charging.user.entity.PrivatePile;
import com.charging.user.mapper.PileIncomeMapper;
import com.charging.user.mapper.PileReservationMapper;
import com.charging.user.mapper.PrivatePileMapper;
import com.charging.user.service.PrivatePileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class PrivatePileServiceImpl implements PrivatePileService {

    @Resource
    private PrivatePileMapper privatePileMapper;

    @Resource
    private PileReservationMapper pileReservationMapper;

    @Resource
    private PileIncomeMapper pileIncomeMapper;

    private static final BigDecimal PLATFORM_FEE_RATE = new BigDecimal("0.1");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrivatePile createPile(Long userId, PileDTO dto) {
        PrivatePile pile = new PrivatePile();
        pile.setUserId(userId);
        pile.setPileName(dto.getPileName());
        pile.setAddress(dto.getAddress());
        pile.setLatitude(dto.getLatitude());
        pile.setLongitude(dto.getLongitude());
        pile.setPowerType(dto.getPowerType());
        pile.setConnectorType(dto.getConnectorType());
        pile.setChargingSpeed(dto.getChargingSpeed());
        pile.setAvailableTime(dto.getAvailableTime());
        pile.setPricePerKwh(dto.getPricePerKwh());
        pile.setServiceFee(dto.getServiceFee());
        pile.setStatus(dto.getStatus() != null ? dto.getStatus() : 0);
        pile.setTotalEnergy(BigDecimal.ZERO);
        pile.setTotalSessions(0);
        pile.setRating(new BigDecimal("0.00"));
        pile.setCreatedAt(LocalDateTime.now());
        pile.setUpdatedAt(LocalDateTime.now());

        privatePileMapper.insert(pile);
        log.info("用户 {} 创建私人充电桩 {}", userId, pile.getPileId());
        return pile;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PrivatePile updatePile(Long userId, Long pileId, PileDTO dto) {
        PrivatePile pile = privatePileMapper.selectById(pileId);
        if (pile == null || !pile.getUserId().equals(userId)) {
            throw new RuntimeException("充电桩不存在或无权限");
        }

        if (dto.getPileName() != null) pile.setPileName(dto.getPileName());
        if (dto.getAddress() != null) pile.setAddress(dto.getAddress());
        if (dto.getLatitude() != null) pile.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) pile.setLongitude(dto.getLongitude());
        if (dto.getPowerType() != null) pile.setPowerType(dto.getPowerType());
        if (dto.getConnectorType() != null) pile.setConnectorType(dto.getConnectorType());
        if (dto.getChargingSpeed() != null) pile.setChargingSpeed(dto.getChargingSpeed());
        if (dto.getAvailableTime() != null) pile.setAvailableTime(dto.getAvailableTime());
        if (dto.getPricePerKwh() != null) pile.setPricePerKwh(dto.getPricePerKwh());
        if (dto.getServiceFee() != null) pile.setServiceFee(dto.getServiceFee());
        if (dto.getStatus() != null) pile.setStatus(dto.getStatus());

        pile.setUpdatedAt(LocalDateTime.now());
        privatePileMapper.updateById(pile);

        log.info("用户 {} 更新充电桩 {}", userId, pileId);
        return pile;
    }

    @Override
    public List<PrivatePile> getUserPiles(Long userId) {
        LambdaQueryWrapper<PrivatePile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivatePile::getUserId, userId)
               .orderByDesc(PrivatePile::getCreatedAt);
        return privatePileMapper.selectList(wrapper);
    }

    @Override
    public PrivatePile getPileDetail(Long pileId) {
        return privatePileMapper.selectById(pileId);
    }

    @Override
    public List<PrivatePile> getNearbyPiles(BigDecimal latitude, BigDecimal longitude, Double radius) {
        LambdaQueryWrapper<PrivatePile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PrivatePile::getStatus, 1);
        
        List<PrivatePile> allPiles = privatePileMapper.selectList(wrapper);
        double radiusDegrees = radius / 111.0;
        
        List<PrivatePile> nearbyPiles = new ArrayList<>();
        for (PrivatePile pile : allPiles) {
            if (pile.getLatitude() != null && pile.getLongitude() != null) {
                double distance = calculateDistance(
                    latitude.doubleValue(), longitude.doubleValue(),
                    pile.getLatitude().doubleValue(), pile.getLongitude().doubleValue()
                );
                if (distance <= radius) {
                    nearbyPiles.add(pile);
                }
            }
        }
        
        return nearbyPiles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PileReservation reservePile(Long userId, Long pileId, LocalDateTime startTime, LocalDateTime endTime) {
        PrivatePile pile = privatePileMapper.selectById(pileId);
        if (pile == null || pile.getStatus() != 1) {
            throw new RuntimeException("充电桩不可用");
        }

        LambdaQueryWrapper<PileReservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PileReservation::getPileId, pileId)
               .in(PileReservation::getStatus, 0, 1)
               .and(w -> w
                   .between(PileReservation::getStartTime, startTime, endTime)
                   .or(o -> o.between(PileReservation::getEndTime, startTime, endTime))
                   .or(o -> o.le(PileReservation::getStartTime, startTime)
                           .ge(PileReservation::getEndTime, endTime))
               );
        
        Long count = pileReservationMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("该时间段已被预约");
        }

        PileReservation reservation = new PileReservation();
        reservation.setPileId(pileId);
        reservation.setUserId(userId);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);
        reservation.setStatus(0);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        
        pileReservationMapper.insert(reservation);
        log.info("用户 {} 预约充电桩 {}，时间 {} - {}", userId, pileId, startTime, endTime);
        
        return reservation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long userId, Long reservationId) {
        PileReservation reservation = pileReservationMapper.selectById(reservationId);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new RuntimeException("预约不存在或无权限");
        }

        if (reservation.getStatus() != 0) {
            throw new RuntimeException("只能取消待使用的预约");
        }

        if (LocalDateTime.now().isAfter(reservation.getStartTime().minusHours(2))) {
            throw new RuntimeException("开始前 2 小时内不能取消");
        }

        reservation.setStatus(3);
        reservation.setUpdatedAt(LocalDateTime.now());
        pileReservationMapper.updateById(reservation);
        
        log.info("用户 {} 取消预约 {}", userId, reservationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startCharging(Long userId, Long reservationId) {
        PileReservation reservation = pileReservationMapper.selectById(reservationId);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new RuntimeException("预约不存在或无权限");
        }

        if (reservation.getStatus() != 0) {
            throw new RuntimeException("预约状态不正确");
        }

        reservation.setStatus(1);
        reservation.setActualStartTime(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());
        pileReservationMapper.updateById(reservation);
        
        log.info("用户 {} 开始充电，预约 {}", userId, reservationId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void stopCharging(Long userId, Long reservationId, BigDecimal energy) {
        PileReservation reservation = pileReservationMapper.selectById(reservationId);
        if (reservation == null || !reservation.getUserId().equals(userId)) {
            throw new RuntimeException("预约不存在或无权限");
        }

        if (reservation.getStatus() != 1) {
            throw new RuntimeException("充电未开始");
        }

        PrivatePile pile = privatePileMapper.selectById(reservation.getPileId());
        BigDecimal totalPrice = energy.multiply(pile.getPricePerKwh().add(pile.getServiceFee()));

        reservation.setStatus(2);
        reservation.setActualEndTime(LocalDateTime.now());
        reservation.setActualEnergy(energy);
        reservation.setTotalFee(totalPrice);
        reservation.setUpdatedAt(LocalDateTime.now());
        pileReservationMapper.updateById(reservation);

        pile.setTotalEnergy(pile.getTotalEnergy().add(energy));
        pile.setTotalSessions(pile.getTotalSessions() + 1);
        privatePileMapper.updateById(pile);

        BigDecimal platformFee = totalPrice.multiply(PLATFORM_FEE_RATE);
        BigDecimal actualIncome = totalPrice.subtract(platformFee);

        PileIncome income = new PileIncome();
        income.setPileId(reservation.getPileId());
        income.setUserId(pile.getUserId());
        income.setOrderId(IdUtil.getSnowflakeNextIdStr());
        income.setIncomeAmount(totalPrice);
        income.setPlatformFee(platformFee);
        income.setActualIncome(actualIncome);
        income.setStatus(0);
        income.setCreatedAt(LocalDateTime.now());
        pileIncomeMapper.insert(income);

        log.info("用户 {} 结束充电，用电量 {}kWh，费用 {} 元", userId, energy, totalPrice);
    }

    @Override
    public List<PileIncome> getIncomeRecords(Long userId, Integer status) {
        LambdaQueryWrapper<PileIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PileIncome::getUserId, userId);
        if (status != null) {
            wrapper.eq(PileIncome::getStatus, status);
        }
        wrapper.orderByDesc(PileIncome::getCreatedAt);
        return pileIncomeMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getIncomeStats(Long userId) {
        LambdaQueryWrapper<PileIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PileIncome::getUserId, userId);
        List<PileIncome> incomes = pileIncomeMapper.selectList(wrapper);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal settledIncome = BigDecimal.ZERO;
        BigDecimal pendingIncome = BigDecimal.ZERO;

        for (PileIncome income : incomes) {
            totalIncome = totalIncome.add(income.getActualIncome());
            if (income.getStatus() >= 1) {
                settledIncome = settledIncome.add(income.getActualIncome());
            } else {
                pendingIncome = pendingIncome.add(income.getActualIncome());
            }
        }

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("settledIncome", settledIncome);
        stats.put("pendingIncome", pendingIncome);
        stats.put("totalCount", incomes.size());

        return stats;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
