package com.charging.finance.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@TableName("invoice")
public class Invoice {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String invoiceNo;
    private BigDecimal amount;
    private String type;
    private String title;
    private String taxId;
    private String status;
    private LocalDateTime createTime;
}
