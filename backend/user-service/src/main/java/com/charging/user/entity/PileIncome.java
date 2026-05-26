package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pile_income")
public class PileIncome {

    @TableId(type = IdType.AUTO)
    private Long incomeId;

    private Long pileId;

    private Long userId;

    private String orderId;

    private BigDecimal incomeAmount;

    private BigDecimal platformFee;

    private BigDecimal actualIncome;

    private Integer status;

    private LocalDateTime settlementTime;

    private LocalDateTime createdAt;
}
