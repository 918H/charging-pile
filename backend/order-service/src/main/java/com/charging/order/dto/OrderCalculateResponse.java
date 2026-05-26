package com.charging.order.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderCalculateResponse {

    private BigDecimal originalAmount;

    private BigDecimal discountAmount;

    private BigDecimal finalAmount;

    private String message;

    public void calculate(BigDecimal unitPrice, BigDecimal powerConsumed, BigDecimal discount) {
        this.originalAmount = unitPrice.multiply(powerConsumed);
        this.discountAmount = discount;
        this.finalAmount = this.originalAmount.subtract(discount).max(BigDecimal.ZERO);
    }
}
