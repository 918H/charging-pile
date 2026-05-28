<template>
  <div class="data-screen">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stats-cards">
      <el-col :span="6">
        <div class="stat-card revenue">
          <div class="card-label">今日收益</div>
          <div class="card-value">¥{{ dashboard.todayRevenue || 0 }}</div>
          <div class="card-trend up">↑ 12.5%</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card orders">
          <div class="card-label">今日订单</div>
          <div class="card-value">{{ dashboard.todayOrders || 0 }}</div>
          <div class="card-trend up">↑ 8.3%</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card kwh">
          <div class="card-label">今日充电量</div>
          <div class="card-value">{{ dashboard.todayKwh || 0 }} kWh</div>
          <div class="card-trend up">↑ 15.2%</div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card piles">
          <div class="card-label">活跃充电桩</div>
          <div class="card-value">{{ dashboard.activePiles || 0 }}/{{ dashboard.onlinePiles || 0 }}</div>
          <div class="card-trend">使用率 {{ dashboard.pileUsageRate || 0 }}%</div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts">
      <el-col :span="12">
        <div class="chart-container">
          <h3>收益趋势</h3>
          <div ref="revenueChart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-container">
          <h3>订单趋势</h3>
          <div ref="orderChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts">
      <el-col :span="8">
        <div class="chart-container">
          <h3>用户增长</h3>
          <div ref="userChart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-container">
          <h3>峰谷时段分布</h3>
          <div ref="timeDistChart" class="chart"></div>
        </div>
      </el-col>
      <el-col :span="8">
        <div class="chart-container">
          <h3>充电桩使用率</h3>
          <div ref="pileUsageChart" class="chart"></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'

export default {
  name: 'DataScreen',
  setup() {
    const dashboard = ref({
      todayRevenue: 0,
      todayOrders: 0,
      todayKwh: 0,
      activePiles: 0,
      onlinePiles: 0,
      pileUsageRate: 0,
      revenueTrend: [],
      orderTrend: [],
      userGrowth: {}
    })
    
    let charts = {}
    let timer = null

    // 加载数据
    const loadDashboard = async () => {
      try {
        const res = await fetch('/api/statistics/dashboard')
        const result = await res.json()
        if (result.code === 200) {
          dashboard.value = result.data
          updateCharts()
        }
      } catch (error) {
        ElMessage.error('加载数据失败')
      }
    }

    // 初始化图表
    const initCharts = () => {
      charts.revenue = echarts.init(document.querySelector('[ref="revenueChart"]'))
      charts.order = echarts.init(document.querySelector('[ref="orderChart"]'))
      charts.user = echarts.init(document.querySelector('[ref="userChart"]'))
      charts.timeDist = echarts.init(document.querySelector('[ref="timeDistChart"]'))
      charts.pileUsage = echarts.init(document.querySelector('[ref="pileUsageChart"]'))
    }

    // 更新图表
    const updateCharts = () => {
      // 收益趋势图
      charts.revenue.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { 
          type: 'category',
          data: dashboard.value.revenueTrend?.map(item => item.date) || []
        },
        yAxis: { type: 'value' },
        series: [{
          data: dashboard.value.revenueTrend?.map(item => item.value) || [],
          type: 'line',
          smooth: true,
          areaStyle: {}
        }]
      })

      // 订单趋势图
      charts.order.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: { 
          type: 'category',
          data: dashboard.value.orderTrend?.map(item => item.date) || []
        },
        yAxis: { type: 'value' },
        series: [{
          data: dashboard.value.orderTrend?.map(item => item.value) || [],
          type: 'bar'
        }]
      })
    }

    // 自动刷新
    const startAutoRefresh = () => {
      timer = setInterval(() => {
        loadDashboard()
      }, 30000) // 30 秒刷新
    }

    onMounted(() => {
      loadDashboard()
      initCharts()
      startAutoRefresh()
    })

    onUnmounted(() => {
      if (timer) clearInterval(timer)
      Object.values(charts).forEach(chart => chart.dispose())
    })

    return {
      dashboard
    }
  }
}
</script>

<style scoped>
.data-screen {
  padding: 20px;
  background: #f5f7fa;
  min-height: 100vh;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.card-label {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.card-trend {
  font-size: 12px;
  color: #67C23A;
}

.card-trend.up {
  color: #67C23A;
}

.card-trend.down {
  color: #F56C6C;
}

.chart-container {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.chart-container h3 {
  margin: 0 0 20px 0;
  color: #303133;
}

.chart {
  height: 300px;
}
</style>
