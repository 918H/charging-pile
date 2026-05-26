package com.charging.charging.controller;

import com.charging.charging.common.Result;
import com.charging.charging.dto.FaultReportRequest;
import com.charging.charging.entity.ChargingFault;
import com.charging.charging.service.FaultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Api(tags = "故障上报管理")
@RestController
@RequestMapping("/fault")
public class FaultController {

    @Resource
    private FaultService faultService;

    @PostMapping("/report")
    @ApiOperation("上报故障")
    public Result<Boolean> reportFault(@RequestBody FaultReportRequest request) {
        boolean success = faultService.reportFault(request);
        return success ? Result.success() : Result.error("上报失败");
    }

    @GetMapping("/{faultId}")
    @ApiOperation("获取故障详情")
    public Result<ChargingFault> getFaultDetail(@PathVariable Long faultId) {
        ChargingFault fault = faultService.getFaultDetail(faultId);
        if (fault == null) {
            return Result.error("未找到故障记录");
        }
        return Result.success(fault);
    }

    @GetMapping("/pile/list")
    @ApiOperation("获取充电桩故障列表")
    public Result<List<ChargingFault>> getPileFaults(
            @RequestParam Long pileId,
            @RequestParam(required = false) Integer status
    ) {
        List<ChargingFault> faults = faultService.getPileFaults(pileId, status);
        return Result.success(faults);
    }

    @GetMapping("/user/list")
    @ApiOperation("获取用户上报故障列表")
    public Result<List<ChargingFault>> getUserFaults(@RequestParam Long userId) {
        List<ChargingFault> faults = faultService.getUserFaults(userId);
        return Result.success(faults);
    }

    @PostMapping("/{faultId}/handle")
    @ApiOperation("处理故障")
    public Result<Boolean> handleFault(
            @PathVariable Long faultId,
            @RequestParam Long handlerId,
            @RequestParam String response
    ) {
        boolean success = faultService.handleFault(faultId, handlerId, response);
        return success ? Result.success() : Result.error("处理失败");
    }
}
