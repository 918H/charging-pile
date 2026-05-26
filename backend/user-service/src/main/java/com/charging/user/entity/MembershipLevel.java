package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("membership_level")
public class MembershipLevel {

    @TableId(type = IdType.AUTO)
    private Long levelId;

    private String levelName;

    private Integer levelCode;

    private BigDecimal discountRate;

    private BigDecimal upgradeThreshold;

    private Integer validDays;

    private String benefits;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
