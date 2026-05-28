package com.charging.support.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
@Data
@TableName("ticket_reply")
public class TicketReply {
    @TableId(type = IdType.AUTO) private Long id;
    private Long ticketId;
    private Long userId;
    private String content;
    private Integer type;
    private LocalDateTime createTime;
}
