package com.charging.message.controller;

import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/message")
public class MessageController {
    
    @GetMapping("/list")
    public R list() {
        return R.ok("消息列表");
    }
    
    @PostMapping("/send")
    public R send() {
        return R.ok("发送成功");
    }
}
