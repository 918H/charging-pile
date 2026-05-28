package com.charging.user.controller;

import com.charging.common.core.response.R;
import com.charging.user.service.SignInService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Api(tags = "签到管理")
@RestController
@RequestMapping("/signin")
public class SignInController {

    @Resource
    private SignInService signInService;

    @PostMapping("/create")
    @ApiOperation("用户签到")
    public Result<Map<String, Object>> signIn(@RequestParam Long userId) {
        try {
            Map<String, Object> result = signInService.signIn(userId);
            return R.ok(result);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }

    @GetMapping("/continuous")
    @ApiOperation("获取连续签到天数")
    public Result<Integer> getContinuousDays(@RequestParam Long userId) {
        int days = signInService.getContinuousDays(userId);
        return R.ok(days);
    }

    @GetMapping("/calendar")
    @ApiOperation("获取签到日历")
    public Result<Map<String, Object>> getCalendar(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        Map<String, Object> calendar = signInService.getCalendar(userId, year, month);
        return R.ok(calendar);
    }

    @PostMapping("/makeup")
    @ApiOperation("补签")
    public Result<Boolean> makeUpSignIn(
            @RequestParam Long userId,
            @RequestParam String date
    ) {
        try {
            LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            boolean success = signInService.makeUpSignIn(userId, localDate);
            return R.ok(success);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }
}
