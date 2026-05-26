package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PileDTO {
    private String pileName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer powerType;
    private Integer connectorType;
    private BigDecimal chargingSpeed;
    private String availableTime;
    private BigDecimal pricePerKwh;
    private BigDecimal serviceFee;
    private Integer status;
}
