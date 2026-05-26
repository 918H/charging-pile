package com.charging.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.ExchangeRequest;
import com.charging.user.dto.PointsMallItemDTO;
import com.charging.user.entity.PointsExchangeRecord;
import com.charging.user.entity.PointsMallItem;
import com.charging.user.mapper.PointsExchangeRecordMapper;
import com.charging.user.mapper.PointsMallItemMapper;
import com.charging.user.service.PointsMallService;
import com.charging.user.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointsMallServiceImpl implements PointsMallService {

    @Resource
    private PointsMallItemMapper pointsMallItemMapper;

    @Resource
    private PointsExchangeRecordMapper pointsExchangeRecordMapper;

    @Resource
    private PointsService pointsService;

    @Override
    public List<PointsMallItemDTO> getItems(Integer type, Integer status) {
        LambdaQueryWrapper<PointsMallItem> wrapper = new LambdaQueryWrapper<>();
        if (type != null) {
            wrapper.eq(PointsMallItem::getType, type);
        }
        if (status != null) {
            wrapper.eq(PointsMallItem::getStatus, status);
        } else {
            wrapper.eq(PointsMallItem::getStatus, 1);
        }
        wrapper.le(PointsMallItem::getStartTime, LocalDateTime.now())
               .ge(PointsMallItem::getEndTime, LocalDateTime.now())
               .orderByAsc(PointsMallItem::getPointsPrice);
        
        List<PointsMallItem> items = pointsMallItemMapper.selectList(wrapper);
        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public PointsMallItemDTO getItemDetail(Long itemId) {
        PointsMallItem item = pointsMallItemMapper.selectById(itemId);
        if (item == null) {
            return null;
        }
        return convertToDTO(item);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointsExchangeRecord exchangeItem(Long userId, ExchangeRequest request) {
        PointsMallItem item = pointsMallItemMapper.selectById(request.getItemId());
        if (item == null || item.getStatus() != 1) {
            throw new RuntimeException("商品不存在或已下架");
        }

        if (item.getStock() <= 0) {
            throw new RuntimeException("库存不足");
        }

        Long count = pointsExchangeRecordMapper.selectCount(
            new LambdaQueryWrapper<PointsExchangeRecord>()
                .eq(PointsExchangeRecord::getUserId, userId)
                .eq(PointsExchangeRecord::getItemId, request.getItemId())
        );
        if (item.getLimitPerUser() > 0 && count >= item.getLimitPerUser()) {
            throw new RuntimeException("超出用户兑换限制");
        }

        pointsService.consumePoints(userId, item.getPointsPrice(), "兑换：" + item.getItemName());

        PointsExchangeRecord record = new PointsExchangeRecord();
        record.setUserId(userId);
        record.setItemId(item.getItemId());
        record.setItemName(item.getItemName());
        record.setPointsUsed(item.getPointsPrice());
        record.setStatus(0);
        record.setShippingAddress(request.getShippingAddress());
        record.setExchangeTime(LocalDateTime.now());
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        pointsExchangeRecordMapper.insert(record);

        item.setStock(item.getStock() - 1);
        item.setUpdatedAt(LocalDateTime.now());
        pointsMallItemMapper.updateById(item);

        log.info("用户 {} 兑换商品 {}，消耗 {} 积分", userId, item.getItemName(), item.getPointsPrice());
        
        return record;
    }

    @Override
    public List<PointsExchangeRecord> getUserRecords(Long userId, Integer status) {
        LambdaQueryWrapper<PointsExchangeRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointsExchangeRecord::getUserId, userId);
        if (status != null) {
            wrapper.eq(PointsExchangeRecord::getStatus, status);
        }
        wrapper.orderByDesc(PointsExchangeRecord::getExchangeTime);
        return pointsExchangeRecordMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipItem(Long recordId, String trackingNumber) {
        PointsExchangeRecord record = pointsExchangeRecordMapper.selectById(recordId);
        if (record == null) {
            throw new RuntimeException("兑换记录不存在");
        }

        record.setStatus(1);
        record.setTrackingNumber(trackingNumber);
        record.setShippingTime(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        pointsExchangeRecordMapper.updateById(record);

        log.info("兑换记录 {} 已发货，快递单号 {}", recordId, trackingNumber);
    }

    private PointsMallItemDTO convertToDTO(PointsMallItem item) {
        PointsMallItemDTO dto = new PointsMallItemDTO();
        dto.setItemId(item.getItemId());
        dto.setItemName(item.getItemName());
        dto.setItemDesc(item.getItemDesc());
        dto.setItemImage(item.getItemImage());
        dto.setType(item.getType());
        dto.setPointsPrice(item.getPointsPrice());
        dto.setCashValue(item.getCashValue());
        dto.setStock(item.getStock());
        dto.setLimitPerUser(item.getLimitPerUser());
        dto.setCouponConfig(item.getCouponConfig());
        
        if (item.getStock() <= 0) {
            dto.setStatusText("售罄");
        } else if (LocalDateTime.now().isBefore(item.getStartTime())) {
            dto.setStatusText("未开始");
        } else if (LocalDateTime.now().isAfter(item.getEndTime())) {
            dto.setStatusText("已结束");
        } else {
            dto.setStatusText("可售");
        }
        
        return dto;
    }
}
