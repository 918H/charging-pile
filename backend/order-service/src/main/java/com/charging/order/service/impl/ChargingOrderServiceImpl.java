package com.charging.order.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.order.dto.*;
import com.charging.order.entity.ChargingOrder;
import com.charging.order.entity.ChargingSession;
import com.charging.order.mapper.ChargingOrderMapper;
import com.charging.order.mapper.ChargingSessionMapper;
import com.charging.order.service.ChargingOrderService;
import com.charging.order.client.UserClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChargingOrderServiceImpl implements ChargingOrderService {

    @Resource
    private ChargingOrderMapper chargingOrderMapper;

    @Resource
    private ChargingSessionMapper chargingSessionMapper;

    @Resource(required = false)
    private UserClient userClient;

    private static final BigDecimal DEFAULT_ELECTRICITY_PRICE = new BigDecimal("1.0");
    private static final BigDecimal DEFAULT_SERVICE_PRICE = new BigDecimal("0.5");
    private static final Map<Long, ChargingProgressDTO> CHARGING_PROGRESS_CACHE = new ConcurrentHashMap<>();

    @Override
    public Page<ChargingOrder> getPage(int current, int size, Long userId) {
        Page<ChargingOrder> page = new Page<>(current, size);
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            wrapper.eq(ChargingOrder::getUserId, userId);
        }
        wrapper.orderByDesc(ChargingOrder::getCreatedAt);
        return chargingOrderMapper.selectPage(page, wrapper);
    }

    @Override
    public List<ChargingOrder> getList() {
        return chargingOrderMapper.selectList(null);
    }

    @Override
    public ChargingOrder getById(Long orderId) {
        return chargingOrderMapper.selectById(orderId);
    }

    @Override
    public ChargingOrder getByOrderNumber(String orderNumber) {
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingOrder::getOrderNumber, orderNumber);
        return chargingOrderMapper.selectOne(wrapper);
    }

    @Override
    public boolean save(ChargingOrder order) {
        order.setOrderNumber(generateOrderNumber());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return chargingOrderMapper.insert(order) > 0;
    }

    @Override
    public boolean update(ChargingOrder order) {
        order.setUpdatedAt(LocalDateTime.now());
        return chargingOrderMapper.updateById(order) > 0;
    }

    @Override
    public boolean cancel(Long orderId) {
        ChargingOrder order = chargingOrderMapper.selectById(orderId);
        if (order == null) {
            return false;
        }
        order.setStatus(3);
        order.setUpdatedAt(LocalDateTime.now());
        return chargingOrderMapper.updateById(order) > 0;
    }

    @Override
    public ChargingStartResponse startCharging(ChargingStartRequest request) {
        ChargingStartResponse response = new ChargingStartResponse();
        
        try {
            ChargingOrder order = new ChargingOrder();
            order.setUserId(request.getUserId());
            order.setPileId(request.getPileId());
            order.setSlotId(request.getSlotId());
            order.setChargingMode(request.getChargingMode() != null ? request.getChargingMode() : 1);
            order.setStatus(1);
            order.setPaymentStatus(0);
            order.setStartSoc(request.getTargetSoc());
            order.setActualStart(LocalDateTime.now());
            
            BigDecimal unitPrice = getUnitPrice(request.getPileId());
            order.setUnitPrice(unitPrice);
            
            chargingOrderMapper.insert(order);
            
            ChargingSession session = new ChargingSession();
            session.setOrderId(order.getOrderId());
            session.setUserId(request.getUserId());
            session.setPileId(request.getPileId());
            session.setSlotId(request.getSlotId());
            session.setStartTime(LocalDateTime.now());
            session.setStartSoc(request.getTargetSoc());
            session.setStatus(0);
            
            chargingSessionMapper.insert(session);
            
            ChargingProgressDTO progress = new ChargingProgressDTO();
            progress.setOrderId(order.getOrderId());
            progress.setStatus(1);
            progress.setStartTime(LocalDateTime.now());
            progress.setElapsedMinutes(0);
            progress.setPowerConsumed(BigDecimal.ZERO);
            progress.setCurrentSoc(request.getTargetSoc() != null ? request.getTargetSoc() : 0);
            progress.setTargetSoc(request.getTargetSoc() != null ? request.getTargetSoc() : 100);
            progress.setCurrentAmount(BigDecimal.ZERO);
            progress.setCurrentPrice(unitPrice);
            
            CHARGING_PROGRESS_CACHE.put(order.getOrderId(), progress);
            
            response.setSuccess(true);
            response.setOrderId(order.getOrderId());
            response.setOrderNumber(order.getOrderNumber());
            response.setPileId(request.getPileId());
            response.setSlotId(request.getSlotId());
            response.setStartTime(LocalDateTime.now());
            response.setCurrentPrice(unitPrice);
            response.setMessage("充电已开始");
            
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("启动充电失败：" + e.getMessage());
        }
        
        return response;
    }

    @Override
    public boolean stopCharging(Long orderId, String reason) {
        ChargingOrder order = chargingOrderMapper.selectById(orderId);
        if (order == null || order.getStatus() != 1) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        order.setActualEnd(now);
        order.setStatus(2);
        order.setStopReason(reason);
        
        if (order.getActualStart() != null) {
            int duration = (int) Duration.between(order.getActualStart(), now).toMinutes();
            order.setDurationMinutes(duration);
        }
        
        if (order.getPowerConsumed() == null) {
            ChargingProgressDTO progress = CHARGING_PROGRESS_CACHE.get(orderId);
            if (progress != null && progress.getPowerConsumed() != null) {
                order.setPowerConsumed(progress.getPowerConsumed());
            } else {
                order.setPowerConsumed(BigDecimal.valueOf(30));
            }
        }
        
        BigDecimal electricityFee = order.getElectricityFee() != null ? 
            order.getElectricityFee() : order.getUnitPrice().multiply(order.getPowerConsumed());
        order.setElectricityFee(electricityFee);
        
        BigDecimal serviceFee = order.getServiceFee() != null ? 
            order.getServiceFee() : DEFAULT_SERVICE_PRICE.multiply(order.getPowerConsumed());
        order.setServiceFee(serviceFee);
        
        BigDecimal totalAmount = electricityFee.add(serviceFee);
        order.setTotalAmount(totalAmount);
        
        BigDecimal membershipDiscount = BigDecimal.ZERO;
        if (order.getUserId() != null) {
            membershipDiscount = calculateMembershipDiscount(order.getUserId(), totalAmount);
        }
        
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (order.getCouponId() != null) {
            couponDiscount = calculateDiscount(order.getCouponId(), totalAmount);
        }
        
        BigDecimal totalDiscount = membershipDiscount.add(couponDiscount);
        
        if (totalDiscount.compareTo(BigDecimal.ZERO) > 0) {
            order.setDiscountAmount(totalDiscount);
        }
        
        order.setFinalAmount(totalAmount.subtract(totalDiscount).max(BigDecimal.ZERO));
        order.setPaymentStatus(0);
        order.setUpdatedAt(now);
        
        int result = chargingOrderMapper.updateById(order);
        
        if (result > 0) {
            ChargingSession session = chargingSessionMapper.selectOne(
                new LambdaQueryWrapper<ChargingSession>().eq(ChargingSession::getOrderId, orderId)
            );
            if (session != null) {
                session.setEndTime(now);
                session.setEndSoc(order.getEndSoc());
                session.setPowerConsumed(order.getPowerConsumed());
                if (session.getStartTime() != null) {
                    session.setChargingDuration((int) Duration.between(session.getStartTime(), now).toMinutes());
                }
                session.setStatus(1);
                chargingSessionMapper.updateById(session);
            }
            
            CHARGING_PROGRESS_CACHE.remove(orderId);
            
            if (order.getUserId() != null && order.getFinalAmount() != null && order.getFinalAmount().compareTo(BigDecimal.ZERO) > 0) {
                rewardOrderPoints(order.getUserId(), order.getFinalAmount(), order.getOrderNumber());
            }
        }
        
        return result > 0;
    }

    @Override
    public ChargingProgressDTO getChargingProgress(Long orderId) {
        ChargingProgressDTO progress = CHARGING_PROGRESS_CACHE.get(orderId);
        
        if (progress == null) {
            ChargingOrder order = chargingOrderMapper.selectById(orderId);
            if (order != null && order.getStatus() == 2) {
                progress = new ChargingProgressDTO();
                progress.setOrderId(orderId);
                progress.setStatus(2);
                progress.setStartTime(order.getActualStart());
                progress.setPowerConsumed(order.getPowerConsumed());
                progress.setCurrentAmount(order.getFinalAmount());
                progress.setElapsedMinutes(order.getDurationMinutes());
                return progress;
            }
            return null;
        }
        
        progress.setElapsedMinutes((int) Duration.between(progress.getStartTime(), LocalDateTime.now()).toMinutes());
        
        if (progress.getPowerConsumed() == null || progress.getPowerConsumed().compareTo(BigDecimal.ZERO) == 0) {
            BigDecimal elapsedHours = BigDecimal.valueOf(progress.getElapsedMinutes()).divide(BigDecimal.valueOf(60), 2, BigDecimal.ROUND_HALF_UP);
            progress.setPowerConsumed(elapsedHours.multiply(BigDecimal.valueOf(30)));
        }
        
        if (progress.getCurrentSoc() != null && progress.getTargetSoc() != null) {
            int socIncrease = progress.getElapsedMinutes() / 2;
            progress.setCurrentSoc(Math.min(progress.getCurrentSoc() + socIncrease, progress.getTargetSoc()));
        }
        
        progress.setCurrentVoltage(BigDecimal.valueOf(400));
        progress.setCurrentCurrent(BigDecimal.valueOf(100));
        progress.setCurrentPower(BigDecimal.valueOf(40));
        progress.setAvgPower(BigDecimal.valueOf(35));
        progress.setBatteryTemp(BigDecimal.valueOf(25));
        
        if (progress.getCurrentPrice() != null && progress.getPowerConsumed() != null) {
            progress.setCurrentAmount(progress.getCurrentPrice().multiply(progress.getPowerConsumed()));
        }
        
        int remainingMinutes = (progress.getTargetSoc() - progress.getCurrentSoc()) * 2;
        progress.setEstimatedEndTime(LocalDateTime.now().plusMinutes(remainingMinutes));
        progress.setEstimatedTotalAmount(progress.getCurrentAmount());
        
        return progress;
    }

    @Override
    public OrderCalculateResponse calculateOrder(OrderCalculateRequest request) {
        OrderCalculateResponse response = new OrderCalculateResponse();
        
        BigDecimal unitPrice = getUnitPrice(request.getPileId());
        BigDecimal powerConsumed = request.getPowerConsumed() != null 
            ? request.getPowerConsumed() 
            : calculatePowerConsumed(request.getDurationMinutes(), request.getPileId());
        
        BigDecimal electricityFee = DEFAULT_ELECTRICITY_PRICE.multiply(powerConsumed);
        BigDecimal serviceFee = DEFAULT_SERVICE_PRICE.multiply(powerConsumed);
        BigDecimal originalAmount = electricityFee.add(serviceFee);
        
        BigDecimal discountAmount = BigDecimal.ZERO;
        if (request.getCouponId() != null) {
            discountAmount = calculateDiscount(request.getCouponId(), originalAmount);
        }
        
        BigDecimal finalAmount = originalAmount.subtract(discountAmount).max(BigDecimal.ZERO);
        
        response.calculate(unitPrice, powerConsumed, discountAmount);
        
        if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
            response.setMessage("已使用优惠券，优惠" + discountAmount + "元");
        } else {
            response.setMessage("订单计算成功");
        }
        
        return response;
    }

    @Override
    public BigDecimal getUnitPrice(Long pileId) {
        return DEFAULT_ELECTRICITY_PRICE.add(DEFAULT_SERVICE_PRICE);
    }

    @Override
    public Map<String, Object> getOrderStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingOrder::getUserId, userId);
        if (startDate != null) {
            wrapper.ge(ChargingOrder::getCreatedAt, startDate);
        }
        if (endDate != null) {
            wrapper.le(ChargingOrder::getCreatedAt, endDate);
        }
        
        List<ChargingOrder> orders = chargingOrderMapper.selectList(wrapper);
        
        int totalOrders = orders.size();
        int completedOrders = (int) orders.stream().filter(o -> o.getStatus() == 2).count();
        BigDecimal totalAmount = orders.stream()
            .filter(o -> o.getFinalAmount() != null)
            .reduce(BigDecimal.ZERO, (sum, o) -> sum.add(o.getFinalAmount()), BigDecimal::add);
        BigDecimal totalPower = orders.stream()
            .filter(o -> o.getPowerConsumed() != null)
            .reduce(BigDecimal.ZERO, (sum, o) -> sum.add(o.getPowerConsumed()), BigDecimal::add);
        
        stats.put("totalOrders", totalOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("totalAmount", totalAmount);
        stats.put("totalPower", totalPower);
        stats.put("averageOrderAmount", totalOrders > 0 ? totalAmount.divide(new BigDecimal(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
        
        return stats;
    }

    @Override
    public List<ChargingOrder> getUserHistoryOrders(Long userId, int status) {
        LambdaQueryWrapper<ChargingOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingOrder::getUserId, userId);
        if (status >= 0) {
            wrapper.eq(ChargingOrder::getStatus, status);
        }
        wrapper.orderByDesc(ChargingOrder::getCreatedAt);
        return chargingOrderMapper.selectList(wrapper);
    }

    @Override
    public BigDecimal calculateOccupationFee(Long pileId, LocalDateTime fullTime, LocalDateTime leaveTime) {
        if (fullTime == null || leaveTime == null) {
            return BigDecimal.ZERO;
        }
        
        if (leaveTime.isBefore(fullTime)) {
            return BigDecimal.ZERO;
        }
        
        long occupationMinutes = Duration.between(fullTime, leaveTime).toMinutes();
        
        if (occupationMinutes <= 30) {
            return BigDecimal.ZERO;
        }
        
        long chargedMinutes = occupationMinutes - 30;
        BigDecimal fee = BigDecimal.valueOf(chargedMinutes).multiply(BigDecimal.valueOf(0.5));
        
        return fee.min(BigDecimal.valueOf(100));
    }

    private BigDecimal calculateDiscount(Long couponId, BigDecimal originalAmount) {
        if (couponId == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateMembershipDiscount(Long userId, BigDecimal amount) {
        if (userClient == null) {
            return BigDecimal.ZERO;
        }
        try {
            return userClient.calculateMembershipDiscount(userId, amount);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private void rewardOrderPoints(Long userId, BigDecimal amount, String orderNumber) {
        if (userClient == null) {
            return;
        }
        try {
            Integer points = amount.setScale(0, BigDecimal.ROUND_DOWN).intValue();
            userClient.addPoints(userId, points, "充电奖励", orderNumber);
        } catch (Exception e) {
            log.warn("奖励积分失败：{}", e.getMessage());
        }
    }

    private BigDecimal calculatePowerConsumed(Integer durationMinutes, Long pileId) {
        if (durationMinutes == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal powerRating = new BigDecimal("60");
        return powerRating.multiply(new BigDecimal(durationMinutes)).divide(new BigDecimal("60"), 2, BigDecimal.ROUND_HALF_UP);
    }

    private String generateOrderNumber() {
        return "ORD" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + 
               IdUtil.getSnowflakeNextIdStr();
    }
}
