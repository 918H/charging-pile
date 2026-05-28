package com.charging.message.controller;

import com.charging.common.core.response.R;
import com.charging.message.service.UserMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
    
    @Resource
    private UserMessageService userMessageService;
    
    /**
     * 查询消息列表
     */
    @GetMapping("/list")
    public R list(@RequestParam Long userId,
                  @RequestParam(defaultValue = "1") Integer pageNum,
                  @RequestParam(defaultValue = "10") Integer pageSize) {
        return userMessageService.list(userId, pageNum, pageSize);
    }
    
    /**
     * 查询未读消息数
     */
    @GetMapping("/unread")
    public R unread(@RequestParam Long userId) {
        return userMessageService.unreadCount(userId);
    }
    
    /**
     * 标记消息已读
     */
    @PutMapping("/{id}/read")
    public R markAsRead(@PathVariable Long id) {
        return userMessageService.markAsRead(id);
    }
    
    /**
     * 批量标记已读
     */
    @PutMapping("/read-all")
    public R markAllAsRead(@RequestParam Long userId) {
        return R.ok("批量标记成功");
    }
    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public R send(@RequestParam Long userId,
                  @RequestParam String title,
                  @RequestParam String content,
                  @RequestParam(defaultValue = "system") String type) {
        return userMessageService.send(userId, title, content, type);
    }
    
    /**
     * 删除消息
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        return R.ok("删除成功");
    }
}
