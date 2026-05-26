# 项目运行测试指南

## 环境要求

### 必需软件
- Docker & Docker Compose
- Java 17+
- Maven 3.8+
- Node.js 18+ (管理后台)
- MySQL 8.0+ (或 Docker)
- Redis 7.0+ (或 Docker)

## 快速启动

### 方式一：Docker 一键启动（推荐）

```bash
cd /workspace

# 1. 启动基础设施（MySQL/Redis/Nacos/RabbitMQ/Prometheus/Grafana）
cd docker && docker-compose up -d

# 2. 等待服务就绪（约 30 秒）
sleep 30

# 3. 返回项目根目录
cd /workspace

# 4. 执行一键启动脚本
./scripts/start.sh
```

### 方式二：手动启动

```bash
# 1. 启动基础设施
cd /workspace/docker
docker-compose up -d

# 2. 等待 MySQL 就绪
sleep 10

# 3. 初始化数据库
mysql -h localhost -u root -proot charging_db < /workspace/backend/user-service/src/main/resources/db/schema.sql
mysql -h localhost -u root -proot charging_db < /workspace/backend/common/src/main/resources/db/migration/V20240102005__add_membership_pile_points.sql
mysql -h localhost -u root -proot charging_db < /workspace/backend/common/src/main/resources/db/migration/V20240102006__alter_sys_user_add_membership.sql
mysql -h localhost -u root -proot charging_db < /workspace/backend/common/src/main/resources/db/migration/V20240102007__add_points_mall_package_withdrawal.sql

# 4. 编译后端
cd /workspace
mvn clean package -DskipTests

# 5. 启动微服务（按顺序）
# 5.1 启动 Nacos（已 Docker 启动）

# 5.2 启动用户服务
cd backend/user-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 5.3 启动充电服务
cd ../charging-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 5.4 启动订单服务
cd ../order-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 5.5 启动支付服务
cd ../payment-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 5.6 启动优惠券服务
cd ../coupon-service
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 5.7 启动公共服务
cd ../common
mvn spring-boot:run -Dspring-boot.run.profiles=dev &

# 6. 启动管理后台
cd /workspace/frontend/charging_admin
npm install
npm run dev
```

## 验证服务

### 1. 检查基础设施
```bash
# Docker 容器状态
docker ps

# 应该看到：mysql, redis, nacos, rabbitmq, prometheus, grafana
```

### 2. 检查微服务端口
```bash
# 用户服务
curl http://localhost:8081/user/info?userId=1

# 充电服务
curl http://localhost:8082/charging/pile/list

# 订单服务
curl http://localhost:8083/order/list

# 支付服务
curl http://localhost:8084/payment/status

# 优惠券服务
curl http://localhost:8085/coupon/list

# 公共服务
curl http://localhost:8086/common/config
```

### 3. 检查 API 接口
```bash
# 获取会员等级
curl "http://localhost:8081/membership/levels"

# 获取积分商城商品
curl "http://localhost:8081/points-mall/items"

# 获取充电套餐
curl "http://localhost:8081/charging-package/list"

# 获取附近私桩
curl "http://localhost:8081/private-pile/nearby?latitude=31.2304&longitude=121.4737&radius=5.0"
```

### 4. 检查管理后台
浏览器访问：http://localhost:3000

### 5. 检查监控
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3001 (admin/charging123)
- Nacos: http://localhost:8848 (nacos/nacos)
- RabbitMQ: http://localhost:15672 (charging/charging123)

## 预期输出

### 服务启动成功标志
```
UserApplication: Started in 15.2s
ChargingApplication: Started in 12.8s
OrderApplication: Started in 14.1s
PaymentApplication: Started in 11.5s
CouponApplication: Started in 10.9s
CommonApplication: Started in 9.8s
```

### API 测试成功响应
```json
// 会员等级
{
  "code": 200,
  "data": [
    {"levelName": "普通会员", "discountRate": 1.00},
    {"levelName": "白银会员", "discountRate": 0.98},
    {"levelName": "黄金会员", "discountRate": 0.95},
    {"levelName": "白金会员", "discountRate": 0.92},
    {"levelName": "钻石会员", "discountRate": 0.90}
  ]
}

// 积分商城商品
{
  "code": 200,
  "data": [
    {"itemName": "5 元充电优惠券", "pointsPrice": 500},
    {"itemName": "10 元充电优惠券", "pointsPrice": 1000},
    ...
  ]
}

// 充电套餐
{
  "code": 200,
  "data": [
    {"packageName": "月卡 100 元", "includedEnergy": 120.00},
    {"packageName": "季卡 280 元", "includedEnergy": 360.00},
    ...
  ]
}
```

## 常见问题

### 1. Docker 启动失败
```bash
# 检查 Docker 是否运行
docker info

# 如果未运行，启动 Docker Desktop 或 Docker 服务
```

### 2. 端口冲突
```bash
# 查看端口占用
lsof -i :8081
lsof -i :3306
lsof -i :6379

# 停止冲突进程或修改配置
```

### 3. 数据库连接失败
```bash
# 检查 MySQL 是否启动
docker ps | grep mysql

# 检查数据库是否创建
mysql -h localhost -u root -proot -e "SHOW DATABASES;"

# 重新执行初始化脚本
```

### 4. 微服务启动慢
```bash
# 查看日志
tail -f /workspace/logs/user-service.log

# 增加 JVM 内存
export JAVA_OPTS="-Xms512m -Xmx1024m"
```

## 服务端口一览表

| 服务 | 端口 | 说明 |
|------|------|------|
| 用户服务 | 8081 | 用户/会员/积分/储值卡/私桩 |
| 充电服务 | 8082 | 充电桩/充电站管理 |
| 订单服务 | 8083 | 订单/充电会话/预约/评价 |
| 支付服务 | 8084 | 支付/退款 |
| 优惠券服务 | 8085 | 优惠券管理 |
| 公共服务 | 8086 | 系统配置/公共接口 |
| 管理后台 | 3000 | Vue 前端 |
| Nacos | 8848 | 注册中心/配置中心 |
| MySQL | 3306 | 数据库 |
| Redis | 6379 | 缓存 |
| RabbitMQ | 5672 | 消息队列 |
| Prometheus | 9090 | 监控 |
| Grafana | 3001 | 仪表盘 |

## 性能指标

### 预期启动时间
- 基础设施（Docker）: 30-60 秒
- 用户服务：15-20 秒
- 充电服务：12-15 秒
- 订单服务：12-18 秒
- 支付服务：10-15 秒
- 优惠券服务：10-12 秒
- 公共服务：8-10 秒

### 内存占用
- 6 个微服务：约 2-3 GB
- MySQL: 约 500 MB
- Redis: 约 50 MB
- Nacos: 约 300 MB
- RabbitMQ: 约 150 MB
- 总计：约 3-4 GB

## 下一步

服务启动成功后，请参考以下文档：
- `COMPLETE_FEATURES_GUIDE.md` - 完整功能指南
- `MARKET_FEATURES_GUIDE.md` - 市场对标功能指南
- `API_UPDATE.md` - API 接口文档
