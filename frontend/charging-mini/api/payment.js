const { request } = require('./request');

module.exports = {
  getPaymentList(params = {}) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/payment/payment/page',
      method: 'GET',
      data: params,
      token
    });
  },

  getPaymentDetail(paymentId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/payment/payment/${paymentId}`,
      method: 'GET',
      token
    });
  },

  getPaymentByOrder(orderNumber) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/payment/payment/by-order',
      method: 'GET',
      data: { orderNumber },
      token
    });
  },

  createPayment(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/payment/payment/create',
      method: 'POST',
      data,
      token
    });
  },

  applyRefund(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/payment/payment/refund/apply',
      method: 'POST',
      data,
      token
    });
  },

  getRefundDetail(refundId) {
    const token = wx.getStorageSync('token');
    return request({
      url: `/api/payment/payment/refund/${refundId}`,
      method: 'GET',
      token
    });
  },

  getUserRefunds(userId) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/payment/payment/refund/user/list',
      method: 'GET',
      data: { userId },
      token
    });
  }
};
