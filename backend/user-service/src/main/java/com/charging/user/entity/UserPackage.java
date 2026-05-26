package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_package")
public class UserPackage {

    @TableId(type = IdType.AUTO)
    private Long userPackageId;

    private Long userId;

    private Long packageId;

    private BigDecimal remainingEnergy;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
