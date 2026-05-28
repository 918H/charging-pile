# Windows 10 + VirtualBox 部署充电桩平台

## 📋 方案概述

在 Windows 10 上使用 VirtualBox 运行 Linux 虚拟机，在虚拟机中部署充电桩平台。

**优点**:
- ✅ 不依赖 Windows 环境配置
- ✅ 与主机环境隔离
- ✅ 可复用标准 Linux 部署脚本
- ✅ 性能接近原生 Linux

---

## 🛠️ 准备工作

### 1. 下载必要软件

| 软件 | 下载地址 | 推荐版本 |
|------|---------|---------|
| VirtualBox | https://www.virtualbox.org/ | 7.0.x |
| Ubuntu ISO | https://ubuntu.com/download/desktop | 22.04 LTS |
| MobaXterm | https://mobaxterm.mobatek.net/ | 免费版（可选，用于 SSH 连接） |

### 2. 系统要求

| 资源 | 最低配置 | 推荐配置 |
|------|---------|---------|
| CPU | 4 核 | 8 核 |
| 内存 | 4GB | 8GB |
| 硬盘 | 40GB | 80GB SSD |
| Windows 主机 | 8GB 总内存 | 16GB 总内存 |

---

## 📦 步骤 1: 安装 VirtualBox

### 1.1 安装 VirtualBox
1. 下载 VirtualBox 7.0.x
2. 双击运行安装程序
3. 按默认选项安装
4. 安装完成后重启电脑

### 1.2 安装 VirtualBox Extension Pack
1. 下载 Extension Pack（与 VirtualBox 同版本）
2. 双击安装，输入管理员密码
3. 接受许可协议

---

## 🐧 步骤 2: 创建 Ubuntu 虚拟机

### 2.1 下载 Ubuntu ISO
```
下载地址：https://ubuntu.com/download/desktop
文件：ubuntu-22.04.3-desktop-amd64.iso
大小：约 4.7GB
```

### 2.2 创建虚拟机

1. **打开 VirtualBox** → 点击"新建"

2. **基本信息**
   ```
   名称：charging-pile
   类型：Linux
   版本：Ubuntu (64-bit)
   ```

3. **内存大小**
   ```
   推荐：8192 MB (8GB)
   最低：4096 MB (4GB)
   ```

4. **创建虚拟硬盘**
   ```
   硬盘文件类型：VDI (VirtualBox Disk Image)
   存储在物理硬盘：动态分配
   大小：80GB
   ```

5. **完成创建**

### 2.3 配置虚拟机

**选中虚拟机** → 点击"设置"

#### 系统 → 处理器
```
处理器数量：4 (或更多)
启用 3D 加速：✓
```

#### 显示 → 显存大小
```
显存大小：128MB
```

#### 网络 → 适配器 1
```
启用网络连接：✓
连接方式：网络桥接适配器 (Bridged Adapter)
界面名称：选择你的网卡（有线或无线）
```
> **重要**: 使用"桥接模式"可以让虚拟机获得独立 IP，方便访问

#### 存储 → 控制器：IDE
```
选择"空"的光盘图标
点击右侧光盘图标 → 选择"选择磁盘文件"
找到并选择下载的 ubuntu-22.04.3-desktop-amd64.iso
```

---

## 🔧 步骤 3: 安装 Ubuntu

### 3.1 启动虚拟机安装 Ubuntu

1. 点击"启动"
2. 选择语言：**English** (推荐) 或 简体中文
3. 点击"Install Ubuntu"
4. 键盘布局：English (US)
5. 安装类型：**Erase disk and install Ubuntu** (只影响虚拟硬盘)
6. 时区：Shanghai (北京时间)
7. 创建用户
   ```
   Your name: charging
   Your computer's name: charging-vm
   Username: charging
   Password: charging123
   ```

### 3.2 等待安装完成（约 15-30 分钟）

### 3.3 重启虚拟机
```
点击"Restart Now"
取出虚拟光盘（按提示操作）
```

---

## 🌐 步骤 4: 配置虚拟机

### 4.1 登录 Ubuntu
```
用户名：charging
密码：charging123
```

### 4.2 打开终端
按 `Ctrl+Alt+T` 或点击应用程序菜单搜索"Terminal"

### 4.3 安装 VirtualBox Guest Additions（增强功能）

```bash
# 更新软件源
sudo apt update

# 安装必要工具
sudo apt install -y gcc make perl dkms linux-headers-$(uname -r)

# 在 VirtualBox 菜单：设备 → 安装增强功能

# 挂载并安装（如果在桌面环境下）
cd /media/$USER/VBox_GAs_*
sudo ./VBoxLinuxAdditions.run

# 重启虚拟机
sudo reboot
```

### 4.4 安装必要软件
```bash
sudo apt update
sudo apt upgrade -y

# 安装 Git、Vim、curl、net-tools
sudo apt install -y git vim curl net-tools wget

# 安装 SSH 服务（方便从 Windows 连接）
sudo apt install -y openssh-server
sudo systemctl enable ssh
sudo systemctl start ssh

# 查看虚拟机 IP 地址
ip addr show
# 记录 eth0 或 enp0s3 的 IP，例如：192.168.1.100
```

---

## ☕ 步骤 5: 安装 Java 和 Maven

### 5.1 安装 JDK 17
```bash
# 添加 PPA
sudo apt install -y wget apt-transport-https
wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null
echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list

# 安装 JDK 17
sudo apt update
sudo apt install -y temurin-17-jdk

# 验证安装
java -version
# 应该显示：openjdk version "17.0.x"
```

### 5.2 安装 Maven
```bash
sudo apt install -y maven

# 验证安装
mvn -version
# 应该显示：Apache Maven 3.8.x
```

### 5.3 配置 Maven（可选优化）
```bash
# 使用阿里云镜像加速
cat > ~/.m2/settings.xml << 'XML'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>central</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
XML
```

---

## 🐳 步骤 6: 安装 Docker

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | bash

# 将当前用户加入 docker 组（免 sudo）
sudo usermod -aG docker $USER

# 启动 Docker
sudo systemctl enable docker
sudo systemctl start docker

# 验证安装
docker --version
docker ps

# 安装 Docker Compose
sudo apt install -y docker-compose-plugin
docker compose version
```

---

## 🚀 步骤 7: 部署充电桩平台

### 7.1 克隆项目代码
```bash
cd ~
git clone https://github.com/918H/charging-pile.git
cd charging-pile
```

### 7.2 启动基础设施（Docker）
```bash
cd docker
docker compose up -d

# 等待服务启动（约 60 秒）
sleep 60

# 查看容器状态
docker ps
# 应该看到：mysql, redis, nacos, rabbitmq, prometheus, grafana
```

### 7.3 初始化数据库
```bash
# 等待 MySQL 完全启动
sleep 30

# 执行数据库初始化脚本
docker exec -i mysql mysql -u root -proot charging_db << 'SQL'
source /docker-entrypoint-initdb.d/init.sql;
SQL

# 执行迁移脚本
docker exec -i mysql mysql -u root -proot charging_db << 'SQL'
source /workspace/backend/common/src/main/resources/db/migration/V20240102005__add_membership_pile_points.sql;
source /workspace/backend/common/src/main/resources/db/migration/V20240102006__alter_sys_user_add_membership.sql;
source /workspace/backend/common/src/main/resources/db/migration/V20240102007__add_points_mall_package_withdrawal.sql;
SQL
```

### 7.4 编译后端代码
```bash
cd ~/charging-pile

# 使用阿里云镜像加速编译
mvn clean package -DskipTests -T 1C

# 编译时间：约 5-10 分钟（首次）
```

### 7.5 启动微服务
```bash
# 使用启动脚本
cd ~/charging-pile/scripts
chmod +x start.sh
./start.sh

# 或者手动启动各服务（推荐用于调试）
cd ~/charging-pile/backend

# 终端 1: 启动用户服务
cd user-service
nohup mvn spring-boot:run > /tmp/user-service.log 2>&1 &

# 终端 2: 启动充电服务
cd ../charging-service
nohup mvn spring-boot:run > /tmp/charging-service.log 2>&1 &

# 终端 3: 启动订单服务
cd ../order-service
nohup mvn spring-boot:run > /tmp/order-service.log 2>&1 &

# 终端 4: 启动支付服务
cd ../payment-service
nohup mvn spring-boot:run > /tmp/payment-service.log 2>&1 &

# 终端 5: 启动优惠券服务
cd ../coupon-service
nohup mvn spring-boot:run > /tmp/coupon-service.log 2>&1 &

# 终端 6: 启动公共服务
cd ../common-service
nohup mvn spring-boot:run > /tmp/common-service.log 2>&1 &
```

### 7.6 启动前端
```bash
cd ~/charging-pile/frontend/charging-admin

# 安装依赖（首次）
npm install

# 启动开发服务器
npm run dev

# 会显示：
# ➜  Local:   http://localhost:3000/
# ➜  Network: http://<虚拟机IP>:3000/
```

---

## 🌐 步骤 8: 从 Windows 访问服务

### 8.1 获取虚拟机 IP
在虚拟机中执行：
```bash
ip addr show | grep "inet " | grep -v 127.0.0.1
# 例如：inet 192.168.1.100/24
```

### 8.2 在 Windows 浏览器访问

| 服务 | URL | 说明 |
|------|-----|------|
| 管理后台 | http://<虚拟机IP>:3000 | Vue 前端 |
| Nacos 控制台 | http://<虚拟机IP>:8848/nacos | 注册中心 (nacos/nacos) |
| Grafana | http://<虚拟机IP>:3001 | 监控 (admin/charging123) |
| Prometheus | http://<虚拟机IP>:9090 | 监控查询 |
| RabbitMQ | http://<虚拟机IP>:15672 | 消息队列 (charging/charging123) |

### 8.3 测试 API
在 Windows PowerShell 中执行：
```powershell
# 测试会员接口
curl http://<虚拟机IP>:8081/membership/levels

# 测试积分商城
curl http://<虚拟机IP>:8081/points-mall/items

# 测试充电套餐
curl http://<虚拟机IP>:8081/charging-package/list
```

---

## 🔧 VirtualBox 网络配置详解

### 方案对比

| 网络模式 | IP 获取 | Windows 访问 | 外网访问 | 推荐度 |
|---------|--------|------------|---------|--------|
| **桥接模式** | 独立 IP | ✓ 直接访问 | ✓ | ⭐⭐⭐⭐⭐ |
| NAT + 端口转发 | 10.0.2.x | ✓ 转发端口 | ✓ | ⭐⭐⭐ |
| Host-Only | 仅主机 IP | ✓ | ✗ | ⭐⭐⭐⭐ |

### 推荐使用：桥接模式

**配置方法**:
1. VirtualBox → 设置 → 网络
2. 连接方式：**网络桥接适配器**
3. 界面名称：选择你的网卡
4. 启动虚拟机后自动获取同网段 IP

### 备用方案：Host-Only + NAT

如果桥接模式有问题，可以使用双网卡：

**适配器 1**: NAT（用于上外网）
**适配器 2**: Host-Only（用于 Windows 访问）

```bash
# 安装后会看到两个网卡
ip addr show
# enp0s3: NAT (外网)
# enp0s8: Host-Only (Windows 访问，例如 192.168.56.101)
```

---

## 📊 资源优化配置

### 优化虚拟机性能

在 VirtualBox 设置中：

#### 系统 → 加速器
```
虚拟化引擎：
- 启用嵌套分页：✓
- 启用 PAE/NX: ✓
```

#### 显示 → 屏幕
```
显存：128MB
启用 3D 加速：✓
启用 2D 加速：✓
```

#### 存储
```
使用 SSD 类型的虚拟硬盘：
- 选中虚拟硬盘文件
- 点击"属性"
- 勾选"使用主机 I/O 缓存"
```

### 优化 Linux 性能

```bash
# 禁用不需要服务
sudo systemctl disable bluetooth.service
sudo systemctl disable cups.service
sudo systemctl disable printer.target

# 调整 swappiness（减少交换分区使用）
echo "vm.swappiness=10" | sudo tee -a /etc/sysctl.conf
sudo sysctl -p

# 预读优化
sudo blockdev --setra 4096 /dev/sda
```

---

## 🐛 故障排查

### 问题 1: 虚拟机无法获取 IP
```bash
# 重启网络
sudo systemctl restart NetworkManager

# 检查 DHCP
dhclient -v

# 查看网络状态
nmcli device status
```

### 问题 2: Windows 无法访问虚拟机
```bash
# 检查防火墙
sudo ufw status
sudo ufw allow 3000
sudo ufw allow 8081:8086
sudo ufw allow 8848

# 检查 SSH
sudo systemctl status ssh
```

### 问题 3: Docker 启动失败
```bash
# 检查 Docker 状态
sudo systemctl status docker

# 查看日志
sudo journalctl -u docker -f

# 重启 Docker
sudo systemctl restart docker
```

### 问题 4: 内存不足
```bash
# 在 VirtualBox 中调整内存
# 关闭虚拟机 → 设置 → 系统 → 内存 → 调整为 8GB

# 或者在 Linux 中增加 swap
sudo fallocate -l 4G /swapfile
sudo chmod 600 /swapfile
sudo mkswap /swapfile
sudo swapon /swapfile
echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
```

### 问题 5: 编译过慢
```bash
# 使用 Maven 阿里云镜像（已配置）
# 使用多核并行编译
mvn clean package -T 1C

# 增加 Maven 内存
export MAVEN_OPTS="-Xms512m -Xmx2048m"
```

---

## 📝 快速命令总结

```bash
# 查看虚拟机 IP
ip addr show | grep "inet " | grep -v 127.0.0.1

# 查看 Docker 容器
docker ps

# 查看服务日志
tail -f /tmp/user-service.log

# 重启微服务
pkill -f "user-service"
cd ~/charging-pile/backend/user-service
nohup mvn spring-boot:run > /tmp/user-service.log 2>&1 &

# 查看内存使用
free -h
```

---

## ✅ 验证部署成功

### 在 Windows 浏览器访问：

1. **管理后台**: http://<虚拟机IP>:3000
2. **测试 API**: 
   ```powershell
   curl http://<虚拟机IP>:8081/membership/levels
   ```
3. **Nacos 控制台**: http://<虚拟机IP>:8848/nacos

### 预期响应
```json
{
  "code": 200,
  "data": [
    {"levelName": "普通会员", "discountRate": 1.00},
    {"levelName": "白银会员", "discountRate": 0.98},
    ...
  ]
}
```

---

## 🎯 最佳实践

1. **快照备份**: 安装完成后创建 VirtualBox 快照
2. **共享文件夹**: 设置 Windows 与虚拟机代码共享
3. **固定 IP**: 在路由器中绑定虚拟机 MAC 地址
4. **定期更新**: `sudo apt update && sudo apt upgrade`

---

## 📞 获取帮助

如果遇到问题：
1. 查看虚拟机日志
2. 检查 Docker 容器状态
3. 确认防火墙设置
4. 查看详细错误信息

**推荐配置**: 8GB 内存 + 4 核 CPU + 80GB SSD
