package com.charging.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean delete(String key) {
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }

    public boolean incr(String key) {
        try {
            redisTemplate.opsForValue().increment(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return redisTemplate.expire(key, timeout, unit);
        } catch (Exception e) {
            return false;
        }
    }
}
