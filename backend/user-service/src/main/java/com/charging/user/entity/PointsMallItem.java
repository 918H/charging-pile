package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("points_mall_item")
public class PointsMallItem {

    @TableId(type = IdType.AUTO)
    private Long itemId;

    private String itemName;

    private String itemDesc;

    private String itemImage;

    private Integer type;

    private Integer pointsPrice;

    private BigDecimal cashValue;

    private Integer stock;

    private Integer limitPerUser;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private String couponConfig;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
