package com.charging.charging.service;

import com.charging.charging.dto.FaultReportRequest;
import com.charging.charging.entity.ChargingFault;

import java.util.List;

public interface FaultService {
    boolean reportFault(FaultReportRequest request);
    ChargingFault getFaultDetail(Long faultId);
    List<ChargingFault> getPileFaults(Long pileId, Integer status);
    List<ChargingFault> getUserFaults(Long userId);
    boolean handleFault(Long faultId, Long handlerId, String response);
}
