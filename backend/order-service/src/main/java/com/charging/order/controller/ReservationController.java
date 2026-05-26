package com.charging.order.controller;

import com.charging.order.common.Result;
import com.charging.order.dto.ReservationRequest;
import com.charging.order.dto.ReservationResponse;
import com.charging.order.entity.ChargingReservation;
import com.charging.order.service.ReservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Api(tags = "预约充电管理")
@RestController
@RequestMapping("/reservation")
public class ReservationController {

    @Resource
    private ReservationService reservationService;

    @PostMapping("/create")
    @ApiOperation("创建预约")
    public Result<ReservationResponse> create(@RequestBody ReservationRequest request) {
        ReservationResponse response = reservationService.createReservation(request);
        if (response.isSuccess()) {
            return Result.success(response);
        } else {
            return Result.error(response.getMessage());
        }
    }

    @PostMapping("/{reservationId}/cancel")
    @ApiOperation("取消预约")
    public Result<Boolean> cancel(
            @PathVariable Long reservationId,
            @RequestParam(required = false) String reason
    ) {
        boolean success = reservationService.cancelReservation(reservationId, reason);
        return success ? Result.success() : Result.error("取消失败");
    }

    @GetMapping("/{reservationId}")
    @ApiOperation("获取预约详情")
    public Result<ChargingReservation> detail(@PathVariable Long reservationId) {
        ChargingReservation reservation = reservationService.getReservation(reservationId);
        if (reservation == null) {
            return Result.error("预约不存在");
        }
        return Result.success(reservation);
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户预约列表")
    public Result<List<ChargingReservation>> userReservations(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<ChargingReservation> reservations = reservationService.getUserReservations(userId, status);
        return Result.success(reservations);
    }

    @GetMapping("/check")
    @ApiOperation("检查时段是否可用")
    public Result<Boolean> checkAvailability(
            @RequestParam Long pileId,
            @RequestParam Integer slotId,
            @RequestParam String startTime,
            @RequestParam String endTime
    ) {
        boolean available = reservationService.checkAvailability(
            pileId, slotId,
            LocalDateTime.parse(startTime),
            LocalDateTime.parse(endTime)
        );
        return Result.success(available);
    }
}
