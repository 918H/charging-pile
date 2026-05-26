package com.charging.order.service;

import java.math.BigDecimal;

public interface ChargingProgressService {
    void startProgressTracking(Long orderId);
    void stopProgressTracking(Long orderId);
    void updateProgress(Long orderId, int currentSoc, BigDecimal powerConsumed, BigDecimal voltage, BigDecimal current, BigDecimal power);
}
