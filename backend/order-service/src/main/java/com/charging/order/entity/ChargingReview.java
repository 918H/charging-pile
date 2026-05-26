package com.charging.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_review")
public class ChargingReview {

    @TableId(type = IdType.AUTO)
    private Long reviewId;

    private Long userId;

    private Long orderId;

    private Long pileId;

    private Integer rating;

    private String content;

    private String images;

    private Boolean hasImages;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
