package com.charging.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.user.entity.SignInRecord;
import com.charging.user.mapper.SignInRecordMapper;
import com.charging.user.service.PointsService;
import com.charging.user.service.SignInService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class SignInServiceImpl implements SignInService {

    @Resource
    private SignInRecordMapper signInRecordMapper;

    @Resource
    private PointsService pointsService;

    private static final int[] CONTINUOUS_REWARDS = {5, 5, 5, 5, 5, 5, 10};

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> signIn(Long userId) {
        LocalDate today = LocalDate.now();
        
        LambdaQueryWrapper<SignInRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRecord::getUserId, userId)
               .eq(SignInRecord::getSignInDate, today);
        SignInRecord todayRecord = signInRecordMapper.selectOne(wrapper);
        
        if (todayRecord != null) {
            throw new RuntimeException("今日已签到");
        }
        
        int continuousDays = getContinuousDays(userId);
        continuousDays++;
        
        int rewardIndex = Math.min(continuousDays, CONTINUOUS_REWARDS.length) - 1;
        int pointsAwarded = CONTINUOUS_REWARDS[rewardIndex];
        
        SignInRecord record = new SignInRecord();
        record.setUserId(userId);
        record.setSignInDate(today);
        record.setContinuousDays(continuousDays);
        record.setPointsAwarded(pointsAwarded);
        record.setCreatedAt(new Date());
        signInRecordMapper.insert(record);
        
        pointsService.addPoints(userId, pointsAwarded, "签到奖励", null);
        
        Map<String, Object> result = new HashMap<>();
        result.put("continuousDays", continuousDays);
        result.put("pointsAwarded", pointsAwarded);
        result.put("isCompleted", continuousDays == 7);
        
        log.info("用户 {} 签到，连续 {} 天，奖励 {} 积分", userId, continuousDays, pointsAwarded);
        
        return result;
    }

    @Override
    public Integer getContinuousDays(Long userId) {
        LocalDate today = LocalDate.now();
        LambdaQueryWrapper<SignInRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRecord::getUserId, userId)
               .orderByDesc(SignInRecord::getSignInDate);
        
        List<SignInRecord> records = signInRecordMapper.selectList(wrapper);
        if (records.isEmpty()) {
            return 0;
        }
        
        int continuousDays = 0;
        LocalDate checkDate = today;
        
        for (SignInRecord record : records) {
            if (record.getSignInDate().equals(checkDate)) {
                continuousDays = record.getContinuousDays();
                break;
            } else if (record.getSignInDate().equals(checkDate.minusDays(1))) {
                checkDate = record.getSignInDate();
                continuousDays = record.getContinuousDays();
            } else {
                continuousDays = 0;
                break;
            }
        }
        
        return continuousDays;
    }

    @Override
    public Map<String, Object> getCalendar(Long userId, Integer year, Integer month) {
        if (year == null) year = LocalDate.now().getYear();
        if (month == null) month = LocalDate.now().getMonthValue();
        
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        LambdaQueryWrapper<SignInRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRecord::getUserId, userId)
               .ge(SignInRecord::getSignInDate, yearMonth.atDay(1))
               .le(SignInRecord::getSignInDate, yearMonth.atEndOfMonth());
        
        List<SignInRecord> records = signInRecordMapper.selectList(wrapper);
        
        Set<Integer> signedDays = new HashSet<>();
        Map<Integer, Integer> pointsMap = new HashMap<>();
        
        for (SignInRecord record : records) {
            int day = record.getSignInDate().getDayOfMonth();
            signedDays.add(day);
            pointsMap.put(day, record.getPointsAwarded());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("year", year);
        result.put("month", month);
        result.put("daysInMonth", daysInMonth);
        result.put("signedDays", signedDays);
        result.put("pointsMap", pointsMap);
        result.put("continuousDays", getContinuousDays(userId));
        
        return result;
    }

    @Override
    public boolean makeUpSignIn(Long userId, LocalDate date) {
        LocalDate today = LocalDate.now();
        if (date.isAfter(today) || date.isBefore(today.minusDays(7))) {
            throw new RuntimeException("只能补签 7 天内的日期");
        }
        
        LambdaQueryWrapper<SignInRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SignInRecord::getUserId, userId)
               .eq(SignInRecord::getSignInDate, date);
        SignInRecord record = signInRecordMapper.selectOne(wrapper);
        
        if (record != null) {
            throw new RuntimeException("该日期已签到");
        }
        
        int pointsAwarded = 5;
        SignInRecord makeUpRecord = new SignInRecord();
        makeUpRecord.setUserId(userId);
        makeUpRecord.setSignInDate(date);
        makeUpRecord.setContinuousDays(0);
        makeUpRecord.setPointsAwarded(pointsAwarded);
        makeUpRecord.setCreatedAt(new Date());
        signInRecordMapper.insert(makeUpRecord);
        
        pointsService.addPoints(userId, pointsAwarded, "补签奖励", null);
        
        log.info("用户 {} 补签 {}，奖励 {} 积分", userId, date, pointsAwarded);
        
        return true;
    }
}
