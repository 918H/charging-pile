Page({
  data: {
    latitude: 22.5428,
    longitude: 114.0543,
    locationName: '',
    markers: [],
    piles: []
  },

  onLoad() {
    this.loadNearbyPiles();
  },

  async chooseLocation() {
    try {
      const res = await wx.chooseLocation();
      this.setData({
        latitude: res.latitude,
        longitude: res.longitude,
        locationName: res.name
      });
      this.loadNearbyPiles();
    } catch (err) {
      console.error('选择位置失败', err);
    }
  },

  async loadNearbyPiles() {
    try {
      const chargingApi = require('../../api/charging');
      const piles = await chargingApi.getNearbyPiles(
        this.data.latitude,
        this.data.longitude,
        10
      );

      const markers = piles.map((pile, index) => ({
        id: pile.pileId,
        latitude: pile.latitude,
        longitude: pile.longitude,
        iconPath: '/images/pile-icon.png',
        width: 30,
        height: 30,
        callout: {
          content: pile.pileName,
          display: 'ALWAYS'
        }
      }));

      this.setData({ piles, markers });
    } catch (err) {
      console.error('加载充电桩失败', err);
    }
  },

  onMarkerTap(e) {
    const markerId = e.detail.markerId;
    const pile = this.data.piles.find(p => p.pileId === markerId);
    if (pile) {
      this.onPileTap({ currentTarget: { dataset: { item: pile } } });
    }
  },

  onPileTap(e) {
    const pile = e.currentTarget.dataset.item;
    wx.navigateTo({
      url: `/pages/pile-detail/pile-detail?pileId=${pile.pileId}`
    });
  }
});
