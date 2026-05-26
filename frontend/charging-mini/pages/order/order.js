Page({
  data: {
    orders: []
  },

  onShow() {
    this.loadOrders();
  },

  async loadOrders() {
    try {
      const orderApi = require('../../api/order');
      const result = await orderApi.getOrderList({ current: 1, size: 20 });
      
      const orders = result.records.map(order => ({
        ...order,
        statusText: this.getStatusText(order.status)
      }));
      
      this.setData({ orders });
    } catch (err) {
      console.error('加载订单失败', err);
    }
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

  payOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.showToast({ title: '跳转支付', icon: 'none' });
  },

  viewDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order/detail?orderId=${orderId}`
    });
  }
});
