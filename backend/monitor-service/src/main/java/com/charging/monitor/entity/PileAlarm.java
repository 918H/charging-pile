package com.charging.monitor.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("pile_alarm")
public class PileAlarm {
    @TableId(type = IdType.AUTO) private Long id;
    private Long pileId;
    private String type;
    private String level;
    private String content;
    private String status;
    private String resolveResult;
    private LocalDateTime alarmTime;
    private LocalDateTime resolveTime;
    private LocalDateTime createTime;
}
