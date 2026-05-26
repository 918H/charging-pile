package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MembershipDTO {
    private Long levelId;
    private String levelName;
    private Integer levelCode;
    private BigDecimal discountRate;
    private BigDecimal upgradeThreshold;
    private Integer validDays;
    private String benefits;
}
