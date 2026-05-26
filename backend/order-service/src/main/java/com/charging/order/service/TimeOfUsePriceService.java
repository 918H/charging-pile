package com.charging.order.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface TimeOfUsePriceService {
    BigDecimal getPriceForTime(Long pileId, LocalDateTime time);
    BigDecimal calculateFee(Long pileId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal powerConsumed);
    void updatePrice(Long pileId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal electricityPrice, BigDecimal servicePrice);
}
