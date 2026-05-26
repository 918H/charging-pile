# 快速部署指南

本项目提供两种部署方式:Docker Compose 部署和本地开发部署。

## 方式一：Docker Compose 部署 (推荐)

### 1. 启动基础设施

```bash
cd docker
docker-compose up -d
```

这将启动以下服务:
- MySQL 8.0 (端口：3306)
- Redis 7.0 (端口：6379)
- RabbitMQ 3.9 (端口：5672, 15672)
- Nacos 2.1.2 (端口：8848, 9848, 9849)

### 2. 验证服务

```bash
docker-compose ps
```

### 3. 查看日志

```bash
docker-compose logs -f
```

### 4. 停止服务

```bash
docker-compose down
```

## 方式二：本地开发部署

### 1. 环境准备

确保已安装以下软件:
- JDK 11+
- Maven 3.6+
- Node.js 16+
- MySQL 8.0
- Redis 7.0
- RabbitMQ 3.9
- Nacos 2.1.2

### 2. 启动基础设施

可以手动安装或使用 Docker:

```bash
cd docker
docker-compose up -d
```

### 3. 配置环境变量

```bash
export NACOS_SERVER_ADDR=localhost:8848
export NACOS_NAMESPACE=dev
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=charging_db
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=root
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=
```

### 4. 编译项目

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

## 快速启动脚本

提供一键启动脚本:

```bash
./start.sh
```

该脚本将自动执行以下步骤:
1. 启动 Docker 基础设施
2. 等待 Nacos 启动
3. 编译后端项目
4. 启动所有微服务
5. 安装前端依赖
6. 提供访问地址信息

## 访问地址

启动成功后，可以访问以下地址:

| 服务 | 地址 | 说明 |
|------|------|------|
| Nacos 控制台 | http://localhost:8848/nacos | 服务注册与配置中心 |
| 用户服务 API | http://localhost:8081/api/user | 用户相关接口 |
| 充电桩服务 API | http://localhost:8082/api/charging | 充电桩相关接口 |
| 订单服务 API | http://localhost:8083/api/order | 订单相关接口 |
| 支付服务 API | http://localhost:8084/api/payment | 支付相关接口 |
| 公共服务 API | http://localhost:8085/api/common | 公共服务接口 |
| 前端管理系统 | http://localhost:3000 | Web 管理后台 |

## 默认账户

```
用户名：admin
密码：admin123
```

## 故障排查

### 服务无法启动

1. 检查端口是否被占用
2. 查看日志文件: `logs/*.log`
3. 确认基础设施服务正常

### 数据库连接失败

1. 确认 MySQL 已启动
2. 检查数据库连接配置
3. 确认数据库已初始化

### Nacos 连接失败

1. 确认 Nacos 服务正常: `curl http://localhost:8848/nacos`
2. 检查 bootstrap.yaml 配置
3. 查看 Nacos 日志

## 生产环境部署

生产环境部署建议:
1. 使用 Kubernetes 或 Docker Swarm 进行容器编排
2. 配置 Nacos 集群模式
3. 数据库使用主从复制
4. Redis 使用集群模式
5. 配置 Nginx 反向代理
6. 启用 HTTPS
7. 配置日志收集系统 (ELK)
8. 配置监控系统 (Prometheus + Grafana)
