package com.charging.user.service;

import com.charging.user.entity.PointsRecord;
import com.charging.user.entity.UserPoints;

import java.util.List;

public interface PointsService {
    UserPoints getUserPoints(Long userId);
    void addPoints(Long userId, Integer points, String description, String relatedOrder);
    void consumePoints(Long userId, Integer points, String description);
    List<PointsRecord> getPointsRecords(Long userId, Integer type);
}
