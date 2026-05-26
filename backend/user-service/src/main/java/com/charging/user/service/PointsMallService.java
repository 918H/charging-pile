package com.charging.user.service;

import com.charging.user.dto.ExchangeRequest;
import com.charging.user.dto.PointsMallItemDTO;
import com.charging.user.entity.PointsExchangeRecord;

import java.util.List;

public interface PointsMallService {
    List<PointsMallItemDTO> getItems(Integer type, Integer status);
    PointsMallItemDTO getItemDetail(Long itemId);
    PointsExchangeRecord exchangeItem(Long userId, ExchangeRequest request);
    List<PointsExchangeRecord> getUserRecords(Long userId, Integer status);
    void shipItem(Long recordId, String trackingNumber);
}
