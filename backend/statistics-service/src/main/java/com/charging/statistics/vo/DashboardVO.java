package com.charging.statistics.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardVO {
    
    // 实时数据
    private BigDecimal todayRevenue;           // 今日收益
    private Integer todayOrders;               // 今日订单
    private BigDecimal todayKwh;               // 今日充电量
    private Integer activePiles;               // 活跃充电桩
    private Integer onlinePiles;               // 在线充电桩
    
    // 趋势数据
    private List<TrendItem> revenueTrend;      // 收益趋势
    private List<TrendItem> orderTrend;        // 订单趋势
    private UserGrowthVO userGrowth;           // 用户增长
    
    // 统计信息
    private BigDecimal totalRevenue;           // 总收益
    private Integer totalUsers;                // 总用户数
    private Integer totalPiles;                // 总充电桩数
    private Double pileUsageRate;              // 桩使用率
    
    @Data
    public static class TrendItem {
        private String date;
        private BigDecimal value;
    }
    
    @Data
    public static class UserGrowthVO {
        private Integer todayNew;              // 今日新增
        private Integer weekNew;               // 本周新增
        private Integer monthNew;              // 本月新增
        private List<TrendItem> trend;         // 增长趋势
    }
}
