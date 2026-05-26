package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("charging_package")
public class ChargingPackage {

    @TableId(type = IdType.AUTO)
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

    private Integer status;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
