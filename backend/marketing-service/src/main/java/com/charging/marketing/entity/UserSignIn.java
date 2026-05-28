package com.charging.marketing.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@TableName("user_sign_in")
public class UserSignIn {
    @TableId(type = IdType.AUTO) private Long id;
    private Long userId;
    private LocalDate signInDate;
    private Integer continuousDays;
    private Integer points;
    private LocalDateTime createTime;
}
