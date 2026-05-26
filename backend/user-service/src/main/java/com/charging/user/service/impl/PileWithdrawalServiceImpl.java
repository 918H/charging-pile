package com.charging.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.WithdrawalRequest;
import com.charging.user.entity.PileIncome;
import com.charging.user.entity.PileWithdrawal;
import com.charging.user.entity.PrivatePile;
import com.charging.user.mapper.PileIncomeMapper;
import com.charging.user.mapper.PileWithdrawalMapper;
import com.charging.user.mapper.PrivatePileMapper;
import com.charging.user.service.PileWithdrawalService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PileWithdrawalServiceImpl implements PileWithdrawalService {

    @Resource
    private PileWithdrawalMapper pileWithdrawalMapper;

    @Resource
    private PileIncomeMapper pileIncomeMapper;

    @Resource
    private PrivatePileMapper privatePileMapper;

    private static final BigDecimal MIN_WITHDRAWAL = new BigDecimal("50");
    private static final BigDecimal WITHDRAWAL_FEE_RATE = new BigDecimal("0.01");

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PileWithdrawal createWithdrawal(Long userId, WithdrawalRequest request) {
        if (request.getAmount().compareTo(MIN_WITHDRAWAL) < 0) {
            throw new RuntimeException("最低提现金额¥" + MIN_WITHDRAWAL);
        }

        BigDecimal settledIncome = getSettledIncome(userId);
        if (settledIncome.compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("可提现余额不足");
        }

        PileWithdrawal withdrawal = new PileWithdrawal();
        withdrawal.setUserId(userId);
        withdrawal.setAmount(request.getAmount());
        withdrawal.setFee(request.getAmount().multiply(WITHDRAWAL_FEE_RATE));
        withdrawal.setActualAmount(request.getAmount().subtract(withdrawal.getFee()));
        withdrawal.setStatus(0);
        withdrawal.setWithdrawMethod(request.getWithdrawMethod());
        
        if (request.getWithdrawMethod() == 1) {
            withdrawal.setAlipayAccount(request.getAlipayAccount());
            withdrawal.setAlipayName(request.getAlipayName());
        } else {
            withdrawal.setBankAccount(request.getBankAccount());
            withdrawal.setBankName(request.getBankName());
            withdrawal.setBankCard(request.getBankCard());
        }
        
        withdrawal.setCreatedAt(LocalDateTime.now());
        withdrawal.setUpdatedAt(LocalDateTime.now());
        pileWithdrawalMapper.insert(withdrawal);

        log.info("用户 {} 申请提现 ¥{}, 实际到账 ¥{}", userId, request.getAmount(), withdrawal.getActualAmount());
        
        return withdrawal;
    }

    @Override
    public List<PileWithdrawal> getUserWithdrawals(Long userId, Integer status) {
        LambdaQueryWrapper<PileWithdrawal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PileWithdrawal::getUserId, userId);
        if (status != null) {
            wrapper.eq(PileWithdrawal::getStatus, status);
        }
        wrapper.orderByDesc(PileWithdrawal::getCreatedAt);
        return pileWithdrawalMapper.selectList(wrapper);
    }

    @Override
    public Map<String, Object> getWithdrawalStats(Long userId) {
        LambdaQueryWrapper<PileIncome> incomeWrapper = new LambdaQueryWrapper<>();
        incomeWrapper.eq(PileIncome::getUserId, userId);
        List<PileIncome> incomes = pileIncomeMapper.selectList(incomeWrapper);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal settledIncome = BigDecimal.ZERO;
        BigDecimal pendingIncome = BigDecimal.ZERO;

        for (PileIncome income : incomes) {
            totalIncome = totalIncome.add(income.getActualIncome());
            if (income.getStatus() >= 1) {
                settledIncome = settledIncome.add(income.getActualIncome());
            } else {
                pendingIncome = pendingIncome.add(income.getActualIncome());
            }
        }

        LambdaQueryWrapper<PileWithdrawal> withdrawWrapper = new LambdaQueryWrapper<>();
        withdrawWrapper.eq(PileWithdrawal::getUserId, userId);
        withdrawWrapper.in(PileWithdrawal::getStatus, 1, 2, 3);
        List<PileWithdrawal> withdrawals = pileWithdrawalMapper.selectList(withdrawWrapper);

        BigDecimal withdrawnAmount = BigDecimal.ZERO;
        for (PileWithdrawal w : withdrawals) {
            withdrawnAmount = withdrawnAmount.add(w.getAmount());
        }

        BigDecimal availableAmount = settledIncome.subtract(withdrawnAmount);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalIncome", totalIncome);
        stats.put("settledIncome", settledIncome);
        stats.put("pendingIncome", pendingIncome);
        stats.put("withdrawnAmount", withdrawnAmount);
        stats.put("availableAmount", availableAmount);
        stats.put("minWithdrawal", MIN_WITHDRAWAL);
        stats.put("withdrawalFeeRate", WITHDRAWAL_FEE_RATE);

        return stats;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveWithdrawal(Long withdrawalId, String transactionId) {
        PileWithdrawal withdrawal = pileWithdrawalMapper.selectById(withdrawalId);
        if (withdrawal == null) {
            throw new RuntimeException("提现记录不存在");
        }

        withdrawal.setStatus(1);
        withdrawal.setTransactionId(transactionId);
        withdrawal.setProcessTime(LocalDateTime.now());
        withdrawal.setUpdatedAt(LocalDateTime.now());
        pileWithdrawalMapper.updateById(withdrawal);

        log.info("提现记录 {} 已审核通过，交易号 {}", withdrawalId, transactionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectWithdrawal(Long withdrawalId, String reason) {
        PileWithdrawal withdrawal = pileWithdrawalMapper.selectById(withdrawalId);
        if (withdrawal == null) {
            throw new RuntimeException("提现记录不存在");
        }

        withdrawal.setStatus(2);
        withdrawal.setRejectReason(reason);
        withdrawal.setProcessTime(LocalDateTime.now());
        withdrawal.setUpdatedAt(LocalDateTime.now());
        pileWithdrawalMapper.updateById(withdrawal);

        log.info("提现记录 {} 被拒绝，原因 {}", withdrawalId, reason);
    }

    private BigDecimal getSettledIncome(Long userId) {
        LambdaQueryWrapper<PileIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PileIncome::getUserId, userId)
               .in(PileIncome::getStatus, 1, 2);
        List<PileIncome> incomes = pileIncomeMapper.selectList(wrapper);
        
        return incomes.stream()
            .map(PileIncome::getActualIncome)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
