# 统一 IP 配置说明

## 🎯 统一 IP 地址

**`192.168.56.180`**

所有服务已统一使用此 IP 地址。

---

## 📦 已完成的配置

### 1. 环境变量 (`.env`)
```bash
SERVER_HOST=192.168.56.180
MYSQL_HOST=192.168.56.180
REDIS_HOST=192.168.56.180
NACOS_HOST=192.168.56.180
```

### 2. Docker 配置 (`docker/.env`)
```bash
DOCKER_HOST=0.0.0.0  # 允许外部访问
# 所有端口已配置
```

### 3. 前端配置 (`frontend/.env.local`)
```bash
VITE_SERVER_HOST=192.168.56.180
VITE_API_BASE_URL=http://192.168.56.180
```

### 4. Vite 配置 (`vite.config.js`)
```javascript
const SERVER_HOST = '192.168.56.180'
```

### 5. 部署脚本 (`~/deploy.sh`)
```bash
UNIFIED_IP="192.168.56.180"
```

---

## 🚀 访问地址

所有服务统一通过 **192.168.56.180** 访问：

| 服务 | URL | 端口 |
|------|-----|------|
| **管理后台** | http://192.168.56.180:3000 | 3000 |
| **用户服务** | http://192.168.56.180:8081 | 8081 |
| **充电服务** | http://192.168.56.180:8082 | 8082 |
| **订单服务** | http://192.168.56.180:8083 | 8083 |
| **支付服务** | http://192.168.56.180:8084 | 8084 |
| **优惠券服务** | http://192.168.56.180:8085 | 8085 |
| **公共服务** | http://192.168.56.180:8086 | 8086 |
| **Nacos** | http://192.168.56.180:8848/nacos | 8848 |
| **Grafana** | http://192.168.56.180:3001 | 3001 |
| **Prometheus** | http://192.168.56.180:9090 | 9090 |
| **RabbitMQ** | http://192.168.56.180:15672 | 15672 |
| **MySQL** | 192.168.56.180:3306 | 3306 |
| **Redis** | 192.168.56.180:6379 | 6379 |

---

## 🔧 VirtualBox 网络设置

### 必须配置：Host-Only 网络

1. **VirtualBox 管理** → **工具** → **网络管理器**

2. **创建 Host-Only 网络**（如果不存在）:
   ```
   名称：VirtualBox Host-Only Ethernet Adapter
   IPv4 地址：192.168.56.1
   IPv4 掩码：255.255.255.0
   ```

3. **虚拟机设置** → **网络**:
   ```
   适配器 1:
   - 启用网络连接：✓
   - 连接方式：Host-Only 适配器
   - 界面名称：VirtualBox Host-Only Ethernet Adapter
   ```

4. **启动虚拟机后设置静态 IP**:

   **Ubuntu/Debian**:
   ```bash
   # 编辑 Netplan 配置
   sudo vi /etc/netplan/01-netcfg.yaml
   
   # 添加配置
   network:
     version: 2
     ethernets:
       enp0s3:
         addresses:
           - 192.168.56.180/24
         gateway4: 192.168.56.1
         nameservers:
           addresses: [8.8.8.8, 1.1.1.1]
   
   # 应用配置
   sudo netplan apply
   ```

   **CentOS/Rocky**:
   ```bash
   # 编辑网卡配置
   sudo vi /etc/sysconfig/network-scripts/ifcfg-enp0s3
   
   # 添加/修改配置
   BOOTPROTO=static
   ONBOOT=yes
   IPADDR=192.168.56.180
   NETMASK=255.255.255.0
   GATEWAY=192.168.56.1
   DNS1=8.8.8.8
   
   # 重启网络
   sudo systemctl restart NetworkManager
   ```

5. **验证 IP**:
   ```bash
   ip addr show
   # 应该显示：inet 192.168.56.180
   ```

---

## ✅ 验证配置

### 在虚拟机中验证
```bash
# 检查 IP 地址
ip addr show | grep "inet " | grep -v 127.0.0.1
# 应该显示：192.168.56.180

# 测试服务
curl http://192.168.56.180:8081/membership/levels
```

### 在 Windows 主机验证
```powershell
# Ping 虚拟机
ping 192.168.56.180

# 测试 API
curl http://192.168.56.180:8081/membership/levels

# 访问管理后台
# 浏览器：http://192.168.56.180:3000
```

---

## 📝 配置文件清单

所有使用 `192.168.56.180` 的文件：

| 文件 | 路径 | 作用 |
|------|------|------|
| `.env` | `/workspace/` | 主环境配置 |
| `.env` | `/workspace/docker/` | Docker 环境 |
| `.env.local` | `/workspace/frontend/charging-admin/` | 前端环境 |
| `vite.config.js` | `/workspace/frontend/charging-admin/` | Vite 代理配置 |
| `deploy.sh` | `~/` | 一键部署脚本 |

---

## 🔄 如需更换 IP

如果要更换为其他 IP，只需修改这些文件：

1. `/workspace/.env` - SERVER_HOST
2. `/workspace/docker/.env` - 各 HOST 配置
3. `/workspace/frontend/charging-admin/.env.local` - VITE_SERVER_HOST
4. `/workspace/frontend/charging-admin/vite.config.js` - SERVER_HOST
5. `~/deploy.sh` - UNIFIED_IP

然后重启服务：
```bash
./scripts/stop.sh
./scripts/start.sh
```

---

## 🎯 总结

**统一 IP**: `192.168.56.180`

**一键部署后**，所有服务自动使用该 IP，Windows 主机可直接访问所有服务。
