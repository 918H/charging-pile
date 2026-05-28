package com.charging.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user_message")
public class UserMessage {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private String channel;
    private Boolean isRead;
    private LocalDateTime readTime;
    private Long templateId;
    private String bizId;
    private LocalDateTime createTime;
}
