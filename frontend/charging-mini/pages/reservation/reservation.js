const app = getApp();
const orderApi = require('../../api/order');

Page({
  data: {
    today: '',
    selectedDate: '',
    timeSlots: [],
    selectedSlot: null,
    piles: [],
    selectedPile: null,
    durationMinutes: 60,
    estimatedFee: '0.00'
  },

  onLoad() {
    const today = new Date();
    const todayStr = today.toISOString().split('T')[0];
    this.setData({ today: todayStr });
    
    this.generateTimeSlots();
    this.loadNearbyPiles();
  },

  generateTimeSlots() {
    const slots = [];
    const startHour = 8;
    const endHour = 22;
    
    for (let hour = startHour; hour < endHour; hour++) {
      const startTime = `${hour.toString().padStart(2, '0')}:00`;
      const endTime = `${(hour + 1).toString().padStart(2, '0')}:00`;
      
      slots.push({
        startTime,
        endTime,
        available: Math.random() > 0.3
      });
    }
    
    this.setData({ timeSlots: slots });
  },

  async loadNearbyPiles() {
    try {
      const chargingApi = require('../../api/charging');
      const piles = await chargingApi.getNearbyPiles(22.5428, 114.0543, 10);
      
      const pilesWithInfo = piles.map(pile => ({
        ...pile,
        availableSlots: Math.floor(Math.random() * 5) + 1,
        totalSlots: 6,
        unitPrice: (Math.random() * 0.8 + 1.2).toFixed(2)
      }));
      
      this.setData({ piles: pilesWithInfo });
    } catch (err) {
      console.error('Load piles error:', err);
    }
  },

  onDateChange(e) {
    this.setData({ selectedDate: e.detail.value });
  },

  selectTimeSlot(e) {
    const index = e.currentTarget.dataset.index;
    const slot = this.data.timeSlots[index];
    
    if (!slot.available) {
      wx.showToast({ title: '该时段不可预约', icon: 'none' });
      return;
    }
    
    if (this.data.selectedSlot === index) {
      this.setData({ selectedSlot: null });
    } else {
      this.setData({ selectedSlot: index });
      this.calculateFee();
    }
  },

  selectPile(e) {
    const index = e.currentTarget.dataset.index;
    
    if (this.data.selectedPile === index) {
      this.setData({ selectedPile: null });
    } else {
      this.setData({ selectedPile: index });
      this.calculateFee();
    }
  },

  onDurationChange(e) {
    this.setData({ durationMinutes: e.detail.value });
    this.calculateFee();
  },

  calculateFee() {
    const { selectedPile, selectedSlot, durationMinutes, piles, timeSlots } = this.data;
    
    if (selectedPile === null || selectedSlot === null) {
      return;
    }
    
    const pile = piles[selectedPile];
    const slot = timeSlots[selectedSlot];
    
    const powerConsumed = durationMinutes / 60;
    const fee = (powerConsumed * parseFloat(pile.unitPrice)).toFixed(2);
    
    this.setData({ estimatedFee: fee });
  },

  async createReservation() {
    const { selectedDate, selectedSlot, selectedPile, durationMinutes, timeSlots, piles } = this.data;
    
    if (!selectedDate) {
      wx.showToast({ title: '请选择日期', icon: 'none' });
      return;
    }
    
    if (selectedSlot === null) {
      wx.showToast({ title: '请选择时段', icon: 'none' });
      return;
    }
    
    if (selectedPile === null) {
      wx.showToast({ title: '请选择充电桩', icon: 'none' });
      return;
    }
    
    wx.showLoading({ title: '提交中...' });
    
    try {
      const slot = timeSlots[selectedSlot];
      const pile = piles[selectedPile];
      
      const startTime = `${selectedDate}T${slot.startTime}:00`;
      
      const result = await orderApi.createReservation({
        userId: app.globalData.userId,
        pileId: pile.pileId,
        slotId: 1,
        startTime: startTime,
        durationMinutes: durationMinutes
      });
      
      wx.hideLoading();
      
      if (result.success) {
        wx.showToast({ title: '预约成功', icon: 'success' });
        
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/order/order?tab=reservation`
          });
        }, 1500);
      } else {
        wx.showToast({ title: result.message || '预约失败', icon: 'none' });
      }
    } catch (err) {
      wx.hideLoading();
      console.error('Create reservation error:', err);
      wx.showToast({ title: '预约失败，请重试', icon: 'none' });
    }
  }
});
