Page({
  data: {
    isLogin: false,
    avatarUrl: '',
    nickname: '',
    phone: ''
  },

  onShow() {
    this.loadProfile();
  },

  loadProfile() {
    const token = wx.getStorageSync('token');
    const userInfo = wx.getStorageSync('userInfo');
    
    if (token && userInfo) {
      this.setData({
        isLogin: true,
        avatarUrl: userInfo.avatarUrl,
        nickname: userInfo.nickname,
        phone: userInfo.phone
      });
    }
  },

  async login() {
    try {
      const res = await wx.getUserProfile({
        desc: '用于完善用户资料'
      });

      const userInfo = res.userInfo;
      
      const userApi = require('../../api/user');
      const loginRes = await userApi.login({
        username: userInfo.nickName,
        avatarUrl: userInfo.avatarUrl
      });

      wx.setStorageSync('token', loginRes.token);
      wx.setStorageSync('userInfo', {
        nickname: userInfo.nickName,
        avatarUrl: userInfo.avatarUrl
      });

      this.setData({
        isLogin: true,
        avatarUrl: userInfo.avatarUrl,
        nickname: userInfo.nickName
      });

      wx.showToast({ title: '登录成功', icon: 'success' });
    } catch (err) {
      console.error('登录失败', err);
    }
  },

  logout() {
    wx.showModal({
      title: '确认退出',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          this.setData({
            isLogin: false,
            avatarUrl: '',
            nickname: '',
            phone: ''
          });
          wx.showToast({ title: '已退出', icon: 'success' });
        }
      }
    });
  },

  navigateTo(e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({ url });
  }
});
