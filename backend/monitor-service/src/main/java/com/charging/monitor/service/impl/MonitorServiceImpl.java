package com.charging.monitor.service.impl;
import com.charging.common.core.response.R;
import com.charging.monitor.service.MonitorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {
    @Override
    public R pileStatus(Long pileId) {
        log.info("查询充电桩状态：pileId={}", pileId);
        Map<String, Object> status = new HashMap<>();
        status.put("pileId", pileId);
        status.put("status", "charging");
        status.put("power", 7.5);
        status.put("temperature", 35.5);
        return R.ok(status);
    }
    @Override
    public R allPilesStatus() {
        log.info("查询所有充电桩状态");
        List<Map<String, Object>> statusList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> status = new HashMap<>();
            status.put("pileId", (long)i);
            status.put("status", Math.random() > 0.2 ? "charging" : "idle");
            statusList.add(status);
        }
        return R.ok(statusList);
    }
    @Override
    public R alarmList(String status) {
        log.info("查询告警列表：status={}", status);
        return R.ok(new ArrayList<>());
    }
    @Override
    public R resolveAlarm(Long alarmId, String result) {
        log.info("处理告警：alarmId={}", alarmId);
        return R.ok("告警已处理");
    }
    @Override
    public R pileHealth(Long pileId) {
        log.info("查询充电桩健康度：pileId={}", pileId);
        return R.ok(95);
    }
}
