package com.charging.monitor.controller;

import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    
    @GetMapping("/pile/status")
    public R getPileStatus() {
        return R.ok("充电桩状态");
    }
    
    @GetMapping("/alarm/list")
    public R listAlarms() {
        return R.ok("告警列表");
    }
    
    @PostMapping("/alarm/{id}/resolve")
    public R resolveAlarm(@PathVariable Long id) {
        return R.ok("告警处理完成");
    }
}
