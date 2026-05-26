const app = getApp();

Page({
  data: {
    pileId: null,
    pileInfo: {},
    slots: [],
    selectedSlot: null,
    chargingModes: ['快充', '慢充', '智能充电'],
    chargingModeIndex: 0,
    targetSoc: 80,
    coupons: [],
    selectedCoupon: null,
    unitPrice: 1.5,
    availableSlots: 0,
    totalSlots: 0,
    statusText: '未知',
    slotStatusText: ['空闲', '可用', '使用中', '故障']
  },

  onLoad(options) {
    if (options.pileId) {
      this.setData({ pileId: options.pileId });
      this.loadPileDetail();
      this.loadPilePrice();
      this.loadUserCoupons();
    }
  },

  async loadPileDetail() {
    try {
      const orderApi = require('../../api/order');
      const pileInfo = await orderApi.getPileDetail(this.data.pileId);
      
      this.setData({
        pileInfo,
        statusText: ['离线', '空闲', '使用中', '故障'][pileInfo.status] || '未知'
      });
      
      this.loadSlots();
    } catch (err) {
      console.error('Load pile detail error:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  async loadSlots() {
    try {
      const orderApi = require('../../api/order');
      const slots = await orderApi.getPileSlots(this.data.pileId);
      
      const availableSlots = slots.filter(s => s.status === 1).length;
      
      this.setData({
        slots,
        availableSlots,
        totalSlots: slots.length
      });
    } catch (err) {
      console.error('Load slots error:', err);
    }
  },

  async loadPilePrice() {
    try {
      const orderApi = require('../../api/order');
      const price = await orderApi.getChargingPrice(this.data.pileId);
      this.setData({ unitPrice: price });
    } catch (err) {
      console.error('Load price error:', err);
    }
  },

  async loadUserCoupons() {
    try {
      const couponApi = require('../../api/coupon');
      const coupons = await couponApi.getUserCoupons(app.globalData.userId);
      this.setData({ coupons });
    } catch (err) {
      console.error('Load coupons error:', err);
    }
  },

  selectSlot(e) {
    const slot = e.currentTarget.dataset.slot;
    if (slot.status !== 1) {
      wx.showToast({ title: '请选择可用槽位', icon: 'none' });
      return;
    }
    
    if (this.data.selectedSlot && this.data.selectedSlot.slotId === slot.slotId) {
      this.setData({ selectedSlot: null });
    } else {
      this.setData({ selectedSlot: slot });
    }
  },

  onChargingModeChange(e) {
    this.setData({ chargingModeIndex: e.detail.value });
  },

  onTargetSocChange(e) {
    this.setData({ targetSoc: e.detail.value });
  },

  selectCoupon(e) {
    const coupon = e.currentTarget.dataset.coupon;
    if (this.data.selectedCoupon && this.data.selectedCoupon.couponId === coupon.couponId) {
      this.setData({ selectedCoupon: null });
    } else {
      this.setData({ selectedCoupon: coupon });
    }
  },

  async startCharging() {
    if (!this.data.selectedSlot) {
      wx.showToast({ title: '请选择槽位', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '启动中...' });

    try {
      const orderApi = require('../../api/order');
      const result = await orderApi.startCharging({
        userId: app.globalData.userId,
        pileId: this.data.pileId,
        slotId: this.data.selectedSlot.slotId,
        chargingMode: this.data.chargingModeIndex + 1,
        targetSoc: this.data.targetSoc,
        couponId: this.data.selectedCoupon ? this.data.selectedCoupon.couponId : null
      });

      wx.hideLoading();

      if (result.success) {
        wx.showToast({ title: '充电已开始', icon: 'success' });
        
        wx.redirectTo({
          url: `/pages/charging/charging?orderId=${result.orderId}`
        });
      } else {
        wx.showToast({ title: result.message || '启动失败', icon: 'none' });
      }
    } catch (err) {
      wx.hideLoading();
      console.error('Start charging error:', err);
      wx.showToast({ title: '启动失败，请重试', icon: 'none' });
    }
  }
});
