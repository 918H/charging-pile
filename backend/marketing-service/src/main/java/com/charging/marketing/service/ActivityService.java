package com.charging.marketing.service;
import com.charging.common.core.response.R;
import java.time.LocalDateTime;
public interface ActivityService {
    R list(String status);
    R create(String name, String type, LocalDateTime startTime, LocalDateTime endTime);
    R launch(Long id);
    R participate(Long activityId, Long userId);
    R statistics(Long activityId);
}
