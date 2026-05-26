const app = getApp();

Page({
  data: {
    pileId: null,
    slotId: null,
    faultTypes: ['无法启动充电', '充电中断', '计费异常', '设备损坏', '屏幕故障', '急停按钮按下', '网络故障', '其他'],
    faultTypeIndex: 0,
    pileCode: '',
    description: '',
    contactPhone: ''
  },

  onLoad(options) {
    if (options.pileId) {
      this.setData({ pileId: options.pileId });
    }
    if (options.slotId) {
      this.setData({ slotId: options.slotId });
    }
  },

  onFaultTypeChange(e) {
    this.setData({
      faultTypeIndex: e.detail.value
    });
  },

  onPileCodeInput(e) {
    this.setData({
      pileCode: e.detail.value
    });
  },

  onDescInput(e) {
    this.setData({
      description: e.detail.value
    });
  },

  onPhoneInput(e) {
    this.setData({
      contactPhone: e.detail.value
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

  async submitReport() {
    if (!this.data.description.trim()) {
      wx.showToast({ title: '请填写故障描述', icon: 'none' });
      return;
    }

    if (!this.data.contactPhone.trim()) {
      wx.showToast({ title: '请填写联系电话', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      const chargingApi = require('../../api/charging');
      
      await chargingApi.reportFault({
        pileId: this.data.pileId,
        slotId: this.data.slotId || 1,
        userId: app.globalData.userId,
        faultType: this.data.faultTypes[this.data.faultTypeIndex],
        description: this.data.description.trim(),
        images: '',
        contactPhone: this.data.contactPhone.trim()
      });

      wx.hideLoading();
      wx.showToast({ title: '上报成功', icon: 'success' });

      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      wx.hideLoading();
      console.error('Submit fault report error:', err);
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    }
  }
});
