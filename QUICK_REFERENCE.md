# 快速参考手册

## 端口速查

| 服务 | 端口 | 说明 |
|------|------|------|
| user-service | 8081 | 用户服务 |
| charging-service | 8082 | 充电服务 |
| order-service | 8083 | 订单服务 |
| payment-service | 8084 | 支付服务 |
| coupon-service | 8085 | 优惠券服务 |
| statistics-service | 8087 | 统计服务 |
| message-service | 8088 | 消息服务 |
| support-service | 8089 | 工单服务 |
| marketing-service | 8090 | 营销服务 |
| finance-service | 8091 | 财务服务 |
| monitor-service | 8092 | 监控服务 |
| Nacos | 8848 | 注册中心 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| Prometheus | 9090 | 监控 |
| Grafana | 3000 | 可视化 |

## 常用命令

### 启动服务

```bash
# Docker 一键启动
cd deploy && ./start.sh

# 单个服务启动 (以 user-service 为例)
cd backend/user-service && mvn spring-boot:run

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f user-service
```

### 停止服务

```bash
# 停止所有
cd deploy && ./stop.sh

# 停止单个
docker-compose stop user-service
```

### 数据库操作

```bash
# 连接数据库
mysql -h localhost -u root -p

# 初始化数据库
mysql -u root -p < database/init.sql

# 执行迁移
mysql -u root -p charging_pile < database/V4__new_features.sql
```

### Git 操作

```bash
# 切换分支
git checkout 260526-feat-charging-scan-flow

# 查看提交历史
git log --oneline

# 推送代码
git push
```

## API 速查

### 用户服务
```bash
curl http://localhost:8081/user/profile?userId=1
```

### 充电服务
```bash
curl -X POST http://localhost:8082/charging/start?userId=1&pileId=1
```

### 订单服务
```bash
curl http://localhost:8083/order/list?userId=1
```

### 统计服务
```bash
curl http://localhost:8087/statistics/dashboard
```

### 监控服务
```bash
curl http://localhost:8092/monitor/piles/status
```

## 前端路由

```
/admin/dashboard      - 数据大屏
/admin/users          - 用户管理
/admin/piles          - 充电桩管理
/admin/orders         - 订单管理
/admin/messages       - 消息中心
/admin/activities     - 营销活动
/admin/finance        - 财务管理
/admin/monitor        - 设备监控
/admin/support        - 客服工单
```

## 数据库表速查

### 核心表
- user_user - 用户
- charging_pile - 充电桩
- charging_order - 订单
- payment_transaction - 支付

### 新增表
- statistics_daily - 日统计
- user_message - 用户消息
- support_ticket - 工单
- marketing_activity - 活动
- finance_transaction - 流水
- pile_status - 桩状态

## 故障排查

### 服务启动失败
```bash
# 查看日志
docker-compose logs [service-name]

# 检查端口占用
netstat -tlnp | grep 8081

# 检查数据库连接
mysql -h localhost -u root -p -e "SELECT 1"
```

### API 无响应
```bash
# 检查服务健康状态
curl http://localhost:8081/actuator/health

# 检查 Nacos 注册
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=user-service
```

### 数据库连接失败
```bash
# 检查 MySQL 是否运行
docker ps | grep mysql

# 测试连接
mysql -h localhost -u root -proot123 -e "SHOW DATABASES"
```

## 配置位置

```
backend/*/src/main/resources/
  ├── application.yaml    # 应用配置
  ├── bootstrap.yaml      # Nacos 配置
  └── logback.xml         # 日志配置

deploy/
  ├── docker-compose.yml  # Docker 配置
  └── prometheus.yml      # 监控配置
```

## 开发技巧

### 添加新接口
1. 在 Service 接口定义方法
2. 在 ServiceImpl 实现业务逻辑
3. 在 Controller 添加路由

### 添加新表
1. 在 database/ 创建迁移 SQL
2. 在 entity/ 创建实体类
3. 在 mapper/ 创建 Mapper 接口

### 添加新服务
1. 复制现有服务目录
2. 修改 pom.xml artifactId
3. 修改 application.yaml 端口
4. 更新 docker-compose.yml

## 性能优化

### Redis 缓存
```java
@Cacheable(value = "user", key = "#id")
public User getUser(Long id) { ... }
```

### 批量操作
```java
// 批量插入
userMapper.insertBatch(userList);

// 批量更新
userMapper.updateBatch(userList);
```

### 分页查询
```java
Page<User> page = new Page<>(1, 10);
userMapper.selectPage(page, wrapper);
```

---

**更新**: 2026-05-28  
**版本**: 1.0.0
