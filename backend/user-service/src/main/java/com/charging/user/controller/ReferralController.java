package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.entity.ReferralRelation;
import com.charging.user.service.ReferralService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "推荐管理")
@RestController
@RequestMapping("/referral")
public class ReferralController {

    @Resource
    private ReferralService referralService;

    @PostMapping("/code/generate")
    @ApiOperation("生成推荐码")
    public Result<String> generateCode(@RequestParam Long userId) {
        String code = referralService.generateReferralCode(userId);
        return Result.success(code);
    }

    @GetMapping("/relation")
    @ApiOperation("获取推荐关系")
    public Result<ReferralRelation> getRelation(@RequestParam Long userId) {
        ReferralRelation relation = referralService.getReferralRelation(userId);
        return Result.success(relation);
    }

    @PostMapping("/bind")
    @ApiOperation("绑定推荐人")
    public Result<Boolean> bindReferral(
            @RequestParam Long refereeId,
            @RequestParam String referralCode
    ) {
        try {
            referralService.bindReferral(refereeId, referralCode);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reward")
    @ApiOperation("领取推荐奖励")
    public Result<Boolean> rewardReferral(@RequestParam Long userId) {
        try {
            referralService.rewardReferral(userId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/info")
    @ApiOperation("获取推荐信息")
    public Result<Map<String, Object>> getReferralInfo(@RequestParam Long userId) {
        ReferralRelation relation = referralService.getReferralRelation(userId);
        
        Map<String, Object> info = new HashMap<>();
        if (relation != null) {
            info.put("referralCode", relation.getReferralCode());
            info.put("rewardPoints", relation.getRewardPoints());
            info.put("status", relation.getStatus());
        } else {
            String code = referralService.generateReferralCode(userId);
            info.put("referralCode", code);
            info.put("rewardPoints", 0);
            info.put("status", 0);
        }
        
        info.put("referrerReward", 500);
        info.put("refereeReward", 100);
        
        return Result.success(info);
    }
}
