package com.charging.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.entity.SysUser;
import com.charging.user.mapper.SysUserMapper;
import com.charging.user.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public SysUser getByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public SysUser getByUserId(Long userId) {
        return sysUserMapper.selectById(userId);
    }

    @Override
    public boolean register(SysUser user, String password) {
        user.setPassword(encodePassword(password));
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return sysUserMapper.insert(user) > 0;
    }

    @Override
    public boolean updateProfile(SysUser user) {
        user.setUpdatedAt(LocalDateTime.now());
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }

    @Override
    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt());
    }
}
