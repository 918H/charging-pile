package com.charging.order.controller;

import com.charging.order.common.Result;
import com.charging.order.dto.ReviewRequest;
import com.charging.order.dto.ReviewResponse;
import com.charging.order.service.ReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "评价管理")
@RestController
@RequestMapping("/review")
public class ReviewController {

    @Resource
    private ReviewService reviewService;

    @PostMapping("/create")
    @ApiOperation("创建评价")
    public Result<Boolean> createReview(@RequestBody ReviewRequest request) {
        boolean success = reviewService.createReview(request);
        return success ? Result.success() : Result.error("评价失败，订单不存在或已评价");
    }

    @GetMapping("/pile/list")
    @ApiOperation("获取充电桩评价列表")
    public Result<List<ReviewResponse>> getPileReviews(
            @RequestParam Long pileId,
            @RequestParam(required = false) Integer limit
    ) {
        List<ReviewResponse> reviews = reviewService.getPileReviews(pileId, limit);
        return Result.success(reviews);
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户评价列表")
    public Result<List<ReviewResponse>> getUserReviews(@RequestParam Long userId) {
        List<ReviewResponse> reviews = reviewService.getUserReviews(userId);
        return Result.success(reviews);
    }

    @GetMapping("/pile/rating")
    @ApiOperation("获取充电桩评分")
    public Result<Map<String, Object>> getPileRating(@RequestParam Long pileId) {
        Map<String, Object> rating = reviewService.getPileRating(pileId);
        return Result.success(rating);
    }

    @GetMapping("/can-review")
    @ApiOperation("检查是否可以评价")
    public Result<Boolean> canReview(
            @RequestParam Long userId,
            @RequestParam Long orderId
    ) {
        boolean canReview = reviewService.canReview(userId, orderId);
        return Result.success(canReview);
    }
}
