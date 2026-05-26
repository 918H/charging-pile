package com.charging.user.service;

import com.charging.user.entity.SysUser;
import com.charging.user.mapper.SysUserMapper;
import com.charging.user.service.impl.SysUserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SysUserServiceTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetByUsername() {
        SysUser mockUser = new SysUser();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("test@example.com");

        when(sysUserMapper.selectOne(any())).thenReturn(mockUser);

        SysUser result = sysUserService.getByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(sysUserMapper, times(1)).selectOne(any());
    }

    @Test
    void testRegister() {
        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        newUser.setEmail("new@example.com");

        when(sysUserMapper.insert(any())).thenReturn(1);

        boolean result = sysUserService.register(newUser, "Test123!");

        assertTrue(result);
        assertNotNull(newUser.getPassword());
        assertEquals(1, newUser.getStatus());
        verify(sysUserMapper, times(1)).insert(any());
    }

    @Test
    void testUpdateProfile() {
        SysUser user = new SysUser();
        user.setUserId(1L);
        user.setRealName("Updated Name");

        when(sysUserMapper.updateById(any())).thenReturn(1);

        boolean result = sysUserService.updateProfile(user);

        assertTrue(result);
        assertNotNull(user.getUpdatedAt());
        verify(sysUserMapper, times(1)).updateById(any());
    }

    @Test
    void testCheckPassword() {
        String rawPassword = "Test123!";
        String encodedPassword = sysUserService.encodePassword(rawPassword);

        assertTrue(sysUserService.checkPassword(rawPassword, encodedPassword));
        assertFalse(sysUserService.checkPassword("WrongPassword", encodedPassword));
    }

    @Test
    void testGetByUserId() {
        SysUser mockUser = new SysUser();
        mockUser.setUserId(1L);
        mockUser.setUsername("testuser");

        when(sysUserMapper.selectById(1L)).thenReturn(mockUser);

        SysUser result = sysUserService.getByUserId(1L);

        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(sysUserMapper, times(1)).selectById(1L);
    }
}
