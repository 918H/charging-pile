import request from '@/utils/request'
export function listActivities(params) { return request({ url: '/marketing/activity/list', method: 'get', params }) }
export function createActivity(data) { return request({ url: '/marketing/activity/create', method: 'post', params: data }) }
export function launchActivity(id) { return request({ url: `/marketing/activity/${id}/launch`, method: 'put' }) }
export function participateActivity(id, userId) { return request({ url: `/marketing/activity/${id}/participate`, method: 'post', params: { userId }}) }
export function getActivityStats(id) { return request({ url: `/marketing/activity/${id}/stats`, method: 'get' }) }
