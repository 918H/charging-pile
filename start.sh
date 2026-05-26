#!/bin/bash

# 充电桩小程序快速启动脚本

echo "======================================"
echo "  充电桩小程序 - 启动脚本"
echo "======================================"

# 检查 Docker 是否运行
if ! command -v docker &> /dev/null; then
    echo "错误：Docker 未安装"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "错误：Docker Compose 未安装"
    exit 1
fi

echo ""
echo "步骤 1: 启动基础设施 (MySQL, Redis, RabbitMQ, Nacos)"
echo "------------------------------------------------------"
cd docker
docker-compose up -d
cd ..

echo ""
echo "等待 Nacos 启动..."
sleep 30

echo ""
echo "步骤 2: 初始化数据库"
echo "------------------------------------------------------"
echo "数据库将自动通过 docker-compose 初始化脚本初始化"

echo ""
echo "步骤 3: 编译后端项目"
echo "------------------------------------------------------"
cd backend
mvn clean package -DskipTests
cd ..

echo ""
echo "步骤 4: 启动微服务"
echo "------------------------------------------------------"

# 设置环境变量
export NACOS_SERVER_ADDR=localhost:8848
export NACOS_NAMESPACE=dev
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=charging_db
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=root
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=

# 启动各个微服务
echo "启动用户服务 (端口 8081)..."
cd backend/user-service
java -jar target/user-service-1.0.0.jar > ../../logs/user-service.log 2>&1 &
echo "用户服务 PID: $!"
cd ../..

echo "启动充电桩服务 (端口 8082)..."
cd backend/charging-service
java -jar target/charging-service-1.0.0.jar > ../../logs/charging-service.log 2>&1 &
echo "充电桩服务 PID: $!"
cd ../..

echo "启动订单服务 (端口 8083)..."
cd backend/order-service
java -jar target/order-service-1.0.0.jar > ../../logs/order-service.log 2>&1 &
echo "订单服务 PID: $!"
cd ../..

echo "启动支付服务 (端口 8084)..."
cd backend/payment-service
java -jar target/payment-service-1.0.0.jar > ../../logs/payment-service.log 2>&1 &
echo "支付服务 PID: $!"
cd ../..

echo "启动公共服务 (端口 8085)..."
cd backend/common-service
java -jar target/common-service-1.0.0.jar > ../../logs/common-service.log 2>&1 &
echo "公共服务 PID: $!"
cd ../..

echo ""
echo "等待微服务启动..."
sleep 15

echo ""
echo "步骤 5: 安装前端依赖"
echo "------------------------------------------------------"
cd frontend/charging-admin
if [ ! -d "node_modules" ]; then
    npm install
else
    echo "前端依赖已安装，跳过"
fi
cd ../..

echo ""
echo "======================================"
echo "  启动完成!"
echo "======================================"
echo ""
echo "服务访问地址:"
echo "  - Nacos 控制台：http://localhost:8848/nacos (账号：nacos/nacos)"
echo "  - 用户服务：http://localhost:8081/api/user"
echo "  - 充电桩服务：http://localhost:8082/api/charging"
echo "  - 订单服务：http://localhost:8083/api/order"
echo "  - 支付服务：http://localhost:8084/api/payment"
echo "  - 公共服务：http://localhost:8085/api/common"
echo "  - 前端管理：http://localhost:3000"
echo ""
echo "默认账户:"
echo "  用户名：admin"
echo "  密码：admin123"
echo ""
echo "查看日志:"
echo "  cd logs && tail -f user-service.log"
echo ""
echo "停止服务:"
echo "  cd docker && docker-compose down"
echo ""
echo "======================================"
