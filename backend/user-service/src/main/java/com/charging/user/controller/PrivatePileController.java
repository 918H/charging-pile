package com.charging.user.controller;

import com.charging.user.common.Result;
import com.charging.user.dto.PileDTO;
import com.charging.user.entity.PileIncome;
import com.charging.user.entity.PileReservation;
import com.charging.user.entity.PrivatePile;
import com.charging.user.service.PrivatePileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Api(tags = "私人充电桩管理")
@RestController
@RequestMapping("/private-pile")
public class PrivatePileController {

    @Resource
    private PrivatePileService privatePileService;

    @PostMapping("/create")
    @ApiOperation("创建私人充电桩")
    public Result<PrivatePile> createPile(
            @RequestParam Long userId,
            @RequestBody PileDTO dto
    ) {
        try {
            PrivatePile pile = privatePileService.createPile(userId, dto);
            return Result.success(pile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PutMapping("/update")
    @ApiOperation("更新私人充电桩")
    public Result<PrivatePile> updatePile(
            @RequestParam Long userId,
            @RequestParam Long pileId,
            @RequestBody PileDTO dto
    ) {
        try {
            PrivatePile pile = privatePileService.updatePile(userId, pileId, dto);
            return Result.success(pile);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户的充电桩列表")
    public Result<List<PrivatePile>> getUserPiles(@RequestParam Long userId) {
        List<PrivatePile> piles = privatePileService.getUserPiles(userId);
        return Result.success(piles);
    }

    @GetMapping("/detail")
    @ApiOperation("获取充电桩详情")
    public Result<PrivatePile> getPileDetail(@RequestParam Long pileId) {
        PrivatePile pile = privatePileService.getPileDetail(pileId);
        return Result.success(pile);
    }

    @GetMapping("/nearby")
    @ApiOperation("获取附近充电桩")
    public Result<List<PrivatePile>> getNearbyPiles(
            @RequestParam BigDecimal latitude,
            @RequestParam BigDecimal longitude,
            @RequestParam(required = false, defaultValue = "5.0") Double radius
    ) {
        List<PrivatePile> piles = privatePileService.getNearbyPiles(latitude, longitude, radius);
        return Result.success(piles);
    }

    @PostMapping("/reserve")
    @ApiOperation("预约充电桩")
    public Result<PileReservation> reservePile(
            @RequestParam Long userId,
            @RequestParam Long pileId,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            LocalDateTime end = LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            PileReservation reservation = privatePileService.reservePile(userId, pileId, start, end);
            return Result.success(reservation);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/reserve/cancel")
    @ApiOperation("取消预约")
    public Result<Boolean> cancelReservation(
            @RequestParam Long userId,
            @RequestParam Long reservationId
    ) {
        try {
            privatePileService.cancelReservation(userId, reservationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/charging/start")
    @ApiOperation("开始充电")
    public Result<Boolean> startCharging(
            @RequestParam Long userId,
            @RequestParam Long reservationId
    ) {
        try {
            privatePileService.startCharging(userId, reservationId);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/charging/stop")
    @ApiOperation("结束充电")
    public Result<Boolean> stopCharging(
            @RequestParam Long userId,
            @RequestParam Long reservationId,
            @RequestParam BigDecimal energy
    ) {
        try {
            privatePileService.stopCharging(userId, reservationId, energy);
            return Result.success();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/income/list")
    @ApiOperation("获取收入记录")
    public Result<List<PileIncome>> getIncomeRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<PileIncome> records = privatePileService.getIncomeRecords(userId, status);
        return Result.success(records);
    }

    @GetMapping("/income/stats")
    @ApiOperation("获取收入统计")
    public Result<Map<String, Object>> getIncomeStats(@RequestParam Long userId) {
        Map<String, Object> stats = privatePileService.getIncomeStats(userId);
        return Result.success(stats);
    }
}
