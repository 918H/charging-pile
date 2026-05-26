package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("points_exchange_record")
public class PointsExchangeRecord {

    @TableId(type = IdType.AUTO)
    private Long recordId;

    private Long userId;

    private Long itemId;

    private String itemName;

    private Integer pointsUsed;

    private Integer status;

    private String shippingAddress;

    private String trackingNumber;

    private LocalDateTime exchangeTime;

    private LocalDateTime shippingTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
