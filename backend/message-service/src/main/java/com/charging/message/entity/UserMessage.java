package com.charging.message.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("user_message")
public class UserMessage {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private LocalDateTime readTime;
    private LocalDateTime createTime;
}
