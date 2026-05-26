import request from './request'

export function getPaymentDetail(paymentId) {
  return request({
    url: `/api/payment/record/${paymentId}`,
    method: 'get'
  })
}

export function getPaymentByOrder(orderId) {
  return request({
    url: `/api/payment/record/order/${orderId}`,
    method: 'get'
  })
}

export function createPayment(data) {
  return request({
    url: '/api/payment/record',
    method: 'post',
    data
  })
}
