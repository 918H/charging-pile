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
    return request({
      url: '/api/charging/pile/nearby',
      method: 'GET',
      data: { latitude, longitude, radius }
    });
  },

  getAvailablePiles() {
    return request({
      url: '/api/charging/pile/available',
      method: 'GET'
    });
  }
};
