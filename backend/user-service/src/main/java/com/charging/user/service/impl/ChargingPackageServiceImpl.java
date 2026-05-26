package com.charging.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.dto.ChargingPackageDTO;
import com.charging.user.entity.ChargingPackage;
import com.charging.user.entity.UserPackage;
import com.charging.user.mapper.ChargingPackageMapper;
import com.charging.user.mapper.UserPackageMapper;
import com.charging.user.service.ChargingPackageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChargingPackageServiceImpl implements ChargingPackageService {

    @Resource
    private ChargingPackageMapper chargingPackageMapper;

    @Resource
    private UserPackageMapper userPackageMapper;

    @Override
    public List<ChargingPackageDTO> getAvailablePackages() {
        LambdaQueryWrapper<ChargingPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChargingPackage::getStatus, 1)
               .le(ChargingPackage::getStartTime, LocalDateTime.now())
               .ge(ChargingPackage::getEndTime, LocalDateTime.now())
               .orderByAsc(ChargingPackage::getPrice);
        
        List<ChargingPackage> packages = chargingPackageMapper.selectList(wrapper);
        return packages.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public ChargingPackageDTO getPackageDetail(Long packageId) {
        ChargingPackage pkg = chargingPackageMapper.selectById(packageId);
        return pkg != null ? convertToDTO(pkg) : null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserPackage purchasePackage(Long userId, Long packageId) {
        ChargingPackage pkg = chargingPackageMapper.selectById(packageId);
        if (pkg == null || pkg.getStatus() != 1) {
            throw new RuntimeException("套餐不存在或已下架");
        }

        if (pkg.getSoldCount() >= pkg.getPurchaseLimit()) {
            throw new RuntimeException("套餐已售罄");
        }

        Long userPackageCount = userPackageMapper.selectCount(
            new LambdaQueryWrapper<UserPackage>()
                .eq(UserPackage::getUserId, userId)
                .eq(UserPackage::getPackageId, packageId)
                .eq(UserPackage::getStatus, 1)
        );
        if (userPackageCount >= pkg.getPurchaseLimit()) {
            throw new RuntimeException("超出购买限制");
        }

        UserPackage userPackage = new UserPackage();
        userPackage.setUserId(userId);
        userPackage.setPackageId(packageId);
        userPackage.setRemainingEnergy(pkg.getIncludedEnergy());
        userPackage.setStartTime(LocalDateTime.now());
        userPackage.setEndTime(LocalDateTime.now().plusDays(pkg.getValidDays()));
        userPackage.setStatus(1);
        userPackage.setCreatedAt(LocalDateTime.now());
        userPackage.setUpdatedAt(LocalDateTime.now());
        userPackageMapper.insert(userPackage);

        pkg.setSoldCount(pkg.getSoldCount() + 1);
        pkg.setUpdatedAt(LocalDateTime.now());
        chargingPackageMapper.updateById(pkg);

        log.info("用户 {} 购买套餐 {}，电量 {}kWh", userId, pkg.getPackageName(), pkg.getIncludedEnergy());
        
        return userPackage;
    }

    @Override
    public List<UserPackage> getUserPackages(Long userId) {
        LambdaQueryWrapper<UserPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPackage::getUserId, userId)
               .orderByDesc(UserPackage::getCreatedAt);
        return userPackageMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void consumePackageEnergy(Long userId, BigDecimal energy) {
        LambdaQueryWrapper<UserPackage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserPackage::getUserId, userId)
               .eq(UserPackage::getStatus, 1)
               .ge(UserPackage::getEndTime, LocalDateTime.now())
               .gt(UserPackage::getRemainingEnergy, 0)
               .orderByAsc(UserPackage::getEndTime)
               .last("LIMIT 1");
        
        UserPackage userPackage = userPackageMapper.selectOne(wrapper);
        if (userPackage != null) {
            userPackage.setRemainingEnergy(userPackage.getRemainingEnergy().subtract(energy));
            if (userPackage.getRemainingEnergy().compareTo(BigDecimal.ZERO) <= 0) {
                userPackage.setStatus(0);
            }
            userPackage.setUpdatedAt(LocalDateTime.now());
            userPackageMapper.updateById(userPackage);
            
            log.info("用户 {} 使用套餐电量 {}kWh，剩余 {}kWh", 
                userId, energy, userPackage.getRemainingEnergy());
        }
    }

    private ChargingPackageDTO convertToDTO(ChargingPackage pkg) {
        ChargingPackageDTO dto = new ChargingPackageDTO();
        dto.setPackageId(pkg.getPackageId());
        dto.setPackageName(pkg.getPackageName());
        dto.setPackageDesc(pkg.getPackageDesc());
        dto.setType(pkg.getType());
        dto.setPrice(pkg.getPrice());
        dto.setOriginalPrice(pkg.getOriginalPrice());
        dto.setIncludedEnergy(pkg.getIncludedEnergy());
        dto.setValidDays(pkg.getValidDays());
        dto.setTimeLimitStart(pkg.getTimeLimitStart());
        dto.setTimeLimitEnd(pkg.getTimeLimitEnd());
        dto.setPurchaseLimit(pkg.getPurchaseLimit());
        dto.setSoldCount(pkg.getSoldCount());
        dto.setStartTime(pkg.getStartTime());
        dto.setEndTime(pkg.getEndTime());
        
        if (pkg.getSoldCount() >= pkg.getPurchaseLimit()) {
            dto.setStatusText("已售罄");
        } else if (LocalDateTime.now().isBefore(pkg.getStartTime())) {
            dto.setStatusText("未开售");
        } else if (LocalDateTime.now().isAfter(pkg.getEndTime())) {
            dto.setStatusText("已售罄");
        } else {
            dto.setStatusText("可售");
        }
        
        return dto;
    }
}
