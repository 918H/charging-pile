#!/bin/bash

echo "========================================"
echo "  充电桩平台 - 开发环境快速启动脚本"
echo "========================================"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 检查 Docker
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}错误：Docker 未安装${NC}"
        exit 1
    fi
    
    if ! docker info &> /dev/null; then
        echo -e "${RED}错误：Docker 未运行${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ Docker 检查通过${NC}"
}

# 检查 Node.js
check_node() {
    if ! command -v node &> /dev/null; then
        echo -e "${YELLOW}警告：Node.js 未安装，管理后台无法启动${NC}"
        return 1
    fi
    
    echo -e "${GREEN}✓ Node.js 检查通过 (v$(node -v))${NC}"
    return 0
}

# 检查 MySQL
check_mysql() {
    if ! command -v mysql &> /dev/null; then
        echo -e "${YELLOW}警告：MySQL 客户端未安装，跳过数据库初始化${NC}"
        return 1
    fi
    
    return 0
}

# 启动基础设施
start_infrastructure() {
    echo ""
    echo -e "${YELLOW}[1/5] 启动基础设施...${NC}"
    
    cd docker
    docker-compose up -d
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}错误：基础设施启动失败${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 基础设施启动完成${NC}"
    echo "  - MySQL: localhost:3306"
    echo "  - Redis: localhost:6379"
    echo "  - Nacos: http://localhost:8848"
    echo "  - Prometheus: http://localhost:9090"
    echo "  - Grafana: http://localhost:3000"
    
    cd ..
    
    # 等待服务就绪
    echo -e "${YELLOW}等待数据库就绪...${NC}"
    sleep 10
}

# 初始化数据库
init_database() {
    echo ""
    echo -e "${YELLOW}[2/5] 初始化数据库...${NC}"
    
    if check_mysql; then
        if mysql -h localhost -u root -proot -e "SELECT 1" &> /dev/null; then
            echo -e "${GREEN}✓ 连接 MySQL 成功${NC}"
            
            mysql -h localhost -u root -proot < backend/user-service/src/main/resources/db/schema.sql
            mysql -h localhost -u root -proot < backend/user-service/src/main/resources/db/schema-ux-features.sql
            
            echo -e "${GREEN}✓ 数据库初始化完成${NC}"
        else
            echo -e "${RED}错误：无法连接 MySQL${NC}"
            echo -e "${YELLOW}请手动导入 SQL 文件:${NC}"
            echo "  mysql -h localhost -u root -p < backend/user-service/src/main/resources/db/schema.sql"
        fi
    else
        echo -e "${YELLOW}跳过数据库初始化${NC}"
    fi
}

# 构建后端
build_backend() {
    echo ""
    echo -e "${YELLOW}[3/5] 构建后端服务...${NC}"
    
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}错误：Maven 未安装${NC}"
        exit 1
    fi
    
    cd backend
    mvn clean package -DskipTests -q
    
    if [ $? -ne 0 ]; then
        echo -e "${RED}错误：Maven 构建失败${NC}"
        exit 1
    fi
    
    echo -e "${GREEN}✓ 后端构建完成${NC}"
    cd ..
}

# 启动后端服务
start_backend() {
    echo ""
    echo -e "${YELLOW}[4/5] 启动后端服务...${NC}"
    
    cd backend
    bash start.sh > /dev/null 2>&1 &
    
    echo "  等待服务启动..."
    sleep 5
    
    echo -e "${GREEN}✓ 后端服务启动中${NC}"
    echo "  - user-service (8081): http://localhost:8081"
    echo "  - charging-service (8082): http://localhost:8082"
    echo "  - order-service (8083): http://localhost:8083"
    echo "  - payment-service (8084): http://localhost:8084"
    echo "  - coupon-service (8085): http://localhost:8085"
    echo "  - common-service (8086): http://localhost:8086"
    
    cd ..
}

# 启动管理后台
start_admin() {
    echo ""
    echo -e "${YELLOW}[5/5] 启动管理后台...${NC}"
    
    if ! check_node; then
        echo -e "${YELLOW}跳过管理后台启动${NC}"
        return
    fi
    
    cd frontend/charging-admin
    
    if [ ! -d "node_modules" ]; then
        echo -e "${YELLOW}安装依赖...${NC}"
        npm install --registry=https://registry.npmmirror.com
    fi
    
    npm run dev > /dev/null 2>&1 &
    
    echo -e "${GREEN}✓ 管理后台启动成功${NC}"
    echo "  - 访问地址：http://localhost:3000"
    
    cd ../..
}

# 显示完成信息
show_complete() {
    echo ""
    echo "========================================"
    echo -e "${GREEN}✓ 开发环境启动完成!${NC}"
    echo "========================================"
    echo ""
    echo "服务访问地址:"
    echo "  - 管理后台：http://localhost:3000"
    echo "  - Nacos:  http://localhost:8848"
    echo "  - Grafana: http://localhost:3000"
    echo "  - Swagger: http://localhost:8081/doc.html"
    echo ""
    echo -e "${YELLOW}提示:${NC}"
    echo "  - 小程序开发请导入 frontend/charging-mini 到微信开发者工具"
    echo "  - 查看日志：docker-compose logs -f"
    echo "  - 停止服务：./scripts/stop.sh"
    echo ""
}

# 主流程
main() {
    check_docker
    start_infrastructure
    init_database
    build_backend
    start_backend
    start_admin
    show_complete
}

# 执行
main
