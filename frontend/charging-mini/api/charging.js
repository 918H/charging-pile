const { request } = require('./request');

module.exports = {
  getPileList() {
    return request({
      url: '/api/charging/pile/list',
      method: 'GET'
    });
  },

  getPileDetail(pileId) {
    return request({
      url: `/api/charging/pile/${pileId}`,
      method: 'GET'
    });
  },

  getNearbyPiles(latitude, longitude, radius = 10) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/charging/pile/nearby',
      method: 'GET',
      data: { latitude, longitude, radius },
      token
    });
  },

  getPileDetail(pileId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/charging/pile/${pileId}`,
      method: 'GET',
      token
    });
  },

  reportFault(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/charging/fault/report',
      method: 'POST',
      data,
      token
    });
  },

  getPileFaults(pileId, status = null) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/charging/fault/pile/list',
      method: 'GET',
      data: { pileId, status },
      token
    });
  },

  getUserFaults(userId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/charging/fault/user/list',
      method: 'GET',
      data: { userId },
      token
    });
  }
};
