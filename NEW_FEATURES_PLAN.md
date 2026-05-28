# 新功能开发计划

## 📅 开发日期
2026-05-28

## 🎯 新功能总览

开发 **6 大模块**，全面提升平台能力：

1. 数据分析大屏
2. 消息通知中心
3. 客服工单系统
4. 营销活动系统
5. 财务报表系统
6. 设备监控告警

---

## 1️⃣ 数据分析大屏

### 功能列表

#### 1.1 数据统计
- [ ] 实时充电量统计（今日/本周/本月）
- [ ] 收益分析（日/周/月/年趋势）
- [ ] 用户增长曲线
- [ ] 充电桩使用率热力图
- [ ] 订单分布地图
- [ ] peak/off-peak 时段分析

#### 1.2 技术实现
- **后端**: charging-service + statistics 模块
- **前端**: ECharts 可视化
- **数据库**: 聚合查询 + 缓存预计算
- **接口**: RESTful API + WebSocket 实时更新

### 数据库表

```sql
-- 统计数据表
CREATE TABLE statistics_daily (
  id BIGINT PRIMARY KEY,
  stat_date DATE,
  total_orders INT,
  total_amount DECIMAL(10,2),
  total_kwh DECIMAL(10,2),
  active_users INT,
  new_users INT,
  avg_charging_time INT
);

-- 充电桩使用统计
CREATE TABLE pile_statistics (
  id BIGINT PRIMARY KEY,
  pile_id BIGINT,
  stat_date DATE,
  usage_count INT,
  usage_hours DECIMAL(10,2),
  revenue DECIMAL(10,2),
  avg_power DECIMAL(10,2)
);
```

---

## 2️⃣ 消息通知中心

### 功能列表

#### 2.1 通知类型
- [ ] 站内信（系统通知、订单状态）
- [ ] 短信通知（验证码、订单提醒）
- [ ] 邮件通知（账单、营销）
- [ ] 微信小程序模板消息
- [ ] App Push 推送

#### 2.2 触发场景
- 订单状态变更（创建/支付/完成）
- 退款进度通知
- 充值成功通知
- 活动推送
- 系统公告

### 数据库表

```sql
-- 消息模板
CREATE TABLE message_template (
  id BIGINT PRIMARY KEY,
  type VARCHAR(50),
  title VARCHAR(200),
  content TEXT,
  params VARCHAR(500),
  enabled BOOLEAN
);

-- 用户消息
CREATE TABLE user_message (
  id BIGINT PRIMARY KEY,
  user_id BIGINT,
  title VARCHAR(200),
  content TEXT,
  type VARCHAR(50),
  is_read BOOLEAN,
  create_time DATETIME
);
```

---

## 3️⃣ 客服工单系统

### 功能列表

#### 3.1 工单类型
- [ ] 用户反馈（功能建议、体验问题）
- [ ] 投诉建议（服务质量、收费问题）
- [ ] 故障报修（充电桩故障、APP 问题）
- [ ] 在线客服（实时聊天）

#### 3.2 工单流程
```
提交 → 分配 → 处理 → 回复 → 评价 → 关闭
```

#### 3.3 功能特性
- 自动分配客服
- 工单优先级
- SLA 时效管理
- 满意度评价
- 统计分析

### 数据库表

```sql
-- 工单主表
CREATE TABLE support_ticket (
  id BIGINT PRIMARY KEY,
  ticket_no VARCHAR(50),
  user_id BIGINT,
  type VARCHAR(50),
  priority VARCHAR(20),
  status VARCHAR(20),
  subject VARCHAR(200),
  content TEXT,
  assigned_to BIGINT,
  created_time DATETIME,
  resolved_time DATETIME
);

-- 工单回复
CREATE TABLE ticket_reply (
  id BIGINT PRIMARY KEY,
  ticket_id BIGINT,
  user_type VARCHAR(20),
  content TEXT,
  attachments VARCHAR(1000),
  create_time DATETIME
);
```

---

## 4️⃣ 营销活动系统

### 功能列表

#### 4.1 营销工具
- [ ] 限时折扣（峰谷电价优惠）
- [ ] 满减活动（满 100 减 10）
- [ ] 邀请有礼（邀请奖励）
- [ ] 签到打卡（连续签到奖励）
- [ ] 新人礼包
- [ ] 充值返利

#### 4.2 活动管理
- 活动创建/编辑/下架
- 预算控制
- 参与条件设置
- 效果统计

### 数据库表

```sql
-- 营销活动
CREATE TABLE marketing_activity (
  id BIGINT PRIMARY KEY,
  name VARCHAR(100),
  type VARCHAR(50),
  start_time DATETIME,
  end_time DATETIME,
  budget DECIMAL(10,2),
  rule_json TEXT,
  status VARCHAR(20)
);

-- 签到记录
CREATE TABLE user_sign_in (
  id BIGINT PRIMARY KEY,
  user_id BIGINT,
  sign_date DATE,
  continuous_days INT,
  reward_points INT,
  reward_amount DECIMAL(10,2)
);
```

---

## 5️⃣ 财务报表系统

### 功能列表

#### 5.1 财务报表
- [ ] 收支明细表
- [ ] 日/月/年对账单
- [ ] 发票管理（开票申请）
- [ ] 税务报表
- [ ] 成本分析
- [ ] 利润报表

#### 5.2 功能特性
- 多维度筛选
- 导出 Excel/PDF
- 自动对账
- 财务审核流程

### 数据库表

```sql
-- 财务流水
CREATE TABLE finance_transaction (
  id BIGINT PRIMARY KEY,
  transaction_no VARCHAR(50),
  user_id BIGINT,
  type VARCHAR(50),
  amount DECIMAL(10,2),
  balance_before DECIMAL(10,2),
  balance_after DECIMAL(10,2),
  related_order_id BIGINT,
  create_time DATETIME
);

-- 发票记录
CREATE TABLE invoice (
  id BIGINT PRIMARY KEY,
  invoice_no VARCHAR(50),
  user_id BIGINT,
  amount DECIMAL(10,2),
  type VARCHAR(50),
  status VARCHAR(20),
  invoice_url VARCHAR(500)
);
```

---

## 6️⃣ 设备监控告警

### 功能列表

#### 6.1 实时监控
- [ ] 充电桩在线状态
- [ ] 充电功率监控
- [ ] 故障状态检测
- [ ] 远程重启/控制

#### 6.2 告警系统
- [ ] 离线告警
- [ ] 故障告警
- [ ] 功率异常告警
- [ ] 温度过高告警
- [ ] 自动派单维修

#### 6.3 运维管理
- 维护计划
- 巡检记录
- 维修工单
- 备件管理

### 数据库表

```sql
-- 设备状态
CREATE TABLE pile_status (
  id BIGINT PRIMARY KEY,
  pile_id BIGINT,
  status VARCHAR(20),
  power DECIMAL(10,2),
  voltage DECIMAL(10,2),
  temperature DECIMAL(5,2),
  error_code VARCHAR(50),
  last_heartbeat DATETIME
);

-- 告警记录
CREATE TABLE pile_alarm (
  id BIGINT PRIMARY KEY,
  pile_id BIGINT,
  alarm_type VARCHAR(50),
  alarm_level VARCHAR(20),
  content TEXT,
  status VARCHAR(20),
  handler_id BIGINT,
  create_time DATETIME,
  resolve_time DATETIME
);
```

---

## 📋 开发计划

### 阶段 1：基础架构（2 天）
- [ ] 数据库表设计
- [ ] 创建新模块
- [ ] 配置路由和菜单

### 阶段 2：核心功能（5 天）
- [ ] 数据统计模块
- [ ] 消息通知模块
- [ ] 工单系统模块

### 阶段 3：高级功能（5 天）
- [ ] 营销活动模块
- [ ] 财务报表模块
- [ ] 设备监控模块

### 阶段 4：前端页面（5 天）
- [ ] 数据大屏页面
- [ ] 各模块管理页面
- [ ] 移动端适配

---

## 🎯 预期成果

**新增**:
- 6 大功能模块
- 20+ 个数据库表
- 50+ 个 API 接口
- 15+ 个前端页面

**提升**:
- 数据分析能力 ✅
- 用户触达效率 ✅
- 客服响应速度 ✅
- 营销转化率 ✅
- 财务管理效率 ✅
- 设备运维质量 ✅

---

**开始时间**: 2026-05-28  
**预计完成**: 2026-06-10  
**总工时**: 约 17 个工作日
