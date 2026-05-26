const { request } = require('./request');

module.exports = {
  getOrderList(params = {}) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/page',
      method: 'GET',
      data: params,
      token
    });
  },

  getOrderDetail(orderId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/order/order/${orderId}`,
      method: 'GET',
      token
    });
  },

  createOrder(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/create',
      method: 'POST',
      data,
      token
    });
  },

  cancelOrder(orderId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/order/order/${orderId}/cancel`,
      method: 'PUT',
      token
    });
  },

  getOrderHistory(userId, status = null) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/user/history',
      method: 'GET',
      data: { userId, status },
      token
    });
  },

  getOrderStatistics(userId, startDate, endDate) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/statistics',
      method: 'GET',
      data: { userId, startDate, endDate },
      token
    });
  },

  getUncalculatedOrders(userId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/user/uncalculated',
      method: 'GET',
      data: { userId },
      token
    });
  },

  calculateOrder(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/calculate',
      method: 'POST',
      data,
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

  getPileSlots(pileId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/charging/pile/${pileId}/slots`,
      method: 'GET',
      token
    });
  },

  startCharging(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/charging/start',
      method: 'POST',
      data,
      token
    });
  },

  stopCharging(orderId, reason = '用户主动停止') {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/charging/stop',
      method: 'POST',
      data: { orderId, reason },
      token
    });
  },

  getChargingProgress(orderId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/charging/progress',
      method: 'GET',
      data: { orderId },
      token
    });
  },

  getChargingPrice(pileId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/charging/price',
      method: 'GET',
      data: { pileId },
      token
    });
  },

  calculateOccupationFee(pileId, fullTime, leaveTime) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/order/charging/occupation-fee',
      method: 'POST',
      data: { pileId, fullTime, leaveTime },
      token
    });
  },

  createReservation(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/reservation/create',
      method: 'POST',
      data,
      token
    });
  },

  cancelReservation(reservationId, reason = '用户取消') {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/order/reservation/${reservationId}/cancel`,
      method: 'POST',
      data: { reason },
      token
    });
  },

  getReservationDetail(reservationId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/order/reservation/${reservationId}`,
      method: 'GET',
      token
    });
  },

  getUserReservations(userId, status = null) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/reservation/user/list',
      method: 'GET',
      data: { userId, status },
      token
    });
  },

  checkAvailability(pileId, slotId, startTime, endTime) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/reservation/check',
      method: 'GET',
      data: { pileId, slotId, startTime, endTime },
      token
    });
  },

  createReview(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/review/create',
      method: 'POST',
      data,
      token
    });
  },

  getPileReviews(pileId, limit = 10) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/review/pile/list',
      method: 'GET',
      data: { pileId, limit },
      token
    });
  },

  getUserReviews(userId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/review/user/list',
      method: 'GET',
      data: { userId },
      token
    });
  },

  getPileRating(pileId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/review/pile/rating',
      method: 'GET',
      data: { pileId },
      token
    });
  },

  canReview(userId, orderId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/order/review/can-review',
      method: 'GET',
      data: { userId, orderId },
      token
    });
  }
};
