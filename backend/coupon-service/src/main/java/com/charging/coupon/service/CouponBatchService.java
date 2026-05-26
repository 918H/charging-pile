package com.charging.coupon.service;

import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;

import java.util.List;

public interface CouponBatchService {
    void createBatchCoupons(Long templateId, int count);
    List<Coupon> getBatchCoupons(Long templateId);
    void distributeToUser(Long couponId, Long userId);
    void distributeToAllUsers(Long couponId);
    List<UserCoupon> getUserCouponList(Long userId, Integer status);
}
