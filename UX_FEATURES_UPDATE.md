# 用户体验功能更新文档

## 新增功能概览

本次更新完善了充电桩平台的核心用户体验功能，包括评价系统和故障上报系统。

## 一、评价系统 ⭐

### 1. 功能特性
- ✅ 5 星评分系统
- ✅ 标签快捷选择 (充电速度快/环境舒适/停车方便/等)
- ✅ 文字评价 (200 字以内)
- ✅ 图片上传支持 (预留接口)
- ✅ 防重复评价机制
- ✅ 充电桩平均分统计
- ✅ 评分分布统计

### 2. 后端接口

#### 创建评价
```
POST /api/order/review/create
Content-Type: application/json

{
  "userId": 1001,
  "orderId": 12345,
  "pileId": 1,
  "rating": 5,
  "content": "充电速度很快，环境也不错",
  "images": ""
}
```

#### 获取充电桩评价列表
```
GET /api/order/review/pile/list?pileId=1&limit=10

Response:
{
  "code": 200,
  "data": [
    {
      "reviewId": 1,
      "userId": 1001,
      "pileId": 1,
      "rating": 5,
      "content": "很好!",
      "createdAt": "2026-01-26T12:00:00"
    }
  ]
}
```

#### 获取充电桩评分统计
```
GET /api/order/review/pile/rating?pileId=1

Response:
{
  "code": 200,
  "data": {
    "averageRating": 4.5,
    "totalReviews": 128,
    "ratingDistribution": [2, 5, 10, 31, 80]
  }
}
```

#### 检查是否可评价
```
GET /api/order/review/can-review?userId=1001&orderId=12345

Response:
{
  "code": 200,
  "data": true
}
```

### 3. 前端页面

#### review/review.js
- 星级评分交互
- 标签多选功能
- 字数统计显示
- 评价提交验证

#### 订单列表集成
- 已完成订单显示"评价"按钮
- 点击跳转到评价页面
- 防重复评价提示

---

## 二、故障上报功能 🔧

### 1. 功能特性
- ✅ 8 种故障类型选择
- ✅ 故障桩号选填
- ✅ 详细描述 (200 字)
- ✅ 联系电话必填
- ✅ 现场照片上传 (最多 3 张)
- ✅ 状态跟踪 (待处理/已处理)
- ✅ 处理回复功能

### 2. 故障类型
1. 无法启动充电
2. 充电中断
3. 计费异常
4. 设备损坏
5. 屏幕故障
6. 急停按钮按下
7. 网络故障
8. 其他

### 3. 后端接口

#### 上报故障
```
POST /api/charging/fault/report
Content-Type: application/json

{
  "pileId": 1,
  "slotId": 1,
  "userId": 1001,
  "faultType": "无法启动充电",
  "description": "扫描二维码后无法启动充电，屏幕显示正常",
  "images": "",
  "contactPhone": "13800138000"
}
```

#### 获取故障详情
```
GET /api/charging/fault/{faultId}

Response:
{
  "code": 200,
  "data": {
    "faultId": 1,
    "pileId": 1,
    "faultType": "无法启动充电",
    "status": 0,
    "createdAt": "2026-01-26T10:00:00"
  }
}
```

#### 获取桩故障列表
```
GET /api/charging/fault/pile/list?pileId=1&status=0
```

#### 获取用户故障列表
```
GET /api/charging/fault/user/list?userId=1001
```

#### 处理故障
```
POST /api/charging/fault/{faultId}/handle?handlerId=100&response=已安排维修人员处理

Response:
{
  "code": 200,
  "data": true
}
```

### 4. 前端页面

#### fault-report/fault-report.js
- 故障类型选择器
- 表单输入验证
- 图片上传功能
- 温馨提示展示

#### 桩详情页集成
- 故障上报按钮
- 跳转上报页面
- 自动传递 pileId

---

## 三、数据库表结构

### charging_review (评价表)
| 字段 | 类型 | 说明 |
|------|------|------|
| review_id | BIGINT | 评价 ID (主键) |
| user_id | BIGINT | 用户 ID |
| order_id | BIGINT | 订单 ID |
| pile_id | BIGINT | 充电桩 ID |
| rating | TINYINT | 评分 1-5 |
| content | TEXT | 评价内容 |
| images | VARCHAR | 图片 URL |
| has_images | BOOLEAN | 是否有图片 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**索引**: idx_user_id, idx_order_id, idx_pile_id  
**唯一索引**: uk_user_order (防止重复评价)

### charging_fault (故障表)
| 字段 | 类型 | 说明 |
|------|------|------|
| fault_id | BIGINT | 故障 ID (主键) |
| pile_id | BIGINT | 充电桩 ID |
| slot_id | INT | 槽位 ID |
| user_id | BIGINT | 上报用户 ID |
| fault_type | VARCHAR | 故障类型 |
| description | TEXT | 故障描述 |
| images | VARCHAR | 图片 URL |
| contact_phone | VARCHAR | 联系电话 |
| status | TINYINT | 状态 0 待处理 1 已处理 |
| handler_response | TEXT | 处理回复 |
| handler_id | BIGINT | 处理人 ID |
| handled_at | DATETIME | 处理时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

**索引**: idx_pile_id, idx_user_id, idx_status

---

## 四、数据统计

### 桩评分计算逻辑
```java
平均评分 = 总评分 / 评价总数
评分分布 = [1 星数量，2 星数量，3 星数量，4 星数量，5 星数量]
```

### 评价规则
1. 仅已完成订单 (status=2) 可评价
2. 每个订单只能评价一次
3. 评分必须 1-5 星
4. 文字评价可选，最多 200 字
5. 标签可选多个

### 故障处理流程
1. 用户提交故障报告
2. 状态：待处理 (status=0)
3. 管理员处理并填写回复
4. 状态：已处理 (status=1)
5. 记录处理时间和处理人

---

## 五、UI/UX 设计

### 评价页面
- 星级评分：⭐⭐⭐⭐⭐ (金色/灰色)
- 标签选择：圆角按钮，选中变绿
- 文字输入框：200 字限制，右下角计数
- 提交按钮：绿色主按钮

### 故障上报页面
- 故障类型：选择器下拉
- 联系电话：数字键盘
- 图片上传：虚线框 + 号
- 温馨提示：黄色背景提示框
- 提交按钮：橙色主按钮

### 集成展示
- 桩详情页：显示评分⭐4.5 (128 条评价)
- 订单列表：已完成订单显示"评价"按钮
- 桩详情页：故障上报按钮 (橙色)

---

## 六、文件清单

### 后端 (12 个文件)
```
backend/order-service/
  src/main/java/com/charging/order/
    entity/ChargingReview.java
    dto/ReviewRequest.java
    dto/ReviewResponse.java
    mapper/ChargingReviewMapper.java
    service/ReviewService.java
    service/impl/ReviewServiceImpl.java
    controller/ReviewController.java

backend/charging-service/
  src/main/java/com/charging/charging/
    entity/ChargingFault.java
    dto/FaultReportRequest.java
    mapper/ChargingFaultMapper.java
    service/FaultService.java
    service/impl/FaultServiceImpl.java
    controller/FaultController.java
```

### 前端 (12 个文件)
```
frontend/charging-mini/
  pages/review/
    review.js
    review.json
    review.wxml
    review.wxss
  
  pages/fault-report/
    fault-report.js
    fault-report.json
    fault-report.wxml
    fault-report.wxss
  
  api/order.js (新增 5 个接口)
  api/charging.js (新增 3 个接口)
```

### 数据库 (1 个文件)
```
backend/user-service/src/main/resources/db/schema-ux-features.sql
```

---

## 七、测试建议

### 评价系统测试
1. ✅ 已完成订单可评价
2. ✅ 充电中/待支付/已取消订单不可评价
3. ✅ 同一订单重复评价被阻止
4. ✅ 评分为空时提交提示
5. ✅ 标签多选功能正常
6. ✅ 字数统计准确
7. ✅ 评分计算正确

### 故障上报测试
1. ✅ 必填项验证 (故障类型/描述/电话)
2. ✅ 电话格式验证
3. ✅ 图片上传数量限制 (3 张)
4. ✅ 故障列表显示正确
5. ✅ 状态跟踪正常
6. ✅ 处理回复功能正常

---

## 八、后续优化建议

1. **图片上传**: 实现真实的图片上传到 OSS
2. **评价回复**: 商家回复评价功能
3. **评价晒图**: 评价列表展示图片
4. **故障分类**: 按故障类型统计分布
5. **处理时效**: 故障处理 SLA 监控
6. **积分奖励**: 评价送积分激励
7. **评价排序**: 按时间/有用性排序
8. **举报功能**: 恶意评价举报处理

---

## 九、项目进度更新

| 模块 | 完成度 |
|------|--------|
| 扫码充电 | ✅ 100% |
| 实时进度 | ✅ 100% |
| 分时电价 | ✅ 100% |
| 预约充电 | ✅ 100% |
| **评价系统** | ✅ **100%** |
| **故障上报** | ✅ **100%** |
| 优惠券 | ✅ 95% |
| 支付集成 | ⏳ 80% |
| 管理后台 | ✅ 95% |

**总体进度：约 97%**

---

## 十、API 更新摘要

### order-service 新增接口
- POST `/order/review/create` - 创建评价
- GET `/order/review/pile/list` - 获取桩评价
- GET `/order/review/user/list` - 获取用户评价
- GET `/order/review/pile/rating` - 获取桩评分
- GET `/order/review/can-review` - 检查可评价

### charging-service 新增接口
- POST `/charging/fault/report` - 上报故障
- GET `/charging/fault/{faultId}` - 故障详情
- GET `/charging/fault/pile/list` - 桩故障列表
- GET `/charging/fault/user/list` - 用户故障列表
- POST `/charging/fault/{faultId}/handle` - 处理故障

---

**更新时间**: 2026-01-26  
**版本**: v1.2.0  
**提交分支**: 260526-feat-charging-scan-flow
