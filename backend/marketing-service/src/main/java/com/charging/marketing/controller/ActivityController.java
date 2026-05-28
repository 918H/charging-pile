package com.charging.marketing.controller;

import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/marketing/activity")
public class ActivityController {
    
    @PostMapping("/create")
    public R createActivity() {
        return R.ok("活动创建成功");
    }
    
    @GetMapping("/list")
    public R listActivities() {
        return R.ok("活动列表");
    }
    
    @PutMapping("/{id}/launch")
    public R launchActivity(@PathVariable Long id) {
        return R.ok("活动上线成功");
    }
}
