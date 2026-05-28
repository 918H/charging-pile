package com.charging.statistics.service;

import com.charging.common.core.response.R;
import com.charging.statistics.vo.DashboardVO;
import java.time.LocalDate;

public interface StatisticsService {
    
    /**
     * 获取数据概览
     */
    R<DashboardVO> getDashboardOverview();
    
    /**
     * 获取日统计数据
     */
    R<DashboardVO> getDailyStatistics(LocalDate startDate, LocalDate endDate);
    
    /**
     * 实时刷新统计数据
     */
    void refreshStatistics();
}
