package com.charging.statistics.controller;

import com.charging.common.core.response.R;
import com.charging.statistics.service.StatisticsService;
import com.charging.statistics.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    
    @Resource
    private StatisticsService statisticsService;
    
    /**
     * 获取数据大屏概览
     */
    @GetMapping("/dashboard")
    public R<DashboardVO> getDashboard() {
        return statisticsService.getDashboardOverview();
    }
    
    /**
     * 获取指定日期范围统计
     */
    @GetMapping("/dashboard/range")
    public R<DashboardVO> getDashboardByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return statisticsService.getDailyStatistics(startDate, endDate);
    }
    
    /**
     * 手动刷新统计数据
     */
    @PostMapping("/refresh")
    public R<Void> refresh() {
        statisticsService.refreshStatistics();
        return R.ok("统计数据已刷新");
    }
}
