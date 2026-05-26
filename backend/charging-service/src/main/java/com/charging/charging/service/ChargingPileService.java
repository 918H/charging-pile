package com.charging.charging.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.charging.entity.ChargingPile;

import java.util.List;

public interface ChargingPileService {
    
    Page<ChargingPile> getPage(int current, int size);
    
    List<ChargingPile> getList();
    
    ChargingPile getById(Long pileId);
    
    boolean save(ChargingPile pile);
    
    boolean update(ChargingPile pile);
    
    boolean delete(Long pileId);
    
    List<ChargingPile> getNearby(Double latitude, Double longitude, Double radius);
    
    List<ChargingPile> getAvailable();
}
