import request from './request'

export function getMembershipLevels() {
  return request({
    url: '/api/user/membership/levels',
    method: 'get'
  })
}

export function getCurrentMembership(userId) {
  return request({
    url: '/api/user/membership/user/current',
    method: 'get',
    params: { userId }
  })
}

export function calculateDiscount(userId, amount) {
  return request({
    url: '/api/user/membership/user/discount',
    method: 'get',
    params: { userId, amount }
  })
}

export function upgradeMembership(userId, levelId) {
  return request({
    url: '/api/user/membership/user/upgrade',
    method: 'post',
    params: { userId, levelId }
  })
}
