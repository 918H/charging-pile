package com.charging.user.service;

import com.charging.user.dto.WithdrawalRequest;
import com.charging.user.entity.PileWithdrawal;

import java.util.List;
import java.util.Map;

public interface PileWithdrawalService {
    PileWithdrawal createWithdrawal(Long userId, WithdrawalRequest request);
    List<PileWithdrawal> getUserWithdrawals(Long userId, Integer status);
    Map<String, Object> getWithdrawalStats(Long userId);
    void approveWithdrawal(Long withdrawalId, String transactionId);
    void rejectWithdrawal(Long withdrawalId, String reason);
}
