package com.charging.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.order.dto.*;
import com.charging.order.entity.ChargingOrder;
import com.charging.order.entity.ChargingSession;

import java.util.List;
import java.util.Map;

public interface ChargingOrderService {
    Page<ChargingOrder> getPage(int current, int size, Long userId);
    List<ChargingOrder> getList();
    ChargingOrder getById(Long orderId);
    ChargingOrder getByOrderNumber(String orderNumber);
    boolean save(ChargingOrder order);
    boolean update(ChargingOrder order);
    boolean cancel(Long orderId);
    
    OrderCalculateResponse calculateOrder(OrderCalculateRequest request);
    
    ChargingStartResponse startCharging(ChargingStartRequest request);
    
    boolean stopCharging(Long orderId, String reason);
    
    ChargingProgressDTO getChargingProgress(Long orderId);
    
    BigDecimal getUnitPrice(Long pileId);
    
    Map<String, Object> getOrderStatistics(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    
    List<ChargingOrder> getUserHistoryOrders(Long userId, int status);
    
    BigDecimal calculateOccupationFee(Long pileId, LocalDateTime fullTime, LocalDateTime leaveTime);
}
