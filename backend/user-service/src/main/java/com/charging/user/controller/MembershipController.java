package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.dto.MembershipDTO;
import com.charging.user.entity.UserMembership;
import com.charging.user.service.MembershipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "会员管理")
@RestController
@RequestMapping("/membership")
public class MembershipController {

    @Resource
    private MembershipService membershipService;

    @GetMapping("/levels")
    @ApiOperation("获取会员等级列表")
    public Result<List<MembershipDTO>> getLevels() {
        List<MembershipDTO> levels = membershipService.getMembershipLevels();
        return Result.success(levels);
    }

    @GetMapping("/user/current")
    @ApiOperation("获取用户当前会员")
    public Result<UserMembership> getCurrentMembership(@RequestParam Long userId) {
        UserMembership membership = membershipService.getUserMembership(userId);
        return Result.success(membership);
    }

    @GetMapping("/user/discount")
    @ApiOperation("计算会员折扣")
    public Result<Object> calculateDiscount(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount
    ) {
        BigDecimal discount = membershipService.calculateDiscount(userId, amount);
        return Result.success(discount);
    }

    @PostMapping("/user/upgrade")
    @ApiOperation("升级会员")
    public Result<Boolean> upgradeMembership(
            @RequestParam Long userId,
            @RequestParam Long levelId
    ) {
        try {
            membershipService.upgradeMembership(userId, levelId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/user/check")
    @ApiOperation("检查并更新会员等级")
    public Result<Boolean> checkMembership(@RequestParam Long userId) {
        membershipService.checkAndUpdateMembership(userId);
        return Result.success();
    }
}
