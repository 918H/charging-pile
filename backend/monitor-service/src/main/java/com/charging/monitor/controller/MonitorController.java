package com.charging.monitor.controller;
import com.charging.common.core.response.R;
import com.charging.monitor.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
@Slf4j
@RestController
@RequestMapping("/monitor")
public class MonitorController {
    @Resource private MonitorService monitorService;
    @GetMapping("/pile/{id}/status")
    public R pileStatus(@PathVariable Long id) {
        return monitorService.pileStatus(id);
    }
    @GetMapping("/piles/status")
    public R allPilesStatus() {
        return monitorService.allPilesStatus();
    }
    @GetMapping("/alarm/list")
    public R alarmList(@RequestParam(required = false) String status) {
        return monitorService.alarmList(status);
    }
    @PutMapping("/alarm/{id}/resolve")
    public R resolveAlarm(@PathVariable Long id, @RequestParam String result) {
        return monitorService.resolveAlarm(id, result);
    }
    @GetMapping("/pile/{id}/health")
    public R pileHealth(@PathVariable Long id) {
        return monitorService.pileHealth(id);
    }
}
