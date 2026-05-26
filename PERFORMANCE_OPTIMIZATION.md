# 性能优化建议

## 一、数据库优化

### 1. 索引优化

#### 现有索引
```sql
-- charging_order 表
INDEX idx_user_id (user_id)
INDEX idx_status (status)
INDEX idx_created_at (created_at)

-- charging_review 表
INDEX idx_user_id (user_id)
INDEX idx_pile_id (pile_id)
UNIQUE KEY uk_user_order (user_id, order_id)
```

#### 建议添加
```sql
-- 订单列表查询优化
CREATE INDEX idx_user_status_created 
ON charging_order(user_id, status, created_at);

-- 预约可用性检查优化
CREATE INDEX idx_pile_slot_status_time 
ON charging_reservation(pile_id, slot_id, status, start_time);

-- 评价列表查询优化
CREATE INDEX idx_pile_created 
ON charging_review(pile_id, created_at);
```

### 2. 查询优化

#### 优化前
```java
// ❌ 问题：SELECT * + 多次查询
List<ChargingOrder> orders = chargingOrderMapper.selectList(null);
for (ChargingOrder order : orders) {
    User user = userService.getById(order.getUserId());
    ChargingPile pile = chargingService.getPile(order.getPileId());
}
```

#### 优化后
```java
// ✅ 方案：指定字段 + JOIN 查询
@Select("""
    SELECT 
        o.order_id,
        o.order_number,
        o.user_id,
        o.pile_id,
        o.total_amount,
        o.status,
        o.created_at,
        u.nick_name,
        p.pile_name
    FROM charging_order o
    LEFT JOIN sys_user u ON o.user_id = u.user_id
    LEFT JOIN charging_pile p ON o.pile_id = p.pile_id
    WHERE o.status = #{status}
    ORDER BY o.created_at DESC
    LIMIT #{offset}, #{limit}
""")
List<OrderVO> getOrderList(int status, int offset, int limit);
```

### 3. 分表策略

当订单表超过 100 万行：
```sql
-- 按时间分表
charging_order_2026_01
charging_order_2026_02
charging_order_2026_03

-- 或使用 MyCat/ShardingSphere 自动分片
```

---

## 二、缓存优化

### 1. Redis 缓存策略

#### 缓存内容
```java
// 用户信息 (30 分钟)
redisTemplate.opsForValue().set("user:" + userId, user, 30, TimeUnit.MINUTES);

// 充电桩信息 (1 小时)
redisTemplate.opsForValue().set("pile:" + pileId, pile, 1, TimeUnit.HOURS);

// 电价信息 (24 小时)
redisTemplate.opsForValue().set("price:" + pileId + ":" + date, price, 24, TimeUnit.HOURS);

// 订单详情 (24 小时)
redisTemplate.opsForValue().set("order:" + orderNumber, order, 24, TimeUnit.HOURS);
```

#### 缓存更新
```java
// 更新时删除缓存
@Transactional
public void updateOrder(Order order) {
    chargingOrderMapper.updateById(order);
    redisTemplate.delete("order:" + order.getOrderNumber());
}
```

### 2. 热点数据预加载

```java
@Component
public class DataPreloader {
    
    @Scheduled(cron = "0 0 2 * * ?") // 每天凌晨 2 点
    public void preloadPileData() {
        List<ChargingPile> piles = chargingPileMapper.selectList(null);
        for (ChargingPile pile : piles) {
            redisTemplate.opsForValue().set(
                "pile:" + pile.getPileId(), 
                pile, 
                1, 
                TimeUnit.HOURS
            );
        }
    }
}
```

### 3. 分布式锁

```java
// 防止重复下单
public String createOrder(OrderRequest request) {
    String lockKey = "lock:order:" + request.getUserId();
    
    RLock lock = redissonClient.getLock(lockKey);
    if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
        try {
            return doCreateOrder(request);
        } finally {
            lock.unlock();
        }
    }
    throw new BusinessException("系统繁忙，请稍后重试");
}
```

---

## 三、接口优化

### 1. 分页查询

```java
// ✅ 正确：使用分页
@GetMapping("/list")
public Result<Page<ChargingOrder>> list(
    @RequestParam(defaultValue = "1") int current,
    @RequestParam(defaultValue = "20") int size
) {
    Page<ChargingOrder> page = new Page<>(current, size);
    return Result.success(chargingOrderMapper.selectPage(page, null));
}

// ❌ 错误：不分页
@GetMapping("/list")
public Result<List<ChargingOrder>> list() {
    return Result.success(chargingOrderMapper.selectList(null)); // 可能返回数万条
}
```

### 2. 批量操作

```java
// ✅ 批量插入
@Transactional
public void batchInsert(List<ChargingOrder> orders) {
    for (ChargingOrder order : orders) {
        chargingOrderMapper.insert(order);
    }
}

// ✅ 使用 MyBatis 批量
@Insert("<script>INSERT INTO charging_order ...</script>")
int batchInsert(@Param("list") List<ChargingOrder> orders);
```

### 3. 异步处理

```java
// 异步发送通知
@Async
public void sendNotification(Long orderId) {
    ChargingOrder order = chargingOrderMapper.selectById(orderId);
    notificationService.sendSMS(order.getUserId(), "充电完成");
}

// 配置线程池
@Configuration
@EnableAsync
public class AsyncConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(100);
        return executor;
    }
}
```

---

## 四、前端优化

### 1. 小程序优化

#### 图片优化
```javascript
// ✅ 使用压缩图片
const imageUrl = 'https://cdn.example.com/pile-thumb.jpg'; // 20kb

// ❌ 使用原图
const imageUrl = 'https://cdn.example.com/pile-original.jpg'; // 2mb
```

#### 防抖节流
```javascript
// 搜索框防抖
let timer;
onSearchInput(e) {
  clearTimeout(timer);
  timer = setTimeout(() => {
    this.search(e.detail.value);
  }, 300);
}

// 按钮节流
let lastClick = 0;
onSubmit() {
  const now = Date.now();
  if (now - lastClick < 500) return;
  lastClick = now;
  this.doSubmit();
}
```

#### 列表渲染优化
```xml
<!-- ✅ 指定唯一的 key -->
<view wx:for="{{orders}}" wx:key="orderId">

<!-- ❌ 使用 index 作为 key -->
<view wx:for="{{orders}}" wx:key="*this">
```

### 2. Vue 优化

#### 按需加载
```javascript
// 路由懒加载
const routes = [
  {
    path: '/refund',
    component: () => import('@/views/refund/list.vue')
  }
];

// 组件懒加载
const CouponTable = defineAsyncComponent(() => 
  import('@/components/CouponTable.vue')
);
```

#### 计算属性缓存
```javascript
// ✅ 使用 computed 缓存
computed: {
  filteredOrders() {
    return this.orders.filter(o => o.status === this.status);
  }
}

// ❌ 在模板中计算
{{ orders.filter(o => o.status === status).length }}
```

---

## 五、监控告警

### 1. 应用监控

```yaml
# Prometheus 配置
scrape_configs:
  - job_name: 'charging-platform'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: 
        - 'user-service:8081'
        - 'order-service:8083'
        - 'payment-service:8084'
```

### 2. 关键指标

```java
// 接口响应时间
@Timed(value = "order.create", description = "创建订单耗时")
public Result<Long> create(@RequestBody OrderRequest request) {
}

// 业务指标计数器
private final Counter orderCounter = Counter.build()
    .name("order_total")
    .help("订单总数")
    .labelNames("status")
    .register();

orderCounter.labels("success").inc();
```

### 3. 告警规则

```yaml
# Grafana 告警
- alert: HighErrorRate
  expr: rate(http_requests_total{status="500"}[5m]) > 0.1
  for: 5m
  
- alert: SlowAPI
  expr: http_request_duration_seconds{quantile="0.95"} > 2
  for: 5m
```

---

## 六、JVM 优化

```bash
# 生产环境 JVM 参数
JAVA_OPTS="-Xms2g -Xmx2g \
           -XX:NewSize=512m \
           -XX:MaxNewSize=512m \
           -XX:+UseG1GC \
           -XX:MaxGCPauseMillis=200 \
           -Xlog:gc*:file=/logs/gc.log:time"
```

---

**更新日期**: 2026-01-26
