package com.charging.finance.controller;
import com.charging.common.core.response.R;
import com.charging.finance.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.math.BigDecimal;
@Slf4j
@RestController
@RequestMapping("/finance")
public class FinanceController {
    @Resource private FinanceService financeService;
    @GetMapping("/transaction/list")
    public R transactionList(@RequestParam Long userId, @RequestParam(required = false) String type) {
        return financeService.transactionList(userId, type);
    }
    @GetMapping("/report/daily")
    public R dailyReport(@RequestParam String date) {
        return financeService.dailyReport(date);
    }
    @GetMapping("/report/monthly")
    public R monthlyReport(@RequestParam String yearMonth) {
        return financeService.monthlyReport(yearMonth);
    }
    @GetMapping("/balance")
    public R balance(@RequestParam Long userId) {
        return financeService.balance(userId);
    }
    @PostMapping("/recharge")
    public R recharge(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        return financeService.recharge(userId, amount);
    }
    @PostMapping("/withdraw")
    public R withdraw(@RequestParam Long userId, @RequestParam BigDecimal amount) {
        return financeService.withdraw(userId, amount);
    }
}
