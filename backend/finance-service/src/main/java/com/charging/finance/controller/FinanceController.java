package com.charging.finance.controller;

import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/finance")
public class FinanceController {
    
    @GetMapping("/transaction/list")
    public R listTransactions() {
        return R.ok("流水列表");
    }
    
    @GetMapping("/report/daily")
    public R dailyReport() {
        return R.ok("日报表");
    }
}
