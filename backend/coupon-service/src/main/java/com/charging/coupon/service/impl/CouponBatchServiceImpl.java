package com.charging.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;
import com.charging.coupon.mapper.CouponMapper;
import com.charging.coupon.mapper.UserCouponMapper;
import com.charging.coupon.service.CouponBatchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponBatchServiceImpl implements CouponBatchService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    public void createBatchCoupons(Long templateId, int count) {
        Coupon template = couponMapper.selectById(templateId);
        if (template == null) {
            throw new RuntimeException("优惠券模板不存在");
        }

        for (int i = 0; i < count; i++) {
            Coupon coupon = new Coupon();
            coupon.setCouponType(template.getCouponType());
            coupon.setName(template.getName() + "_" + System.currentTimeMillis());
            coupon.setAmount(template.getAmount());
            coupon.setDiscount(template.getDiscount());
            coupon.setThreshold(template.getThreshold());
            coupon.setStartTime(template.getStartTime());
            coupon.setEndTime(template.getEndTime());
            coupon.setTotalCount(1);
            coupon.setIsBatch(true);
            coupon.setBatchId(template.getId());
            coupon.setCreatedAt(LocalDateTime.now());
            coupon.setUpdatedAt(LocalDateTime.now());
            
            couponMapper.insert(coupon);
        }
    }

    @Override
    public List<Coupon> getBatchCoupons(Long templateId) {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getBatchId, templateId);
        return couponMapper.selectList(wrapper);
    }

    @Override
    public void distributeToUser(Long couponId, Long userId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 0) {
            throw new RuntimeException("优惠券不可用");
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setCouponId(couponId);
        userCoupon.setUserId(userId);
        userCoupon.setStatus(0);
        userCoupon.setExpiredTime(coupon.getEndTime());
        userCoupon.setCreatedAt(LocalDateTime.now());
        userCoupon.setUpdatedAt(LocalDateTime.now());
        
        userCouponMapper.insert(userCoupon);
    }

    @Override
    public void distributeToAllUsers(Long couponId) {
    }

    @Override
    public List<UserCoupon> getUserCouponList(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        return userCouponMapper.selectList(wrapper);
    }
}
