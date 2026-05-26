# 微信支付沙箱配置

## 沙箱环境

微信支付提供沙箱环境用于开发测试，无需真实资金。

### 沙箱接入步骤

1. 申请沙箱账号
   - 登录微信商户平台
   - 申请沙箱测试账号
   - 获取沙箱密钥

2. 配置沙箱参数
```yaml
wechat:
  pay:
    appid: wx8888888888888888
    mchid: 1900000000
    apiKey: sandbox_key_xxxxxxxxxxxxx
    notifyUrl: https://your-domain.com/api/payment/wechat/notify
    refundNotifyUrl: https://your-domain.com/api/payment/wechat/refund-notify
```

3. API 接口
   - 统一下单：POST /api/payment/wechat/unifiedorder
   - 查询订单：GET /api/payment/wechat/query/{orderId}
   - 申请退款：POST /api/payment/wechat/refund
   - 退款查询：GET /api/payment/wechat/refund-query/{refundId}

### 测试卡片

```
测试卡号：6226 1234 5678 9012
密码：000000
CVN2: 123
有效期：2025/12
手机号：12345678901
验证码：123456
```

## 支付宝沙箱

### 配置参数
```yaml
alipay:
  appId: 9021000131654607
  privateKey: MIIEvQIBADANBgkq...
  alipayPublicKey: MIIBIjANBgkq...
  notifyUrl: https://your-domain.com/api/payment/alipay/notify
  returnUrl: https://your-domain.com/payment/success
```

### 沙箱账号
- 买家账号：jxtthh9603@sandbox.com
- 卖家账号：rmnapt9825@sandbox.com
- 登录密码：111111
- 支付密码：111111
