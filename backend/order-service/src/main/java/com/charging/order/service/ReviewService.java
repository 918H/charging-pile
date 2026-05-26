package com.charging.order.service;

import com.charging.order.dto.ReviewRequest;
import com.charging.order.dto.ReviewResponse;

import java.util.List;
import java.util.Map;

public interface ReviewService {
    boolean createReview(ReviewRequest request);
    List<ReviewResponse> getPileReviews(Long pileId, Integer limit);
    List<ReviewResponse> getUserReviews(Long userId);
    Map<String, Object> getPileRating(Long pileId);
    boolean canReview(Long userId, Long orderId);
}
