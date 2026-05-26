package com.charging.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewRequest {
    private Long userId;
    private Long orderId;
    private Long pileId;
    private Integer rating;
    private String content;
    private String images;
}
