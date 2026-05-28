package com.charging.marketing.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("activity_participation")
public class ActivityParticipation {
    @TableId(type = IdType.AUTO) private Long id;
    private Long activityId;
    private Long userId;
    private String status;
    private String reward;
    private LocalDateTime participateTime;
    private LocalDateTime createTime;
}
