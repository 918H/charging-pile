package com.charging.payment.controller;

import com.charging.common.core.response.R;
import com.charging.payment.dto.RefundRequest;
import com.charging.payment.entity.PaymentRefund;
import com.charging.payment.service.PaymentRefundService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "退款管理")
@RestController
@RequestMapping("/payment/refund")
public class PaymentRefundController {

    @Resource
    private PaymentRefundService paymentRefundService;

    @PostMapping("/apply")
    @ApiOperation("申请退款")
    public Result<String> applyRefund(@RequestBody RefundRequest request) {
        String refundNumber = paymentRefundService.applyRefund(request);
        if (refundNumber != null) {
            return R.ok(refundNumber);
        } else {
            return R.fail("退款申请失败，订单不存在或未支付");
        }
    }

    @PostMapping("/{refundId}/approve")
    @ApiOperation("审核通过退款")
    public Result<Boolean> approveRefund(
            @PathVariable Long refundId,
            @RequestParam Long auditorId
    ) {
        boolean success = paymentRefundService.approveRefund(refundId, auditorId);
        return success ? R.ok() : R.fail("审核失败");
    }

    @PostMapping("/{refundId}/reject")
    @ApiOperation("拒绝退款")
    public Result<Boolean> rejectRefund(
            @PathVariable Long refundId,
            @RequestParam Long auditorId,
            @RequestParam String reason
    ) {
        boolean success = paymentRefundService.rejectRefund(refundId, auditorId, reason);
        return success ? R.ok() : R.fail("操作失败");
    }

    @GetMapping("/{refundId}")
    @ApiOperation("获取退款详情")
    public Result<PaymentRefund> getRefundDetail(@PathVariable Long refundId) {
        PaymentRefund refund = paymentRefundService.getRefundDetail(refundId);
        if (refund == null) {
            return R.fail("未找到退款记录");
        }
        return R.ok(refund);
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户退款列表")
    public Result<List<PaymentRefund>> getUserRefunds(@RequestParam Long userId) {
        List<PaymentRefund> refunds = paymentRefundService.getUserRefunds(userId);
        return R.ok(refunds);
    }

    @GetMapping("/pending")
    @ApiOperation("获取待审核退款")
    public Result<List<PaymentRefund>> getPendingRefunds() {
        List<PaymentRefund> refunds = paymentRefundService.getPendingRefunds();
        return R.ok(refunds);
    }

    @PostMapping("/{refundId}/process")
    @ApiOperation("执行退款打款")
    public Result<Boolean> processRefund(
            @PathVariable Long refundId,
            @RequestParam String transactionId
    ) {
        boolean success = paymentRefundService.processRefundPayment(refundId, transactionId);
        return success ? R.ok() : R.fail("打款失败");
    }
}
