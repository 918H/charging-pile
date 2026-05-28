package com.charging.user.controller;

import com.charging.common.core.response.R;
import com.charging.user.dto.ExchangeRequest;
import com.charging.user.dto.PointsMallItemDTO;
import com.charging.user.entity.PointsExchangeRecord;
import com.charging.user.service.PointsMallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "积分商城")
@RestController
@RequestMapping("/points-mall")
public class PointsMallController {

    @Resource
    private PointsMallService pointsMallService;

    @GetMapping("/items")
    @ApiOperation("获取积分商城商品列表")
    public Result<List<PointsMallItemDTO>> getItems(
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status
    ) {
        List<PointsMallItemDTO> items = pointsMallService.getItems(type, status);
        return R.ok(items);
    }

    @GetMapping("/item/detail")
    @ApiOperation("获取商品详情")
    public Result<PointsMallItemDTO> getItemDetail(@RequestParam Long itemId) {
        PointsMallItemDTO item = pointsMallService.getItemDetail(itemId);
        if (item == null) {
            return R.fail("商品不存在");
        }
        return R.ok(item);
    }

    @PostMapping("/exchange")
    @ApiOperation("兑换商品")
    public Result<PointsExchangeRecord> exchangeItem(
            @RequestParam Long userId,
            @RequestBody ExchangeRequest request
    ) {
        try {
            PointsExchangeRecord record = pointsMallService.exchangeItem(userId, request);
            return R.ok(record);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/records")
    @ApiOperation("获取用户兑换记录")
    public Result<List<PointsExchangeRecord>> getRecords(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status
    ) {
        List<PointsExchangeRecord> records = pointsMallService.getUserRecords(userId, status);
        return R.ok(records);
    }

    @PostMapping("/ship")
    @ApiOperation("发货（后台管理）")
    public Result<Boolean> shipItem(
            @RequestParam Long recordId,
            @RequestParam String trackingNumber
    ) {
        try {
            pointsMallService.shipItem(recordId, trackingNumber);
            return R.ok();
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }
}
