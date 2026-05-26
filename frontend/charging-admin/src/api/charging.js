import request from './request'

export function getPileList() {
  return request({
    url: '/api/charging/pile/list',
    method: 'get'
  })
}

export function getPilePage(current, size) {
  return request({
    url: '/api/charging/pile/page',
    method: 'get',
    params: { current, size }
  })
}

export function getPileDetail(pileId) {
  return request({
    url: `/api/charging/pile/${pileId}`,
    method: 'get'
  })
}

export function addPile(data) {
  return request({
    url: '/api/charging/pile',
    method: 'post',
    data
  })
}

export function updatePile(pileId, data) {
  return request({
    url: `/api/charging/pile/${pileId}`,
    method: 'put',
    data
  })
}

export function deletePile(pileId) {
  return request({
    url: `/api/charging/pile/${pileId}`,
    method: 'delete'
  })
}

export function getNearbyPiles(latitude, longitude, radius) {
  return request({
    url: '/api/charging/pile/nearby',
    method: 'get',
    params: { latitude, longitude, radius }
  })
}

export function getAvailablePiles() {
  return request({
    url: '/api/charging/pile/available',
    method: 'get'
  })
}
