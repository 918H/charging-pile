package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pile_withdrawal")
public class PileWithdrawal {

    @TableId(type = IdType.AUTO)
    private Long withdrawalId;

    private Long userId;

    private BigDecimal amount;

    private BigDecimal fee;

    private BigDecimal actualAmount;

    private Integer status;

    private String alipayAccount;

    private String alipayName;

    private String bankAccount;

    private String bankName;

    private String bankCard;

    private Integer withdrawMethod;

    private String transactionId;

    private LocalDateTime processTime;

    private String rejectReason;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
