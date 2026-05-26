package com.charging.user.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("real_name")
    private String realName;

    @TableField("phone")
    private String phone;

    @TableField("email")
    private String email;

    @TableField("avatar_url")
    private String avatarUrl;

    @TableField("status")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    @TableField(value = "deleted_at", fill = FieldFill.INSERT)
    private LocalDateTime deletedAt;
}
