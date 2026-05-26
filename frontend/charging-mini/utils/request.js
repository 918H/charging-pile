const config = require('./config');

function request(options) {
  return new Promise((resolve, reject) => {
    wx.request({
      url: config.API_BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        'Authorization': options.token ? `Bearer ${options.token}` : ''
      },
      timeout: config.API_TIMEOUT,
      success: (res) => {
        if (res.statusCode === 200 && res.data.code === 200) {
          resolve(res.data.data);
        } else {
          wx.showToast({
            title: res.data.msg || '请求失败',
            icon: 'none'
          });
          reject(res.data);
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络错误',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
}

module.exports = {
  request
};
