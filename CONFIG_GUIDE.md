# 统一配置指南

## 📋 配置说明

本项目已统一使用环境变量管理所有 IP 和端口配置，支持三种部署场景：

| 场景 | IP 配置 | 说明 |
|------|--------|------|
| 本地开发 | `127.0.0.1` 或 `localhost` | 本机开发和测试 |
| VirtualBox | 虚拟机 IP (如 `192.168.1.100`) | 虚拟机部署 |
| 云服务器 | 云服务器公网 IP | 生产环境部署 |

---

## 🛠️ 配置文件结构

```
/workspace/
├── .env                 # 主环境配置（需手动创建）
├── .env.example         # 环境配置模板
├── docker/.env          # Docker 专用配置
├── frontend/charging-admin/.env.local  # 前端配置
```

---

## 🚀 快速配置

### 场景 1: 本地开发（Windows/macOS/Linux）

**无需修改**，默认使用 `localhost`:

```bash
# 使用默认配置即可
./scripts/start.sh
```

**访问地址**:
- 管理后台：http://localhost:3000
- Nacos: http://localhost:8848/nacos
- Grafana: http://localhost:3001

---

### 场景 2: VirtualBox 虚拟机部署

#### 步骤 1: 获取虚拟机 IP
```bash
# 在虚拟机中运行
ip addr show | grep "inet " | grep -v 127.0.0.1
# 输出示例：inet 192.168.1.100/24
```

#### 步骤 2: 修改配置文件

**修改 `/workspace/.env`**:
```bash
SERVER_HOST=192.168.1.100
```

**修改 `/workspace/docker/.env`**:
```bash
# 允许外部访问
DOCKER_HOST=0.0.0.0

# 数据库配置
MYSQL_USER=root
MYSQL_PASSWORD=root
```

#### 步骤 3: 重启服务
```bash
# 停止服务
./scripts/stop.sh

# 重启 Docker
cd docker && docker compose down && docker compose up -d

# 重启微服务
cd /workspace && ./scripts/start.sh
```

#### 步骤 4: 在 Windows 访问
打开浏览器访问：
```
http://192.168.1.100:3000      # 管理后台
http://192.168.1.100:8848/nacos # Nacos
http://192.168.1.100:3001      # Grafana
```

---

### 场景 3: 云服务器部署（阿里云/腾讯云/AWS）

#### 步骤 1: 修改 `.env`

```bash
SERVER_HOST=你的公网 IP
```

#### 步骤 2: 配置安全组

在云服务商控制台放行以下端口：

```bash
3000    # 前端
8081-8086  # 微服务
8848    # Nacos
3001    # Grafana
9090    # Prometheus
15672   # RabbitMQ
3306    # MySQL (可选)
6379    # Redis (可选)
```

#### 步骤 3: 配置防火墙
```bash
# Ubuntu
sudo ufw allow 3000
sudo ufw allow 8081:8086
sudo ufw allow 8848
sudo ufw allow 3001
sudo ufw allow 9090
sudo ufw allow 15672

# CentOS/Firewalld
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --permanent --add-port=8081-8086/tcp
sudo firewall-cmd --permanent --add-port=8848/tcp
sudo firewall-cmd --permanent --add-port=3001/tcp
sudo firewall-cmd --reload
```

#### 步骤 4: 重启服务
```bash
./scripts/start.sh
```

#### 步骤 5: 访问
```
http://你的公网 IP:3000
```

---

## 📦 环境变量详解

### 主环境配置 (`.env`)

```bash
# 服务器 IP 地址
SERVER_HOST=127.0.0.1    # 本地/虚拟机 IP/公网 IP

# 微服务端口
USER_SERVICE_PORT=8081
CHARGING_SERVICE_PORT=8082
ORDER_SERVICE_PORT=8083
PAYMENT_SERVICE_PORT=8084
COUPON_SERVICE_PORT=8085
COMMON_SERVICE_PORT=8086

# 前端端口
FRONTEND_PORT=3000

# 中间件端口
NACOS_PORT=8848
GRAFANA_PORT=3001
PROMETHEUS_PORT=9090
RABBITMQ_PORT=15672
MYSQL_PORT=3306
REDIS_PORT=6379

# 数据库配置
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_DATABASE=charging_db

# Redis 配置
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=charging123

# Nacos 配置
NACOS_HOST=localhost
NACOS_PORT=8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos
```

### Docker 配置 (`docker/.env`)

```bash
DOCKER_HOST=0.0.0.0  # 允许外部访问
NACOS_PORT=8848
GRAFANA_PORT=3001
PROMETHEUS_PORT=9090
RABBITMQ_PORT=15672
MYSQL_PORT=3306
REDIS_PORT=6379
MYSQL_DATABASE=charging_db
MYSQL_USER=root
MYSQL_PASSWORD=root
REDIS_PASSWORD=charging123
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos
```

### 前端配置 (`frontend/charging-admin/.env.local`)

```bash
VITE_API_BASE_URL=http://localhost
VITE_USER_SERVICE_PORT=8081
VITE_CHARGING_SERVICE_PORT=8082
VITE_ORDER_SERVICE_PORT=8083
VITE_PAYMENT_SERVICE_PORT=8084
VITE_COUPON_SERVICE_PORT=8085
```

---

## 🔧 常用配置场景

### 1. 修改微服务端口
编辑 `.env`:
```bash
USER_SERVICE_PORT=9081   # 改为 9081
CHARGING_SERVICE_PORT=9082
```

### 2. 使用非标准数据库端口
编辑 `docker/.env`:
```bash
MYSQL_PORT=3307  # 改为 3307
```

### 3. 前端使用 HTTPS
编辑 `frontend/charging-admin/vite.config.js`:
```javascript
server: {
  https: true,
  port: 3000
}
```

### 4. 禁用某些服务
编辑 `docker/.env`:
```bash
# 注释掉不需要的服务
# GRAFANA_PORT=3001
# PROMETHEUS_PORT=9090
```

---

## 🐛 故障排查

### 问题 1: 修改配置后不生效
```bash
# 清理并重启
cd docker && docker compose down
cd /workspace && ./scripts/stop.sh

# 重新加载环境变量
source .env

# 重新启动
./scripts/start.sh
```

### 问题 2: 端口冲突
```bash
# 查看端口占用
lsof -i :3000
netstat -tlnp | grep :3000

# 修改端口或杀死进程
```

### 问题 3: 无法从外部访问
```bash
# 检查防火墙
sudo ufw status

# 检查安全组（云服务器）
# 检查 Docker 绑定地址
docker ps
```

### 问题 4: IP 配置错误
```bash
# 查看当前配置
cat .env
cat docker/.env

# 验证 IP
ip addr show
curl ifconfig.me  # 查看公网 IP
```

---

## 📊 配置验证

启动后运行以下命令验证配置：

```bash
# 1. 检查服务状态
./scripts/deploy-check.sh

# 2. 测试 API
curl http://${SERVER_HOST:-127.0.0.1}:8081/membership/levels

# 3. 检查端口监听
netstat -tlnp | grep -E "3000|8081|8848"

# 4. 查看 Docker 服务
docker ps
```

**预期输出**:
```
✅ 用户服务运行在端口 8081
✅ 前端服务运行在端口 3000
✅ Nacos 运行在端口 8848
```

---

## ✅ 总结

**已统一配置**:
- ✅ 所有 IP 通过环境变量管理
- ✅ 所有端口可配置
- ✅ 支持三种部署场景
- ✅ 一键切换不同环境

**快速切换**:
```bash
# 本地开发
cp .env.example .env
./scripts/start.sh

# VirtualBox 部署
# 修改 .env 中的 SERVER_HOST 为虚拟机 IP
./scripts/start.sh

# 云服务器部署
# 修改 .env 中的 SERVER_HOST 为公网 IP
# 配置安全组和防火墙
./scripts/start.sh
```

---

## 📝 文件清单

| 文件 | 用途 | 修改频率 |
|------|------|---------|
| `.env` | 主环境配置 | 首次配置 |
| `.env.example` | 配置模板 | 参考用 |
| `docker/.env` | Docker 配置 | 首次配置 |
| `frontend/.env.local` | 前端配置 | 可选修改 |
| `scripts/start.sh` | 启动脚本 | 修改端口时 |
