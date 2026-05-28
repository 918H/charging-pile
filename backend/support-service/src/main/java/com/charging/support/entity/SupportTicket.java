package com.charging.support.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("support_ticket")
public class SupportTicket {
    private Long id;
    private String ticketNo;
    private Long userId;
    private String type;
    private String priority;
    private String status;
    private String subject;
    private String content;
    private String attachments;
    private Long assignedTo;
    private Long createdBy;
    private Long resolvedBy;
    private LocalDateTime resolvedTime;
    private Integer satisfactionScore;
    private String satisfactionComment;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
