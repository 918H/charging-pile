package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RechargeRequest {
    private BigDecimal amount;
    private Integer paymentMethod;
    private String transactionId;
}
