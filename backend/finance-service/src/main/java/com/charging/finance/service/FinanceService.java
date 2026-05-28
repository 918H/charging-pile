package com.charging.finance.service;
import com.charging.common.core.response.R;
import java.math.BigDecimal;
public interface FinanceService {
    R transactionList(Long userId, String type);
    R dailyReport(String date);
    R monthlyReport(String yearMonth);
    R balance(Long userId);
    R recharge(Long userId, BigDecimal amount);
    R withdraw(Long userId, BigDecimal amount);
}
