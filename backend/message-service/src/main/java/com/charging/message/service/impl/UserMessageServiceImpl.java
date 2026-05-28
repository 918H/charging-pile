package com.charging.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.core.response.R;
import com.charging.common.db.PageResult;
import com.charging.message.entity.UserMessage;
import com.charging.message.mapper.UserMessageMapper;
import com.charging.message.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserMessageServiceImpl implements UserMessageService {
    
    @Resource
    private UserMessageMapper userMessageMapper;
    
    @Override
    public R list(Long userId, Integer pageNum, Integer pageSize) {
        log.info("查询用户消息列表：userId={}", userId);
        
        Page<UserMessage> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .orderByDesc(UserMessage::getCreateTime);
        
        List<UserMessage> messages = userMessageMapper.selectPage(page, wrapper).getRecords();
        PageResult<UserMessage> result = PageResult.fromIPage(page);
        
        return R.ok(result);
    }
    
    @Override
    public R unreadCount(Long userId) {
        log.info("查询未读消息数：userId={}", userId);
        
        LambdaQueryWrapper<UserMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMessage::getUserId, userId)
               .eq(UserMessage::getIsRead, false);
        
        Long count = userMessageMapper.selectCount(wrapper);
        return R.ok(count);
    }
    
    @Override
    public R markAsRead(Long messageId) {
        log.info("标记消息已读：messageId={}", messageId);
        
        UserMessage message = userMessageMapper.selectById(messageId);
        if (message != null) {
            message.setIsRead(true);
            message.setReadTime(LocalDateTime.now());
            userMessageMapper.updateById(message);
        }
        
        return R.ok("操作成功");
    }
    
    @Override
    public R send(Long userId, String title, String content, String type) {
        log.info("发送消息：userId={}, title={}", userId, title);
        
        UserMessage message = new UserMessage();
        message.setUserId(userId);
        message.setTitle(title);
        message.setContent(content);
        message.setType(type);
        message.setChannel("站内信");
        message.setIsRead(false);
        
        userMessageMapper.insert(message);
        
        return R.ok("发送成功");
    }
}
