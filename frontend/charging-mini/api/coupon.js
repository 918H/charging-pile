const { request } = require('./request');

module.exports = {
  getAvailableCoupons() {
    return request({
      url: '/api/coupon/coupon/available',
      method: 'GET'
    });
  },

  receiveCoupon(userId, couponId) {
    return request({
      url: '/api/coupon/coupon/receive',
      method: 'POST',
      data: { userId, couponId }
    });
  },

  getUserCoupons(userId, status = null) {
    return request({
      url: '/api/coupon/coupon/user/list',
      method: 'GET',
      data: { userId, status }
    });
  },

  useCoupon(userCouponId, orderId) {
    return request({
      url: '/api/coupon/coupon/use',
      method: 'POST',
      data: { userCouponId, orderId }
    });
  }
};
