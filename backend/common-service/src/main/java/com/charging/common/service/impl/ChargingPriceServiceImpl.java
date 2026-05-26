package com.charging.common.service.impl;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class ChargingPriceServiceImpl implements com.charging.common.service.ChargingPriceService {

    private static final BigDecimal DEFAULT_UNIT_PRICE = new BigDecimal("1.5");
    private static final BigDecimal PEAK_PRICE = new BigDecimal("2.0");
    private static final BigDecimal OFF_PEAK_PRICE = new BigDecimal("1.0");

    @Override
    public BigDecimal getUnitPrice(Long pileId) {
        return DEFAULT_UNIT_PRICE;
    }

    @Override
    public BigDecimal getPeakPrice(Long pileId) {
        return PEAK_PRICE;
    }

    @Override
    public BigDecimal getOffPeakPrice(Long pileId) {
        return OFF_PEAK_PRICE;
    }

    @Override
    public com.charging.common.service.ChargingPrice getPriceAtTime(Long pileId, LocalDateTime time) {
        com.charging.common.service.ChargingPrice price = new com.charging.common.service.ChargingPrice();
        price.setUnitPrice(DEFAULT_UNIT_PRICE);
        
        if (time != null) {
            int hour = time.getHour();
            if (hour >= 8 && hour <= 22) {
                price.setPriceType("peak");
                price.setCurrentPrice(PEAK_PRICE);
            } else {
                price.setPriceType("off-peak");
                price.setCurrentPrice(OFF_PEAK_PRICE);
            }
        }
        
        return price;
    }

    public BigDecimal calculateTotal(Long pileId, BigDecimal powerConsumed, LocalDateTime startTime, LocalDateTime endTime) {
        if (powerConsumed == null) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal unitPrice = getUnitPrice(pileId);
        return unitPrice.multiply(powerConsumed);
    }
}
