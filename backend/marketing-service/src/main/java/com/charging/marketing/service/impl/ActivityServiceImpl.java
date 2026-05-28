package com.charging.marketing.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.charging.common.core.response.R;
import com.charging.marketing.entity.MarketingActivity;
import com.charging.marketing.mapper.MarketingActivityMapper;
import com.charging.marketing.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
public class ActivityServiceImpl implements ActivityService {
    @Resource private MarketingActivityMapper activityMapper;
    @Override
    public R list(String status) {
        log.info("查询活动列表：status={}", status);
        LambdaQueryWrapper<MarketingActivity> wrapper = new LambdaQueryWrapper<>();
        if (status != null) wrapper.eq(MarketingActivity::getStatus, status);
        List<MarketingActivity> list = activityMapper.selectList(wrapper);
        return R.ok(list);
    }
    @Override
    public R create(String name, String type, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("创建活动：name={}, type={}", name, type);
        MarketingActivity activity = new MarketingActivity();
        activity.setName(name);
        activity.setType(type);
        activity.setStartTime(startTime);
        activity.setEndTime(endTime);
        activity.setStatus("draft");
        activityMapper.insert(activity);
        return R.ok("创建成功");
    }
    @Override
    public R launch(Long id) {
        log.info("上线活动：id={}", id);
        MarketingActivity activity = activityMapper.selectById(id);
        if (activity != null) {
            activity.setStatus("active");
            activityMapper.updateById(activity);
        }
        return R.ok("上线成功");
    }
    @Override
    public R participate(Long activityId, Long userId) { return R.ok("参与成功"); }
    @Override
    public R statistics(Long activityId) { return R.ok("统计数据"); }
}
