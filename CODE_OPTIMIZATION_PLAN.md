# 代码优化实施计划

## 第一阶段：代码统一和重构 ✅ (进行中)

### 1.1 已完成的统一组件

**位置**: `backend/charging-common/charging-common-core`

| 类 | 路径 | 说明 |
|----|------|------|
| `R.java` | `response/R.java` | 统一响应结果封装 |
| `BaseException.java` | `exception/BaseException.java` | 基础异常类 |
| `BusinessException.java` | `exception/BusinessException.java` | 业务异常类 |
| `AccessDeniedException.java` | `exception/AccessDeniedException.java` | 访问拒绝异常 |
| `UnauthorizedException.java` | `exception/UnauthorizedException.java` | 未授权异常 |
| `GlobalExceptionHandler.java` | `config/GlobalExceptionHandler.java` | 全局异常处理器 |

### 1.2 待完成：删除各服务重复代码

**需要删除的文件** (共 6 个):
```
/usr/local/user-service/src/main/java/com/charging/user/common/Result.java
/charging-service/src/main/java/com/charging/charging/common/Result.java
/coupon-service/src/main/java/com/charging/coupon/common/Result.java
/payment-service/src/main/java/com/charging/payment/common/Result.java
/order-service/src/main/java/com/charging/order/common/Result.java
/common-service/src/main/java/com/charging/common/common/Result.java
```

**需要替换的导入**:
```java
// 旧
import com.charging.user.common.Result;
// 新
import com.charging.common.core.response.R;
```

**需要删除的类引用**:
- 所有 `Result.success()` → `R.ok()`
- 所有 `Result.error()` → `R.fail()`

### 1.3 迁移步骤

1. ✅ 创建 common-core 统一类
2. 🔄 逐个服务替换引用
3.  删除重复的 Result.java
4.  编译验证
5.  运行测试

---

## 第二阶段：配置优化

### 2.1 Druid 数据库连接池配置

**添加到各服务的 bootstrap.yaml**:
```yaml
spring:
  datasource:
    type: com.alibaba.druid.pool.DruidDataSource
    druid:
      driver-class-name: com.mysql.cj.jdbc.Driver
      url: jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${MYSQL_DATABASE}?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
      username: ${MYSQL_USERNAME:root}
      password: ${MYSQL_PASSWORD:root}
      initial-size: 5
      min-idle: 10
      max-active: 20
      max-wait: 60000
      validation-query: SELECT 1
      test-while-idle: true
      test-on-borrow: false
      test-on-return: false
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      filters: stat,wall,slf4j
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=5000
      web-stat-filter:
        enabled: true
        url-pattern: /*
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*"
      stat-view-servlet:
        enabled: true
        url-pattern: /druid/*
        login-username: admin
        login-password: charging123
```

### 2.2 统一日志配置

**创建文件**: `backend/charging-common/charging-common-core/src/main/resources/logback-spring.xml`

配置内容：
- 异步日志（提升性能）
- 日志滚动策略（按天 + 大小）
- 日志脱敏（手机号、身份证、银行卡）
- 分布式追踪（TraceId）

---

## 第三阶段：中间件增强

### 3.1 RabbitMQ 整合

**添加依赖**:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-amqp</artifactId>
</dependency>
```

**配置项**:
- 订单创建消息
- 支付成功通知
- 积分变更通知
- 死信队列
- 消息重试机制

### 3.2 Redis 工具完善

**新增 RedisUtil 方法**:
- 分布式锁：`lock()`, `unlock()`
- Hash 操作：`hGet()`, `hSet()`, `hDel()`
- List 操作：`lPush()`, `rPop()`
- Set 操作：`sAdd()`, `sMembers()`
- ZSet 操作：`zAdd()`, `zRange()`
- Pipeline 批量操作

---

## 第四阶段：微服务增强

### 4.1 Sentinel 流量控制

**添加依赖**:
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

**配置**:
- 接口 QPS 限制
- 熔断降级策略
- 系统自适应保护
- Sentinel Dashboard 集成

### 4.2 Seata 分布式事务

**添加依赖**:
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-seata</artifactId>
</dependency>
```

**应用场景**:
- 订单创建 + 扣减积分 + 支付
- 优惠券核销 + 订单更新
- 充值 + 余额更新 + 积分赠送

---

## 第五阶段：前端优化

### 5.1 Vite 配置优化

**添加插件**:
- `vite-plugin-compression` (gzip 压缩)
- `unplugin-vue-components` (Element Plus 按需引入)
- `vite-plugin-visualizer` (打包分析)

### 5.2 路由懒加载

```javascript
const routes = [
  {
    path: '/user',
    name: 'User',
    component: () => import('@/views/user/index.vue')
  }
]
```

### 5.3 打包优化

```javascript
build: {
  rollupOptions: {
    output: {
      manualChunks: {
        'element-plus': ['element-plus'],
        'vue-vendor': ['vue', 'vue-router', 'pinia']
      }
    }
  }
}
```

---

## 优化收益

| 优化项 | 影响 | 收益 |
|--------|------|------|
| 统一响应类 | 删除 6 个重复文件 | 代码复用率↑，维护成本↓ |
| 统一异常处理 | 删除重复配置 | 异常处理标准化 |
| Druid 配置 | 连接池性能优化 | 数据库访问性能↑20% |
| 异步日志 | 日志 I/O 优化 | 请求响应时间↓10ms |
| Sentinel | 流量保护 | 系统稳定性↑ |
| Seata | 分布式事务 | 数据一致性保障 |
| 前端懒加载 | 首屏加载优化 | 首屏时间↓40% |

---

## 时间估算

| 阶段 | 工时 | 优先级 |
|------|------|--------|
| 第一阶段：代码重构 | 1-2 天 | 🔴 高 |
| 第二阶段：配置优化 | 1 天 | 🟡 中 |
| 第三阶段：中间件增强 | 2-3 天 | 🟡 中 |
| 第四阶段：微服务增强 | 3-4 天 | 🔴 高 |
| 第五阶段：前端优化 | 1-2 天 | 🟢 低 |

**总计**: 8-12 个工作日

---

## 验证清单

优化完成后需要验证：

- [ ] 所有服务能正常编译
- [ ] 单元测试通过
- [ ] API 接口正常返回
- [ ] 异常处理符合预期
- [ ] 日志输出正常
- [ ] 性能指标达标
- [ ] 文档同步更新

