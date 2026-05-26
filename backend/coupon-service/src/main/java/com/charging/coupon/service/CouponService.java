package com.charging.coupon.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;

import java.util.List;

public interface CouponService {
    Page<Coupon> getCouponPage(int current, int size);
    List<Coupon> getAvailableCoupons();
    Coupon getCouponById(Long couponId);
    boolean saveCoupon(Coupon coupon);
    boolean updateCoupon(Coupon coupon);
    boolean deleteCoupon(Long couponId);
    boolean issueCoupon(Long couponId);
    
    List<UserCoupon> getUserCoupons(Long userId, Integer status);
    UserCoupon getUserCouponByCode(String couponCode);
    boolean receiveCoupon(Long userId, Long couponId);
    boolean useCoupon(Long userCouponId, Long orderId);
    boolean returnCoupon(Long userCouponId);
}
