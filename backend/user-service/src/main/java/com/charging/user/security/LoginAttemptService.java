package com.charging.user.security;

import com.charging.common.redis.RedisUtil;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class LoginAttemptService {

    @Resource
    private RedisUtil redisUtil;

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MINUTES = 30;

    public void loginFailed(String username) {
        String key = "login:attempts:" + username;
        redisUtil.incr(key);
        redisUtil.expire(key, LOCK_TIME_MINUTES, TimeUnit.MINUTES);
    }

    public void loginSucceeded(String username) {
        String key = "login:attempts:" + username;
        redisUtil.delete(key);
    }

    public boolean isLocked(String username) {
        String key = "login:attempts:" + username;
        Object attempts = redisUtil.get(key);
        if (attempts == null) {
            return false;
        }
        return Integer.parseInt(attempts.toString()) >= MAX_ATTEMPTS;
    }

    public long getLockTimeMinutes() {
        return LOCK_TIME_MINUTES;
    }
}
