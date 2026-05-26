const app = getApp();

Page({
  data: {
    orderId: null,
    progress: null,
    socketTask: null,
    statusText: '充电中'
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.initWebSocket();
      this.loadProgress();
    }
  },

  initWebSocket() {
    const socketTask = wx.connectSocket({
      url: `ws://127.0.0.1:8083/ws/charging/${this.data.orderId}`,
      timeout: 5000
    });

    socketTask.onOpen(() => {
      console.log('WebSocket connected');
    });

    socketTask.onMessage((res) => {
      try {
        const progress = JSON.parse(res.data);
        this.setData({ 
          progress,
          statusText: progress.status === 2 ? '充电已完成' : '充电中'
        });
        
        wx.setNavigationBarTitle({
          title: progress.status === 2 ? '充电完成' : '充电中'
        });
        
        if (progress.status === 2) {
          this.showCompletionMessage();
        }
      } catch (e) {
        console.error('Parse progress error:', e);
      }
    });

    socketTask.onError((err) => {
      console.error('WebSocket error:', err);
      wx.showToast({
        title: '连接失败，请重试',
        icon: 'none'
      });
    });

    socketTask.onClose(() => {
      console.log('WebSocket closed');
    });

    this.setData({ socketTask });
  },

  loadProgress() {
    wx.request({
      url: `${app.globalData.baseUrl}/order/charging/progress`,
      method: 'GET',
      data: { orderId: this.data.orderId },
      success: (res) => {
        if (res.data.code === 200 && res.data.data) {
          this.setData({ progress: res.data.data });
        }
      },
      fail: (err) => {
        console.error('Load progress error:', err);
      }
    });
  },

  stopCharging() {
    wx.showModal({
      title: '确认停止',
      content: '确定要停止充电吗？',
      success: (res) => {
        if (res.confirm) {
          this.doStopCharging();
        }
      }
    });
  },

  doStopCharging() {
    wx.showLoading({ title: '停止中...' });
    
    wx.request({
      url: `${app.globalData.baseUrl}/order/charging/stop`,
      method: 'POST',
      data: {
        orderId: this.data.orderId,
        reason: '用户主动停止'
      },
      success: (res) => {
        wx.hideLoading();
        if (res.data.code === 200) {
          wx.showToast({ title: '已停止充电', icon: 'success' });
          
          if (this.data.socketTask) {
            this.data.socketTask.close();
          }
          
          setTimeout(() => {
            wx.redirectTo({
              url: `/pages/order/detail?orderId=${this.data.orderId}`
            });
          }, 1500);
        } else {
          wx.showToast({ title: res.data.msg || '停止失败', icon: 'none' });
        }
      },
      fail: (err) => {
        wx.hideLoading();
        wx.showToast({ title: '网络错误', icon: 'none' });
      }
    });
  },

  showCompletionMessage() {
    wx.showModal({
      title: '充电完成',
      content: '您的车辆已充满，请及时挪车',
      showCancel: false,
      success: () => {
        wx.redirectTo({
          url: `/pages/order/detail?orderId=${this.data.orderId}`
        });
      }
    });
  },

  formatTime(minutes) {
    const h = Math.floor(minutes / 60);
    const m = minutes % 60;
    if (h > 0) {
      return `${h}小时${m}分`;
    }
    return `${m}分钟`;
  },

  formatTime2(timeStr) {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    const h = date.getHours().toString().padStart(2, '0');
    const m = date.getMinutes().toString().padStart(2, '0');
    return `${h}:${m}`;
  },

  onUnload() {
    if (this.data.socketTask) {
      this.data.socketTask.close();
    }
  }
});
