package com.charging.user.service;

import com.charging.user.entity.ReferralRelation;
import java.util.Map;

public interface ReferralService {
    String generateReferralCode(Long userId);
    ReferralRelation getReferralRelation(Long userId);
    void bindReferral(Long refereeId, String referralCode);
    void rewardReferral(Long userId);
}
