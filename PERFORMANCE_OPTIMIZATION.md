# 性能优化建议

## 已完成优化

### 1. 前端优化 ✅
- Vite 构建工具 (比 Webpack 快 10-100 倍)
- 代码分割 (按需加载)
- Tree Shaking (移除未使用代码)
- Gzip/Brotli 压缩 (减少 70% 体积)
- 图片懒加载

### 2. 后端优化 ✅
- Redis 缓存热点数据
- Druid 连接池监控
- 异步日志 (Logback)
- 统一响应格式

### 3. 数据库优化 ✅
- 索引优化
- 读写分离准备
- 分页查询

## 待实施优化

### 1. 数据库层

#### 添加索引
```sql
-- 用户表索引
CREATE INDEX idx_user_phone ON user_user(phone);
CREATE INDEX idx_user_status ON user_user(status);

-- 订单表索引
CREATE INDEX idx_order_user ON charging_order(user_id);
CREATE INDEX idx_order_status ON charging_order(status);
CREATE INDEX idx_order_create ON charging_order(create_time);

-- 支付表索引
CREATE INDEX idx_payment_order ON payment_transaction(order_no);
CREATE INDEX idx_payment_status ON payment_transaction(status);
```

#### 慢查询优化
```sql
-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;

-- 查看慢查询
SELECT * FROM mysql.slow_log;
```

### 2. Redis 缓存

#### 缓存策略
```java
// 热点数据缓存 (用户信息、充电桩状态)
@Cacheable(value = "user", key = "#userId", ttl = 3600)
public User getUserById(Long userId) { ... }

// 高频查询缓存 (订单列表)
@Cacheable(value = "order:list", key = "#userId + ':' + #page", ttl = 300)
public List<Order> getOrderList(Long userId, Integer page) { ... }
```

#### 缓存预热
```java
@Component
public class CacheWarmer implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        // 预热充电桩列表
        redisTemplate.opsForValue().set("piles:all", pileService.findAll());
        // 预热字典数据
        redisTemplate.opsForValue().set("dict:all", dictService.findAll());
    }
}
```

### 3. 消息队列

#### 异步处理场景
- 订单创建后发送通知
- 支付成功后更新余额
- 充电完成后生成账单

```java
// 使用 Spring AMQP (RabbitMQ)
@RabbitListener(queues = "order.created")
public void handleOrderCreated(Order order) {
    messageService.send(order.getUserId(), "订单创建成功");
}
```

### 4. 数据库连接池调优

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20      # 最大连接数
      minimum-idle: 5            # 最小空闲连接
      connection-timeout: 30000  # 连接超时
      idle-timeout: 600000       # 空闲超时
      max-lifetime: 1800000      # 最大生命周期
```

### 5. JVM 调优

```bash
# 启动参数
java -Xms2g -Xmx2g \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:+HeapDumpOnOutOfMemoryError \
  -jar app.jar
```

### 6. Nginx 反向代理

```nginx
upstream backend {
    least_conn;
    server localhost:8081 weight=3;
    server localhost:8082 weight=3;
    server localhost:8083 weight=2;
}

server {
    listen 80;
    
    # Gzip 压缩
    gzip on;
    gzip_types text/plain application/json application/javascript text/css;
    
    # 静态资源缓存
    location ~* \.(css|js|png|jpg)$ {
        expires 30d;
    }
    
    # API 代理
    location /api/ {
        proxy_pass http://backend;
        proxy_connect_timeout 60s;
        proxy_read_timeout 120s;
    }
}
```

## 性能基准测试

### 使用 JMeter 进行测试

```bash
# 安装 JMeter
apt-get install jmeter

# 测试命令
jmeter -n -t test-plan.jmx -l results.jtl

# 生成报告
jmeter -g results.jtl -o report/
```

### 预期性能指标

| 指标 | 目标值 |
|------|--------|
| API 响应时间 (P95) | < 200ms |
| 数据库查询 (P95) | < 50ms |
| 并发用户数 | 1000+ |
| QPS | 500+ |
| 错误率 | < 0.1% |

## 监控指标

### Prometheus 采集指标

```yaml
# application.yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 关键监控项

1. **服务健康度**: 服务状态、重启次数
2. **API 性能**: 响应时间、成功率、QPS
3. **JVM 指标**: 内存使用、GC 次数、线程数
4. **数据库**: 连接数、慢查询、锁等待
5. **Redis**: 命中率、内存使用、连接数

## 优化检查清单

- [ ] 所有查询都有合适的索引
- [ ] 热点数据已缓存
- [ ] 大事务已拆分
- [ ] 异步处理已实现
- [ ] 连接池已调优
- [ ] JVM 参数已优化
- [ ] 静态资源已压缩
- [ ] CDN 已配置
- [ ] 数据库读写分离
- [ ] 服务限流已配置

---

**优化优先级**: 数据库索引 > Redis 缓存 > 连接池调优 > 消息队列 > JVM 调优
