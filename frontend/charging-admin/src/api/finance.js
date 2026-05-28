import request from '@/utils/request'
export function getTransactionList(params) { return request({ url: '/finance/transaction/list', method: 'get', params }) }
export function getDailyReport(params) { return request({ url: '/finance/report/daily', method: 'get', params }) }
export function getMonthlyReport(params) { return request({ url: '/finance/report/monthly', method: 'get', params }) }
export function getBalance(params) { return request({ url: '/finance/balance', method: 'get', params }) }
export function recharge(data) { return request({ url: '/finance/recharge', method: 'post', params: data }) }
export function withdraw(data) { return request({ url: '/finance/withdraw', method: 'post', params: data }) }
