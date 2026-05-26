package com.charging.user.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.RechargeRequest;
import com.charging.user.entity.RechargeRecord;
import com.charging.user.entity.UserRechargeCard;
import com.charging.user.mapper.RechargeRecordMapper;
import com.charging.user.mapper.UserRechargeCardMapper;
import com.charging.user.service.RechargeCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class RechargeCardServiceImpl implements RechargeCardService {

    @Resource
    private UserRechargeCardMapper userRechargeCardMapper;

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;

    private static final BigDecimal[] RECHARGE_BONUS = {
        new BigDecimal("50"),    // 充 50 送 0
        new BigDecimal("100"),   // 充 100 送 5
        new BigDecimal("200"),   // 充 200 送 15
        new BigDecimal("500"),   // 充 500 送 50
        new BigDecimal("1000")   // 充 1000 送 120
    };

    @Override
    public UserRechargeCard getOrCreateCard(Long userId) {
        UserRechargeCard card = getCard(userId);
        if (card == null) {
            card = createCard(userId);
        }
        return card;
    }

    @Override
    public UserRechargeCard getCard(Long userId) {
        LambdaQueryWrapper<UserRechargeCard> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRechargeCard::getUserId, userId)
               .eq(UserRechargeCard::getStatus, 1);
        return userRechargeCardMapper.selectOne(wrapper);
    }

    @Override
    public List<RechargeRecord> getRechargeRecords(Long userId) {
        LambdaQueryWrapper<RechargeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RechargeRecord::getUserId, userId)
               .orderByDesc(RechargeRecord::getCreatedAt);
        return rechargeRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String recharge(Long userId, RechargeRequest request) {
        UserRechargeCard card = getOrCreateCard(userId);
        
        BigDecimal bonusAmount = calculateBonus(request.getAmount());
        
        RechargeRecord record = new RechargeRecord();
        record.setCardId(card.getCardId());
        record.setUserId(userId);
        record.setAmount(request.getAmount());
        record.setBonusAmount(bonusAmount);
        record.setPaymentMethod(request.getPaymentMethod());
        record.setTransactionId(request.getTransactionId());
        record.setStatus(1);
        record.setPayTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        
        rechargeRecordMapper.insert(record);
        
        card.setBalance(card.getBalance().add(request.getAmount()).add(bonusAmount));
        card.setUpdatedAt(LocalDateTime.now());
        userRechargeCardMapper.updateById(card);
        
        log.info("用户 {} 充值 {} 元，赠送 {} 元，余额 {} 元", 
            userId, request.getAmount(), bonusAmount, card.getBalance());
        
        return record.getRecordId().toString();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payByCard(Long userId, BigDecimal amount, String orderNumber) {
        UserRechargeCard card = getCard(userId);
        if (card == null || card.getStatus() != 1) {
            return false;
        }
        
        if (card.getBalance().compareTo(amount) < 0) {
            return false;
        }
        
        card.setBalance(card.getBalance().subtract(amount));
        card.setFreezeAmount(card.getFreezeAmount().add(amount));
        card.setUpdatedAt(LocalDateTime.now());
        userRechargeCardMapper.updateById(card);
        
        log.info("用户 {} 使用储值卡支付 {} 元，订单 {}", userId, amount, orderNumber);
        
        return true;
    }

    private UserRechargeCard createCard(Long userId) {
        UserRechargeCard card = new UserRechargeCard();
        card.setUserId(userId);
        card.setCardNumber("C" + DateUtil.format(DateUtil.date(), "yyyyMMdd") + 
            IdUtil.getSnowflakeNextIdStr());
        card.setBalance(BigDecimal.ZERO);
        card.setFreezeAmount(BigDecimal.ZERO);
        card.setStatus(1);
        card.setExpireTime(LocalDateTime.now().plusYears(10));
        card.setCreatedAt(LocalDateTime.now());
        card.setUpdatedAt(LocalDateTime.now());
        
        userRechargeCardMapper.insert(card);
        return card;
    }

    private BigDecimal calculateBonus(BigDecimal amount) {
        for (int i = RECHARGE_BONUS.length - 1; i >= 0; i--) {
            if (amount.compareTo(RECHARGE_BONUS[i]) >= 0) {
                switch (i) {
                    case 0: return BigDecimal.ZERO;
                    case 1: return new BigDecimal("5");
                    case 2: return new BigDecimal("15");
                    case 3: return new BigDecimal("50");
                    case 4: return new BigDecimal("120");
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
