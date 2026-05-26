package com.charging.user.service;

import com.charging.user.entity.SysUser;

public interface SysUserService {
    
    SysUser getByUsername(String username);
    
    SysUser getByUserId(Long userId);
    
    boolean register(SysUser user, String password);
    
    boolean updateProfile(SysUser user);
    
    boolean checkPassword(String rawPassword, String encodedPassword);
    
    String encodePassword(String rawPassword);
}
