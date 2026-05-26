package com.charging.order.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponse {
    private Long reviewId;
    private Long userId;
    private Long pileId;
    private String pileName;
    private Integer rating;
    private String content;
    private String images;
    private LocalDateTime createdAt;
    private String userName;
    private String userAvatar;
}
