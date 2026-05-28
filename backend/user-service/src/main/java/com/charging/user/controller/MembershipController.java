package com.charging.user.controller;

import com.charging.common.core.response.R;
import com.charging.user.dto.MembershipDTO;
import com.charging.user.dto.MembershipDiscountDTO;
import com.charging.user.entity.UserMembership;
import com.charging.user.service.MembershipService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
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
        return R.ok(levels);
    }

    @GetMapping("/user/current")
    @ApiOperation("获取用户当前会员")
    public Result<UserMembership> getCurrentMembership(@RequestParam Long userId) {
        UserMembership membership = membershipService.getUserMembership(userId);
        return R.ok(membership);
    }

    @GetMapping("/user/discount")
    @ApiOperation("计算会员折扣")
    public Result<MembershipDiscountDTO> calculateDiscount(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount
    ) {
        MembershipDiscountDTO dto = new MembershipDiscountDTO();
        dto.setUserId(userId);
        dto.setOriginalAmount(amount);
        
        UserMembership membership = membershipService.getUserMembership(userId);
        if (membership != null) {
            dto.setLevelCode(membership.getLevelCode());
            
            List<MembershipDTO> levels = membershipService.getMembershipLevels();
            for (MembershipDTO level : levels) {
                if (level.getLevelCode().equals(membership.getLevelCode())) {
                    dto.setLevelName(level.getLevelName());
                    dto.setDiscountRate(level.getDiscountRate());
                    break;
                }
            }
            
            BigDecimal discountAmount = membershipService.calculateDiscount(userId, amount);
            dto.setDiscountAmount(discountAmount);
            dto.setFinalAmount(amount.subtract(discountAmount));
        } else {
            dto.setLevelCode(0);
            dto.setLevelName("普通会员");
            dto.setDiscountRate(BigDecimal.ONE);
            dto.setDiscountAmount(BigDecimal.ZERO);
            dto.setFinalAmount(amount);
        }
        
        return R.ok(dto);
    }

    @PostMapping("/user/upgrade")
    @ApiOperation("升级会员")
    public Result<Boolean> upgradeMembership(
            @RequestParam Long userId,
            @RequestParam Long levelId
    ) {
        try {
            membershipService.upgradeMembership(userId, levelId);
            return R.ok();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/user/check")
    @ApiOperation("检查并更新会员等级")
    public Result<Boolean> checkMembership(@RequestParam Long userId) {
        membershipService.checkAndUpdateMembership(userId);
        return R.ok();
    }
}
