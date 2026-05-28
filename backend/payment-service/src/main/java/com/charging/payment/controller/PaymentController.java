package com.charging.payment.controller;

import com.charging.common.core.response.R;
import com.charging.payment.dto.PaymentRequest;
import com.charging.payment.dto.PaymentResponse;
import com.charging.payment.entity.PaymentRecord;
import com.charging.payment.service.PaymentRecordService;
import com.charging.payment.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "支付管理")
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @Resource
    private PaymentRecordService paymentRecordService;

    @GetMapping("/list")
    @ApiOperation("获取支付记录列表")
    public Result<List<PaymentRecord>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<PaymentRecord> records = paymentRecordService.getList(userId, status);
        return R.ok(records);
    }

    @GetMapping("/{paymentId}")
    @ApiOperation("获取支付详情")
    public Result<PaymentRecord> detail(@PathVariable Long paymentId) {
        PaymentRecord record = paymentRecordService.getById(paymentId);
        if (record == null) {
            return R.fail("未找到支付记录");
        }
        return R.ok(record);
    }

    @GetMapping("/by-order")
    @ApiOperation("根据订单号获取支付记录")
    public Result<PaymentRecord> getByOrder(@RequestParam String orderNumber) {
        List<PaymentRecord> list = paymentRecordService.getList(null, null);
        PaymentRecord record = list.stream()
            .filter(p -> p.getOrderNumber().equals(orderNumber))
            .findFirst()
            .orElse(null);
        
        if (record == null) {
            return R.fail("未找到支付记录");
        }
        return R.ok(record);
    }

    @PostMapping("/create")
    @ApiOperation("创建支付记录")
    public Result<PaymentResponse> create(@RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.createPayment(request);
        if (response.isSuccess()) {
            return R.ok(response);
        } else {
            return R.fail(response.getMessage());
        }
    }

    @PostMapping("/verify")
    @ApiOperation("验证支付结果")
    public Result<Boolean> verify(
            @RequestParam String orderNumber,
            @RequestParam String transactionId
    ) {
        boolean verified = paymentService.verifyPayment(orderNumber, transactionId);
        return R.ok(verified);
    }
}
