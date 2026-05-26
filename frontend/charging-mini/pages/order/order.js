const app = getApp();

Page({
  data: {
    currentTab: 0,
    orders: [],
    reservations: []
  },

  onShow() {
    const options = this.getQueryOptions();
    if (options.tab === 'reservation') {
      this.setData({ currentTab: 1 });
    }
    this.loadData();
  },

  getQueryOptions() {
    const pages = getCurrentPages();
    const currentPage = pages[pages.length - 1];
    return currentPage.options || {};
  },

  async loadData() {
    await Promise.all([
      this.loadOrders(),
      this.loadReservations()
    ]);
  },

  async loadOrders() {
    try {
      const orderApi = require('../../api/order');
      const result = await orderApi.getOrderList({ current: 1, size: 20 });
      
      const orders = (result.records || []).map(order => ({
        ...order,
        statusText: this.getStatusText(order.status)
      }));
      
      this.setData({ orders });
    } catch (err) {
      console.error('加载订单失败', err);
    }
  },

  async loadReservations() {
    try {
      const orderApi = require('../../api/order');
      const reservations = await orderApi.getUserReservations(app.globalData.userId, null);
      
      const reservationsWithText = (reservations || []).map(res => ({
        ...res,
        statusText: this.getReservationStatusText(res.status),
        startTime: this.formatDateTime(res.startTime)
      }));
      
      this.setData({ reservations });
    } catch (err) {
      console.error('加载预约失败', err);
    }
  },

  switchTab(e) {
    this.setData({
      currentTab: parseInt(e.currentTarget.dataset.tab)
    });
  },

  getStatusText(status) {
    const texts = {
      0: '待支付',
      1: '充电中',
      2: '已完成',
      3: '已取消'
    };
    return texts[status] || '未知';
  },

  getReservationStatusText(status) {
    const texts = {
      0: '待使用',
      1: '已取消',
      2: '已完成'
    };
    return texts[status] || '未知';
  },

  formatDateTime(datetime) {
    if (!datetime) return '';
    const date = new Date(datetime);
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    const hour = date.getHours().toString().padStart(2, '0');
    const minute = date.getMinutes().toString().padStart(2, '0');
    return `${month}-${day} ${hour}:${minute}`;
  },

  async cancelOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const orderApi = require('../../api/order');
            await orderApi.cancelOrder(orderId);
            wx.showToast({ title: '取消成功', icon: 'success' });
            this.loadOrders();
          } catch (err) {
            wx.showToast({ title: '取消失败', icon: 'none' });
          }
        }
      }
    });
  },

  async cancelReservation(e) {
    const reservationId = e.currentTarget.dataset.id;
    
    wx.showModal({
      title: '确认取消',
      content: '确定要取消该预约吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const orderApi = require('../../api/order');
            await orderApi.cancelReservation(reservationId);
            wx.showToast({ title: '取消成功', icon: 'success' });
            this.loadReservations();
          } catch (err) {
            wx.showToast({ title: '取消失败', icon: 'none' });
          }
        }
      }
    });
  },

  payOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.showToast({ title: '跳转支付', icon: 'none' });
  },

  viewDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order/detail?orderId=${orderId}`
    });
  },

  viewReservationDetail(e) {
    const reservationId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/reservation/detail?reservationId=${reservationId}`
    });
  }
});
