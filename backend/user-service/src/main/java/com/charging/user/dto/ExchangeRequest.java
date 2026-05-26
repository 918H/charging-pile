package com.charging.user.dto;

import lombok.Data;

@Data
public class ExchangeRequest {
    private Long itemId;
    private String shippingAddress;
}
