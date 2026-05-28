package com.charging.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("finance_transaction")
public class FinanceTransaction {
    private Long id;
    private String transactionNo;
    private Long userId;
    private String type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private Long relatedOrderId;
    private String remark;
    private LocalDateTime createTime;
}
