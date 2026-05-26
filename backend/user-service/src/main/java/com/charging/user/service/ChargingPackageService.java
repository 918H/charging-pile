package com.charging.user.service;

import com.charging.user.dto.ChargingPackageDTO;
import com.charging.user.entity.ChargingPackage;
import com.charging.user.entity.UserPackage;

import java.math.BigDecimal;
import java.util.List;

public interface ChargingPackageService {
    List<ChargingPackageDTO> getAvailablePackages();
    ChargingPackageDTO getPackageDetail(Long packageId);
    UserPackage purchasePackage(Long userId, Long packageId);
    List<UserPackage> getUserPackages(Long userId);
    void consumePackageEnergy(Long userId, BigDecimal energy);
}
