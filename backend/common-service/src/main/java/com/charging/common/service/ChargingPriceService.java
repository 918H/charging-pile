package com.charging.common.service;

import java.math.BigDecimal;

public interface ChargingPriceService {
    BigDecimal getUnitPrice(Long pileId);
    
    BigDecimal getPeakPrice(Long pileId);
    
    BigDecimal getOffPeakPrice(Long pileId);
    
    ChargingPrice getPriceAtTime(Long pileId, java.time.LocalDateTime time);
}
