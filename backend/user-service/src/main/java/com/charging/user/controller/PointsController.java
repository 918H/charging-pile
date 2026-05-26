package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.entity.PointsRecord;
import com.charging.user.entity.UserPoints;
import com.charging.user.service.PointsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "积分管理")
@RestController
@RequestMapping("/points")
public class PointsController {

    @Resource
    private PointsService pointsService;

    @GetMapping("/user")
    @ApiOperation("获取用户积分")
    public Result<UserPoints> getUserPoints(@RequestParam Long userId) {
        UserPoints points = pointsService.getUserPoints(userId);
        return Result.success(points);
    }

    @GetMapping("/records")
    @ApiOperation("获取积分记录")
    public Result<List<PointsRecord>> getRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer type
    ) {
        List<PointsRecord> records = pointsService.getPointsRecords(userId, type);
        return Result.success(records);
    }

    @PostMapping("/add")
    @ApiOperation("增加积分")
    public Result<Boolean> addPoints(
            @RequestParam Long userId,
            @RequestParam Integer points,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String relatedOrder
    ) {
        try {
            pointsService.addPoints(userId, points, description, relatedOrder);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/consume")
    @ApiOperation("消费积分")
    public Result<Boolean> consumePoints(
            @RequestParam Long userId,
            @RequestParam Integer points,
            @RequestParam(required = false) String description
    ) {
        try {
            pointsService.consumePoints(userId, points, description);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
