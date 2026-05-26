# 支付网关集成指南

## 概述

本平台支持多种支付网关接入，当前默认使用 Mock 模式进行测试。生产环境需要配置真实的支付网关。

---

## 支持的支付网关

### 1. 微信支付 (WeChat Pay)
- ✅ 接口框架已实现
- ✅ 支持统一下单
- ✅ 支持订单查询
- ✅ 支持退款
- ⏳ 需要配置商户号

### 2. 支付宝 (Alipay)
- ⏳ 待实现
- 计划支持
  - APP 支付
  - 小程序支付
  - 退款

### 3. Mock 模式 (默认)
- ✅ 测试环境使用
- ✅ 生成虚拟交易 ID
- ✅ 模拟支付成功
- ✅ 模拟退款成功

---

## 配置方式

### application.yml 配置

```yaml
# 微信支付配置
wechat:
  pay:
    appid: wx8888888888888888        # 微信公众号/小程序 AppID
    mchid: 1234567890                # 商户号
    key: your_api_key_here           # API v3 密钥
    notifyUrl: https://your-domain.com/api/payment/gateway/notify  # 回调地址

# 支付宝配置 (待实现)
alipay:
  appid: 2021000000000000
  privatekey: your_private_key
  alipaypublickey: your_public_key
  notifyUrl: https://your-domain.com/api/payment/gateway/notify
```

### 环境变量配置 (推荐生产环境)

```bash
export WECHAT_PAY_APPID=wx8888888888888888
export WECHAT_PAY_MCHID=1234567890
export WECHAT_PAY_KEY=your_api_key
export WECHAT_PAY_NOTIFY_URL=https://your-domain.com/api/payment/gateway/notify
```

---

## 切换支付网关

### 使用 Mock 模式 (测试)
```yaml
payment:
  gateway:
    type: mock
```

### 使用微信支付 (生产)
```yaml
payment:
  gateway:
    type: wechat
```

---

## API 接口

### 创建支付订单
```http
POST /api/payment/gateway/create?orderNumber=ORD123&amount=50.00&userId=1001
```

Response:
```json
{
  "code": 200,
  "data": "WX1234567890"
}
```

### 验证支付结果
```http
POST /api/payment/gateway/verify?orderNumber=ORD123&transactionId=WXTXN123
```

Response:
```json
{
  "code": 200,
  "data": true
}
```

### 发起退款
```http
POST /api/payment/gateway/refund?orderNumber=ORD123&transactionId=WXTXN123&amount=50.00
```

Response:
```json
{
  "code": 200,
  "data": true
}
```

### 查询订单状态
```http
GET /api/payment/gateway/status?orderNumber=ORD123
```

Response:
```json
{
  "code": 200,
  "data": "SUCCESS"
}
```

---

## 支付流程

### 1. 用户发起支付
```
小程序 → OrderService → PaymentService → PaymentGateway
```

### 2. 生成支付订单
```
PaymentGateway.createOrder(orderNumber, amount, userId)
  ↓
返回 transactionId
  ↓
保存到 PaymentRecord
```

### 3. 用户完成支付
```
支付平台 → 回调接口 (/payment/gateway/notify)
  ↓
验证签名
  ↓
更新订单状态
```

### 4. 查询支付结果
```
PaymentGateway.verifyOrder(orderNumber, transactionId)
  ↓
调用支付平台查询 API
  ↓
返回验证结果
```

---

## 退款流程

### 1. 用户申请退款
```
小程序 → 提交退款申请 → PaymentRefund
```

### 2. 管理员审核
```
管理后台 → 审核通过 → status=1
```

### 3. 执行退款
```
PaymentRefundService.processRefundPayment(refundId, transactionId)
  ↓
PaymentGateway.refundOrder()
  ↓
支付平台原路退回
  ↓
更新退款状态
```

---

## 测试步骤

### 1. Mock 模式测试
```bash
# 无需配置，默认使用 Mock

# 创建支付
curl -X POST "http://localhost:8084/payment/gateway/create?orderNumber=TEST001&amount=10.00&userId=1"

# 验证支付
curl -X POST "http://localhost:8084/payment/gateway/verify?orderNumber=TEST001&transactionId=MOCK123"

# 退款
curl -X POST "http://localhost:8084/payment/gateway/refund?orderNumber=TEST001&transactionId=MOCK123&amount=10.00"
```

### 2. 微信支付测试
```bash
# 1. 配置微信商户信息
# 2. 启动服务
# 3. 使用真实小程序测试
# 4. 验证回调
```

---

## 安全注意事项

### 1. 密钥管理
- ✅ 密钥不应硬编码在代码中
- ✅ 使用环境变量或配置中心
- ✅ 定期更换密钥
- ✅ 限制密钥访问权限

### 2. 签名验证
- ✅ 必须验证支付平台回调签名
- ✅ 使用 HTTPS 传输
- ✅ 防止重放攻击

### 3. 金额校验
- ✅ 服务器端校验金额
- ✅ 防止金额篡改
- ✅ 精确到分 (使用 BigDecimal)

### 4. 订单校验
- ✅ 验证订单唯一性
- ✅ 防止重复支付
- ✅ 订单超时处理

---

## 生产环境检查清单

### 配置检查
- [ ] 商户号配置正确
- [ ] API 密钥已更换为生产密钥
- [ ] 回调地址配置为公网地址
- [ ] 证书已正确配置

### 功能测试
- [ ] 支付流程完整测试
- [ ] 退款流程完整测试
- [ ] 回调处理正确
- [ ] 异常场景处理

### 安全测试
- [ ] 签名验证正常
- [ ] 金额校验正确
- [ ] 订单唯一性验证
- [ ] 防重放攻击测试

### 监控告警
- [ ] 支付成功率监控
- [ ] 退款成功率监控
- [ ] 异常订单告警
- [ ] 回调失败告警

---

## 常见问题

### Q1: 如何从 Mock 切换到微信支付？
A: 修改配置文件，设置 `payment.gateway.type=wechat` 并配置微信商户信息。

### Q2: 回调地址不生效？
A: 确保回调地址是公网可访问的 HTTPS 地址，且防火墙允许访问。

### Q3: 退款失败如何处理？
A: 检查商户号是否有退款权限，确认原支付订单已完成，确认退款金额不超过订单金额。

### Q4: 如何测试支付回调？
A: 可以使用微信支付测试工具或手动发送 POST 请求到回调接口。

---

## 联系支持

如遇到支付集成问题，请参考：
- 微信支付文档：https://pay.weixin.qq.com/wiki/doc/apiv3/
- 支付宝开放平台：https://opendocs.alipay.com/

---

**更新日期**: 2026-01-26  
**版本**: v1.3.0
