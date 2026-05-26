package com.charging.user.dto;

import lombok.Data;

@Data
public class LoginResponse {

    private Long userId;
    private String username;
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private String realName;
    private String avatarUrl;
}
