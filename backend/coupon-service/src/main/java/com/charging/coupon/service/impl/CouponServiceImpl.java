package com.charging.coupon.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;
import com.charging.coupon.mapper.CouponMapper;
import com.charging.coupon.mapper.UserCouponMapper;
import com.charging.coupon.service.CouponService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponServiceImpl implements CouponService {

    @Resource
    private CouponMapper couponMapper;

    @Resource
    private UserCouponMapper userCouponMapper;

    @Override
    public Page<Coupon> getCouponPage(int current, int size) {
        Page<Coupon> page = new Page<>(current, size);
        return couponMapper.selectPage(page, null);
    }

    @Override
    public List<Coupon> getAvailableCoupons() {
        LambdaQueryWrapper<Coupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Coupon::getStatus, 1)
               .lt(Coupon::getIssuedCount, Coupon::getTotalCount)
               .ge(LocalDateTime::now, Coupon::getStartTime)
               .le(LocalDateTime::now, Coupon::getEndTime);
        return couponMapper.selectList(wrapper);
    }

    @Override
    public Coupon getCouponById(Long couponId) {
        return couponMapper.selectById(couponId);
    }

    @Override
    public boolean saveCoupon(Coupon coupon) {
        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());
        return couponMapper.insert(coupon) > 0;
    }

    @Override
    public boolean updateCoupon(Coupon coupon) {
        coupon.setUpdatedAt(LocalDateTime.now());
        return couponMapper.updateById(coupon) > 0;
    }

    @Override
    public boolean deleteCoupon(Long couponId) {
        return couponMapper.deleteById(couponId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean issueCoupon(Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getIssuedCount() >= coupon.getTotalCount()) {
            return false;
        }
        coupon.setIssuedCount(coupon.getIssuedCount() + 1);
        return couponMapper.updateById(coupon) > 0;
    }

    @Override
    public List<UserCoupon> getUserCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        wrapper.orderByDesc(UserCoupon::getCreatedAt);
        return userCouponMapper.selectList(wrapper);
    }

    @Override
    public UserCoupon getUserCouponByCode(String couponCode) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserCoupon::getCouponCode, couponCode)
               .last("LIMIT 1");
        return userCouponMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean receiveCoupon(Long userId, Long couponId) {
        Coupon coupon = couponMapper.selectById(couponId);
        if (coupon == null || coupon.getStatus() != 1) {
            return false;
        }
        
        if (coupon.getIssuedCount() >= coupon.getTotalCount()) {
            return false;
        }

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setCouponCode(generateCouponCode(userId, couponId));
        userCoupon.setStatus(0);
        userCoupon.setCreatedAt(LocalDateTime.now());
        userCoupon.setUpdatedAt(LocalDateTime.now());

        int result = userCouponMapper.insert(userCoupon);
        if (result > 0) {
            issueCoupon(couponId);
        }
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean useCoupon(Long userCouponId, Long orderId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || userCoupon.getStatus() != 0) {
            return false;
        }

        Coupon coupon = couponMapper.selectById(userCoupon.getCouponId());
        if (coupon == null) {
            return false;
        }

        userCoupon.setStatus(1);
        userCoupon.setOrderId(orderId);
        userCoupon.setUsedTime(LocalDateTime.now());
        userCoupon.setUpdatedAt(LocalDateTime.now());

        return userCouponMapper.updateById(userCoupon) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean returnCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || userCoupon.getStatus() != 1) {
            return false;
        }

        userCoupon.setStatus(0);
        userCoupon.setOrderId(null);
        userCoupon.setUsedTime(null);
        userCoupon.setUpdatedAt(LocalDateTime.now());

        return userCouponMapper.updateById(userCoupon) > 0;
    }

    private String generateCouponCode(Long userId, Long couponId) {
        return "CPN" + System.currentTimeMillis() + IdUtil.getSnowflakeNextIdStr();
    }
}
