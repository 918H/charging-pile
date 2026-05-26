package com.charging.common.service;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargingPrice {
    private BigDecimal unitPrice;
    
    private BigDecimal currentPrice;
    
    private String priceType;
}
