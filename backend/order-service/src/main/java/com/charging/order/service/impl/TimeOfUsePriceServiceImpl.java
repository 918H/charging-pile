package com.charging.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.order.entity.ChargingPrice;
import com.charging.order.mapper.ChargingPriceMapper;
import com.charging.order.service.TimeOfUsePriceService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeOfUsePriceServiceImpl implements TimeOfUsePriceService {

    @Resource
    private ChargingPriceMapper chargingPriceMapper;

    private static final BigDecimal DEFAULT_ELECTRICITY_PRICE = new BigDecimal("1.0");
    private static final BigDecimal DEFAULT_SERVICE_PRICE = new BigDecimal("0.5");
    private static final BigDecimal PEAK_ELECTRICITY_PRICE = new BigDecimal("1.7");
    private static final BigDecimal OFF_PEAK_ELECTRICITY_PRICE = new BigDecimal("0.9");

    @Override
    public BigDecimal getPriceForTime(Long pileId, LocalDateTime time) {
        List<ChargingPrice> prices = getPriceRules(pileId, time);
        
        if (prices.isEmpty()) {
            return getDefaultPriceForTime(time);
        }
        
        ChargingPrice price = prices.get(0);
        return price.getElectricityPrice().add(price.getServicePrice());
    }

    @Override
    public BigDecimal calculateFee(Long pileId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal powerConsumed) {
        if (startTime == null || endTime == null || powerConsumed == null) {
            return BigDecimal.ZERO;
        }
        
        long totalMinutes = Duration.between(startTime, endTime).toMinutes();
        if (totalMinutes <= 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal electricityFee = BigDecimal.ZERO;
        BigDecimal serviceFee = BigDecimal.ZERO;
        
        LocalDateTime currentTime = startTime;
        while (currentTime.isBefore(endTime)) {
            LocalDateTime nextTime = currentTime.plusMinutes(1);
            if (nextTime.isAfter(endTime)) {
                nextTime = endTime;
            }
            
            BigDecimal price = getPriceForTime(pileId, currentTime);
            BigDecimal servicePrice = getServicePriceForTime(pileId, currentTime);
            
            long minutes = Duration.between(currentTime, nextTime).toMinutes();
            BigDecimal powerPerMinute = powerConsumed.divide(BigDecimal.valueOf(totalMinutes), 6, BigDecimal.ROUND_HALF_UP);
            BigDecimal powerForPeriod = powerPerMinute.multiply(BigDecimal.valueOf(minutes));
            
            electricityFee = electricityFee.add(price.multiply(powerForPeriod));
            serviceFee = serviceFee.add(servicePrice.multiply(powerForPeriod));
            
            currentTime = nextTime;
        }
        
        return electricityFee.add(serviceFee);
    }

    @Override
    public void updatePrice(Long pileId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal electricityPrice, BigDecimal servicePrice) {
        ChargingPrice price = new ChargingPrice();
        price.setPileId(pileId);
        price.setStartTime(startTime);
        price.setEndTime(endTime);
        price.setElectricityPrice(electricityPrice);
        price.setServicePrice(servicePrice);
        price.setPriceType(getPriceType(startTime));
        price.setValid(true);
        
        chargingPriceMapper.insert(price);
    }

    private BigDecimal getServicePriceForTime(Long pileId, LocalDateTime time) {
        List<ChargingPrice> prices = getPriceRules(pileId, time);
        
        if (prices.isEmpty()) {
            return DEFAULT_SERVICE_PRICE;
        }
        
        return prices.get(0).getServicePrice();
    }

    private List<ChargingPrice> getPriceRules(Long pileId, LocalDateTime time) {
        LambdaQueryWrapper<ChargingPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingPrice::getPileId, pileId)
               .eq(ChargingPrice::isValid, true)
               .le(ChargingPrice::getStartTime, time)
               .ge(ChargingPrice::getEndTime, time);
        return chargingPriceMapper.selectList(wrapper);
    }

    private BigDecimal getDefaultPriceForTime(LocalDateTime time) {
        if (time == null) {
            return DEFAULT_ELECTRICITY_PRICE.add(DEFAULT_SERVICE_PRICE);
        }
        
        int hour = time.getHour();
        BigDecimal electricityPrice;
        
        if (hour >= 8 && hour < 22) {
            electricityPrice = PEAK_ELECTRICITY_PRICE;
        } else {
            electricityPrice = OFF_PEAK_ELECTRICITY_PRICE;
        }
        
        return electricityPrice.add(DEFAULT_SERVICE_PRICE);
    }

    private Integer getPriceType(LocalDateTime time) {
        if (time == null) {
            return 0;
        }
        
        int hour = time.getHour();
        if (hour >= 8 && hour < 22) {
            return 1;
        } else {
            return 0;
        }
    }
}
