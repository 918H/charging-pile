package com.charging.charging.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.charging.dto.FaultReportRequest;
import com.charging.charging.entity.ChargingFault;
import com.charging.charging.mapper.ChargingFaultMapper;
import com.charging.charging.service.FaultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FaultServiceImpl implements FaultService {

    @Resource
    private ChargingFaultMapper chargingFaultMapper;

    @Override
    public boolean reportFault(FaultReportRequest request) {
        ChargingFault fault = new ChargingFault();
        fault.setPileId(request.getPileId());
        fault.setSlotId(request.getSlotId());
        fault.setUserId(request.getUserId());
        fault.setFaultType(request.getFaultType());
        fault.setDescription(request.getDescription());
        fault.setImages(request.getImages());
        fault.setContactPhone(request.getContactPhone());
        fault.setStatus(0);
        fault.setCreatedAt(LocalDateTime.now());
        fault.setUpdatedAt(LocalDateTime.now());

        return chargingFaultMapper.insert(fault) > 0;
    }

    @Override
    public ChargingFault getFaultDetail(Long faultId) {
        return chargingFaultMapper.selectById(faultId);
    }

    @Override
    public List<ChargingFault> getPileFaults(Long pileId, Integer status) {
        LambdaQueryWrapper<ChargingFault> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingFault::getPileId, pileId);
        if (status != null) {
            wrapper.eq(ChargingFault::getStatus, status);
        }
        wrapper.orderByDesc(ChargingFault::getCreatedAt);
        return chargingFaultMapper.selectList(wrapper);
    }

    @Override
    public List<ChargingFault> getUserFaults(Long userId) {
        LambdaQueryWrapper<ChargingFault> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingFault::getUserId, userId)
               .orderByDesc(ChargingFault::getCreatedAt);
        return chargingFaultMapper.selectList(wrapper);
    }

    @Override
    public boolean handleFault(Long faultId, Long handlerId, String response) {
        ChargingFault fault = chargingFaultMapper.selectById(faultId);
        if (fault == null) {
            return false;
        }

        fault.setStatus(1);
        fault.setHandlerId(handlerId);
        fault.setHandlerResponse(response);
        fault.setHandledAt(LocalDateTime.now());
        fault.setUpdatedAt(LocalDateTime.now());

        return chargingFaultMapper.updateById(fault) > 0;
    }
}
