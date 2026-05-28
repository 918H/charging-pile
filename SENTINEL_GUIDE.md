# Sentinel 流量控制使用指南

## 简介

Sentinel 是阿里云开源的流量控制组件，提供：
- QPS 限流
- 线程池隔离
- 熔断降级
- 系统自适应保护

---

## 添加 Sentinel 支持

### 1. 添加依赖

**各服务的 pom.xml**:
```xml
<dependency>
    <groupId>com.alibaba.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
    <version>2021.0.6.1</version>
</dependency>
```

### 2. 配置引用

**bootstrap.yaml**:
```yaml
spring:
  config:
    import:
      - optional:classpath:bootstrap-sentinel.yaml
      - optional:classpath:bootstrap-druid.yaml
      - optional:classpath:logback-spring.xml
```

### 3. 环境变量

**.env**:
```bash
SENTINEL_DASHBOARD=localhost:8080
SENTINEL_API_PORT=8719
```

---

## 安装 Sentinel Dashboard

### 方式 1: 直接下载 JAR

```bash
# 下载 Dashboard
wget https://github.com/alibaba/Sentinel/releases/download/v1.8.6/sentinel-dashboard-1.8.6.jar

# 启动
java -jar sentinel-dashboard-1.8.6.jar --server.port=8080
```

### 方式 2: Docker 启动

```bash
docker run -d \
  -p 8080:8080 \
  -e SENTINEL_API_PORT=8719 \
  bladex/sentinel-dashboard
```

### 访问 Dashboard

- **地址**: http://192.168.56.180:8080
- **账号**: sentinel
- **密码**: sentinel

---

## 限流规则配置

### QPS 限流

**场景**: 限制每秒请求数

```yaml
flow:
  rules:
    - resource: "POST:/api/order/create"
      limit-app: default
      grade: 1          # QPS 模式
      count: 50         # 每秒 50 个请求
      strategy: 0       # 直接限流
      control-behavior: 0  # 快速失败
```

### 并发线程数限流

**场景**: 限制同时处理的请求数

```yaml
flow:
  rules:
    - resource: "GET:/api/charging/pile/list"
      grade: 0          # 并发线程数
      count: 20         # 最多 20 个并发
```

### 排队等待

**场景**: 平滑处理突发流量

```yaml
flow:
  rules:
    - resource: "POST:/api/payment/create"
      grade: 1
      count: 100
      control-behavior: 2  # 排队等待
      max-queueing-time-ms: 500  # 最多等待 500ms
```

### 热点参数限流

**场景**: 针对特定参数限流（如热门商品）

```yaml
param-flow:
  rules:
    - resource: "GET:/api/charging/pile/{pileId}"
      count: 10          # 每个参数值最多 10 QPS
      param-index: 0     # 第一个参数
```

---

## 熔断降级

### 响应时间降级

**场景**: 接口响应超过阈值自动熔断

```yaml
degrade:
  rules:
    - resource: "GET:/api/user/profile"
      count: 500         # 平均响应时间 500ms
      time-window: 60    # 熔断 60 秒
      grade: 0           # 响应时间模式
      min-request-amount: 10  # 最小请求数
```

### 异常比例降级

**场景**: 错误率超过阈值自动熔断

```yaml
degrade:
  rules:
    - resource: "POST:/api/payment/notify"
      count: 0.5         # 异常比例 50%
      time-window: 30    # 熔断 30 秒
      grade: 1           # 异常比例模式
      min-request-amount: 10
```

### 异常数降级

**场景**: 异常次数超过阈值自动熔断

```yaml
degrade:
  rules:
    - resource: "GET:/api/order/detail"
      count: 100         # 异常次数超过 100
      time-window: 60    # 熔断 60 秒
      grade: 2           # 异常数模式
```

---

## 系统自适应保护

**场景**: 根据系统负载自动保护

```yaml
system:
  rules:
    - highest-system-cpu: 0.7    # CPU 使用率 70%
      highest-system-load: 2.5   # 系统负载 2.5
      qps: 100                   # 总 QPS 限制
      avg-rt: 500                # 平均 RT 500ms
```

---

## 注解限流

### @SentinelResource 注解

**使用方式**:
```java
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.charging.common.core.response.R;

@Service
public class OrderService {
    
    @SentinelResource(
        value = "createOrder",
        blockHandler = "handleBlock",
        fallback = "handleFallback"
    )
    public R<OrderVO> createOrder(OrderDTO dto) {
        // 业务逻辑
        return R.ok(order);
    }
    
    // 限流处理
    public R<OrderVO> handleBlock(OrderDTO dto, BlockException e) {
        return R.fail(429, "请求过于频繁");
    }
    
    // 降级处理
    public R<OrderVO> handleFallback(OrderDTO dto, Throwable e) {
        return R.fail("服务暂时不可用");
    }
}
```

### 配置类启用

```java
@Configuration
public class SentinelAnnotationConfig {
    
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }
}
```

---

## 监控告警

### Dashboard 监控

访问 http://192.168.56.180:8080 查看：
- 实时 QPS
- 响应时间
- 通过率
- 线程数

### 告警配置

**sentinel-dashboard/conf/app.properties**:
```properties
# 告警 webhook
sentinel.dashboard.webhook=http://your-alert-service/api/alert
```

---

## 性能优化建议

### 1. 限流阈值设置

| 接口类型 | 推荐 QPS |
|---------|---------|
| 登录接口 | 20-50 |
| 查询接口 | 100-200 |
| 创建订单 | 50-100 |
| 支付回调 | 100-200 |

### 2. 降级策略

- 响应时间：>500ms 降级
- 异常比例：>50% 降级
- 熔断时长：30-60 秒

### 3. 集群限流（分布式）

```yaml
sentinel:
  datasource:
    ds1:
      nacos:
        server-addr: ${NACOS_SERVER_ADDR}
        dataId: ${spring.application.name}-sentinel.json
        rule-type: flow
```

---

## 常见问题

### Q1: 限流不生效

**原因**: 
- 未添加 sentinel 依赖
- 资源名称不匹配

**解决**:
```bash
# 检查依赖
grep -r "spring-cloud-starter-alibaba-sentinel" pom.xml

# 查看实际资源名
curl http://localhost:8719/metric
```

### Q2: Dashboard 看不到应用

**原因**:
- 心跳未上报
- 端口冲突

**解决**:
```bash
# 查看心跳日志
grep "heartbeat" logs/*.log

# 指定 API 端口
export SENTINEL_API_PORT=8720
```

### Q3: 降级后无法恢复

**原因**:
- 降级时间设置过长
- 健康检查未通过

**解决**:
```yaml
degrade:
  rules:
    - resource: "api"
      time-window: 30  # 缩短熔断时间
```

---

## 总结

Sentinel 提供：
- ✅ QPS 限流
- ✅ 并发限流
- ✅ 熔断降级
- ✅ 系统保护
- ✅ 热点参数限流
- ✅ 注解限流
- ✅ Dashboard 监控

**建议配置**:
- 全局限流：100 QPS
- 订单创建：50 QPS (排队)
- 响应时间：500ms 降级
- 系统 CPU: 70% 保护

---

**更新日期**: 2026-05-28  
**版本**: v1.0.0

