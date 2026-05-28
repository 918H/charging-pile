package com.charging.statistics.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("statistics_daily")
public class StatisticsDaily {
    
    private Long id;
    private LocalDate statDate;
    private Integer totalOrders;
    private BigDecimal totalAmount;
    private BigDecimal totalKwh;
    private Integer activeUsers;
    private Integer newUsers;
    private Integer avgChargingTime;
    private Integer peakOrders;
    private Integer offPeakOrders;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
