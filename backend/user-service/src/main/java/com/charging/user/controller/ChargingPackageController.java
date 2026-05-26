package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.dto.ChargingPackageDTO;
import com.charging.user.entity.ChargingPackage;
import com.charging.user.entity.UserPackage;
import com.charging.user.service.ChargingPackageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "充电套餐")
@RestController
@RequestMapping("/charging-package")
public class ChargingPackageController {

    @Resource
    private ChargingPackageService chargingPackageService;

    @GetMapping("/list")
    @ApiOperation("获取可购买套餐列表")
    public Result<List<ChargingPackageDTO>> getAvailablePackages() {
        List<ChargingPackageDTO> packages = chargingPackageService.getAvailablePackages();
        return Result.success(packages);
    }

    @GetMapping("/detail")
    @ApiOperation("获取套餐详情")
    public Result<ChargingPackageDTO> getPackageDetail(@RequestParam Long packageId) {
        ChargingPackageDTO pkg = chargingPackageService.getPackageDetail(packageId);
        if (pkg == null) {
            return Result.error("套餐不存在");
        }
        return Result.success(pkg);
    }

    @PostMapping("/purchase")
    @ApiOperation("购买套餐")
    public Result<UserPackage> purchasePackage(
            @RequestParam Long userId,
            @RequestParam Long packageId
    ) {
        try {
            UserPackage userPackage = chargingPackageService.purchasePackage(userId, packageId);
            return Result.success(userPackage);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户已购套餐")
    public Result<List<UserPackage>> getUserPackages(@RequestParam Long userId) {
        List<UserPackage> packages = chargingPackageService.getUserPackages(userId);
        return Result.success(packages);
    }
}
