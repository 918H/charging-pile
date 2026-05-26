package com.charging.user.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class MembershipDiscountDTO {
    private Long userId;
    private Integer levelCode;
    private String levelName;
    private BigDecimal discountRate;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private BigDecimal finalAmount;
}
