import request from './request'

export function getUserPoints(userId) {
  return request({
    url: '/api/user/points/user',
    method: 'get',
    params: { userId }
  })
}

export function getPointsRecords(userId, type) {
  return request({
    url: '/api/user/points/records',
    method: 'get',
    params: { userId, type }
  })
}

export function getMallItems(type, status) {
  return request({
    url: '/api/user/points-mall/items',
    method: 'get',
    params: { type, status }
  })
}

export function exchangeItem(userId, itemId, shippingAddress) {
  return request({
    url: '/api/user/points-mall/exchange',
    method: 'post',
    params: { userId },
    data: { itemId, shippingAddress }
  })
}

export function getExchangeRecords(userId, status) {
  return request({
    url: '/api/user/points-mall/records',
    method: 'get',
    params: { userId, status }
  })
}
