package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.entity.SysUser;
import com.charging.user.service.SysUserService;
import com.charging.user.util.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private JwtUtil jwtUtil;

    @GetMapping
    public Result<SysUser> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            SysUser user = sysUserService.getByUserId(userId);
            
            if (user != null) {
                user.setPassword(null);
            }
            
            return Result.success(user);
        } catch (Exception e) {
            return Result.error("获取用户信息失败");
        }
    }

    @PutMapping
    public Result<Void> updateProfile(@RequestHeader("Authorization") String token,
                                       @RequestBody SysUser user) {
        try {
            String jwt = token.replace("Bearer ", "");
            Long userId = jwtUtil.getUserIdFromToken(jwt);
            
            SysUser existingUser = sysUserService.getByUserId(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            existingUser.setRealName(user.getRealName());
            existingUser.setPhone(user.getPhone());
            existingUser.setEmail(user.getEmail());
            existingUser.setAvatarUrl(user.getAvatarUrl());
            
            sysUserService.updateProfile(existingUser);
            return Result.success();
        } catch (Exception e) {
            return Result.error("更新用户信息失败");
        }
    }
}
