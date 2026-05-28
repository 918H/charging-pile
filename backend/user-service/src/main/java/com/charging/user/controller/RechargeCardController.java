package com.charging.user.controller;

import com.charging.common.core.response.R;
import com.charging.user.dto.RechargeRequest;
import com.charging.user.entity.RechargeRecord;
import com.charging.user.entity.UserRechargeCard;
import com.charging.user.service.RechargeCardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "储值卡管理")
@RestController
@RequestMapping("/recharge")
public class RechargeCardController {

    @Resource
    private RechargeCardService rechargeCardService;

    @GetMapping("/card")
    @ApiOperation("获取用户储值卡")
    public Result<UserRechargeCard> getCard(@RequestParam Long userId) {
        UserRechargeCard card = rechargeCardService.getOrCreateCard(userId);
        return R.ok(card);
    }

    @GetMapping("/records")
    @ApiOperation("获取充值记录")
    public Result<List<RechargeRecord>> getRecords(@RequestParam Long userId) {
        List<RechargeRecord> records = rechargeCardService.getRechargeRecords(userId);
        return R.ok(records);
    }

    @PostMapping("/create")
    @ApiOperation("创建充值")
    public Result<String> recharge(
            @RequestParam Long userId,
            @RequestBody RechargeRequest request
    ) {
        try {
            String recordId = rechargeCardService.recharge(userId, request);
            return R.ok(recordId);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @PostMapping("/pay")
    @ApiOperation("储值卡支付")
    public Result<Boolean> payByCard(
            @RequestParam Long userId,
            @RequestParam BigDecimal amount,
            @RequestParam String orderNumber
    ) {
        boolean success = rechargeCardService.payByCard(userId, amount, orderNumber);
        return R.ok(success);
    }
}
