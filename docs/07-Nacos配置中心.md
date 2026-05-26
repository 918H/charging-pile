# 07. Nacos 配置中心

## 7.1 Nacos 简介

Nacos 是一个更易于构建云原生应用的动态服务发现、配置管理和服务管理平台。

### 7.1.1 核心功能

```
┌─────────────────────────────────┐
│      Nacos (服务发现 & 配置)     │
├────────────────┬────────────────┤
│ 注册中心        │ 配置中心        │
├────────────────┼────────────────┤
│ - 服务注册      │ - 配置管理      │
│ - 服务发现      │ - 版本管理      │
│ - 健康检查      │ - 灰度发布      │
│ - 负载均衡      │ - 权限管理      │
│ - 故障转移      │ - 审计日志      │
└────────────────┴────────────────┘
```

---

## 7.2 服务注册与发现

### 7.2.1 服务自动注册

```yaml
# user-service bootstrap.yaml
spring:
  application:
    name: user-service
  cloud:
    nacos:
      discovery:
        # Nacos服务器地址
        server-addr: nacos.charging.com:8848
        # 命名空间 (生产环境使用不同的namespace隔离)
        namespace: prod
        # 分组
        group: charging-platform
        # 服务IP (可选，不指定则自动获取)
        ip: ${server.address}
        # 服务端口
        port: ${server.port}
        # 元数据 (自定义标签)
        metadata:
          version: 1.0.0
          region: beijing
          env: production
        # 健康检查
        heartbeat-interval: 5000
        ip-delete-timeout: 60000

server:
  port: 8081
```

### 7.2.2 服务发现示例

```java
// 方式1: 使用 Nacos 原生 SDK
@Resource
private NacosDiscoveryProperties nacosDiscoveryProperties;

public void discoverService() {
    String serviceName = "charging-service";
    String groupName = "charging-platform";
    
    // 获取所有实例
    List<ServiceInstance> instances = 
        nacosDiscoveryProperties.getNamingServiceInstance()
            .getAllInstances(serviceName, groupName);
    
    for (ServiceInstance instance : instances) {
        System.out.println("实例: " + instance.getInstanceId());
        System.out.println("IP: " + instance.getIp());
        System.out.println("端口: " + instance.getPort());
        System.out.println("健康: " + instance.isHealthy());
    }
}

// 方式2: 使用 Spring Cloud 标准接口 (推荐)
@Resource
private DiscoveryClient discoveryClient;

public void discoverService() {
    String serviceName = "charging-service";
    
    // 获取服务实例列表
    List<ServiceInstance> instances = 
        discoveryClient.getInstances(serviceName);
    
    for (ServiceInstance instance : instances) {
        System.out.println("实例ID: " + instance.getInstanceId());
        System.out.println("IP: " + instance.getHost());
        System.out.println("端口: " + instance.getPort());
        System.out.println("元数据: " + instance.getMetadata());
    }
}
```

### 7.2.3 健康检查机制

```yaml
# 健康检查配置
spring:
  cloud:
    nacos:
      discovery:
        # 心跳间隔 (ms)
        heartbeat-interval: 5000
        # IP删除超时时间 (ms)
        ip-delete-timeout: 60000
        # 健康检查URL (Spring Boot Actuator)
        health-check-url: http://localhost:8081/actuator/health

# Actuator 端点配置
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always
```

---

## 7.3 动态配置管理

### 7.3.1 配置文件结构

```
Nacos 配置组织:

Namespace (命名空间)
  └─ Group (分组)
     ├─ DataId 1: user-service-prod.yaml
     ├─ DataId 2: charging-service-prod.yaml
     ├─ DataId 3: order-service-prod.yaml
     ├─ DataId 4: payment-service-prod.yaml
     └─ DataId 5: common-service-prod.yaml

命名约定:
{application-name}-{environment}.{file-extension}

例如:
- user-service-prod.yaml
- user-service-test.yaml
- user-service-dev.yaml
```

### 7.3.2 配置示例

#### 用户服务配置 (user-service-prod.yaml)

```yaml
# 服务配置
spring:
  datasource:
    url: jdbc:mysql://mysql.charging.com:3306/charging_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  redis:
    host: redis.charging.com
    port: 6379
    password: ${REDIS_PASSWORD}
    timeout: 3000
    database: 0
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

# JWT 配置
jwt:
  secret: ${JWT_SECRET}
  expiration: 86400
  refreshExpiration: 604800
  tokenHeader: "Authorization"
  tokenPrefix: "Bearer "

# 业务配置
user:
  passwordPolicy:
    minLength: 8
    maxLength: 20
    requireUpperCase: true
    requireLowerCase: true
    requireDigit: true
    requireSpecialChar: true
  loginPolicy:
    maxFailedAttempts: 5
    lockDurationMinutes: 30
  sessionTimeout: 1800

# 日志配置
logging:
  level:
    root: INFO
    com.charging: DEBUG
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
```

#### 充电桩服务配置 (charging-service-prod.yaml)

```yaml
spring:
  datasource:
    url: jdbc:mysql://mysql.charging.com:3306/charging_db
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  
  redis:
    host: redis.charging.com
    port: 6379
    password: ${REDIS_PASSWORD}

charging:
  # 缓存配置
  cache:
    pileListTtl: 1800  # 30分钟
    pileDetailTtl: 3600  # 1小时
    statusUpdateTtl: 300  # 5分钟
  
  # 地理位置配置
  location:
    # 搜索半径 (km)
    searchRadius: 10
    # 最大返回结果数
    maxResults: 50
  
  # 实时更新配置
  realtime:
    # 是否启用实时推送
    enabled: true
    # 推送间隔 (秒)
    pushInterval: 5
    # 连接超时 (秒)
    connectionTimeout: 30

logging:
  level:
    root: INFO
    com.charging: DEBUG
```

### 7.3.3 配置客户端集成

```java
// 方式1: @Value 注解 (简单值)
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Value("${user.passwordPolicy.minLength}")
    private Integer passwordMinLength;
    
    @Value("${user.sessionTimeout}")
    private Integer sessionTimeout;
}

// 方式2: @ConfigurationProperties (配置类)
@ConfigurationProperties(prefix = "user.password-policy")
@Data
public class PasswordPolicy {
    private Integer minLength;
    private Integer maxLength;
    private Boolean requireUpperCase;
    private Boolean requireLowerCase;
    private Boolean requireDigit;
    private Boolean requireSpecialChar;
}

@Configuration
@EnableConfigurationProperties(PasswordPolicy.class)
public class UserConfig {
    
    @Bean
    public PasswordValidator passwordValidator(PasswordPolicy policy) {
        return new PasswordValidator(policy);
    }
}

// 方式3: 动态监听配置变更
@Service
public class ConfigListener {
    
    @NacosValue(value = "${charging.cache.pileListTtl}", autoRefreshed = true)
    private Long pileListTtl;
    
    @NacosConfigListener(dataId = "charging-service-prod", groupId = "charging-platform")
    public void onConfigChange(String configInfo) {
        System.out.println("配置已更新: " + configInfo);
        // 处理配置变更逻辑
    }
}

// 方式4: 配置变更刷新
@RefreshScope
@Service
public class ChargingService {
    
    @Value("${charging.cache.pileListTtl}")
    private Long pileListTtl;
    
    public List<ChargingPile> getPileList() {
        // 使用最新的配置值
        return chargingCache.get("piles", pileListTtl);
    }
}
```

---

## 7.4 配置发布与灰度

### 7.4.1 配置版本管理

```
配置发布流程:

1. 开发环境测试
   - 在 dev namespace 创建配置
   - 本地测试验证

2. 测试环境验证
   - 复制到 test namespace
   - QA 团队验证

3. 灰度发布 (可选)
   - 在 prod namespace 创建新版本
   - 配置灰度规则 (例如: 10% 流量)
   - 监控和验证

4. 全量发布
   - 灰度通过后进行全量发布
   - 100% 流量使用新配置

5. 回滚准备
   - 保留旧版本配置
   - 快速回滚
```

### 7.4.2 Nacos 控制台操作

```
Web 控制台:
http://nacos-server:8848/nacos

操作步骤:

1. 登录 (用户名/密码: nacos/nacos)

2. 选择命名空间: prod

3. 配置管理 -> 配置列表

4. 新增配置
   - Data ID: charging-service-prod
   - Group: charging-platform
   - 配置格式: YAML
   - 配置内容: (粘贴YAML)
   - 发布

5. 编辑配置
   - 选择配置
   - 编辑内容
   - 发布

6. 查看历史
   - 配置详情 -> 历史版本
   - 对比差异
   - 快速回滚
```

---

## 7.5 服务隔离与分组

### 7.5.1 多环境隔离

```yaml
# 通过 Namespace 隔离不同环境

# 开发环境
spring.cloud.nacos.discovery.namespace: dev
spring.cloud.nacos.config.namespace: dev

# 测试环境
spring.cloud.nacos.discovery.namespace: test
spring.cloud.nacos.config.namespace: test

# 预发布环境
spring.cloud.nacos.discovery.namespace: pre-prod
spring.cloud.nacos.config.namespace: pre-prod

# 生产环境
spring.cloud.nacos.discovery.namespace: prod
spring.cloud.nacos.config.namespace: prod
```

### 7.5.2 服务分组管理

```yaml
# 按业务模块分组

# 用户相关服务
user-service:
  group: charging-platform

# 充电相关服务
charging-service:
  group: charging-platform

# 订单相关服务
order-service:
  group: charging-platform

# 支付相关服务
payment-service:
  group: charging-platform

# 公共服务
common-service:
  group: charging-platform
```

---

## 7.6 常见配置项

### 7.6.1 数据库配置

```yaml
# MySQL 主库配置
spring:
  datasource:
    primary:
      url: jdbc:mysql://mysql-master:3306/charging_db
      username: root
      password: ${DB_PRIMARY_PASSWORD}
    secondary:  # 读从库 (可选)
      url: jdbc:mysql://mysql-slave1:3306/charging_db
      username: root
      password: ${DB_SECONDARY_PASSWORD}
```

### 7.6.2 Redis 配置

```yaml
# Redis 缓存配置
spring:
  redis:
    cluster:
      nodes:
        - redis-node1:6379
        - redis-node2:6379
        - redis-node3:6379
    password: ${REDIS_PASSWORD}
    timeout: 3000
    lettuce:
      pool:
        max-active: 10
        max-idle: 5
        min-idle: 2
```

### 7.6.3 MQ 配置

```yaml
# RabbitMQ 配置
spring:
  rabbitmq:
    host: rabbitmq.charging.com
    port: 5672
    username: guest
    password: ${RABBITMQ_PASSWORD}
    virtual-host: /
    publisher-confirm-type: correlated
    publisher-returns: true
```

---

## 7.7 最佳实践

### 7.7.1 配置命名规范

```
配置ID命名:
{模块名}-{环境}.{格式}

例如:
- user-service-prod.yaml
- charging-service-test.properties
- order-service-dev.yaml

分组命名:
{项目名或企业名}

例如:
- charging-platform
- company-billing
```

### 7.7.2 敏感信息处理

```
不要在配置中直接写入敏感信息:

✗ 错误做法:
database:
  password: root123456

✓ 正确做法:
database:
  password: ${DB_PASSWORD}  # 使用环境变量

# 敏感信息来源:
1. 环境变量
2. 密钥管理服务 (KMS)
3. Vault 等密钥存储
4. 配置中心的加密存储
```

### 7.7.3 配置版本管理

```
始终保持旧版本配置:

理由:
1. 快速回滚
2. 问题追踪
3. 历史查证

做法:
- 发布前备份
- 保留最近10个版本
- 记录修改人和修改原因
- 通过审核流程发布
```

