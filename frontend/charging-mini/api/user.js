const { request } = require('./request');

module.exports = {
  login(code) {
    return request({
      url: '/api/user/auth/login',
      method: 'POST',
      data: { code }
    });
  },

  getProfile() {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/user/profile',
      method: 'GET',
      token
    });
  },

  updateProfile(data) {
    const token = wx.getStorageSync('token');
    return request({
      url: '/api/user/profile',
      method: 'PUT',
      data,
      token
    });
  }
};
