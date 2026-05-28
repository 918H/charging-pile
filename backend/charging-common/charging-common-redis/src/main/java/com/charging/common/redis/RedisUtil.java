package com.charging.common.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String DEFAULT_LOCK_KEY_PREFIX = "lock:";
    private static final long DEFAULT_LOCK_EXPIRE_SECONDS = 30;

    // ==================== String 操作 ====================

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
            return Boolean.TRUE.equals(redisTemplate.delete(key));
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

    public long incrBy(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            return -1;
        }
    }

    // ==================== Hash 操作 ====================

    public boolean hSet(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object hGet(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<Object, Object> hGetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    public boolean hDel(String key, Object... fields) {
        try {
            return redisTemplate.opsForHash().delete(key, fields) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hExists(String key, String field) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, field));
        } catch (Exception e) {
            return false;
        }
    }

    public long hSize(String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    public Set<Object> hKeys(String key) {
        try {
            return redisTemplate.opsForHash().keys(key);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    // ==================== List 操作 ====================

    public boolean lPush(String key, Object value) {
        try {
            redisTemplate.opsForList().leftPush(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean rPush(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Object lGet(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            return null;
        }
    }

    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public boolean lRemove(String key, long count, Object value) {
        try {
            redisTemplate.opsForList().remove(key, count, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean lTrim(String key, long start, long end) {
        try {
            redisTemplate.opsForList().trim(key, start, end);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long lSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            return null;
        }
    }

    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            return null;
        }
    }

    // ==================== Set 操作 ====================

    public boolean sAdd(String key, Object value) {
        try {
            redisTemplate.opsForSet().add(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sAdd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public boolean sRemove(String key, Object... values) {
        try {
            redisTemplate.opsForSet().remove(key, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sIsMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            return false;
        }
    }

    public long sSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== ZSet 操作 ====================

    public boolean zAdd(String key, Object value, double score) {
        try {
            redisTemplate.opsForZSet().add(key, value, score);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public Set<Object> zReverseRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public boolean zRemove(String key, Object... values) {
        try {
            redisTemplate.opsForZSet().remove(key, values);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public long zRank(String key, Object value) {
        try {
            Long rank = redisTemplate.opsForZSet().rank(key, value);
            return rank != null ? rank : -1;
        } catch (Exception e) {
            return -1;
        }
    }

    public long zSize(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    // ==================== 分布式锁 ====================

    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, DEFAULT_LOCK_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

    public boolean tryLock(String lockKey, long expireTime, TimeUnit unit) {
        try {
            String key = DEFAULT_LOCK_KEY_PREFIX + lockKey;
            String requestId = UUID.randomUUID().toString();
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, requestId, expireTime, unit);
            
            if (Boolean.TRUE.equals(success)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean tryLockWithWait(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        try {
            String key = DEFAULT_LOCK_KEY_PREFIX + lockKey;
            String requestId = UUID.randomUUID().toString();
            boolean acquired = false;
            
            long sleepTimeMs = TimeUnit.MILLISECONDS.convert(waitTime, unit);
            long endTime = System.currentTimeMillis() + sleepTimeMs;
            
            while (System.currentTimeMillis() < endTime) {
                Boolean success = redisTemplate.opsForValue()
                    .setIfAbsent(key, requestId, leaseTime, unit);
                
                if (Boolean.TRUE.equals(success)) {
                    acquired = true;
                    break;
                }
                Thread.sleep(100);
            }
            
            return acquired;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean unlock(String lockKey) {
        return unlock(DEFAULT_LOCK_KEY_PREFIX + lockKey);
    }

    public boolean unlockWithRequest(String lockKey, String requestId) {
        String key = DEFAULT_LOCK_KEY_PREFIX + lockKey;
        try {
            String currentValue = (String) redisTemplate.opsForValue().get(key);
            if (requestId.equals(currentValue)) {
                redisTemplate.delete(key);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String generateLockRequestId() {
        return UUID.randomUUID().toString();
    }

    // ==================== 批量操作 ====================

    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception e) {
            return Collections.emptySet();
        }
    }

    public boolean deletePattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
