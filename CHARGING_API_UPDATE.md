# 充电服务 API 更新

## 新增接口

### 1. 扫码开始充电
```
POST /order/charging/start
Content-Type: application/json

Request:
{
  "userId": 1001,
  "pileId": 1,
  "slotId": 1,
  "chargingMode": 1,
  "targetSoc": 80,
  "couponId": null
}

Response:
{
  "code": 200,
  "data": {
    "success": true,
    "orderId": 12345,
    "orderNumber": "ORD20260126123456789",
    "pileId": 1,
    "slotId": 1,
    "startTime": "2026-01-26T12:34:56",
    "currentPrice": 1.5,
    "message": "充电已开始"
  }
}
```

### 2. 停止充电
```
POST /order/charging/stop?orderId=12345&reason=用户主动停止

Response:
{
  "code": 200,
  "data": true
}
```

### 3. 获取充电进度
```
GET /order/charging/progress?orderId=12345

Response:
{
  "code": 200,
  "data": {
    "orderId": 12345,
    "status": 1,
    "startTime": "2026-01-26T12:34:56",
    "elapsedMinutes": 15,
    "powerConsumed": 7.5,
    "currentSoc": 30,
    "targetSoc": 80,
    "currentVoltage": 400,
    "currentCurrent": 100,
    "currentPower": 40,
    "avgPower": 35,
    "batteryTemp": 25,
    "currentAmount": 11.25,
    "currentPrice": 1.5,
    "estimatedEndTime": "2026-01-26T13:34:56",
    "estimatedTotalAmount": 30.00
  }
}
```

### 4. 获取充电桩实时电价
```
GET /order/charging/price?pileId=1

Response:
{
  "code": 200,
  "data": 1.5
}
```

### 5. 计算占位费
```
POST /order/charging/occupation-fee?pileId=1&fullTime=2026-01-26T14:00:00&leaveTime=2026-01-26T14:45:00

Response:
{
  "code": 200,
  "data": 7.5
}
```

## WebSocket 实时推送

### 连接地址
```
ws://localhost:8083/ws/charging/{orderId}
```

### 推送数据格式
```json
{
  "orderId": 12345,
  "status": 1,
  "elapsedMinutes": 15,
  "powerConsumed": 7.5,
  "currentSoc": 30,
  "targetSoc": 80,
  "currentVoltage": 400,
  "currentCurrent": 100,
  "currentPower": 40,
  "currentAmount": 11.25
}
```

## 前端集成示例

### 微信小程序充电进度页
```javascript
// pages/charging/charging.js
Page({
  data: {
    orderId: null,
    progress: null,
    socketTask: null
  },

  onLoad(options) {
    this.setData({ orderId: options.orderId });
    this.initWebSocket();
  },

  initWebSocket() {
    const socketTask = wx.connectSocket({
      url: `ws://localhost:8083/ws/charging/${this.data.orderId}`
    });

    socketTask.onMessage((res) => {
      const progress = JSON.parse(res.data);
      this.setData({ progress });
    });

    this.setData({ socketTask });
  },

  onUnload() {
    if (this.data.socketTask) {
      this.data.socketTask.close();
    }
  }
});
```
