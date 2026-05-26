package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_membership")
public class UserMembership {

    @TableId(type = IdType.AUTO)
    private Long userMembershipId;

    private Long userId;

    private Long levelId;

    private Integer levelCode;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
