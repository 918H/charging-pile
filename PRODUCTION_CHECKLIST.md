# 生产上线清单

## 上线前必须完成的事项

### 1. 安全配置 ✓

- [x] JWT Token 密钥使用环境变量
- [x] 密码强度校验
- [x] 登录失败限制 (5 次锁定 30 分钟)
- [x] XSS 防护过滤器
- [x] 跨域配置
- [x] 敏感信息加密存储
- [ ] HTTPS 证书配置
- [ ] 防重放攻击机制
- [ ] 接口签名验证

### 2. 数据库优化

- [ ] 数据库连接池优化 (HikariCP)
- [ ] 慢查询日志开启
- [ ] 索引优化
- [ ] 数据库主从配置
- [ ] 备份策略配置 (每日备份)

### 3. 缓存策略

- [x] Redis 基础配置
- [ ] 热点数据缓存
- [ ] 缓存穿透防护
- [ ] 缓存雪崩防护
- [ ] 缓存更新策略

### 4. 日志配置 ✓

- [x] Logback 生产配置
- [x] 日志分文件存储
- [x] 日志轮转配置 (100MB, 保留 30 天)
- [ ] 结构化日志 (JSON 格式)
- [ ] 日志收集系统 (ELK)
- [ ] 错误日志告警

### 5. 监控告警 ✓

- [x] Spring Boot Actuator
- [x] Prometheus 配置
- [x] Grafana 仪表盘
- [ ] 告警规则配置
- [ ] 告警通知配置 (邮件/短信/钉钉)

### 6. 性能测试

- [ ] JMeter 压力测试
- [ ] 接口响应时间 < 200ms
- [ ] 并发能力 > 1000QPS
- [ ] 内存泄漏检测
- [ ] 数据库性能测试

### 7. 文档准备

- [x] API 接口文档
- [ ] 部署文档
- [ ] 运维手册
- [ ] 故障处理手册
- [ ] 用户操作指南

### 8. 备份与恢复

- [ ] 数据库自动备份脚本
- [ ] 配置文件备份
- [ ] 灾难恢复预案
- [ ] 回滚方案测试

### 9. 小程序端

- [ ] 微信小程序代码完成
- [ ] 微信支付沙箱测试
- [ ] 用户授权流程
- [ ] 扫码充电功能
- [ ] 支付功能测试

### 10. 灰度发布

- [ ] 制定灰度计划 (10% → 50% → 100%)
- [ ] 监控指标配置
- [ ] 快速回滚方案
- [ ] 用户反馈收集

## 环境配置

### 开发环境
```bash
NACOS_SERVER_ADDR=localhost:8848
NACOS_NAMESPACE=dev
MYSQL_HOST=localhost
MYSQL_PORT=3306
MYSQL_DATABASE=charging_db
MYSQL_USERNAME=root
MYSQL_PASSWORD=root
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=dev-secret-key-change-in-prod
```

### 生产环境
```bash
NACOS_SERVER_ADDR=nacos.prod:8848
NACOS_NAMESPACE=prod
MYSQL_HOST=mysql.prod:3306
MYSQL_DATABASE=charging_prod
MYSQL_USERNAME=charging_user
MYSQL_PASSWORD=STRONG_PASSWORD_HERE
REDIS_HOST=redis.prod:6379
REDIS_PASSWORD=STRONG_PASSWORD_HERE
JWT_SECRET=RANDOM_SECURE_KEY_MIN_32_CHARS
```

## 上线步骤

1. **代码冻结** - 提前 1 周停止新功能
2. **回归测试** - 完成所有回归测试用例
3. **性能验证** - 确认性能指标达标
4. **安全审计** - 第三方安全公司审计
5. **预发布验证** - 在预发布环境完整验证
6. **灰度发布** - 小范围用户试用
7. **全量发布** - 100% 用户可用
8. **监控观察** - 密切监控 24 小时

## 回滚方案

如果上线后发现问题：

1. 立即停止新版本
2. 回滚到上一稳定版本
3. 分析问题原因
4. 修复后重新走上线流程

---

**签字确认**

开发负责人：___________ 日期：___________

测试负责人：___________ 日期：___________

运维负责人：___________ 日期：___________

产品负责人：___________ 日期：___________
