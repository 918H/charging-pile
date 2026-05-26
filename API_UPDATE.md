# API 更新说明

## 新增接口

### 订单服务

#### 1. 订单试算接口
```
POST /api/order/order/calculate
Content-Type: application/json

请求参数：
{
  "pileId": 1,
  "durationMinutes": 60,
  "powerConsumed": 30.5,
  "couponId": 1
}

响应：
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "originalAmount": 45.75,
    "discountAmount": 10.00,
    "finalAmount": 35.75,
    "message": "已使用优惠券，优惠 10.00 元"
  }
}
```

#### 2. 用户历史订单查询
```
GET /api/order/order/user/history?userId=1&status=2
status: 0-待支付 1-充电中 2-已完成 3-已取消 (不传则查询全部)

响应：
{
  "code": 200,
  "data": [订单列表]
}
```

#### 3. 订单统计接口
```
GET /api/order/order/statistics?userId=1&startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59

响应：
{
  "code": 200,
  "data": {
    "totalOrders": 25,
    "completedOrders": 20,
    "totalAmount": 1250.50,
    "totalPower": 850.5,
    "averageOrderAmount": 50.02
  }
}
```

#### 4. 未结算订单查询
```
GET /api/order/order/user/uncalculated?userId=1
返回正在充电中（未结算）的订单

响应：
{
  "code": 200,
  "data": [充电中的订单]
}
```

### 优惠券服务

#### 1. 可领取优惠券列表
```
GET /api/coupon/coupon/available

响应：
{
  "code": 200,
  "data": [
    {
      "couponId": 1,
      "couponName": "新用户专享券",
      "couponType": 1,
      "discountAmount": 10.00,
      "minPurchaseAmount": 20.00,
      "totalCount": 1000,
      "issuedCount": 156
    }
  ]
}
```

#### 2. 领取优惠券
```
POST /api/coupon/coupon/receive?userId=1&couponId=1

响应：
{
  "code": 200,
  "msg": "领取成功"
}
```

#### 3. 我的优惠券列表
```
GET /api/coupon/coupon/user/list?userId=1&status=0
status: 0-未使用 1-已使用 2-已过期

响应：
{
  "code": 200,
  "data": [
    {
      "userCouponId": 1,
      "couponId": 1,
      "couponCode": "CPN1716825600000...",
      "status": 0
    }
  ]
}
```

#### 4. 使用优惠券
```
POST /api/coupon/coupon/use?userCouponId=1&orderId=1001

响应：
{
  "code": 200,
  "msg": "使用成功"
}
```

#### 5. 退回优惠券
```
POST /api/coupon/coupon/return?userCouponId=1
用于订单取消后退回优惠券

响应：
{
  "code": 200,
  "msg": "退回成功"
}
```

## 优惠券类型说明

| 类型 | couponType | 说明 | discountAmount 含义 |
|------|-----------|------|-------------------|
| 满减券 | 1 | 满 X 减 Y | 优惠金额（元） |
| 折扣券 | 2 | 打 X 折 | 折扣率（0.8=8 折） |
| 无门槛券 | 3 | 直接减免 | 优惠金额（元） |

## 订单状态说明

| 状态 | 值 | 说明 |
|------|-----|------|
| 待支付 | 0 | 订单创建，等待支付 |
| 充电中 | 1 | 正在充电，未结算 |
| 已完成 | 2 | 充电完成并支付 |
| 已取消 | 3 | 订单已取消 |
