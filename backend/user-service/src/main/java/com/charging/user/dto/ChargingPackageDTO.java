package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ChargingPackageDTO {
    private Long packageId;
    private String packageName;
    private String packageDesc;
    private Integer type;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private BigDecimal includedEnergy;
    private Integer validDays;
    private Integer timeLimitStart;
    private Integer timeLimitEnd;
    private Integer purchaseLimit;
    private Integer soldCount;
    private String statusText;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
