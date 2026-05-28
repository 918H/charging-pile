# 📝 优化代码示例大全

## 目录
1. [统一响应类 R](#1-统一响应类r)
2. [全局异常处理](#2-全局异常处理)
3. [敏感信息脱敏](#3-敏感信息脱敏)
4. [Redis 分布式锁](#4-redis分布式锁)
5. [分页查询封装](#5-分页查询封装)
6. [Sentinel 限流](#6-sentinel限流)
7. [前端性能优化](#7-前端性能优化)

---

## 1. 统一响应类 R

### 完整代码

```java
// 位置：backend/charging-common/charging-common-core/src/main/java/com/charging/common/core/response/R.java
package com.charging.common.core.response;

import lombok.Data;
import java.io.Serializable;

@Data
public class R<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Integer code;      // 状态码
    private String message;    // 消息
    private T data;           // 数据
    private Long timestamp;   // 时间戳
    
    // 成功响应（无数据）
    public static <T> R<T> ok() {
        return restResult(200, "success", null);
    }
    
    // 成功响应（有数据）
    public static <T> R<T> ok(T data) {
        return restResult(200, "success", data);
    }
    
    // 成功响应（自定义消息）
    public static <T> R<T> ok(String message, T data) {
        return restResult(200, message, data);
    }
    
    // 失败响应
    public static <T> R<T> fail(String message) {
        return restResult(500, message, null);
    }
    
    // 失败响应（自定义状态码）
    public static <T> R<T> fail(Integer code, String message) {
        return restResult(code, message, null);
    }
    
    // 失败响应（自定义状态码和数据）
    public static <T> R<T> fail(Integer code, String message, T data) {
        return restResult(code, message, data);
    }
    
    // 通用响应构建方法
    public static <T> R<T> restResult(Integer code, String message, T data) {
        R<T> result = new R<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    // 判断是否成功
    public static <T> Boolean isSuccess(R<T> result) {
        return result != null && result.getCode() != null && result.getCode() == 200;
    }
}
```

### 使用示例

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 查询单个用户
    @GetMapping("/{id}")
    public R<UserVO> getUser(@PathVariable Long id) {
        UserVO user = userService.getById(id);
        return R.ok(user);
    }
    
    // 创建用户
    @PostMapping
    public R<Void> createUser(@RequestBody @Validated UserDTO user) {
        userService.create(user);
        return R.ok("创建成功");
    }
    
    // 删除用户
    @DeleteMapping("/{id}")
    public R<Void> deleteUser(@PathVariable Long id) {
        if (id <= 0) {
            return R.fail(400, "用户 ID 无效");
        }
        userService.removeById(id);
        return R.ok();
    }
    
    // 更新用户
    @PutMapping("/{id}")
    public R<UserVO> updateUser(@PathVariable Long id, 
                                @RequestBody UserDTO user) {
        UserVO updated = userService.update(id, user);
        return R.ok("更新成功", updated);
    }
}
```

### API 响应示例

**成功响应**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "张三",
    "phone": "138****5678"
  },
  "timestamp": 1716912000000
}
```

**失败响应**:
```json
{
  "code": 400,
  "message": "用户 ID 无效",
  "data": null,
  "timestamp": 1716912000000
}
```

---

## 2. 全局异常处理

### 完整代码

```java
// 位置：backend/charging-common/charging-common-core/src/main/java/com/charging/common/core/config/GlobalExceptionHandler.java
package com.charging.common.core.config;

import com.charging.common.core.exception.*;
import com.charging.common.core.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 参数校验异常（RequestBody）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleMethodArgumentNotValid(
            MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("参数校验失败：{}", message);
        return R.fail(400, message);
    }
    
    // 参数绑定异常（表单）
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleBindException(BindException e) {
        String message = e.getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        log.warn("参数绑定失败：{}", message);
        return R.fail(400, message);
    }
    
    // 单个参数校验异常
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public R<Void> handleConstraintViolationException(
            ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.joining(", "));
        log.warn("参数校验失败：{}", message);
        return R.fail(400, message);
    }
    
    // 业务异常
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("业务异常：code={}, message={}", e.getCode(), e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }
    
    // 未授权异常
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public R<Void> handleUnauthorizedException(UnauthorizedException e) {
        log.warn("未授权异常：{}", e.getMessage());
        return R.fail(401, e.getMessage());
    }
    
    // 访问拒绝异常
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public R<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("访问拒绝异常：{}", e.getMessage());
        return R.fail(403, e.getMessage());
    }
    
    // 运行时异常
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常", e);
        return R.fail("服务器内部错误：" + e.getMessage());
    }
    
    // 未知异常
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public R<Void> handleException(Exception e) {
        log.error("未知异常", e);
        return R.fail("服务器内部错误");
    }
}
```

### 业务异常使用

```java
@Service
public class UserService {
    
    public void recharge(Long userId, BigDecimal amount) {
        // 参数校验
        if (userId == null || userId <= 0) {
            throw new BusinessException("用户 ID 无效");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("充值金额必须大于 0");
        }
        
        // 业务逻辑
        User user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        
        // 执行充值
        user.setBalance(user.getBalance().add(amount));
        update(user);
    }
}
```

---

## 3. 敏感信息脱敏

### 完整代码

```java
// 位置：backend/charging-common/charging-common-core/src/main/java/com/charging/common/core/util/SensitiveUtils.java
package com.charging.common.core.util;

public class SensitiveUtils {
    
    // 脱敏手机号：13812345678 -> 138****5678
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
    
    // 脱敏身份证：110101199003071234 -> 110101********1234
    public static String maskIdCard(String idCard) {
        if (idCard == null || idCard.length() < 15) {
            return idCard;
        }
        return idCard.replaceAll("(\\d{6})\\d{8}(\\w{4})", "$1********$2");
    }
    
    // 脱敏银行卡：6222021234567890123 -> 6222 **** **** 7890123
    public static String maskBankCard(String bankCard) {
        if (bankCard == null || bankCard.length() < 16) {
            return bankCard;
        }
        return bankCard.replaceAll("(\\d{4})\\d+(\\d{7})", "$1 **** **** $2");
    }
    
    // 脱敏邮箱：test@example.com -> t**t@example.com
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];
        
        if (username.length() <= 2) {
            return username + "@" + domain;
        }
        
        return username.charAt(0) + "**" + 
               username.charAt(username.length() - 1) + 
               "@" + domain;
    }
    
    // 脱敏姓名：张三 -> 张*，欧阳小明 -> 欧阳*
    public static String maskName(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        return name.substring(0, name.length() - 1) + "*";
    }
}
```

### 使用示例

```java
@Slf4j
@Service
public class UserService {
    
    public UserVO getUserDetail(Long userId) {
        User user = getById(userId);
        UserVO vo = new UserVO();
        
        // 脱敏处理
        vo.setPhone(SensitiveUtils.maskPhone(user.getPhone()));
        vo.setIdCard(SensitiveUtils.maskIdCard(user.getIdCard()));
        vo.setBankCard(SensitiveUtils.maskBankCard(user.getBankCard()));
        vo.setEmail(SensitiveUtils.maskEmail(user.getEmail()));
        vo.setRealName(SensitiveUtils.maskName(user.getRealName()));
        
        return vo;
    }
    
    public void logUserInfo(User user) {
        // 日志中脱敏
        log.info("用户充值：phone={}, amount={}", 
            SensitiveUtils.maskPhone(user.getPhone()),
            user.getAmount());
    }
}
```

### 脱敏效果

| 类型 | 原始值 | 脱敏后 |
|------|--------|--------|
| 手机 | 13812345678 | 138****5678 |
| 身份证 | 110101199003071234 | 110101********1234 |
| 银行卡 | 6222021234567890123 | 6222 **** **** 7890123 |
| 邮箱 | test@example.com | t**t@example.com |
| 姓名 | 张三 | 张* |
| 姓名 | 欧阳小明 | 欧阳* |

---

## 4. Redis 分布式锁

### 核心方法

```java
// 位置：backend/charging-common/charging-common-redis/src/main/java/com/charging/common/redis/RedisUtil.java
@Component
public class RedisUtil {
    
    // 尝试获取锁（默认 30 秒过期）
    public boolean tryLock(String lockKey) {
        return tryLock(lockKey, 30, TimeUnit.SECONDS);
    }
    
    // 尝试获取锁（自定义过期时间）
    public boolean tryLock(String lockKey, long expireTime, TimeUnit unit) {
        try {
            String key = "lock:" + lockKey;
            String requestId = UUID.randomUUID().toString();
            Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, requestId, expireTime, unit);
            return Boolean.TRUE.equals(success);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 带等待的尝试锁
    public boolean tryLockWithWait(String lockKey, long waitTime, 
                                   long leaseTime, TimeUnit unit) {
        try {
            String key = "lock:" + lockKey;
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
    
    // 释放锁
    public boolean unlock(String lockKey) {
        try {
            String key = "lock:" + lockKey;
            return redisTemplate.delete(key);
        } catch (Exception e) {
            return false;
        }
    }
    
    // 生成锁请求 ID
    public String generateLockRequestId() {
        return UUID.randomUUID().toString();
    }
    
    // 带 requestId 的释放锁
    public boolean unlockWithRequest(String lockKey, String requestId) {
        String key = "lock:" + lockKey;
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
}
```

### 使用示例

#### 4.1 订单创建（防止重复提交）

```java
@Service
public class OrderService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    public R<OrderVO> createOrder(OrderDTO dto) {
        // 生成锁 key：lock:order:create:userId:timestamp
        String lockKey = "order:create:" + dto.getUserId() + ":" 
            + (System.currentTimeMillis() / 1000);
        
        // 尝试获取锁，等待 3 秒，锁有效期 30 秒
        if (!redisUtil.tryLockWithWait(lockKey, 3, 30, TimeUnit.SECONDS)) {
            return R.fail(429, "系统繁忙，请稍后再试");
        }
        
        try {
            // 检查是否重复下单
            if (orderMapper.existsByUserIdAndTime(dto.getUserId(), 
                System.currentTimeMillis() - 60000)) {
                return R.fail("下单过于频繁");
            }
            
            // 创建订单
            Order order = new Order();
            // ... 订单逻辑
            orderMapper.insert(order);
            
            return R.ok(convertToVO(order));
            
        } finally {
            // 释放锁
            redisUtil.unlock(lockKey);
        }
    }
}
```

#### 4.2 库存扣减

```java
@Service
public class InventoryService {
    
    @Autowired
    private RedisUtil redisUtil;
    
    @Transactional(rollbackFor = Exception.class)
    public boolean deductStock(Long productId, Integer quantity) {
        String lockKey = "stock:" + productId;
        
        // 获取锁，等待 5 秒
        if (!redisUtil.tryLockWithWait(lockKey, 5, 30, TimeUnit.SECONDS)) {
            throw new BusinessException("库存锁定失败");
        }
        
        try {
            // 查询库存
            Product product = productMapper.selectById(productId);
            if (product.getStock() < quantity) {
                throw new BusinessException("库存不足");
            }
            
            // 扣减库存
            product.setStock(product.getStock() - quantity);
            productMapper.updateById(product);
            
            // 记录库存流水
            inventoryLogMapper.insert(...);
            
            return true;
            
        } finally {
            redisUtil.unlock(lockKey);
        }
    }
}
```

---

## 5. 分页查询封装

### PageResult 工具类

```java
// 位置：backend/charging-common/charging-common-db/src/main/java/com/charging/common/db/PageResult.java
package com.charging.common.db;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private List<T> records;     // 当前页数据
    private long total;          // 总记录数
    private long current;        // 当前页码
    private long size;           // 每页大小
    private long pages;          // 总页数
    private boolean hasPrevious; // 是否有上一页
    private boolean hasNext;     // 是否有下一页
    
    // 从 MyBatis-Plus IPage 转换
    public static <T> PageResult<T> fromIPage(IPage<T> iPage) {
        PageResult<T> result = new PageResult<>();
        result.setRecords(iPage.getRecords());
        result.setTotal(iPage.getTotal());
        result.setCurrent(iPage.getCurrent());
        result.setSize(iPage.getSize());
        result.setPages(iPage.getPages());
        result.setHasPrevious(iPage.getCurrent() > 1);
        result.setHasNext(iPage.getCurrent() < iPage.getPages());
        return result;
    }
    
    // 创建分页参数
    public static Page<?> createPage(long current, long size) {
        return new Page<>(current, size);
    }
}
```

### 使用示例

```java
@RestController
@RequestMapping("/order")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    // 分页查询订单
    @GetMapping("/list")
    public R<PageResult<OrderVO>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId) {
        
        // 创建查询条件
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }
        if (userId != null) {
            wrapper.eq(Order::getUserId, userId);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        
        // 分页查询
        Page<Order> page = new Page<>(pageNum, pageSize);
        IPage<Order> result = orderService.page(page, wrapper);
        
        // 转换为 PageResult
        PageResult<OrderVO> pageResult = PageResult.fromIPage(result);
        
        return R.ok(pageResult);
    }
    
    // 带复杂条件的分页查询
    @PostMapping("/search")
    public R<PageResult<OrderVO>> searchOrders(
            @RequestBody OrderSearchDTO search) {
        
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        
        // 动态查询条件
        if (StringUtils.isNotBlank(search.getOrderBy())) {
            if ("asc".equals(search.getOrderDir())) {
                wrapper.orderByAsc(search.getOrderBy());
            } else {
                wrapper.orderByDesc(search.getOrderBy());
            }
        }
        
        if (search.getStartTime() != null) {
            wrapper.ge(Order::getCreateTime, search.getStartTime());
        }
        if (search.getEndTime() != null) {
            wrapper.le(Order::getCreateTime, search.getEndTime());
        }
        
        Page<Order> page = PageResult.createPage(
            search.getPageNum(), search.getPageSize());
        
        IPage<Order> result = orderService.page(page, wrapper);
        PageResult<OrderVO> pageResult = PageResult.fromIPage(result);
        
        return R.ok(pageResult);
    }
}
```

### API 响应示例

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "orderNo": "ORD20260528001",
        "amount": 99.00,
        "status": "paid"
      }
    ],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10,
    "hasPrevious": false,
    "hasNext": true
  },
  "timestamp": 1716912000000
}
```

---

## 6. Sentinel 限流

### 配置引用

```yaml
# bootstrap.yaml
spring:
  config:
    import:
      - optional:classpath:bootstrap-sentinel.yaml
      - optional:classpath:bootstrap-druid.yaml
```

### 注解限流

```java
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

@Service
public class PaymentService {
    
    /**
     * 支付接口限流
     * - QPS 限制：50
     * - 降级响应：handleBlock
     * - 异常降级：handleFallback
     */
    @SentinelResource(
        value = "createPayment",
        blockHandler = "handleBlock",
        fallback = "handleFallback"
    )
    public R<PaymentVO> createPayment(PaymentDTO dto) {
        // 支付逻辑
        Payment payment = paymentService.create(dto);
        return R.ok(convertToVO(payment));
    }
    
    /**
     * 限流处理（QPS 超限）
     */
    public R<PaymentVO> handleBlock(PaymentDTO dto, BlockException e) {
        return R.fail(429, "请求过于频繁，请稍后再试");
    }
    
    /**
     * 降级处理（业务异常）
     */
    public R<PaymentVO> handleFallback(PaymentDTO dto, Throwable e) {
        log.error("支付失败", e);
        return R.fail("支付服务暂时不可用");
    }
}
```

### 热点参数限流

```java
@SentinelResource(value = "getProductDetail")
public R<ProductVO> getProductDetail(@PathVariable Long productId) {
    // 商品详情
    Product product = productMapper.selectById(productId);
    return R.ok(convertToVO(product));
}
```

**配置文件限流规则**:
```yaml
param-flow:
  rules:
    - resource: "getProductDetail"
      count: 10              # 每个商品 ID 最多 10 QPS
      param-index: 0         # 第一个参数 (productId)
```

---

## 7. 前端性能优化

### Vite 配置

```javascript
// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import viteCompression from 'vite-plugin-compression'

export default defineConfig({
  plugins: [
    vue(),
    // Gzip 压缩
    viteCompression({ algorithm: 'gzip', threshold: 10240 }),
    // Brotli 压缩
    viteCompression({ 
      algorithm: 'brotliCompress', 
      threshold: 10240 
    })
  ],
  build: {
    target: 'es2015',
    chunkSizeWarningLimit: 500,
    rollupOptions: {
      output: {
        // 手动拆分代码块
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'element-plus': ['element-plus'],
          'axios': ['axios']
        }
      }
    },
    // 移除 console.log
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  }
})
```

### 路由懒加载

```javascript
// router/index.js
const routes = [
  {
    path: '/user',
    component: Layout,
    children: [
      {
        path: 'list',
        name: 'UserList',
        component: () => import('@/views/user/list.vue')  // 懒加载
      }
    ]
  }
]
```

---

## 总结

以上代码示例展示了优化后的核心功能使用方法：

✅ 统一响应格式  
✅ 全局异常处理  
✅ 敏感信息脱敏  
✅ Redis 分布式锁  
✅ 分页查询封装  
✅ Sentinel 限流  
✅ 前端性能优化

**所有代码已提交到 GitHub**:  
https://github.com/918H/charging-pile

**分支**: `260526-feat-charging-scan-flow`

