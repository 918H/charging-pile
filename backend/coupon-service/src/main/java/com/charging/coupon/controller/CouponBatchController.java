package com.charging.coupon.controller;

import com.charging.coupon.common.Result;
import com.charging.coupon.entity.Coupon;
import com.charging.coupon.entity.UserCoupon;
import com.charging.coupon.service.CouponBatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "优惠券批量管理")
@RestController
@RequestMapping("/coupon/batch")
public class CouponBatchController {

    @Resource
    private CouponBatchService couponBatchService;

    @PostMapping("/create")
    @ApiOperation("批量生成优惠券")
    public Result<Boolean> createBatch(
            @RequestParam Long templateId,
            @RequestParam int count
    ) {
        try {
            couponBatchService.createBatchCoupons(templateId, count);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    @ApiOperation("获取批次优惠券列表")
    public Result<List<Coupon>> getBatchList(@RequestParam Long templateId) {
        List<Coupon> coupons = couponBatchService.getBatchCoupons(templateId);
        return Result.success(coupons);
    }

    @PostMapping("/distribute")
    @ApiOperation("发放给用户")
    public Result<Boolean> distribute(
            @RequestParam Long couponId,
            @RequestParam Long userId
    ) {
        try {
            couponBatchService.distributeToUser(couponId, userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户优惠券列表")
    public Result<List<UserCoupon>> getUserCoupons(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<UserCoupon> list = couponBatchService.getUserCouponList(userId, status);
        return Result.success(list);
    }
}
