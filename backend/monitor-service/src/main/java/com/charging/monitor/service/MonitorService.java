package com.charging.monitor.service;
import com.charging.common.core.response.R;
public interface MonitorService {
    R pileStatus(Long pileId);
    R allPilesStatus();
    R alarmList(String status);
    R resolveAlarm(Long alarmId, String result);
    R pileHealth(Long pileId);
}
