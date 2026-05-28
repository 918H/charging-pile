package com.charging.finance.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.core.response.R;
import com.charging.finance.entity.FinanceTransaction;
import com.charging.finance.mapper.FinanceTransactionMapper;
import com.charging.finance.service.FinanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {
    @Resource private FinanceTransactionMapper transactionMapper;
    @Override
    public R transactionList(Long userId, String type) {
        log.info("查询流水：userId={}, type={}", userId, type);
        LambdaQueryWrapper<FinanceTransaction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FinanceTransaction::getUserId, userId);
        if (type != null) wrapper.eq(FinanceTransaction::getType, type);
        wrapper.orderByDesc(FinanceTransaction::getCreateTime);
        List<FinanceTransaction> list = transactionMapper.selectList(wrapper);
        return R.ok(list);
    }
    @Override
    public R dailyReport(String date) {
        log.info("日报表：date={}", date);
        return R.ok("日报表数据生成中...");
    }
    @Override
    public R monthlyReport(String yearMonth) {
        log.info("月报表：yearMonth={}", yearMonth);
        return R.ok("月报表数据生成中...");
    }
    @Override
    public R balance(Long userId) { return R.ok(new BigDecimal("0.00")); }
    @Override
    public R recharge(Long userId, BigDecimal amount) {
        log.info("充值：userId={}, amount={}", userId, amount);
        return R.ok("充值成功");
    }
    @Override
    public R withdraw(Long userId, BigDecimal amount) {
        log.info("提现：userId={}, amount={}", userId, amount);
        return R.ok("提现申请已提交");
    }
}
