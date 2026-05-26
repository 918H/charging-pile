# 充电桩小程序前端 - 后台管理系统

## 技术栈

- Vue 3 + Vite
- Element Plus UI 组件库
- Pinia 状态管理
- Vue Router 路由管理
- Axios HTTP 客户端

## 开发指南

### 安装依赖

```bash
npm install
```

### 启动开发服务器

```bash
npm run dev
```

访问 http://localhost:3000

### 构建生产版本

```bash
npm run build
```

### 代码检查

```bash
npm run lint
```

## 项目结构

```
src/
├── api/           # API 接口
├── assets/        # 静态资源
├── components/    # 公共组件
├── layout/        # 布局组件
├── router/        # 路由配置
├── stores/        # Pinia stores
├── styles/        # 全局样式
├── utils/         # 工具函数
├── views/         # 页面组件
├── App.vue        # 根组件
└── main.js        # 入口文件
```

## API 代理配置

开发环境下通过 Vite 代理转发请求到后端微服务:

- /api/user → http://localhost:8081 (用户服务)
- /api/charging → http://localhost:8082 (充电桩服务)
- /api/order → http://localhost:8083 (订单服务)
- /api/payment → http://localhost:8084 (支付服务)
- /api/common → http://localhost:8085 (公共服务)
