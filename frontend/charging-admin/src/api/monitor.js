import request from '@/utils/request'
export function getPileStatus(id) { return request({ url: `/monitor/pile/${id}/status`, method: 'get' }) }
export function getAllPilesStatus() { return request({ url: '/monitor/piles/status', method: 'get' }) }
export function getAlarmList(params) { return request({ url: '/monitor/alarm/list', method: 'get', params }) }
export function resolveAlarm(id, result) { return request({ url: `/monitor/alarm/${id}/resolve`, method: 'put', params: { result }}) }
export function getPileHealth(id) { return request({ url: `/monitor/pile/${id}/health`, method: 'get' }) }
