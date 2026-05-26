import request from './request'

export function login(data) {
  return request({
    url: '/api/user/auth/login',
    method: 'post',
    data
  })
}

export function getUserProfile() {
  return request({
    url: '/api/user/profile',
    method: 'get'
  })
}

export function updateUserProfile(data) {
  return request({
    url: '/api/user/profile',
    method: 'put',
    data
  })
}
