Page({
  data: {
    coupons: [],
    tab: 0
  },

  onShow() {
    this.loadCoupons();
  },

  async loadCoupons() {
    try {
      const couponApi = require('../../api/coupon');
      const coupons = await couponApi.getUserCoupons(wx.getStorageSync('userId'), this.data.tab);
      
      const couponsWithText = coupons.map(coupon => ({
        ...coupon,
        statusText: this.getStatusText(coupon.status)
      }));
      
      this.setData({ coupons: couponsWithText });
    } catch (err) {
      console.error('加载优惠券失败', err);
    }
  },

  getStatusText(status) {
    const texts = {
      0: '未使用',
      1: '已使用',
      2: '已过期'
    };
    return texts[status] || '未知';
  },

  async receiveCoupon(e) {
    const couponId = e.currentTarget.dataset.id;
    const userId = wx.getStorageSync('userId');
    
    try {
      const couponApi = require('../../api/coupon');
      await couponApi.receiveCoupon(userId, couponId);
      
      wx.showToast({
        title: '领取成功',
        icon: 'success'
      });
      
      this.loadCoupons();
    } catch (err) {
      wx.showToast({
        title: '领取失败',
        icon: 'none'
      });
    }
  },

  onTabChange(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ tab });
    this.loadCoupons();
  }
});
