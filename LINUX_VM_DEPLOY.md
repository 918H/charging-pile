# Linux 虚拟机部署指南

## 📋 适用系统

- ✅ **Ubuntu 22.04 LTS** (推荐)
- ✅ **Ubuntu 20.04 LTS**
- ✅ **CentOS 7/8**
- ✅ **Rocky Linux 8/9**
- ✅ **Debian 11/12**

---

## 🚀 快速部署（Ubuntu 示例）

### 步骤 1: 环境准备

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装必要工具
sudo apt install -y git curl wget vim net-tools unzip
```

### 步骤 2: 安装 Java 17

```bash
# 安装 JDK 17
sudo apt install -y openjdk-17-jdk

# 验证
java -version
```

### 步骤 3: 安装 Maven

```bash
# 安装 Maven
sudo apt install -y maven

# 或者手动安装最新版本
wget https://dlcdn.apache.org/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz
sudo tar -xzf apache-maven-3.9.6-bin.tar.gz -C /opt/
echo 'export MAVEN_HOME=/opt/apache-maven-3.9.6' >> ~/.bashrc
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.bashrc
source ~/.bashrc

# 验证
mvn -version
```

### 步骤 4: 安装 Docker

```bash
# 一键安装 Docker
curl -fsSL https://get.docker.com | bash

# 添加用户到 docker 组（免 sudo）
sudo usermod -aG docker $USER

# 启动 Docker
sudo systemctl enable docker
sudo systemctl start docker

# 验证
docker --version
docker ps
```

### 步骤 5: 安装 Docker Compose

```bash
# Ubuntu 22.04+ 已包含插件
docker compose version

# 或者手动安装
sudo apt install -y docker-compose-plugin
```

### 步骤 6: 获取项目代码

```bash
cd ~
git clone https://github.com/918H/charging-pile.git
cd charging-pile
```

### 步骤 7: 配置环境

```bash
# 获取虚拟机 IP
VM_IP=$(ip addr show | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | cut -d/ -f1 | head -1)
echo "虚拟机IP: $VM_IP"

# 修改环境配置
cat > .env << ENVEOF
# 使用虚拟机 IP
SERVER_HOST=$VM_IP

# 端口配置
USER_SERVICE_PORT=8081
CHARGING_SERVICE_PORT=8082
ORDER_SERVICE_PORT=8083
PAYMENT_SERVICE_PORT=8084
COUPON_SERVICE_PORT=8085
COMMON_SERVICE_PORT=8086
FRONTEND_PORT=3000
NACOS_PORT=8848
GRAFANA_PORT=3001
PROMETHEUS_PORT=9090
RABBITMQ_PORT=15672
MYSQL_PORT=3306
REDIS_PORT=6379

# 数据库
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_DATABASE=charging_db

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=charging123

# Nacos
NACOS_HOST=localhost
NACOS_PORT=8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos
ENVEOF

echo "✅ 环境配置完成"
cat .env | grep SERVER_HOST
```

### 步骤 8: 启动服务

```bash
# 一键启动
./scripts/start.sh

# 或者分步启动（推荐首次部署）

# 1. 启动基础设施
cd docker && docker compose up -d
cd ..

# 2. 等待服务启动
echo "等待 60 秒让 MySQL/Nacos 启动..."
sleep 60

# 3. 查看 Docker 状态
docker ps

# 4. 编译后端
mvn clean package -DskipTests -T 1C

# 5. 启动微服务
./scripts/start.sh
```

### 步骤 9: 配置防火墙

```bash
# Ubuntu (UFW)
sudo ufw allow 3000/tcp
sudo ufw allow 8081:8086/tcp
sudo ufw allow 8848/tcp
sudo ufw allow 3001/tcp
sudo ufw allow 9090/tcp
sudo ufw allow 15672/tcp
sudo ufw enable
sudo ufw status

# CentOS/Rocky (Firewalld)
# sudo firewall-cmd --permanent --add-port=3000/tcp
# sudo firewall-cmd --permanent --add-port=8081-8086/tcp
# sudo firewall-cmd --permanent --add-port=8848/tcp
# sudo firewall-cmd --reload
```

### 步骤 10: 访问服务

在 **Windows 主机浏览器**访问：
```
http://虚拟机IP:3000           # 管理后台
http://虚拟机IP:8848/nacos     # Nacos 控制台
http://虚拟机IP:3001           # Grafana
http://虚拟机IP:9090           # Prometheus
http://虚拟机IP:8081/membership/levels  # 测试 API
```

---

## 📦 CentOS/Rocky Linux 专项步骤

### 安装 Java 17

```bash
# 安装 JDK 17
sudo dnf install -y java-17-openjdk-devel

# 或者使用 sdkman
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"
sdk install java 17.0.9-tem
```

### 安装 Docker

```bash
# 安装 Docker
sudo dnf install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo dnf install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker
sudo systemctl enable docker
sudo systemctl start docker

# 验证
docker --version
```

### 配置防火墙

```bash
# 开放端口
sudo firewall-cmd --permanent --add-port=3000/tcp
sudo firewall-cmd --permanent --add-port=8081-8086/tcp
sudo firewall-cmd --permanent --add-port=8848/tcp
sudo firewall-cmd --permanent --add-port=3001/tcp
sudo firewall-cmd --permanent --add-port=9090/tcp
sudo firewall-cmd --permanent --add-port=15672/tcp
sudo firewall-cmd --reload

# 查看状态
sudo firewall-cmd --list-ports
```

---

## 🔧 VirtualBox 网络配置

### 方案 1: 桥接模式（推荐）

**VirtualBox 设置**:
```
设置 → 网络 → 适配器 1
- 启用网络连接：✓
- 连接方式：网络桥接适配器
- 界面名称：选择你的网卡（有线或无线）
```

**优点**: 虚拟机获得独立 IP，与主机同网段

**验证**:
```bash
# 查看 IP（应该与 Windows 主机同网段）
ip addr show
# 例如：192.168.1.x / 192.168.0.x
```

### 方案 2: Host-Only 模式

**VirtualBox 设置**:
```
设置 → 网络 → 适配器 1
- 连接方式：Host-Only 适配器
```

**优点**: 仅主机可访问，更安全

**缺点**: 虚拟机无法访问外网（需要第二个 NAT 网卡）

---

## 📊 自动化部署脚本

创建 `~/deploy.sh`:

```bash
#!/bin/bash

echo "=========================================="
echo "  充电桩平台 - Linux 虚拟机一键部署"
echo "=========================================="

# 获取虚拟机 IP
VM_IP=$(ip addr show | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | cut -d/ -f1 | head -1)
echo "✓ 虚拟机 IP: $VM_IP"

# 检查 Docker
if ! command -v docker &> /dev/null; then
    echo "✗ Docker 未安装，开始安装..."
    curl -fsSL https://get.docker.com | bash
    sudo usermod -aG docker $USER
else
    echo "✓ Docker 已安装"
fi

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "✗ Java 未安装，开始安装..."
    sudo apt install -y openjdk-17-jdk  # Ubuntu/Debian
    # sudo dnf install -y java-17-openjdk-devel  # CentOS/Rocky
else
    echo "✓ Java 已安装 ($(java -version 2>&1 | head -1))"
fi

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "✗ Maven 未安装，开始安装..."
    sudo apt install -y maven  # Ubuntu/Debian
    # sudo dnf install -y maven  # CentOS/Rocky
else
    echo "✓ Maven 已安装 ($(mvn -version 2>&1 | head -1))"
fi

# 配置环境
echo "✓ 配置环境变量..."
cat > .env << ENVEOF
SERVER_HOST=$VM_IP
USER_SERVICE_PORT=8081
CHARGING_SERVICE_PORT=8082
ORDER_SERVICE_PORT=8083
PAYMENT_SERVICE_PORT=8084
COUPON_SERVICE_PORT=8085
COMMON_SERVICE_PORT=8086
FRONTEND_PORT=3000
NACOS_PORT=8848
GRAFANA_PORT=3001
PROMETHEUS_PORT=9090
RABBITMQ_PORT=15672
MYSQL_PORT=3306
REDIS_PORT=6379
MYSQL_HOST=localhost
MYSQL_USER=root
MYSQL_PASSWORD=root
MYSQL_DATABASE=charging_db
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=charging123
NACOS_HOST=localhost
NACOS_PORT=8848
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos
ENVEOF

# 配置 Docker 环境
cat > docker/.env << ENVEOF
DOCKER_HOST=0.0.0.0
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
ENVEOF

# 启动基础设施
echo "✓ 启动基础设施..."
cd docker
docker compose up -d
cd ..

# 等待启动
echo "⏳ 等待 MySQL 和 Nacos 启动（60 秒）..."
sleep 60

# 查看容器状态
docker ps

# 编译后端
echo "✓ 编译后端代码..."
mvn clean package -DskipTests -T 1C

# 启动微服务
echo "✓ 启动微服务..."
./scripts/start.sh

# 配置防火墙（Ubuntu）
if command -v ufw &> /dev/null; then
    echo "✓ 配置防火墙..."
    sudo ufw allow 3000/tcp
    sudo ufw allow 8081:8086/tcp
    sudo ufw allow 8848/tcp
    sudo ufw allow 3001/tcp
    sudo ufw status
fi

echo "=========================================="
echo "  🎉 部署完成！"
echo "=========================================="
echo ""
echo "📱 服务地址:"
echo "   管理后台：http://$VM_IP:3000"
echo "   Nacos:    http://$VM_IP:8848/nacos"
echo "   Grafana:  http://$VM_IP:3001"
echo "   Prometheus: http://$VM_IP:9090"
echo ""
echo "🔑 默认账号:"
echo "   Nacos: admin/nacos"
echo "   Grafana: admin/charging123"
echo ""
echo "📋 常用命令:"
echo "   查看服务：docker ps"
echo "   查看日志：tail -f logs/user-service.log"
echo "   停止服务：./scripts/stop.sh"
echo ""
```

使用方法：
```bash
cd ~
vim deploy.sh
# 粘贴上面的脚本内容
chmod +x deploy.sh
./deploy.sh
```

---

## 🔍 验证部署

### 检查服务状态
```bash
# Docker 容器
docker ps

# 应该有 6 个容器：
# mysql, redis, nacos, rabbitmq, prometheus, grafana
```

### 测试 API
```bash
# 获取虚拟机 IP
VM_IP=$(ip addr show | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | cut -d/ -f1 | head -1)

# 测试会员接口
curl http://$VM_IP:8081/membership/levels

# 测试积分商城
curl http://$VM_IP:8081/points-mall/items

# 测试充电套餐
curl http://$VM_IP:8081/charging-package/list
```

### 查看日志
```bash
# 实时查看日志
tail -f logs/user-service.log
tail -f logs/order-service.log

# 查看 Docker 日志
docker logs nacos --tail 50
docker logs mysql --tail 50
```

---

## 🐛 故障排查

### 问题 1: Docker 容器启动失败
```bash
# 查看容器日志
docker logs <容器名>

# 重启容器
docker restart <容器名>

# 查看配置
docker compose config
```

### 问题 2: Nacos 无法连接
```bash
# 检查 MySQL 是否就绪
docker exec mysql mysql -u root -proot -e "SHOW DATABASES;"

# 手动创建 nacos 数据库
docker exec -it mysql mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS nacos_config;"

# 重启 Nacos
docker restart nacos
```

### 问题 3: 微服务启动失败
```bash
# 查看日志
tail -f logs/*.log

# 检查端口占用
sudo netstat -tlnp | grep :8081

# 检查 Java 版本
java -version

# 增加内存
export MAVEN_OPTS="-Xms512m -Xmx2048m"
```

### 问题 4: 防火墙阻止访问
```bash
# 临时关闭防火墙（测试用）
sudo ufw disable  # Ubuntu
sudo systemctl stop firewalld  # CentOS

# 如果关闭后能访问，说明需要配置防火墙规则
```

---

## 📊 性能优化

### 优化 Maven 编译速度
```bash
# 使用阿里云镜像
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'XML'
<?xml version="1.0" encoding="UTF-8"?>
<settings>
  <mirrors>
    <mirror>
      <id>aliyun</id>
      <mirrorOf>*</mirrorOf>
      <name>Aliyun Maven</name>
      <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
  </mirrors>
</settings>
XML
```

### 优化 Docker 性能
```bash
# 使用国内镜像加速
sudo cat > /etc/docker/daemon.json << 'JSON'
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.docker-cn.com"
  ]
}
JSON

# 重启 Docker
sudo systemctl restart docker
```

### 增加虚拟机内存
在 VirtualBox 设置中调整内存到 **8GB**。

---

## ✅ 部署清单

- [ ] 系统更新完成
- [ ] Java 17 安装成功
- [ ] Maven 安装成功
- [ ] Docker 安装成功
- [ ] 项目代码下载完成
- [ ] 环境配置完成（.env）
- [ ] Docker容器启动成功
- [ ] 微服务编译成功
- [ ] 微服务启动成功
- [ ] 防火墙配置完成
- [ ] 所有服务可访问

---

## 📞 获取帮助

如果遇到问题：
1. 查看日志：`tail -f logs/*.log`
2. 检查容器：`docker ps`
3. 测试 API：`curl http://虚拟机IP:端口/接口`
4. 查看详细文档：`README.md`, `CONFIG_GUIDE.md`
