package com.charging.user.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private BigDecimal amount;
    private Integer withdrawMethod;
    private String alipayAccount;
    private String alipayName;
    private String bankAccount;
    private String bankName;
    private String bankCard;
}
