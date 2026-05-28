package com.charging.marketing.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("marketing_activity")
public class MarketingActivity {
    @TableId(type = IdType.AUTO) private Long id;
    private String name;
    private String type;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer participantCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
