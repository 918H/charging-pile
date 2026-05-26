package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.dto.WithdrawalRequest;
import com.charging.user.entity.PileWithdrawal;
import com.charging.user.service.PileWithdrawalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Api(tags = "私桩提现")
@RestController
@RequestMapping("/pile-withdrawal")
public class PileWithdrawalController {

    @Resource
    private PileWithdrawalService pileWithdrawalService;

    @PostMapping("/create")
    @ApiOperation("申请提现")
    public Result<PileWithdrawal> createWithdrawal(
            @RequestParam Long userId,
            @RequestBody WithdrawalRequest request
    ) {
        try {
            PileWithdrawal withdrawal = pileWithdrawalService.createWithdrawal(userId, request);
            return Result.success(withdrawal);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户提现记录")
    public Result<List<PileWithdrawal>> getUserWithdrawals(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<PileWithdrawal> withdrawals = pileWithdrawalService.getUserWithdrawals(userId, status);
        return Result.success(withdrawals);
    }

    @GetMapping("/user/stats")
    @ApiOperation("获取提现统计")
    public Result<Map<String, Object>> getWithdrawalStats(@RequestParam Long userId) {
        Map<String, Object> stats = pileWithdrawalService.getWithdrawalStats(userId);
        return Result.success(stats);
    }

    @PostMapping("/approve")
    @ApiOperation("审核通过（后台管理）")
    public Result<Boolean> approveWithdrawal(
            @RequestParam Long withdrawalId,
            @RequestParam String transactionId
    ) {
        try {
            pileWithdrawalService.approveWithdrawal(withdrawalId, transactionId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reject")
    @ApiOperation("审核拒绝（后台管理）")
    public Result<Boolean> rejectWithdrawal(
            @RequestParam Long withdrawalId,
            @RequestParam String reason
    ) {
        try {
            pileWithdrawalService.rejectWithdrawal(withdrawalId, reason);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
