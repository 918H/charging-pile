package com.charging.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.coupon.common.Result;
import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;
import com.charging.coupon.service.CouponService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    @Resource
    private CouponService couponService;

    @GetMapping("/page")
    public Result<Page<Coupon>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<Coupon> page = couponService.getCouponPage(current, size);
        return Result.success(page);
    }

    @GetMapping("/available")
    public Result<List<Coupon>> available() {
        List<Coupon> list = couponService.getAvailableCoupons();
        return Result.success(list);
    }

    @GetMapping("/{couponId}")
    public Result<Coupon> detail(@PathVariable Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null) {
            return Result.error("优惠券不存在");
        }
        return Result.success(coupon);
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody Coupon coupon) {
        boolean success = couponService.saveCoupon(coupon);
        return Result.success(success);
    }

    @PutMapping("/{couponId}")
    public Result<Boolean> update(@PathVariable Long couponId, @RequestBody Coupon coupon) {
        coupon.setCouponId(couponId);
        boolean success = couponService.updateCoupon(coupon);
        return Result.success(success);
    }

    @DeleteMapping("/{couponId}")
    public Result<Boolean> delete(@PathVariable Long couponId) {
        boolean success = couponService.deleteCoupon(couponId);
        return Result.success(success);
    }

    @PostMapping("/receive")
    public Result<Boolean> receive(
            @RequestParam Long userId,
            @RequestParam Long couponId
    ) {
        boolean success = couponService.receiveCoupon(userId, couponId);
        return success ? Result.success(true) : Result.error("领取失败");
    }

    @GetMapping("/user/list")
    public Result<List<UserCoupon>> userCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<UserCoupon> list = couponService.getUserCoupons(userId, status);
        return Result.success(list);
    }

    @PostMapping("/use")
    public Result<Boolean> use(
            @RequestParam Long userCouponId,
            @RequestParam Long orderId
    ) {
        boolean success = couponService.useCoupon(userCouponId, orderId);
        return success ? Result.success(true) : Result.error("使用失败");
    }

    @PostMapping("/return")
    public Result<Boolean> returnCoupon(@RequestParam Long userCouponId) {
        boolean success = couponService.returnCoupon(userCouponId);
        return success ? Result.success(true) : Result.error("退回失败");
    }
}
