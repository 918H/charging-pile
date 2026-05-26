package com.charging.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.MembershipDTO;
import com.charging.user.entity.MembershipLevel;
import com.charging.user.entity.UserMembership;
import com.charging.user.entity.SysUser;
import com.charging.user.mapper.MembershipLevelMapper;
import com.charging.user.mapper.UserMembershipMapper;
import com.charging.user.mapper.SysUserMapper;
import com.charging.user.service.MembershipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MembershipServiceImpl implements MembershipService {

    @Resource
    private MembershipLevelMapper membershipLevelMapper;

    @Resource
    private UserMembershipMapper userMembershipMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    private static final BigDecimal[] SPENDING_THRESHOLDS = {
        new BigDecimal("0"),        // 普通用户
        new BigDecimal("500"),      // 白银会员
        new BigDecimal("2000"),     // 黄金会员
        new BigDecimal("5000"),     // 白金会员
        new BigDecimal("10000")     // 钻石会员
    };

    private static final BigDecimal[] DISCOUNT_RATES = {
        new BigDecimal("1.0"),      // 普通用户 无折扣
        new BigDecimal("0.98"),     // 白银会员 98 折
        new BigDecimal("0.95"),     // 黄金会员 95 折
        new BigDecimal("0.92"),     // 白金会员 92 折
        new BigDecimal("0.90")      // 钻石会员 90 折
    };

    @Override
    public List<MembershipDTO> getMembershipLevels() {
        LambdaQueryWrapper<MembershipLevel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MembershipLevel::getStatus, 1);
        wrapper.orderByAsc(MembershipLevel::getLevelCode);
        
        List<MembershipLevel> levels = membershipLevelMapper.selectList(wrapper);
        return levels.stream().map(level -> {
            MembershipDTO dto = new MembershipDTO();
            dto.setLevelId(level.getLevelId());
            dto.setLevelName(level.getLevelName());
            dto.setLevelCode(level.getLevelCode());
            dto.setDiscountRate(level.getDiscountRate());
            dto.setUpgradeThreshold(level.getUpgradeThreshold());
            dto.setValidDays(level.getValidDays());
            dto.setBenefits(level.getBenefits());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserMembership getUserMembership(Long userId) {
        LambdaQueryWrapper<UserMembership> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserMembership::getUserId, userId)
               .eq(UserMembership::getStatus, 1)
               .ge(UserMembership::getEndTime, LocalDateTime.now())
               .orderByDesc(UserMembership::getLevelCode);
        
        return userMembershipMapper.selectOne(wrapper);
    }

    @Override
    public void checkAndUpdateMembership(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            return;
        }

        BigDecimal totalSpending = user.getTotalSpending() != null 
            ? user.getTotalSpending() : BigDecimal.ZERO;

        int targetLevel = 0;
        for (int i = SPENDING_THRESHOLDS.length - 1; i >= 0; i--) {
            if (totalSpending.compareTo(SPENDING_THRESHOLDS[i]) >= 0) {
                targetLevel = i;
                break;
            }
        }

        UserMembership currentMembership = getUserMembership(userId);
        int currentLevel = currentMembership != null ? currentMembership.getLevelCode() : 0;

        if (targetLevel > currentLevel) {
            upgradeMembership(userId, (long) targetLevel);
            log.info("用户 {} 升级会员，等级：{} -> {}", userId, currentLevel, targetLevel);
        }
    }

    @Override
    public BigDecimal calculateDiscount(Long userId, BigDecimal amount) {
        UserMembership membership = getUserMembership(userId);
        if (membership == null) {
            return BigDecimal.ZERO;
        }

        int levelCode = membership.getLevelCode();
        if (levelCode <= 0 || levelCode >= DISCOUNT_RATES.length) {
            return BigDecimal.ZERO;
        }

        BigDecimal discountRate = DISCOUNT_RATES[levelCode];
        return amount.multiply(BigDecimal.ONE.subtract(discountRate));
    }

    @Override
    public void upgradeMembership(Long userId, Long levelId) {
        MembershipLevel level = membershipLevelMapper.selectById(levelId);
        if (level == null) {
            throw new RuntimeException("会员等级不存在");
        }

        UserMembership membership = new UserMembership();
        membership.setUserId(userId);
        membership.setLevelId(levelId);
        membership.setLevelCode(level.getLevelCode());
        membership.setStartTime(LocalDateTime.now());
        membership.setEndTime(LocalDateTime.now().plusDays(level.getValidDays()));
        membership.setStatus(1);
        membership.setCreatedAt(LocalDateTime.now());
        membership.setUpdatedAt(LocalDateTime.now());

        userMembershipMapper.insert(membership);

        SysUser user = sysUserMapper.selectById(userId);
        if (user != null) {
            user.setMembershipLevel(level.getLevelCode());
            sysUserMapper.updateById(user);
        }
    }
}
