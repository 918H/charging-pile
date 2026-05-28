package com.charging.monitor.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("pile_status")
public class PileStatus {
    private Long id;
    private Long pileId;
    private String status;
    private BigDecimal power;
    private BigDecimal voltage;
    private BigDecimal current;
    private BigDecimal temperature;
    private String errorCode;
    private LocalDateTime lastHeartbeat;
    private LocalDateTime updateTime;
}
