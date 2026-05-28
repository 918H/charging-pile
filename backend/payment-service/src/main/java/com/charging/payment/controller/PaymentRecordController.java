package com.charging.payment.controller;

import com.charging.common.core.response.R;
import com.charging.payment.entity.PaymentRecord;
import com.charging.payment.service.PaymentRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/record")
public class PaymentRecordController {

    @Resource
    private PaymentRecordService paymentRecordService;

    @GetMapping("/{paymentId}")
    public Result<PaymentRecord> detail(@PathVariable Long paymentId) {
        PaymentRecord record = paymentRecordService.getById(paymentId);
        if (record == null) {
            return R.fail("支付记录不存在");
        }
        return R.ok(record);
    }

    @GetMapping("/order/{orderId}")
    public Result<PaymentRecord> getByOrder(@PathVariable Long orderId) {
        PaymentRecord record = paymentRecordService.getByOrderId(orderId);
        if (record == null) {
            return R.fail("支付记录不存在");
        }
        return R.ok(record);
    }

    @GetMapping("/user/{userId}")
    public Result<List<PaymentRecord>> listByUser(@PathVariable Long userId) {
        List<PaymentRecord> list = paymentRecordService.getListByUserId(userId);
        return R.ok(list);
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody PaymentRecord record) {
        boolean success = paymentRecordService.save(record);
        return R.ok(success);
    }

    @PutMapping("/{paymentId}")
    public Result<Boolean> update(@PathVariable Long paymentId, @RequestBody PaymentRecord record) {
        record.setPaymentId(paymentId);
        boolean success = paymentRecordService.update(record);
        return R.ok(success);
    }
}
