package com.charging.payment.controller;

import com.charging.payment.common.Result;
import com.charging.payment.service.PaymentGatewayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Api(tags = "支付网关接口")
@RestController
@RequestMapping("/payment/gateway")
public class PaymentGatewayController {

    @Resource
    private PaymentGatewayService paymentGatewayService;

    @PostMapping("/create")
    @ApiOperation("创建支付订单")
    public Result<String> createOrder(
            @RequestParam String orderNumber,
            @RequestParam BigDecimal amount,
            @RequestParam String userId
    ) {
        try {
            String transactionId = paymentGatewayService.createOrder(orderNumber, amount, userId);
            return Result.success(transactionId);
        } catch (Exception e) {
            return Result.error("创建支付订单失败：" + e.getMessage());
        }
    }

    @PostMapping("/verify")
    @ApiOperation("验证支付结果")
    public Result<Boolean> verifyOrder(
            @RequestParam String orderNumber,
            @RequestParam String transactionId
    ) {
        try {
            boolean verified = paymentGatewayService.verifyOrder(orderNumber, transactionId);
            return Result.success(verified);
        } catch (Exception e) {
            return Result.error("验证支付失败：" + e.getMessage());
        }
    }

    @PostMapping("/refund")
    @ApiOperation("发起退款")
    public Result<Boolean> refundOrder(
            @RequestParam String orderNumber,
            @RequestParam String transactionId,
            @RequestParam BigDecimal amount
    ) {
        try {
            boolean success = paymentGatewayService.refundOrder(orderNumber, transactionId, amount);
            return Result.success(success);
        } catch (Exception e) {
            return Result.error("退款失败：" + e.getMessage());
        }
    }

    @GetMapping("/status")
    @ApiOperation("查询订单状态")
    public Result<String> queryOrderStatus(@RequestParam String orderNumber) {
        try {
            String status = paymentGatewayService.queryOrderStatus(orderNumber);
            return Result.success(status);
        } catch (Exception e) {
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
