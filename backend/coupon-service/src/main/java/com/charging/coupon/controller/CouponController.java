package com.charging.coupon.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.core.response.R;
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
        return R.ok(page);
    }

    @GetMapping("/available")
    public Result<List<Coupon>> available() {
        List<Coupon> list = couponService.getAvailableCoupons();
        return R.ok(list);
    }

    @GetMapping("/{couponId}")
    public Result<Coupon> detail(@PathVariable Long couponId) {
        Coupon coupon = couponService.getCouponById(couponId);
        if (coupon == null) {
            return R.fail("优惠券不存在");
        }
        return R.ok(coupon);
    }

    @PostMapping
    public Result<Boolean> add(@RequestBody Coupon coupon) {
        boolean success = couponService.saveCoupon(coupon);
        return R.ok(success);
    }

    @PutMapping("/{couponId}")
    public Result<Boolean> update(@PathVariable Long couponId, @RequestBody Coupon coupon) {
        coupon.setCouponId(couponId);
        boolean success = couponService.updateCoupon(coupon);
        return R.ok(success);
    }

    @DeleteMapping("/{couponId}")
    public Result<Boolean> delete(@PathVariable Long couponId) {
        boolean success = couponService.deleteCoupon(couponId);
        return R.ok(success);
    }

    @PostMapping("/receive")
    public Result<Boolean> receive(
            @RequestParam Long userId,
            @RequestParam Long couponId
    ) {
        boolean success = couponService.receiveCoupon(userId, couponId);
        return success ? R.ok(true) : R.fail("领取失败");
    }

    @GetMapping("/user/list")
    public Result<List<UserCoupon>> userCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<UserCoupon> list = couponService.getUserCoupons(userId, status);
        return R.ok(list);
    }

    @PostMapping("/use")
    public Result<Boolean> use(
            @RequestParam Long userCouponId,
            @RequestParam Long orderId
    ) {
        boolean success = couponService.useCoupon(userCouponId, orderId);
        return success ? R.ok(true) : R.fail("使用失败");
    }

    @PostMapping("/return")
    public Result<Boolean> returnCoupon(@RequestParam Long userCouponId) {
        boolean success = couponService.returnCoupon(userCouponId);
        return success ? R.ok(true) : R.fail("退回失败");
    }
}
