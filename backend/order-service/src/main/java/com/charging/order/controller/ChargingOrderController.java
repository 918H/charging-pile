package com.charging.order.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.charging.common.core.response.R;
import com.charging.order.dto.*;
import com.charging.order.entity.ChargingOrder;
import com.charging.order.service.ChargingOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Api(tags = "充电订单管理")
@RestController
@RequestMapping("/order")
public class ChargingOrderController {

    @Resource
    private ChargingOrderService chargingOrderService;

    @GetMapping("/list")
    public Result<List<ChargingOrder>> list(@RequestParam(required = false) Long userId) {
        List<ChargingOrder> list = chargingOrderService.getList();
        return R.ok(list);
    }

    @GetMapping("/page")
    public Result<Page<ChargingOrder>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long userId
    ) {
        Page<ChargingOrder> page = chargingOrderService.getPage(current, size, userId);
        return R.ok(page);
    }

    @GetMapping("/{orderId}")
    public Result<ChargingOrder> detail(@PathVariable Long orderId) {
        ChargingOrder order = chargingOrderService.getById(orderId);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }

    @GetMapping("/number/{orderNumber}")
    public Result<ChargingOrder> getByNumber(@PathVariable String orderNumber) {
        ChargingOrder order = chargingOrderService.getByOrderNumber(orderNumber);
        if (order == null) {
            return R.fail("订单不存在");
        }
        return R.ok(order);
    }

    @PostMapping("/create")
    public Result<Boolean> create(@RequestBody ChargingOrder order) {
        boolean success = chargingOrderService.save(order);
        return R.ok(success);
    }

    @PutMapping("/{orderId}")
    public Result<Boolean> update(@PathVariable Long orderId, @RequestBody ChargingOrder order) {
        order.setOrderId(orderId);
        boolean success = chargingOrderService.update(order);
        return R.ok(success);
    }

    @PutMapping("/{orderId}/cancel")
    public Result<Boolean> cancel(@PathVariable Long orderId) {
        boolean success = chargingOrderService.cancel(orderId);
        return success ? R.ok() : R.fail("取消失败");
    }

    @PostMapping("/calculate")
    public Result<OrderCalculateResponse> calculate(@RequestBody OrderCalculateRequest request) {
        OrderCalculateResponse response = chargingOrderService.calculateOrder(request);
        return R.ok(response);
    }

    @GetMapping("/user/history")
    public Result<List<ChargingOrder>> userHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<ChargingOrder> orders = chargingOrderService.getUserHistoryOrders(userId, status);
        return R.ok(orders);
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> statistics(
            @RequestParam Long userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate
    ) {
        LocalDateTime start = startDate != null ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null ? LocalDateTime.parse(endDate) : null;
        
        Map<String, Object> stats = chargingOrderService.getOrderStatistics(userId, start, end);
        return R.ok(stats);
    }

    @GetMapping("/user/uncalculated")
    public Result<List<ChargingOrder>> uncalculatedOrders(@RequestParam Long userId) {
        List<ChargingOrder> orders = chargingOrderService.getUserHistoryOrders(userId, 1);
        return R.ok(orders);
    }

    @PostMapping("/charging/start")
    @ApiOperation("扫码开始充电")
    public Result<ChargingStartResponse> startCharging(@RequestBody ChargingStartRequest request) {
        ChargingStartResponse response = chargingOrderService.startCharging(request);
        if (response.isSuccess()) {
            return R.ok(response);
        } else {
            return R.fail(response.getMessage());
        }
    }

    @PostMapping("/charging/stop")
    @ApiOperation("停止充电")
    public Result<Boolean> stopCharging(
            @RequestParam Long orderId,
            @RequestParam(required = false) String reason
    ) {
        boolean success = chargingOrderService.stopCharging(orderId, reason);
        return success ? R.ok() : R.fail("停止充电失败");
    }

    @GetMapping("/charging/progress")
    @ApiOperation("获取充电进度")
    public Result<ChargingProgressDTO> getChargingProgress(@RequestParam Long orderId) {
        ChargingProgressDTO progress = chargingOrderService.getChargingProgress(orderId);
        if (progress == null) {
            return R.fail("未找到充电订单或充电已结束");
        }
        return R.ok(progress);
    }

    @GetMapping("/charging/price")
    @ApiOperation("获取充电桩实时电价")
    public Result<BigDecimal> getChargingPrice(@RequestParam Long pileId) {
        BigDecimal price = chargingOrderService.getUnitPrice(pileId);
        return R.ok(price);
    }

    @PostMapping("/charging/occupation-fee")
    @ApiOperation("计算占位费")
    public Result<BigDecimal> calculateOccupationFee(
            @RequestParam Long pileId,
            @RequestParam String fullTime,
            @RequestParam String leaveTime
    ) {
        LocalDateTime ft = LocalDateTime.parse(fullTime);
        LocalDateTime lt = LocalDateTime.parse(leaveTime);
        BigDecimal fee = chargingOrderService.calculateOccupationFee(pileId, ft, lt);
        return R.ok(fee);
    }
}
