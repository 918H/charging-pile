package com.charging.user.service;

import com.charging.user.dto.MembershipDTO;
import com.charging.user.entity.UserMembership;

import java.util.List;

public interface MembershipService {
    List<MembershipDTO> getMembershipLevels();
    UserMembership getUserMembership(Long userId);
    void checkAndUpdateMembership(Long userId);
    BigDecimal calculateDiscount(Long userId, BigDecimal amount);
    void upgradeMembership(Long userId, Long levelId);
}
