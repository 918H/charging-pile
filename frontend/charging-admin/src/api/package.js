import request from './request'

export function getAvailablePackages() {
  return request({
    url: '/api/user/charging-package/list',
    method: 'get'
  })
}

export function getPackageDetail(packageId) {
  return request({
    url: '/api/user/charging-package/detail',
    method: 'get',
    params: { packageId }
  })
}

export function purchasePackage(userId, packageId) {
  return request({
    url: '/api/user/charging-package/purchase',
    method: 'post',
    params: { userId, packageId }
  })
}

export function getUserPackages(userId) {
  return request({
    url: '/api/user/charging-package/user/list',
    method: 'get',
    params: { userId }
  })
}
