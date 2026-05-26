package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("user_recharge_card")
public class UserRechargeCard {

    @TableId(type = IdType.AUTO)
    private Long cardId;

    private Long userId;

    private String cardNumber;

    private BigDecimal balance;

    private BigDecimal freezeAmount;

    private Integer status;

    private LocalDateTime expireTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
