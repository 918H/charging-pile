package com.charging.message.service;

import com.charging.common.core.response.R;

public interface UserMessageService {
    R list(Long userId, Integer pageNum, Integer pageSize);
    R unreadCount(Long userId);
    R markAsRead(Long messageId);
    R send(Long userId, String title, String content, String type);
}
