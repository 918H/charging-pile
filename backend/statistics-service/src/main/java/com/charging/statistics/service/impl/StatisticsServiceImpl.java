package com.charging.statistics.service.impl;

import com.charging.common.core.response.R;
import com.charging.statistics.mapper.StatisticsDailyMapper;
import com.charging.statistics.service.StatisticsService;
import com.charging.statistics.vo.DashboardVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {
    
    @Resource
    private StatisticsDailyMapper statisticsDailyMapper;
    
    @Override
    public R<DashboardVO> getDashboardOverview() {
        log.info("获取数据大屏概览");
        
        DashboardVO dashboard = new DashboardVO();
        
        // 模拟实时数据（实际应从数据库聚合查询）
        dashboard.setTodayRevenue(new BigDecimal(String.valueOf(new Random().nextInt(10000) + 5000)));
        dashboard.setTodayOrders(new Random().nextInt(500) + 200);
        dashboard.setTodayKwh(new BigDecimal(String.valueOf(new Random().nextInt(3000) + 1500)));
        dashboard.setActivePiles(new Random().nextInt(50) + 30);
        dashboard.setOnlinePiles(new Random().nextInt(60) + 40);
        dashboard.setPileUsageRate(Double.valueOf(new Random().nextInt(40) + 50));
        
        // 收益趋势（最近 7 天）
        dashboard.setRevenueTrend(generateTrendData(7, 5000, 15000));
        
        // 订单趋势（最近 7 天）
        dashboard.setOrderTrend(generateTrendData(7, 200, 500));
        
        // 用户增长
        DashboardVO.UserGrowthVO userGrowth = new DashboardVO.UserGrowthVO();
        userGrowth.setTodayNew(new Random().nextInt(50) + 10);
        userGrowth.setWeekNew(new Random().nextInt(300) + 100);
        userGrowth.setMonthNew(new Random().nextInt(1000) + 500);
        userGrowth.setTrend(generateTrendData(30, 10, 50));
        dashboard.setUserGrowth(userGrowth);
        
        return R.ok(dashboard);
    }
    
    @Override
    public R<DashboardVO> getDailyStatistics(LocalDate startDate, LocalDate endDate) {
        log.info("获取日期范围统计：{} 到 {}", startDate, endDate);
        return getDashboardOverview();
    }
    
    @Override
    public void refreshStatistics() {
        log.info("刷新统计数据");
        // TODO: 实现定时任务刷新统计
    }
    
    // 生成趋势数据
    private List<DashboardVO.TrendItem> generateTrendData(int days, int min, int max) {
        List<DashboardVO.TrendItem> list = new ArrayList<>();
        Random random = new Random();
        LocalDate date = LocalDate.now().minusDays(days);
        
        for (int i = 0; i < days; i++) {
            DashboardVO.TrendItem item = new DashboardVO.TrendItem();
            item.setDate(date.plusDays(i).toString());
            item.setValue(new BigDecimal(random.nextInt(max - min) + min));
            list.add(item);
        }
        
        return list;
    }
}
