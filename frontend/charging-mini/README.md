# 充电桩小程序

基于微信小程序开发的充电桩用户端应用

## 功能清单

- [ ] 微信登录授权
- [ ] 附近充电桩地图展示
- [ ] 充电桩详情查看
- [ ] 扫码充电
- [ ] 在线支付
- [ ] 订单管理
- [ ] 个人中心

## 开发环境

- 微信开发者工具 1.6+
- 基础库 2.19.0+

## 快速开始

1. 使用微信开发者工具打开项目
2. 配置 appid
3. 修改 `utils/config.js` 中的 API 地址
4. 编译运行

## API 配置

```javascript
// utils/config.js
const API_BASE_URL = 'https://api.charging.com';
const API_TIMEOUT = 15000;
```

## 目录结构

```
charging-mini/
├── pages/           # 页面
├── components/      # 组件
├── utils/           # 工具函数
├── api/             # API 接口
├── images/          # 图片资源
└── app.js/wxss      # 全局配置
```
