package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.dto.LoginRequest;
import com.charging.user.dto.LoginResponse;
import com.charging.user.dto.RegisterRequest;
import com.charging.user.entity.SysUser;
import com.charging.user.security.LoginAttemptService;
import com.charging.user.security.PasswordValidator;
import com.charging.user.service.SysUserService;
import com.charging.user.util.JwtUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private LoginAttemptService loginAttemptService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody @Validated LoginRequest request) {
        if (loginAttemptService.isLocked(request.getUsername())) {
            return Result.error(String.format("账户已锁定，请 %d 分钟后再试", 
                loginAttemptService.getLockTimeMinutes()));
        }

        SysUser user = sysUserService.getByUsername(request.getUsername());
        if (user == null || !sysUserService.checkPassword(request.getPassword(), user.getPassword())) {
            loginAttemptService.loginFailed(request.getUsername());
            return Result.error("用户名或密码错误");
        }

        if (user.getStatus() != 1) {
            return Result.error("账户已被禁用");
        }

        loginAttemptService.loginSucceeded(request.getUsername());

        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
        String refreshToken = jwtUtil.generateToken(user.getUserId(), user.getUsername());

        LoginResponse response = new LoginResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setToken(token);
        response.setRefreshToken(refreshToken);
        response.setExpiresIn(86400L);
        response.setRealName(user.getRealName());
        response.setAvatarUrl(user.getAvatarUrl());

        return Result.success(response);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestHeader("Authorization") String token) {
        return Result.success();
    }

    @PostMapping("/refresh-token")
    public Result<Map<String, String>> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        try {
            String jwt = refreshToken.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            String username = jwtUtil.getUsernameFromToken(jwt);
            
            String newToken = jwtUtil.generateToken(userId, username);
            
            Map<String, String> data = new HashMap<>();
            data.put("token", newToken);
            
            return Result.success(data);
        } catch (Exception e) {
            return Result.error("刷新 Token 失败");
        }
    }

    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody @Validated RegisterRequest request) {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(request.getPassword());
        if (!result.isValid()) {
            return Result.error(result.getMessage());
        }

        SysUser existingUser = sysUserService.getByUsername(request.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        SysUser newUser = new SysUser();
        newUser.setUsername(request.getUsername());
        newUser.setRealName(request.getRealName());
        newUser.setPhone(request.getPhone());
        newUser.setEmail(request.getEmail());
        newUser.setAvatarUrl(null);

        boolean success = sysUserService.register(newUser, request.getPassword());
        return success ? Result.success(true) : Result.error("注册失败");
    }
}
