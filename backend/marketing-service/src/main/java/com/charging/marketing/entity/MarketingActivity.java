package com.charging.marketing.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("marketing_activity")
public class MarketingActivity {
    private Long id;
    private String name;
    private String type;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal budget;
    private BigDecimal usedBudget;
    private String ruleJson;
    private Integer participationLimit;
    private Integer totalParticipants;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
