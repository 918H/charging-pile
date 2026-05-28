import request from '@/utils/request'

/**
 * 统计数据 API
 */

// 获取数据大屏概览
export function getDashboard() {
  return request({
    url: '/statistics/dashboard',
    method: 'get'
  })
}

// 获取指定日期范围统计
export function getDashboardByRange(startDate, endDate) {
  return request({
    url: '/statistics/dashboard/range',
    method: 'get',
    params: { startDate, endDate }
  })
}

// 刷新统计数据
export function refreshStatistics() {
  return request({
    url: '/statistics/refresh',
    method: 'post'
  })
}
