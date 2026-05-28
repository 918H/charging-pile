package com.charging.marketing.controller;
import com.charging.common.core.response.R;
import com.charging.marketing.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.time.LocalDateTime;
@Slf4j
@RestController
@RequestMapping("/marketing/activity")
public class ActivityController {
    @Resource private ActivityService activityService;
    @GetMapping("/list")
    public R list(@RequestParam(required = false) String status) {
        return activityService.list(status);
    }
    @PostMapping("/create")
    public R create(@RequestParam String name, @RequestParam String type,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        return activityService.create(name, type, startTime, endTime);
    }
    @PutMapping("/{id}/launch")
    public R launch(@PathVariable Long id) {
        return activityService.launch(id);
    }
    @PostMapping("/{id}/participate")
    public R participate(@PathVariable Long id, @RequestParam Long userId) {
        return activityService.participate(id, userId);
    }
    @GetMapping("/{id}/stats")
    public R statistics(@PathVariable Long id) {
        return activityService.statistics(id);
    }
}
