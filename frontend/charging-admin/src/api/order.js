import request from './request'

export function getOrderList(params) {
  return request({
    url: '/api/order/order/page',
    method: 'get',
    params
  })
}

export function getOrderDetail(orderId) {
  return request({
    url: `/api/order/order/${orderId}`,
    method: 'get'
  })
}

export function createOrder(data) {
  return request({
    url: '/api/order/order/create',
    method: 'post',
    data
  })
}

export function cancelOrder(orderId) {
  return request({
    url: `/api/order/order/${orderId}/cancel`,
    method: 'put'
  })
}
