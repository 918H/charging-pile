package com.charging.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RefundRequest {
    private Long paymentId;
    private String orderNumber;
    private Long userId;
    private BigDecimal refundAmount;
    private Integer refundType;
    private String refundReason;
    private String images;
}
