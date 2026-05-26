# README - 充电桩小程序项目文档

## 📚 文档导航

本项目采用**微服务架构**，使用**Spring Cloud Alibaba**进行服务治理。以下是完整的项目文档体系。

---

## 📖 文档列表

### 基础架构文档

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [01-项目概述](./01-项目概述.md) | 项目背景、目标、范围、风险分析 | 所有成员 |
| [02-技术栈说明](./02-技术栈说明.md) | 前后端技术选型、版本信息、依赖关系 | 技术选型、开发者 |
| [03-整体架构设计](./03-整体架构设计.md) | 系统架构、分层设计、通讯模式、容错机制 | 架构师、高级开发 |
| [04-微服务模块设计](./04-微服务模块设计.md) | 五大微服务详细设计、接口定义、数据库表 | 后端开发、测试 |

### 核心系统文档

| 文档 | 说明 | 适合人群 |
|------|------|---------|
| [05-权限认证体系](./05-权限认证体系.md) | JWT认证、权限管理、Redis会话、密码策略 | 后端开发、安全 |
| [06-数据库设计](./06-数据库设计.md) | 核心表设计、索引策略、分表备份 | 后端开发、DBA |
| [07-Nacos配置中心](./07-Nacos配置中心.md) | 服务注册、配置管理、健康检查 | 后端开发、运维 |

### 待创建文档

- 08-项目目录结构.md
- 09-开发流程指南.md
- 10-API设计规范.md
- 11-安全架构设计.md
- 12-性能优化方案.md
- 13-监控日志方案.md
- 14-部署架构指南.md
- 15-开发规范标准.md
- 16-常见问题解答.md

---

## 🚀 快速开始

### 1. 环境准备

```bash
# 克隆项目
git clone https://github.com/918H/charging-pile.git
cd charging-pile

# 启动开发环境
docker-compose -f docker/docker-compose.yaml up -d

# 等待所有服务启动
sleep 30
```

### 2. 数据库初始化

```bash
# 初始化数据库
mysql -h localhost -u root -p < backend/user-service/src/main/resources/db/schema.sql
```

### 3. 启动后端服务

```bash
cd backend

# 编译项目
mvn clean package

# 启动各个微服务
# User Service
java -jar user-service/target/user-service-1.0.0.jar

# Charging Service
java -jar charging-service/target/charging-service-1.0.0.jar

# Order Service
java -jar order-service/target/order-service-1.0.0.jar

# Payment Service
java -jar payment-service/target/payment-service-1.0.0.jar
```

### 4. 启动前端

```bash
# 小程序开发
# 使用微信开发者工具打开 frontend/charging-mini

# 后台管理系统
cd frontend/charging-admin
npm install
npm run dev
```

---

## 📁 项目结构

```
charging-pile/
├── docs/                          # 文档目录
│   ├── 01-项目概述.md
│   ├── 02-技术栈说明.md
│   ├── 03-整体架构设计.md
│   └── ...
├── backend/                       # 后端代码
│   ├── user-service/
│   ├── charging-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── common-service/
│   └── pom.xml
├── frontend/
│   ├── charging-mini/            # 小程序
│   └── charging-admin/           # 后台管理
├── docker/
│   ├── docker-compose.yaml
│   └── Dockerfile
└── config/
    └── nacos-config/             # Nacos配置导出
```

---

## 🛠️ 核心技术栈

### 前端技术栈
- **小程序**: WeChat Mini Program / uni-app
- **后台管理**: Vue 3 + Element Plus + RuoYi-Vue
- **构建工具**: Vite / Webpack
- **包管理**: npm / yarn / pnpm

### 后端技术栈
- **框架**: Spring Boot 2.7.x + Spring Cloud Alibaba 2021.x
- **微服务**: Dubbo 3.x + OpenFeign
- **注册中心**: Nacos 2.1.x
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **消息队列**: RabbitMQ 3.9.x
- **ORM**: MyBatis Plus 3.5.x

### 基础设施
- **容器化**: Docker + Docker Compose
- **编排**: Kubernetes（可选）
- **监控**: Prometheus + Grafana
- **链路追踪**: Skywalking

---

## 📊 微服务架构

```
5个核心微服务:

1. User Service (8081)
   - 用户认证与授权
   - 权限管理
   - 用户信息管理

2. Charging Service (8082)
   - 充电桩信息管理
   - 实时状态更新
   - 故障维护记录

3. Order Service (8083)
   - 订单创建与管理
   - 订单统计分析
   - 用户评价

4. Payment Service (8084)
   - 支付流程处理
   - 退款管理
   - 积分系统

5. Common Service (8085)
   - 文件上传/下载
   - 消息通知
   - 数据字典
   - 系统日志
```

---

## 🔐 权限体系

### 权限设计
- **认证**: JWT Token (1小时过期)
- **会话**: Redis 分布式Session
- **授权**: RBAC 基于角色权限控制
- **加密**: BCrypt 密码加密

### 角色定义
- **SUPER_ADMIN**: 超级管理员 (所有权限)
- **ADMIN**: 平台管理员 (系统权限)
- **OPERATOR**: 运营人员 (业务权限)
- **USER**: 普通用户 (基础权限)

---

## 📡 API规范

### RESTful 设计

```
GET    /api/{service}/{resource}           获取列表
GET    /api/{service}/{resource}/{id}      获取单个
POST   /api/{service}/{resource}           创建资源
PUT    /api/{service}/{resource}/{id}      更新资源
DELETE /api/{service}/{resource}/{id}      删除资源
```

### 响应格式

```json
成功响应:
{
  "code": 200,
  "msg": "操作成功",
  "data": { ... },
  "timestamp": 1685080800000
}

错误响应:
{
  "code": 400,
  "msg": "参数错误",
  "timestamp": 1685080800000
}
```

---

## 📈 数据库架构

### 核心表设计
- **系统表**: sys_user, sys_role, sys_permission
- **业务表**: charging_pile, charging_order, payment_record
- **关联表**: sys_user_role, sys_role_permission

### 索引策略
- 地理位置索引: (latitude, longitude)
- 时间索引: (created_at)
- 业务索引: (user_id, status, pile_id)

---

## 🔄 服务通讯

### 同步调用
- **OpenFeign**: 服务间 HTTP 调用
- **Ribbon**: 客户端负载均衡
- **Hystrix**: 断路器、限流降级

### 异步调用
- **RabbitMQ**: 事件驱动
- **消息队列**: 订单、支付等异步处理

---

## 🎯 开发工作流

### Git 分支策略

```
main/master      → 生产版本 (发布版本)
develop          → 开发主分支
feature/*        → 功能分支
bugfix/*         → 缺陷修复
release/*        → 版本发布
hotfix/*         → 紧急修复
```

### 提交规范

```
feat:    新功能
fix:     缺陷修复
docs:    文档更新
style:   代码风格
refactor: 代码重构
perf:    性能优化
test:    测试相关
chore:   构建/工具
```

---

## 📞 联系方式

| 角色 | 姓名 | 联系方式 |
|------|------|---------|
| 架构师 | - | - |
| 后端负责人 | - | - |
| 前端负责人 | - | - |
| 项目经理 | - | - |

---

## 📝 变更记录

| 版本 | 日期 | 变更内容 | 作者 |
|------|------|---------|------|
| 1.0.0 | 2024-05-26 | 初始版本 | 918H |

---

## 📚 参考资源

- [Spring Cloud Alibaba 官方文档](https://spring-cloud-alibaba-group.github.io/)
- [Nacos 官方文档](https://nacos.io/zh-cn/)
- [MyBatis Plus 官方文档](https://baomidou.com/)
- [RuoYi-Vue 官方文档](http://doc.ruoyi.vip/)
- [阿里巴巴Java开发规范](https://github.com/alibaba/Alibaba-Java-Coding-Guidelines)

---

**项目名称**: 充电桩小程序  
**版本**: 1.0.0  
**创建日期**: 2024-05-26  
**维护人**: 架构团队

