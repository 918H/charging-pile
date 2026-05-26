package com.charging.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.order.dto.ReviewRequest;
import com.charging.order.dto.ReviewResponse;
import com.charging.order.entity.ChargingOrder;
import com.charging.order.entity.ChargingReview;
import com.charging.order.mapper.ChargingOrderMapper;
import com.charging.order.mapper.ChargingReviewMapper;
import com.charging.order.service.ReviewService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Resource
    private ChargingReviewMapper chargingReviewMapper;

    @Resource
    private ChargingOrderMapper chargingOrderMapper;

    @Override
    public boolean createReview(ReviewRequest request) {
        if (!canReview(request.getUserId(), request.getOrderId())) {
            return false;
        }

        ChargingReview review = new ChargingReview();
        review.setUserId(request.getUserId());
        review.setOrderId(request.getOrderId());
        review.setPileId(request.getPileId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setHasImages(request.getImages() != null && !request.getImages().isEmpty());
        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        return chargingReviewMapper.insert(review) > 0;
    }

    @Override
    public List<ReviewResponse> getPileReviews(Long pileId, Integer limit) {
        LambdaQueryWrapper<ChargingReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReview::getPileId, pileId)
               .orderByDesc(ChargingReview::getCreatedAt);
        
        if (limit != null) {
            wrapper.last("LIMIT " + limit);
        }

        List<ChargingReview> reviews = chargingReviewMapper.selectList(wrapper);
        
        return reviews.stream().map(review -> {
            ReviewResponse response = new ReviewResponse();
            response.setReviewId(review.getReviewId());
            response.setUserId(review.getUserId());
            response.setPileId(review.getPileId());
            response.setRating(review.getRating());
            response.setContent(review.getContent());
            response.setImages(review.getImages());
            response.setCreatedAt(review.getCreatedAt());
            return response;
        }).toList();
    }

    @Override
    public List<ReviewResponse> getUserReviews(Long userId) {
        LambdaQueryWrapper<ChargingReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReview::getUserId, userId)
               .orderByDesc(ChargingReview::getCreatedAt);

        List<ChargingReview> reviews = chargingReviewMapper.selectList(wrapper);
        
        return reviews.stream().map(review -> {
            ReviewResponse response = new ReviewResponse();
            response.setReviewId(review.getReviewId());
            response.setUserId(review.getUserId());
            response.setPileId(review.getPileId());
            response.setRating(review.getRating());
            response.setContent(review.getContent());
            response.setImages(review.getImages());
            response.setCreatedAt(review.getCreatedAt());
            return response;
        }).toList();
    }

    @Override
    public Map<String, Object> getPileRating(Long pileId) {
        Map<String, Object> result = new HashMap<>();
        
        LambdaQueryWrapper<ChargingReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReview::getPileId, pileId);
        
        List<ChargingReview> reviews = chargingReviewMapper.selectList(wrapper);
        
        if (reviews.isEmpty()) {
            result.put("averageRating", 0.0);
            result.put("totalReviews", 0);
            result.put("ratingDistribution", new int[]{0, 0, 0, 0, 0});
            return result;
        }

        double totalRating = reviews.stream().mapToInt(ChargingReview::getRating).sum();
        double averageRating = totalRating / reviews.size();
        
        int[] distribution = new int[5];
        reviews.forEach(review -> {
            if (review.getRating() >= 1 && review.getRating() <= 5) {
                distribution[review.getRating() - 1]++;
            }
        });

        result.put("averageRating", Math.round(averageRating * 10) / 10.0);
        result.put("totalReviews", reviews.size());
        result.put("ratingDistribution", distribution);
        
        return result;
    }

    @Override
    public boolean canReview(Long userId, Long orderId) {
        if (userId == null || orderId == null) {
            return false;
        }

        ChargingOrder order = chargingOrderMapper.selectById(orderId);
        if (order == null || order.getUserId() == null || !order.getUserId().equals(userId)) {
            return false;
        }

        if (order.getStatus() != 2) {
            return false;
        }

        LambdaQueryWrapper<ChargingReview> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingReview::getUserId, userId)
               .eq(ChargingReview::getOrderId, orderId);
        
        long count = chargingReviewMapper.selectCount(wrapper);
        return count == 0;
    }
}
