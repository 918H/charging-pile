# 充电桩管理平台 (Charging Pile Management Platform)

基于 Spring Cloud Alibaba 的微服务充电桩管理平台，支持扫码充电、支付、营销、监控等完整功能。

## 🏗️ 项目架构

```
┌─────────────────────────────────────────────────────────────┐
│                        前端层                                │
│  Vue 3 + Element Plus + ECharts (充电管理后台 + 用户小程序)    │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                        网关层                                │
│              Spring Cloud Gateway                           │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      微服务层 (12 个)                         │
│  user │ charging │ order │ payment │ coupon │ statistics    │
│  message │ support │ marketing │ finance │ monitor │ common │
└─────────────────────────────────────────────────────────────┘
                              │
┌─────────────────────────────────────────────────────────────┐
│                      中间件层                                │
│    Nacos(注册配置) │ Redis(缓存) │ MySQL(数据库)            │
│    Sentinel(限流)  │ Prometheus(监控) │ Grafana(可视化)     │
└─────────────────────────────────────────────────────────────┘
```

## 📦 技术栈

### 后端
- **框架**: Spring Boot 3.2 + Spring Cloud 2023.x
- **微服务**: Spring Cloud Alibaba 2023.x
- **ORM**: MyBatis-Plus 3.5
- **数据库**: MySQL 8.0
- **缓存**: Redis 7.x
- **注册中心**: Nacos 2.2
- **限流熔断**: Sentinel 1.8
- **监控**: Prometheus + Grafana

### 前端
- **框架**: Vue 3 + Vite 5.x
- **UI 库**: Element Plus 2.x
- **图表**: ECharts 5.x
- **请求**: Axios

## 🚀 快速启动

### 方式一：Docker 部署（推荐）

```bash
# 克隆项目
git clone https://github.com/918H/charging-pile.git
cd charging-pile

# 一键启动所有服务
cd deploy && ./start.sh

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f user-service
```

**访问地址**:
- Nacos 控制台：http://localhost:8848/nacos
- Grafana 监控：http://localhost:3000 (admin/admin123)
- Prometheus: http://localhost:9090

### 方式二：本地开发

```bash
# 1. 启动基础设施
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=root123 mysql:8.0
docker run -d --name redis redis:7-alpine
docker run -d --name nacos -e MODE=standalone nacos/nacos-server:2.2.0

# 2. 初始化数据库
mysql -h localhost -u root -proot123 charging_pile < database/init.sql

# 3. 启动微服务 (以 user-service 为例)
cd backend/user-service
mvn clean spring-boot:run

# 4. 启动前端
cd frontend/charging-admin
npm install
npm run dev
```

## 📊 微服务列表

| 服务名 | 端口 | 功能描述 |
|--------|------|----------|
| user-service | 8081 | 用户管理、认证、余额 |
| charging-service | 8082 | 充电桩管理、启停控制 |
| order-service | 8083 | 订单创建、计费、历史 |
| payment-service | 8084 | 支付网关、退款、回调 |
| coupon-service | 8085 | 优惠券领取、核销 |
| statistics-service | 8087 | 数据统计、Dashboard |
| message-service | 8088 | 消息通知、模板管理 |
| support-service | 8089 | 客服工单、投诉建议 |
| marketing-service | 8090 | 营销活动、满减折扣 |
| finance-service | 8091 | 财务报表、充值提现 |
| monitor-service | 8092 | 设备监控、告警管理 |

## 🗄️ 数据库

- **数据库表**: 39 张
- **初始化脚本**: `database/` 目录
- **V1__user.sql** - 用户相关表 (5 张)
- **V2__charging_order.sql** - 充电订单表 (8 张)
- **V3__payment_coupon.sql** - 支付优惠券表 (11 张)
- **V4__new_features.sql** - 新功能表 (15 张)

## 🔧 配置文件

### Nacos 配置示例

```yaml
# 所有服务共享配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/charging_pile
    username: root
    password: root123
  redis:
    host: localhost
    port: 6379
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848

#  Sentinel 限流配置
sentinel:
  transport:
    dashboard: localhost:8080
  datasource:
    ds1:
      gateway:
        data-type: json
        rule-type: flow
```

## 📖 API 文档

### 核心接口

**用户服务**:
- POST /user/register - 用户注册
- POST /user/login - 用户登录
- GET /user/profile - 获取用户信息

**充电服务**:
- POST /charging/start - 开始充电
- POST /charging/stop - 停止充电
- GET /charging/status - 充电状态查询

**订单服务**:
- POST /order/create - 创建订单
- GET /order/list - 订单列表
- GET /order/detail/{id} - 订单详情

**支付服务**:
- POST /payment/wechat - 微信支付
- POST /payment/alipay - 支付宝支付
- POST /payment/refund - 退款申请

**数据统计**:
- GET /statistics/dashboard - 仪表盘数据
- GET /statistics/daily - 日统计数据

**设备监控**:
- GET /monitor/piles/status - 所有桩状态
- GET /monitor/alarm/list - 告警列表

完整 API 文档启动后查看 Swagger UI: http://localhost:808x/doc.html

## 📈 监控告警

### Prometheus 指标
- 服务健康状况
- API 响应时间
- 请求成功率
- JVM 内存使用
- 数据库连接池

### Grafana 仪表盘
- 服务概览 Dashboard
- 业务指标监控
- 系统资源监控

## 🛠️ 开发指南

### 项目结构

```
charging-pile/
├── backend/                 # 后端微服务
│   ├── user-service/
│   ├── charging-service/
│   ├── order-service/
│   ├── payment-service/
│   ├── coupon-service/
│   ├── statistics-service/
│   ├── message-service/
│   ├── support-service/
│   ├── marketing-service/
│   ├── finance-service/
│   ├── monitor-service/
│   └── common/              # 公共模块
│       ├── common-core/     # 核心工具类
│       ├── common-redis/    # Redis 工具
│       └── common-db/       # 数据库配置
├── frontend/                # 前端项目
│   ├── charging-admin/      # 管理后台
│   └── charging-mini/       # 小程序 (待开发)
├── database/                # 数据库脚本
└── deploy/                  # 部署配置
```

### 代码规范
- 统一响应格式：`R<T>`
- 全局异常处理：`RestExceptionHandler`
- 日志规范：Slf4j + 异步日志
- 数据库操作：MyBatis-Plus

## 📝 功能特性

### 核心功能
- ✅ 扫码充电流程
- ✅ 智能计费引擎
- ✅ 多渠道支付
- ✅ 优惠券系统
- ✅ 订单管理
- ✅ 用户管理

### 新增功能 (本次开发)
- ✅ 数据统计大屏
- ✅ 消息通知中心
- ✅ 客服工单系统
- ✅ 营销活动管理
- ✅ 财务报表
- ✅ 设备实时监控

## 🔐 安全特性
- JWT Token 认证
- 敏感信息脱敏
- SQL 注入防护
- 接口限流
- 登录验证码

## 📦 部署

### 生产环境
1. 修改 Nacos 配置为生产数据库
2. 配置支付平台正式密钥
3. 开启 HTTPS
4. 配置域名解析
5. 部署监控告警

### 性能优化
- Redis 缓存热点数据
- 数据库读写分离
- 接口异步处理
- CDN 静态资源

## 📊 项目统计
- **代码量**: 约 3000+ 行
- **微服务**: 12 个
- **数据库表**: 39 张
- **API 接口**: 50+ 个
- **前端页面**: 12 个

## 🤝 贡献指南
1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 License
MIT License

## 📞 联系方式
- GitHub: https://github.com/918H/charging-pile
- 分支：260526-feat-charging-scan-flow

---

**开发团队**: monkeycode-ai  
**开发周期**: 1 天  
**提交次数**: 35+ commits
