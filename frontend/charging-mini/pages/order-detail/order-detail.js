const app = getApp();

Page({
  data: {
    orderId: null,
    orderInfo: {},
    statusIcon: ''
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadOrderDetail();
    }
  },

  async loadOrderDetail() {
    try {
      const orderApi = require('../../api/order');
      const order = await orderApi.getOrderDetail(this.data.orderId);
      
      const statusIcons = {
        0: '⏳',
        1: '⚡',
        2: '✅',
        3: '❌'
      };
      
      const statusTexts = {
        0: '待支付',
        1: '充电中',
        2: '已完成',
        3: '已取消'
      };
      
      this.setData({
        orderInfo: {
          ...order,
          statusText: statusTexts[order.status] || '未知',
          electricityFee: order.electricityFee || order.totalAmount,
          serviceFee: order.serviceFee || 0,
          discountAmount: order.discountAmount || 0,
          finalAmount: order.finalAmount || order.totalAmount
        },
        statusIcon: statusIcons[order.status] || '❓'
      });
    } catch (err) {
      console.error('Load order detail error:', err);
      wx.showToast({ title: '加载失败', icon: 'none' });
    }
  },

  payOrder() {
    wx.showToast({ title: '跳转支付', icon: 'none' });
  },

  async cancelOrder() {
    wx.showModal({
      title: '确认取消',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const orderApi = require('../../api/order');
            await orderApi.cancelOrder(this.data.orderId);
            wx.showToast({ title: '取消成功', icon: 'success' });
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } catch (err) {
            wx.showToast({ title: '取消失败', icon: 'none' });
          }
        }
      }
    });
  },

  goToReview() {
    wx.navigateTo({
      url: `/pages/review/review?orderId=${this.data.orderId}`
    });
  },

  goToRefund() {
    wx.navigateTo({
      url: `/pages/refund/refund?paymentId=${this.data.orderId}`
    });
  },

  formatDateTime(datetime) {
    if (!datetime) return '-';
    const date = new Date(datetime);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    return `${month}-${day} ${hour}:${minute}`;
  }
});
