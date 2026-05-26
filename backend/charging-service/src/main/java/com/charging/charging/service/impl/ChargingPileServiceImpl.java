package com.charging.charging.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.charging.entity.ChargingPile;
import com.charging.charging.mapper.ChargingPileMapper;
import com.charging.charging.service.ChargingPileService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ChargingPileServiceImpl implements ChargingPileService {

    @Resource
    private ChargingPileMapper chargingPileMapper;

    @Override
    public Page<ChargingPile> getPage(int current, int size) {
        Page<ChargingPile> page = new Page<>(current, size);
        return chargingPileMapper.selectPage(page, null);
    }

    @Override
    public List<ChargingPile> getList() {
        return chargingPileMapper.selectList(null);
    }

    @Override
    public ChargingPile getById(Long pileId) {
        return chargingPileMapper.selectById(pileId);
    }

    @Override
    public boolean save(ChargingPile pile) {
        return chargingPileMapper.insert(pile) > 0;
    }

    @Override
    public boolean update(ChargingPile pile) {
        return chargingPileMapper.updateById(pile) > 0;
    }

    @Override
    public boolean delete(Long pileId) {
        return chargingPileMapper.deleteById(pileId) > 0;
    }

    @Override
    public List<ChargingPile> getNearby(Double latitude, Double longitude, Double radius) {
        LambdaQueryWrapper<ChargingPile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingPile::getStatus, 1);
        return chargingPileMapper.selectList(wrapper);
    }

    @Override
    public List<ChargingPile> getAvailable() {
        LambdaQueryWrapper<ChargingPile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingPile::getStatus, 1);
        return chargingPileMapper.selectList(wrapper);
    }
}
