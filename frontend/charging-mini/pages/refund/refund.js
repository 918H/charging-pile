const app = getApp();

Page({
  data: {
    paymentId: null,
    orderNumber: null,
    orderInfo: {},
    paymentMethodText: ['未知', '微信支付', '支付宝', '银行卡'],
    refundAmount: '',
    maxRefundAmount: 0,
    refundReasons: ['充错电桩', '充电失败', '计费异常', '重复支付', '其他原因'],
    refundReasonIndex: 0,
    otherReason: ''
  },

  onLoad(options) {
    if (options.paymentId) {
      this.setData({ paymentId: options.paymentId });
      this.loadPaymentInfo();
    } else if (options.orderNumber) {
      this.setData({ orderNumber: options.orderNumber });
      this.loadPaymentInfo();
    }
  },

  async loadPaymentInfo() {
    try {
      const paymentApi = require('../../api/payment');
      
      let payment;
      if (this.data.paymentId) {
        payment = await paymentApi.getPaymentDetail(this.data.paymentId);
      } else {
        payment = await paymentApi.getPaymentByOrder(this.data.orderNumber);
      }
      
      this.setData({
        orderInfo: {
          orderNumber: payment.orderNumber,
          amount: payment.amount,
          paymentTime: this.formatDateTime(payment.paymentTime),
          paymentMethod: payment.paymentMethod
        },
        maxRefundAmount: payment.amount
      });
    } catch (err) {
      console.error('Load payment info error:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  onRefundAmountInput(e) {
    const value = e.detail.value;
    const maxAmount = this.data.maxRefundAmount;
    
    if (value && parseFloat(value) > maxAmount) {
      wx.showToast({ title: '退款金额不能超过支付金额', icon: 'none' });
      return;
    }
    
    this.setData({ refundAmount: value });
  },

  onRefundReasonChange(e) {
    this.setData({
      refundReasonIndex: e.detail.value
    });
  },

  onOtherReasonInput(e) {
    this.setData({
      otherReason: e.detail.value
    });
  },

  chooseImage() {
    wx.chooseMedia({
      count: 3,
      mediaType: ['image'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        wx.showToast({
          title: `已选择 ${res.tempFiles.length} 张图片`,
          icon: 'none'
        });
      }
    });
  },

  async submitRefund() {
    if (!this.data.refundAmount || parseFloat(this.data.refundAmount) <= 0) {
      wx.showToast({ title: '请输入退款金额', icon: 'none' });
      return;
    }

    if (parseFloat(this.data.refundAmount) > this.data.maxRefundAmount) {
      wx.showToast({ title: '退款金额不能超过支付金额', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      const paymentApi = require('../../api/payment');
      
      const reason = this.data.refundReasonIndex === 4
        ? this.data.otherReason || this.data.refundReasons[this.data.refundReasonIndex]
        : this.data.refundReasons[this.data.refundReasonIndex];
      
      await paymentApi.applyRefund({
        paymentId: this.data.paymentId,
        orderNumber: this.data.orderNumber,
        userId: app.globalData.userId,
        refundAmount: parseFloat(this.data.refundAmount),
        refundType: this.data.refundReasonIndex,
        refundReason: reason,
        images: ''
      });

      wx.hideLoading();
      wx.showToast({ title: '申请已提交', icon: 'success' });

      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      wx.hideLoading();
      console.error('Submit refund error:', err);
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    }
  },

  formatDateTime(datetime) {
    if (!datetime) return '';
    const date = new Date(datetime);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    return `${month}-${day} ${hour}:${minute}`;
  }
});
