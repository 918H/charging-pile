package com.charging.user.service;

import com.charging.user.dto.RechargeRequest;
import com.charging.user.entity.RechargeRecord;
import com.charging.user.entity.UserRechargeCard;

import java.util.List;

public interface RechargeCardService {
    UserRechargeCard getOrCreateCard(Long userId);
    UserRechargeCard getCard(Long userId);
    List<RechargeRecord> getRechargeRecords(Long userId);
    String recharge(Long userId, RechargeRequest request);
    boolean payByCard(Long userId, BigDecimal amount, String orderNumber);
}
