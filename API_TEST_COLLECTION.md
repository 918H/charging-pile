# 充电桩平台 API 测试集合

## 使用说明

启动所有服务后，使用以下 curl 命令测试各服务功能。

---

## 1. 用户服务 (8081)

### 用户注册
```bash
curl -X POST "http://localhost:8081/user/register" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&password=123456&phone=13800138000"
```

### 用户登录
```bash
curl -X POST "http://localhost:8081/user/login" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&password=123456"
```

### 获取用户信息
```bash
curl "http://localhost:8081/user/profile?userId=1"
```

---

## 2. 充电服务 (8082)

### 开始充电
```bash
curl -X POST "http://localhost:8082/charging/start" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&pileId=1"
```

### 停止充电
```bash
curl -X POST "http://localhost:8082/charging/stop" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "orderId=1"
```

### 查询充电状态
```bash
curl "http://localhost:8082/charging/status?orderId=1"
```

---

## 3. 订单服务 (8083)

### 创建订单
```bash
curl -X POST "http://localhost:8083/order/create" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&chargingPileId=1&amount=50.00"
```

### 订单列表
```bash
curl "http://localhost:8083/order/list?userId=1&page=1&size=10"
```

### 订单详情
```bash
curl "http://localhost:8083/order/detail/1"
```

---

## 4. 支付服务 (8084)

### 微信支付
```bash
curl -X POST "http://localhost:8084/payment/wechat" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "orderId=1&userId=1"
```

### 支付宝支付
```bash
curl -X POST "http://localhost:8084/payment/alipay" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "orderId=1&userId=1"
```

### 退款申请
```bash
curl -X POST "http://localhost:8084/payment/refund" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "orderId=1&reason=测试退款"
```

---

## 5. 优惠券服务 (8085)

### 领取优惠券
```bash
curl -X POST "http://localhost:8085/coupon/receive" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&couponId=1"
```

### 我的优惠券
```bash
curl "http://localhost:8085/coupon/my?userId=1"
```

### 核销优惠券
```bash
curl -X POST "http://localhost:8085/coupon/use" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&couponId=1&orderId=1"
```

---

## 6. 数据统计服务 (8087)

### 仪表盘数据
```bash
curl "http://localhost:8087/statistics/dashboard"
```

### 日统计列表
```bash
curl "http://localhost:8087/statistics/daily?startDate=2026-05-01&endDate=2026-05-28"
```

---

## 7. 消息服务 (8088)

### 消息列表
```bash
curl "http://localhost:8088/message/list?userId=1"
```

### 标记已读
```bash
curl -X PUT "http://localhost:8088/message/read" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "messageId=1"
```

### 发送消息
```bash
curl -X POST "http://localhost:8088/message/send" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&title=测试消息&content=这是一条测试消息"
```

---

## 8. 客服工单服务 (8089)

### 创建工单
```bash
curl -X POST "http://localhost:8089/support/ticket/create" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&type=complaint&content=测试工单内容"
```

### 工单列表
```bash
curl "http://localhost:8089/support/ticket/list"
```

### 工单详情
```bash
curl "http://localhost:8089/support/ticket/detail/1"
```

### 回复工单
```bash
curl -X POST "http://localhost:8089/support/ticket/1/reply" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&content=测试回复"
```

---

## 9. 营销活动服务 (8090)

### 活动列表
```bash
curl "http://localhost:8090/marketing/activity/list"
```

### 创建活动
```bash
curl -X POST "http://localhost:8090/marketing/activity/create" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "name=新用户活动&type=discount&startTime=2026-05-28T00:00:00&endTime=2026-06-28T23:59:59"
```

### 上线活动
```bash
curl -X PUT "http://localhost:8090/marketing/activity/1/launch"
```

---

## 10. 财务服务 (8091)

### 流水查询
```bash
curl "http://localhost:8091/finance/transaction/list?userId=1"
```

### 余额查询
```bash
curl "http://localhost:8091/finance/balance?userId=1"
```

### 充值
```bash
curl -X POST "http://localhost:8091/finance/recharge" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&amount=100.00"
```

### 提现
```bash
curl -X POST "http://localhost:8091/finance/withdraw" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "userId=1&amount=50.00"
```

---

## 11. 设备监控服务 (8092)

### 所有桩状态
```bash
curl "http://localhost:8092/monitor/piles/status"
```

### 单个桩状态
```bash
curl "http://localhost:8092/monitor/pile/1/status"
```

### 告警列表
```bash
curl "http://localhost:8092/monitor/alarm/list"
```

### 处理告警
```bash
curl -X PUT "http://localhost:8092/monitor/alarm/1/resolve?result=已处理完成"
```

### 桩健康度
```bash
curl "http://localhost:8092/monitor/pile/1/health"
```

---

## 快速测试脚本

```bash
#!/bin/bash
echo "测试所有服务..."
for port in 8081 8082 8083 8084 8085 8087 8088 8089 8090 8091 8092; do
  echo -n "端口 $port: "
  curl -s "http://localhost:$port/actuator/health" | grep -q "UP" && echo "✅ 正常" || echo "❌ 异常"
done
```
