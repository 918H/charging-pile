# 快速启动故障排查指南

## 🚨 常见问题速查

### 问题 1: Docker 无法启动
**错误信息**: `command not found: docker` 或 `Cannot connect to Docker daemon`

**解决方案**:
```bash
# Windows: 安装 Docker Desktop
# https://www.docker.com/products/docker-desktop

# macOS: 安装 Docker Desktop
brew install --cask docker

# Linux: 安装 Docker
curl -fsSL https://get.docker.com | bash
sudo systemctl start docker
```

---

### 问题 2: Java/Maven 未安装
**错误信息**: `java: command not found` 或 `mvn: command not found`

**解决方案**:
```bash
# 安装 JDK 17
# Windows: 下载安装 https://adoptium.net/
# macOS: brew install openjdk@17
# Linux: sudo apt install openjdk-17-jdk

# 安装 Maven
# macOS: brew install maven
# Linux: sudo apt install maven

# 验证安装
java -version
mvn -version
```

---

### 问题 3: 端口被占用
**错误信息**: `Address already in use` 或 `BindException`

**解决方案**:
```bash
# Windows 查看端口占用
netstat -ano | findstr :8081
netstat -ano | findstr :3306
netstat -ano | findstr :6379

# macOS/Linux 查看端口占用
lsof -i :8081
lsof -i :3306
lsof -i :6379

# 杀死占用端口的进程 (Windows)
taskkill /F /PID <进程 ID>

# macOS/Linux
kill -9 <进程 ID>
```

**常用端口**:
- 8081-8086: 微服务端口
- 3306: MySQL
- 6379: Redis
- 8848: Nacos
- 3000: 前端管理后台
- 9090: Prometheus
- 3001: Grafana

---

### 问题 4: 内存不足
**错误信息**: `OutOfMemoryError` 或 `Killed`

**解决方案**:
1. 关闭不需要的应用
2. 调整 JVM 内存参数:
```bash
# Windows (PowerShell)
$env:MAVEN_OPTS="-Xms512m -Xmx1024m"

# macOS/Linux
export MAVEN_OPTS="-Xms512m -Xmx1024m"
```

3. 减少同时启动的微服务数量

---

### 问题 5: 数据库连接失败
**错误信息**: `Communications link failure` 或 `Connection refused`

**解决方案**:
```bash
# 1. 检查 MySQL 是否启动
docker ps | grep mysql

# 2. 查看 MySQL 日志
docker logs mysql

# 3. 重启 MySQL
docker restart mysql

# 4. 等待 30 秒让 MySQL 完全启动
sleep 30
```

---

### 问题 6: Nacos 启动失败
**错误信息**: `Connection refused` 或 `Nacos server not found`

**解决方案**:
```bash
# 1. 检查 MySQL 中是否创建 nacos 数据库
docker exec -it mysql mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS nacos_config;"

# 2. 查看 Nacos 日志
docker logs nacos

# 3. 重启 Nacos
docker restart nacos

# 4. 等待 60 秒让 Nacos 完全启动
```

---

### 问题 7: 前端构建失败
**错误信息**: `vite build failed` 或 `Cannot find module`

**解决方案**:
```bash
# 1. 清理缓存
cd frontend/charging-admin
rm -rf node_modules package-lock.json
npm cache clean --force

# 2. 重新安装
npm install

# 3. 启动
npm run dev
```

---

### 问题 8: 微服务启动慢
**错误信息**: 启动超过 2 分钟

**解决方案**:
```bash
# 1. 增加 JVM 堆内存
export JAVA_OPTS="-Xms1g -Xmx2g"

# 2. 禁用不需要的服务
# 编辑 application.yml，关闭不必要的配置

# 3. 使用 SSD 硬盘
# Maven 仓库和 Docker 数据卷放在 SSD 上
```

---

## 📋 完整启动步骤

### Windows 用户

```powershell
# 1. 启动 Docker Desktop
# 等待托盘图标变绿

# 2. 启动基础设施
cd docker
docker-compose up -d

# 3. 等待 60 秒
timeout /t 60

# 4. 初始化数据库
docker exec -it mysql mysql -u root -proot charging_db < /workspace/backend/user-service/src/main/resources/db/schema.sql

# 5. 编译后端
cd ..
mvn clean package -DskipTests -T 1C

# 6. 启动微服务
cd scripts
.\start.sh

# 7. 启动前端
cd ..\frontend\charging-admin
npm install
npm run dev
```

### macOS/Linux 用户

```bash
# 1. 启动基础设施
cd /workspace/docker
docker-compose up -d

# 2. 等待 60 秒
sleep 60

# 3. 初始化数据库
docker exec -it mysql mysql -u root -proot charging_db < /workspace/backend/user-service/src/main/resources/db/schema.sql

# 4. 编译后端
cd /workspace
mvn clean package -DskipTests -T 1C

# 5. 启动微服务
./scripts/start.sh

# 6. 启动前端
cd frontend/charging-admin
npm install
npm run dev
```

---

## 🔍 诊断命令

### 检查 Docker 容器
```bash
docker ps -a
# 应该看到 6 个容器：mysql, redis, nacos, rabbitmq, prometheus, grafana
```

### 检查端口监听
```bash
# Windows
netstat -ano | findstr ":808"

# macOS/Linux
lsof -i :808
```

### 查看服务日志
```bash
# 查看 Nacos 日志
docker logs nacos --tail 100

# 查看 MySQL 日志
docker logs mysql --tail 100

# 查看微服务日志
tail -f /workspace/logs/*.log
```

### 测试 API
```bash
# 测试用户服务
curl http://localhost:8081/membership/levels

# 测试充电服务
curl http://localhost:8082/charging/pile/list

# 测试前端
curl http://localhost:3000
```

---

## 🎯 最小化启动（低配置电脑）

如果电脑配置较低（< 8GB 内存），可以只启动必需服务：

```bash
# 只启动 MySQL 和 Redis
cd docker
docker-compose up -d mysql redis

# 手动启动 1-2 个微服务
cd backend/user-service
mvn spring-boot:run

cd ../charging-service
mvn spring-boot:run

# 前端
cd frontend/charging-admin
npm run dev
```

---

## 📞 获取帮助

### 查看系统要求
- CPU: 4 核以上（推荐 8 核）
- 内存：8GB 以上（推荐 16GB）
- 硬盘：10GB 可用空间
- OS: Windows 10+/macOS 11+/Ubuntu 20.04+

### 收集错误信息
1. 错误截图
2. 完整的错误日志
3. 执行的命令
4. 操作系统版本

### 常见错误码
| 错误码 | 含义 | 解决方案 |
|--------|------|----------|
| 127 | 命令未找到 | 安装对应软件 |
| 1 | 一般错误 | 查看详细日志 |
| 137 | 内存不足 | 增加内存或 JVM 参数 |
| 139 | 段错误 | 重新安装软件 |

---

## ✅ 验证安装成功

启动后测试以下 URL：

| 服务 | URL | 预期响应 |
|------|-----|----------|
| 前端后台 | http://localhost:3000 | 登录页面 |
| 用户服务 | http://localhost:8081/membership/levels | JSON 数据 |
| Nacos 控制台 | http://localhost:8848/nacos | 控制台页面 |
| Grafana | http://localhost:3001 | 监控仪表板 |
| Prometheus | http://localhost:9090 | 监控页面 |
