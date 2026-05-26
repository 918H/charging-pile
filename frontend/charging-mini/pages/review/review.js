const app = getApp();

Page({
  data: {
    orderId: null,
    pileId: null,
    orderInfo: {},
    rating: 0,
    ratingText: '',
    tags: ['充电速度快', '环境舒适', '停车方便', '价格实惠', '服务好', '设备新', '位置好找'],
    selectedTags: [],
    comment: ''
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadOrderInfo();
    }
  },

  async loadOrderInfo() {
    try {
      const orderApi = require('../../api/order');
      const order = await orderApi.getOrderDetail(this.data.orderId);
      
      this.setData({
        pileId: order.pileId,
        orderInfo: {
          pileName: order.pileName || '充电桩',
          powerConsumed: order.powerConsumed || 0,
          durationMinutes: order.durationMinutes || 0
        }
      });
    } catch (err) {
      console.error('Load order info error:', err);
    }
  },

  selectRating(e) {
    const rating = e.currentTarget.dataset.rating;
    const ratingTexts = ['', '非常不满意', '不满意', '一般', '满意', '非常满意'];
    
    this.setData({
      rating: rating,
      ratingText: ratingTexts[rating]
    });
  },

  selectTag(e) {
    const tag = e.currentTarget.dataset.tag;
    let { selectedTags } = this.data;
    
    if (selectedTags.includes(tag)) {
      selectedTags = selectedTags.filter(t => t !== tag);
    } else {
      selectedTags.push(tag);
    }
    
    this.setData({ selectedTags });
  },

  onCommentInput(e) {
    this.setData({
      comment: e.detail.value
    });
  },

  async submitReview() {
    if (this.data.rating === 0) {
      wx.showToast({ title: '请评分', icon: 'none' });
      return;
    }

    wx.showLoading({ title: '提交中...' });

    try {
      const orderApi = require('../../api/order');
      
      const content = this.data.selectedTags.length > 0 
        ? `[${this.data.selectedTags.join('] [')}] ${this.data.comment}`
        : this.data.comment;
      
      await orderApi.createReview({
        userId: app.globalData.userId,
        orderId: this.data.orderId,
        pileId: this.data.pileId,
        rating: this.data.rating,
        content: content.trim(),
        images: ''
      });

      wx.hideLoading();
      wx.showToast({ title: '评价成功', icon: 'success' });

      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (err) {
      wx.hideLoading();
      console.error('Submit review error:', err);
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    }
  }
});
