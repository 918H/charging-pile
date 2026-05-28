package com.charging.statistics.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@TableName("statistics_daily")
public class StatisticsDaily {
    @TableId(type = IdType.AUTO) private Long id;
    private LocalDate statDate;
    private Integer newUserCount;
    private Integer activeUserCount;
    private Integer orderCount;
    private BigDecimal orderAmount;
    private BigDecimal revenue;
    private Integer chargingCount;
    private BigDecimal chargingDuration;
    private LocalDateTime createTime;
}
