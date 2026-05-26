# 市场对标功能实现指南

## 已实现功能

### 1. 会员等级体系

#### 数据库表
- `membership_level` - 会员等级配置
- `user_membership` - 用户会员信息

#### API 接口
```
GET  /membership/levels           # 获取会员等级列表
GET  /membership/user/current     # 获取用户当前会员
GET  /membership/user/discount    # 计算会员折扣
POST /membership/user/upgrade     # 升级会员
POST /membership/user/check       # 检查并更新会员等级
```

#### 会员等级
| 等级 | 消费门槛 | 折扣 | 权益 |
|------|---------|------|------|
| 普通会员 | ¥0 | 100% | 基础充电服务 |
| 白银会员 | ¥500 | 98% | 98 折优惠 + 专属客服 |
| 黄金会员 | ¥2,000 | 95% | 95 折 + 免费预约 |
| 白金会员 | ¥5,000 | 92% | 92 折 + 优先充电 |
| 钻石会员 | ¥10,000 | 90% | 90 折 + 生日礼包 |

---

### 2. 储值卡系统

#### 数据库表
- `user_recharge_card` - 用户储值卡
- `recharge_record` - 充值记录

#### API 接口
```
GET  /recharge/card      # 获取用户储值卡
GET  /recharge/records   # 获取充值记录
POST /recharge/create    # 创建充值
POST /recharge/pay       # 储值卡支付
```

#### 充值赠送方案
| 充值金额 | 赠送金额 | 实际到账 | 相当于 |
|---------|---------|---------|--------|
| ¥50 | ¥0 | ¥50 | 100% |
| ¥100 | ¥5 | ¥105 | 105% |
| ¥200 | ¥15 | ¥215 | 107.5% |
| ¥500 | ¥50 | ¥550 | 110% |
| ¥1000 | ¥120 | ¥1,120 | 112% |

---

### 3. 积分系统

#### 数据库表
- `user_points` - 用户积分
- `points_record` - 积分记录

#### API 接口
```
GET  /points/user     # 获取用户积分
GET  /points/records  # 获取积分记录
POST /points/add      # 增加积分
POST /points/consume  # 消费积分
```

#### 积分获取方式
- 充电消费：¥1 = 1 积分
- 每日签到：5-50 积分（连续 7 天 10 积分）
- 完善资料：100 积分
- 邀请好友：500 积分
- 撰写评价：20 积分

#### 积分消耗方式
- 兑换优惠券
- 兑换服务费抵扣券
- 抵扣充电费用（100 积分=¥1）
- 兑换礼品

---

### 4. 签到打卡系统

#### 数据库表
- `sign_in_record` - 签到记录

#### API 接口
```
POST /signin/create   # 用户签到
GET  /signin/continuous # 获取连续签到天数
GET  /signin/calendar  # 获取签到日历
POST /signin/makeup    # 补签
```

#### 签到奖励规则
| 连续天数 | 奖励积分 |
|---------|---------|
| 第 1 天 | 5 |
| 第 2 天 | 5 |
| 第 3 天 | 5 |
| 第 4 天 | 5 |
| 第 5 天 | 5 |
| 第 6 天 | 5 |
| 第 7 天 | 10（重置） |

---

### 5. 私人充电桩共享

#### 数据库表
- `private_pile` - 私人充电桩
- `pile_reservation` - 充电桩预约
- `pile_income` - 充电桩收入

#### API 接口
```
POST /private-pile/create         # 创建私人充电桩
PUT  /private-pile/update         # 更新私人充电桩
GET  /private-pile/user/list      # 获取用户的充电桩列表
GET  /private-pile/detail         # 获取充电桩详情
GET  /private-pile/nearby         # 获取附近充电桩
POST /private-pile/reserve        # 预约充电桩
POST /private-pile/reserve/cancel # 取消预约
POST /private-pile/charging/start # 开始充电
POST /private-pile/charging/stop  # 结束充电
GET  /private-pile/income/list    # 获取收入记录
GET  /private-pile/income/stats   # 获取收入统计
```

#### 收益分配
- 平台抽成：10%
- 桩主收入：90%
- 结算周期：T+1 自动结算

#### 功能特性
- 桩主自主定价（电价 + 服务费）
- 设置共享时间段
- 预约冲突检测
- 充电费用自动结算
- 收益提现（待实现）

---

### 6. 邀请有礼

#### 数据库表
- `referral_relation` - 推荐关系

#### API 接口
```
POST /referral/code/generate  # 生成推荐码
GET  /referral/relation       # 获取推荐关系
POST /referral/bind           # 绑定推荐人
POST /referral/reward         # 领取推荐奖励
GET  /referral/info           # 获取推荐信息
```

#### 奖励规则
- 推荐人奖励：500 积分/人
- 被推荐人奖励：100 积分
- 推荐码格式：R + 13 位数字

---

## 集成到订单流程

### 会员折扣集成

在订单结算时自动计算会员折扣：

```java
// ChargingOrderServiceImpl.java
private BigDecimal calculateMembershipDiscount(Long userId, BigDecimal amount) {
    // 1. 查询用户会员等级
    // 2. 获取对应折扣率
    // 3. 计算折扣金额
    return discountAmount;
}
```

### 积分奖励集成

在订单完成后自动奖励积分：

```java
// 订单完成后调用
pointsService.addPoints(userId, points.intValue(), "充电奖励", orderNumber);
```

---

## 前端集成指南

### 小程序页面建议

#### 会员中心
```
pages/membership/index      - 会员中心首页
pages/membership/levels     - 会员等级说明
pages/membership/benefits   - 会员权益
pages/membership/upgrade    - 会员升级
```

#### 储值卡
```
pages/recharge/index        - 储值卡首页
pages/recharge/amount       - 充值金额选择
pages/recharge/record       - 充值记录
```

#### 积分
```
pages/points/index          - 积分首页
pages/points/mall           - 积分商城
pages/points/record         - 积分记录
```

#### 签到
```
pages/signin/index          - 签到日历
pages/signin/rules          - 签到规则
```

#### 私桩共享
```
pages/private-pile/list     - 私桩列表
pages/private-pile/detail   - 私桩详情
pages/private-pile/reserve  - 预约页面
pages/private-pile/income   - 收益统计
pages/private-pile/apply    - 申请共享
```

#### 邀请
```
pages/referral/index        - 邀请首页
pages/referral/code         - 推荐码展示
pages/referral/record       - 邀请记录
```

---

## 管理后台功能

### 会员管理
- 会员等级配置
- 会员审核
- 折扣率调整

### 储值卡管理
- 充值记录查询
- 异常订单处理

### 积分管理
- 积分规则配置
- 积分商城管理
- 积分发放记录

### 签到管理
- 签到奖励配置
- 签到数据统计

### 私桩管理
- 私桩审核
- 收益结算
- 纠纷处理

### 邀请管理
- 邀请数据统计
- 异常邀请处理

---

## 下一步优化建议

### P0 优先级
1. ✅ 会员等级
2. ✅ 储值卡
3. ✅ 积分系统
4. ✅ 签到打卡
5. ✅ 私桩共享
6. ✅ 邀请有礼

### P1 优先级
1. ⏳ 充电套餐包
2. ⏳ 积分商城
3. ⏳ 企业账户
4. ⏳ 收益提现

### P2 优先级
1. ⏳ 充电保险
2. ⏳ VIP 绿色通道
3. ⏳ 充电社区
4. ⏳ 碳积分系统

---

## 数据库迁移

执行以下 SQL 添加新表：

```bash
mysql -u root -p charging_db < /workspace/backend/common/src/main/resources/db/migration/V20240102005__add_membership_pile_points.sql
```

---

## 测试验证

### 会员功能测试
```bash
# 获取会员等级
curl "http://localhost:8081/membership/levels"

# 获取用户会员
curl "http://localhost:8081/membership/user/current?userId=1"
```

### 储值卡测试
```bash
# 获取储值卡
curl "http://localhost:8081/recharge/card?userId=1"

# 充值
curl -X POST "http://localhost:8081/recharge/create?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"amount":100,"paymentMethod":1,"transactionId":"test123"}'
```

### 积分测试
```bash
# 获取积分
curl "http://localhost:8081/points/user?userId=1"

# 增加积分
curl -X POST "http://localhost:8081/points/add?userId=1&points=100&description=测试奖励"
```

### 签到测试
```bash
# 签到
curl -X POST "http://localhost:8081/signin/create?userId=1"

# 获取签到日历
curl "http://localhost:8081/signin/calendar?userId=1&year=2026&month=5"
```

### 私桩测试
```bash
# 创建私桩
curl -X POST "http://localhost:8081/private-pile/create?userId=1" \
  -H "Content-Type: application/json" \
  -d '{"pileName":"测试私桩","address":"测试地址","pricePerKwh":1.5,"serviceFee":0.5,"status":1}'

# 获取附近私桩
curl "http://localhost:8081/private-pile/nearby?latitude=31.2304&longitude=121.4737&radius=5.0"
```

### 邀请测试
```bash
# 生成推荐码
curl -X POST "http://localhost:8081/referral/code/generate?userId=1"

# 绑定推荐人
curl -X POST "http://localhost:8081/referral/bind?refereeId=2&referralCode=R123456789"
```

---

## 性能优化建议

### 缓存策略
- 会员等级配置：Redis 缓存，1 小时过期
- 用户会员信息：Redis 缓存，30 分钟过期
- 签到日历：Redis 缓存，10 分钟过期

### 数据库索引
- `user_membership(user_id, status, end_time)`
- `sign_in_record(user_id, sign_in_date)` UNIQUE
- `private_pile(status, latitude, longitude)`
- `referral_relation(referee_id)` UNIQUE

---

## 安全注意事项

1. **储值卡支付**
   - 支付前验证余额
   - 支付时冻结金额
   - 支付完成扣款

2. **积分变动**
   - 所有积分变动记录日志
   - 大额积分变动需审核

3. **私桩共享**
   - 桩主身份验证
   - 充电过程监控
   - 异常交易告警

4. **邀请机制**
   - 防止刷积分（同设备/IP 限制）
   - 恶意邀请检测

---

## 对标分析总结

### 特来电
- ✅ 会员体系
- ✅ 储值卡
- ✅ 积分系统
- ✅ 私桩共享
- ⏳ 企业版

### 星星充电
- ✅ 会员体系
- ✅ 储值卡
- ✅ 积分系统
- ⏳ 充电套餐
- ⏳ 积分商城

### 国家电网
- ✅ 会员体系
- ✅ 积分系统
- ⏳ 企业账户
- ⏳ 大客户服务

### 本系统优势
1. 开源免费，无平台抽成
2. 极简 UI 设计
3. 完整业务闭环
4. 代码规范统一
5. 性能优化完善

---

## 联系方式

如有问题或建议，请联系开发团队。
