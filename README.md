# 充电桩小程序

一个基于 Spring Cloud Alibaba 的微服务架构充电桩管理平台。

## 项目架构

### 微服务模块

| 服务名称 | 端口 | 说明 |
|---------|------|------|
| user-service | 8081 | 用户服务（认证、权限、用户管理） |
| charging-service | 8082 | 充电桩服务（充电桩管理、状态更新） |
| order-service | 8083 | 订单服务（订单创建、管理、统计） |
| payment-service | 8084 | 支付服务（支付、退款、积分） |
| common-service | 8085 | 公共服务（文件、消息、日志） |

### 技术栈

#### 后端
- Spring Boot 2.7.x
- Spring Cloud Alibaba 2021.x
- Nacos 2.1.x（服务注册与配置中心）
- MyBatis Plus 3.5.x
- MySQL 8.0
- Redis 7.x
- RabbitMQ 3.9.x

#### 前端
- Vue 3 + Vite
- Element Plus
- Pinia
- Vue Router

## 快速开始

### 1. 环境要求

- JDK 11+
- Maven 3.6+
- Node.js 16+
- Docker & Docker Compose

### 2. 启动基础设施

```bash
cd docker
docker-compose up -d
```

等待 30 秒让所有服务启动完成。

### 3. 验证服务

```bash
docker-compose ps
```

应该看到以下服务:
- nacos (端口：8848)
- mysql (端口：3306)
- redis (端口：6379)
- rabbitmq (端口：5672, 15672)

### 4. 编译后端项目

```bash
cd backend
mvn clean package -DskipTests
```

### 5. 启动微服务

```bash
# 用户服务
java -jar user-service/target/user-service-1.0.0.jar

# 充电桩服务
java -jar charging-service/target/charging-service-1.0.0.jar

# 订单服务
java -jar order-service/target/order-service-1.0.0.jar

# 支付服务
java -jar payment-service/target/payment-service-1.0.0.jar

# 公共服务
java -jar common-service/target/common-service-1.0.0.jar
```

### 6. 启动前端

```bash
cd frontend/charging-admin
npm install
npm run dev
```

访问 http://localhost:3000

## 默认账户

```
用户名：admin
密码：admin123
```

## API 端点

### 用户服务 (http://localhost:8081/api/user)
- POST /auth/login - 用户登录
- POST /auth/logout - 用户登出
- GET /profile - 获取个人资料
- PUT /profile - 更新个人资料

### 充电桩服务 (http://localhost:8082/api/charging)
- GET /list - 获取充电桩列表
- GET /{pileId} - 获取充电桩详情
- GET /nearby - 获取附近充电桩
- POST / - 新增充电桩
- PUT /{pileId} - 编辑充电桩
- DELETE /{pileId} - 删除充电桩

### 订单服务 (http://localhost:8083/api/order)
- POST /create - 创建订单
- GET /{orderId} - 获取订单详情
- GET /list - 订单列表
- PUT /{orderId}/cancel - 取消订单

### 支付服务 (http://localhost:8084/api/payment)
- POST /create - 创建支付单
- GET /{paymentId} - 获取支付详情
- POST /refund/apply - 申请退款

### 公共服务 (http://localhost:8085/api/common)
- POST /upload - 上传文件
- GET /download/{fileId} - 下载文件

## Nacos 控制台

访问 http://localhost:8848/nacos 查看服务注册和配置信息

默认账号：nacos/nacos

## 项目结构

```
charging-pile/
├── backend/                      # 后端代码
│   ├── charging-common/         # 公共模块
│   │   ├── charging-common-core/
│   │   ├── charging-common-redis/
│   │   ├── charging-common-db/
│   ├── user-service/            # 用户服务
│   ├── charging-service/        # 充电桩服务
│   ├── order-service/           # 订单服务
│   ├── payment-service/         # 支付服务
│   ├── common-service/          # 公共服务
│   └── pom.xml
├── frontend/
│   ├── charging-mini/           # 小程序（待开发）
│   └── charging-admin/          # 后台管理系统
├── docker/
│   └── docker-compose.yaml      # Docker 配置
├── config/
│   └── nacos-config/            # Nacos 配置文件
├── docs/                        # 项目文档
└── pom.xml                      # 父 POM
```

## 开发指南

### 添加新的微服务

1. 在 `backend/` 目录创建新的服务模块
2. 继承父 POM 的依赖管理
3. 添加 `@EnableDiscoveryClient` 和 `@EnableFeignClients` 注解
4. 配置 bootstrap.yaml 连接到 Nacos
5. 在父 POM 中添加新模块

### 数据库迁移

数据库初始化脚本位于 `backend/user-service/src/main/resources/db/schema.sql`

### 配置管理

所有微服务的配置通过 Nacos 管理，可以在 Nacos 控制台动态更新配置。

## 常见问题

### Q: 服务无法注册到 Nacos？
A: 检查 Nacos 服务是否正常启动，确认 bootstrap.yaml 中的 server-addr 配置正确。

### Q: 数据库连接失败？
A: 检查 MySQL 是否启动，确认 application.yaml 中的数据库连接信息正确。

### Q: 前端无法访问后端 API？
A: 确保后端服务已启动，检查 vite.config.js 中的代理配置。

## 文档

详细文档请参阅 `docs/` 目录。

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或联系开发团队。
