package com.charging.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.entity.PointsRecord;
import com.charging.user.entity.UserPoints;
import com.charging.user.mapper.PointsRecordMapper;
import com.charging.user.mapper.UserPointsMapper;
import com.charging.user.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class PointsServiceImpl implements PointsService {

    @Resource
    private UserPointsMapper userPointsMapper;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    @Override
    public UserPoints getUserPoints(Long userId) {
        LambdaQueryWrapper<UserPoints> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPoints::getUserId, userId);
        UserPoints points = userPointsMapper.selectOne(wrapper);
        
        if (points == null) {
            points = new UserPoints();
            points.setUserId(userId);
            points.setPoints(0);
            points.setFrozenPoints(0);
            points.setCreatedAt(LocalDateTime.now());
            points.setUpdatedAt(LocalDateTime.now());
            userPointsMapper.insert(points);
        }
        
        return points;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addPoints(Long userId, Integer points, String description, String relatedOrder) {
        UserPoints userPoints = getUserPoints(userId);
        userPoints.setPoints(userPoints.getPoints() + points);
        userPoints.setUpdatedAt(LocalDateTime.now());
        userPointsMapper.updateById(userPoints);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(points);
        record.setType(1);
        record.setDescription(description);
        record.setRelatedOrder(relatedOrder);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("用户 {} 增加 {} 积分，当前积分 {}", userId, points, userPoints.getPoints());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumePoints(Long userId, Integer points, String description) {
        UserPoints userPoints = getUserPoints(userId);
        if (userPoints.getPoints() < points) {
            throw new RuntimeException("积分不足");
        }

        userPoints.setPoints(userPoints.getPoints() - points);
        userPoints.setUpdatedAt(LocalDateTime.now());
        userPointsMapper.updateById(userPoints);

        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setPoints(-points);
        record.setType(2);
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        log.info("用户 {} 消费 {} 积分，剩余积分 {}", userId, points, userPoints.getPoints());
    }

    @Override
    public List<PointsRecord> getPointsRecords(Long userId, Integer type) {
        LambdaQueryWrapper<PointsRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsRecord::getUserId, userId);
        if (type != null) {
            wrapper.eq(PointsRecord::getType, type);
        }
        wrapper.orderByDesc(PointsRecord::getCreatedAt);
        return pointsRecordMapper.selectList(wrapper);
    }
}
