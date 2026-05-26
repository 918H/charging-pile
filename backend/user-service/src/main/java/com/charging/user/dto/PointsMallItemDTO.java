package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PointsMallItemDTO {
    private Long itemId;
    private String itemName;
    private String itemDesc;
    private String itemImage;
    private Integer type;
    private Integer pointsPrice;
    private BigDecimal cashValue;
    private Integer stock;
    private Integer limitPerUser;
    private String statusText;
    private String couponConfig;
}
