package com.charging.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.entity.ReferralRelation;
import com.charging.user.mapper.ReferralRelationMapper;
import com.charging.user.service.PointsService;
import com.charging.user.service.ReferralService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class ReferralServiceImpl implements ReferralService {

    @Resource
    private ReferralRelationMapper referralRelationMapper;

    @Resource
    private PointsService pointsService;

    private static final Integer REFERRER_REWARD = 500;
    private static final Integer REFEREE_REWARD = 100;

    @Override
    public String generateReferralCode(Long userId) {
        LambdaQueryWrapper<ReferralRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReferralRelation::getReferrerId, userId);
        ReferralRelation existing = referralRelationMapper.selectOne(wrapper);

        if (existing != null) {
            return existing.getReferralCode();
        }

        String referralCode = "R" + IdUtil.getSnowflakeNextIdStr().substring(5);
        
        ReferralRelation relation = new ReferralRelation();
        relation.setReferrerId(userId);
        relation.setRefereeId(userId);
        relation.setReferralCode(referralCode);
        relation.setRewardPoints(0);
        relation.setStatus(0);
        relation.setCreatedAt(LocalDateTime.now());
        relation.setUpdatedAt(LocalDateTime.now());
        referralRelationMapper.insert(relation);

        log.info("用户 {} 生成推荐码 {}", userId, referralCode);
        return referralCode;
    }

    @Override
    public ReferralRelation getReferralRelation(Long userId) {
        LambdaQueryWrapper<ReferralRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReferralRelation::getReferrerId, userId);
        return referralRelationMapper.selectOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindReferral(Long refereeId, String referralCode) {
        LambdaQueryWrapper<ReferralRelation> codeWrapper = new LambdaQueryWrapper<>();
        codeWrapper.eq(ReferralRelation::getReferralCode, referralCode);
        ReferralRelation codeRelation = referralRelationMapper.selectOne(codeWrapper);

        if (codeRelation == null) {
            throw new RuntimeException("推荐码无效");
        }

        LambdaQueryWrapper<ReferralRelation> refereeWrapper = new LambdaQueryWrapper<>();
        refereeWrapper.eq(ReferralRelation::getRefereeId, refereeId);
        ReferralRelation existingReferee = referralRelationMapper.selectOne(refereeWrapper);

        if (existingReferee != null) {
            throw new RuntimeException("已绑定过推荐人");
        }

        if (codeRelation.getReferrerId().equals(refereeId)) {
            throw new RuntimeException("不能推荐自己");
        }

        ReferralRelation refereeRelation = new ReferralRelation();
        refereeRelation.setReferrerId(codeRelation.getReferrerId());
        refereeRelation.setRefereeId(refereeId);
        refereeRelation.setReferralCode(referralCode);
        refereeRelation.setRewardPoints(0);
        refereeRelation.setStatus(0);
        refereeRelation.setCreatedAt(LocalDateTime.now());
        refereeRelation.setUpdatedAt(LocalDateTime.now());
        referralRelationMapper.insert(refereeRelation);

        log.info("用户 {} 通过推荐码 {} 绑定推荐人 {}", refereeId, referralCode, codeRelation.getReferrerId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rewardReferral(Long userId) {
        LambdaQueryWrapper<ReferralRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ReferralRelation::getRefereeId, userId)
               .eq(ReferralRelation::getStatus, 0);
        ReferralRelation relation = referralRelationMapper.selectOne(wrapper);

        if (relation != null) {
            pointsService.addPoints(userId, REFEREE_REWARD, "新人注册奖励", null);
            
            pointsService.addPoints(relation.getReferrerId(), REFERRER_REWARD, 
                "推荐好友奖励", null);

            relation.setStatus(1);
            relation.setRewardPoints(REFERRER_REWARD);
            relation.setUpdatedAt(LocalDateTime.now());
            referralRelationMapper.updateById(relation);

            log.info("推荐奖励：用户 {} 获得 {} 积分，推荐人 {} 获得 {} 积分",
                userId, REFEREE_REWARD, relation.getReferrerId(), REFERRER_REWARD);
        }
    }
}
