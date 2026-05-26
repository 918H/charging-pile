package com.charging.order.service.impl;

import com.charging.order.config.ChargingWebSocketHandler;
import com.charging.order.dto.ChargingProgressDTO;
import com.charging.order.entity.ChargingSession;
import com.charging.order.mapper.ChargingSessionMapper;
import com.charging.order.service.ChargingProgressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ChargingProgressServiceImpl implements ChargingProgressService {

    @Resource
    private ChargingWebSocketHandler webSocketHandler;

    @Resource
    private ChargingSessionMapper chargingSessionMapper;

    private final Map<Long, ChargingProgressDTO> PROGRESS_CACHE = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void startProgressTracking(Long orderId) {
        ChargingSession session = chargingSessionMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChargingSession>()
                .eq(ChargingSession::getOrderId, orderId)
        );
        
        if (session != null) {
            ChargingProgressDTO progress = new ChargingProgressDTO();
            progress.setOrderId(orderId);
            progress.setStatus(1);
            progress.setStartTime(session.getStartTime());
            progress.setCurrentSoc(session.getStartSoc() != null ? session.getStartSoc() : 0);
            progress.setTargetSoc(session.getTargetSoc() != null ? session.getTargetSoc() : 100);
            progress.setPowerConsumed(BigDecimal.ZERO);
            progress.setCurrentAmount(BigDecimal.ZERO);
            
            PROGRESS_CACHE.put(orderId, progress);
            
            scheduler.scheduleAtFixedRate(() -> updateAndPushProgress(orderId), 0, 10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void stopProgressTracking(Long orderId) {
        PROGRESS_CACHE.remove(orderId);
    }

    @Override
    public void updateProgress(Long orderId, int currentSoc, BigDecimal powerConsumed, 
                               BigDecimal voltage, BigDecimal current, BigDecimal power) {
        ChargingProgressDTO progress = PROGRESS_CACHE.get(orderId);
        if (progress != null) {
            progress.setCurrentSoc(currentSoc);
            progress.setPowerConsumed(powerConsumed);
            progress.setCurrentVoltage(voltage);
            progress.setCurrentCurrent(current);
            progress.setCurrentPower(power);
            
            if (progress.getCurrentPrice() != null) {
                progress.setCurrentAmount(progress.getCurrentPrice().multiply(powerConsumed));
            }
            
            pushProgress(orderId, progress);
            
            if (currentSoc >= progress.getTargetSoc() || currentSoc >= 100) {
                stopProgressTracking(orderId);
            }
        }
    }

    @Scheduled(fixedRate = 30000)
    public void saveSessionsToDatabase() {
        for (Map.Entry<Long, ChargingProgressDTO> entry : PROGRESS_CACHE.entrySet()) {
            Long orderId = entry.getKey();
            ChargingProgressDTO progress = entry.getValue();
            
            try {
                ChargingSession session = chargingSessionMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ChargingSession>()
                        .eq(ChargingSession::getOrderId, orderId)
                );
                
                if (session != null) {
                    session.setCurrentSoc(progress.getCurrentSoc());
                    session.setPowerConsumed(progress.getPowerConsumed());
                    session.setCurrentVoltage(progress.getCurrentVoltage());
                    session.setCurrentCurrent(progress.getCurrentCurrent());
                    session.setCurrentPower(progress.getCurrentPower());
                    session.setUpdateTime(LocalDateTime.now());
                    
                    chargingSessionMapper.updateById(session);
                }
            } catch (Exception e) {
                System.err.println("Failed to save session " + orderId + ": " + e.getMessage());
            }
        }
    }

    private void updateAndPushProgress(Long orderId) {
        ChargingProgressDTO progress = PROGRESS_CACHE.get(orderId);
        if (progress == null) {
            return;
        }
        
        if (progress.getElapsedTime() == null) {
            progress.setElapsedTime(0);
        }
        progress.setElapsedTime(progress.getElapsedTime() + 10);
        
        if (progress.getCurrentSoc() < progress.getTargetSoc()) {
            int socIncrease = 2;
            progress.setCurrentSoc(Math.min(progress.getCurrentSoc() + socIncrease, progress.getTargetSoc()));
            
            BigDecimal powerIncrease = BigDecimal.valueOf(0.3);
            if (progress.getPowerConsumed() == null) {
                progress.setPowerConsumed(powerIncrease);
            } else {
                progress.setPowerConsumed(progress.getPowerConsumed().add(powerIncrease));
            }
            
            progress.setCurrentVoltage(BigDecimal.valueOf(400));
            progress.setCurrentCurrent(BigDecimal.valueOf(100));
            progress.setCurrentPower(BigDecimal.valueOf(40));
            progress.setAvgPower(BigDecimal.valueOf(35));
            progress.setBatteryTemp(BigDecimal.valueOf(25));
            
            if (progress.getStartTime() != null) {
                progress.setElapsedMinutes((int) java.time.Duration.between(progress.getStartTime(), LocalDateTime.now()).toMinutes());
            }
            
            pushProgress(orderId, progress);
            
            if (progress.getCurrentSoc() >= progress.getTargetSoc()) {
                stopProgressTracking(orderId);
            }
        }
    }

    private void pushProgress(Long orderId, ChargingProgressDTO progress) {
        if (webSocketHandler.hasSubscribers(orderId)) {
            try {
                String json = objectMapper.writeValueAsString(progress);
                webSocketHandler.sendProgressUpdate(orderId, json);
            } catch (Exception e) {
                System.err.println("Failed to push progress: " + e.getMessage());
            }
        }
    }
}
