package com.charging.payment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "订单 ID 不能为空")
    private Long orderId;

    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    private String paymentMethod = "WECHAT";

    private String remark;
}
