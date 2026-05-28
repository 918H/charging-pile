#!/bin/bash

echo "========================================"
echo "  充电桩平台 - 开发环境快速启动脚本"
echo "========================================"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 加载环境变量
if [ -f ".env" ]; then
    echo -e "${GREEN}✓ 加载环境变量配置${NC}"
    export $(cat .env | grep -v '^#' | xargs)
else
    echo -e "${YELLOW}警告：未找到.env 文件，使用默认配置${NC}"
    export SERVER_HOST="0.0.0.0"
fi

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

# 启动基础设施
start_infrastructure() {
    echo -e "${YELLOW}正在启动基础设施...${NC}"
    cd docker
    docker compose up -d
    
    echo -e "${YELLOW}等待 MySQL 和 Redis 就绪...${NC}"
    sleep 30
    
    echo -e "${YELLOW}等待 Nacos 就绪...${NC}"
    sleep 30
    
    cd ..
    echo -e "${GREEN}✓ 基础设施启动完成${NC}"
}

# 初始化数据库
init_database() {
    echo -e "${YELLOW}正在初始化数据库...${NC}"
    
    if docker exec mysql mysql -u root -proot -e "USE charging_db" 2>/dev/null; then
        echo -e "${GREEN}✓ 数据库已初始化${NC}"
        return 0
    fi
    
    docker exec -i mysql mysql -u root -proot charging_db << 'SQL'
source /docker-entrypoint-initdb.d/init.sql;
SQL
    
    echo -e "${GREEN}✓ 数据库初始化完成${NC}"
}

# 编译后端
compile_backend() {
    echo -e "${YELLOW}正在编译后端代码...${NC}"
    mvn clean package -DskipTests -T 1C
    echo -e "${GREEN}✓ 后端编译完成${NC}"
}

# 启动微服务
start_services() {
    echo -e "${YELLOW}正在启动微服务...${NC}"
    
    # 检查端口是否被占用
    check_port() {
        if nc -z localhost $1 2>/dev/null; then
            return 0
        else
            return 1
        fi
    }
    
    # 使用 nohup 启动各服务
    SERVICES=(
        "user-service:8081"
        "charging-service:8082"
        "order-service:8083"
        "payment-service:8084"
        "coupon-service:8085"
        "common-service:8086"
    )
    
    for service_info in "${SERVICES[@]}"; do
        service_name="${service_info%%:*}"
        port="${service_info##*:}"
        
        if check_port $port; then
            echo -e "${GREEN}✓ $service_name 已在端口 $port 运行${NC}"
            continue
        fi
        
        echo -e "${YELLOW}启动 $service_name (端口：$port)...${NC}"
        cd backend/$service_name
        
        mkdir -p ../../logs
        nohup mvn spring-boot:run -Dspring-boot.run.profiles=dev \
            -Dserver.port=$port \
            -Dcatalina.home=../../logs \
            > ../../logs/${service_name}.log 2>&1 &
        
        cd ../..
        sleep 5
    done
    
    echo -e "${GREEN}✓ 微服务启动完成${NC}"
}

# 启动前端
start_frontend() {
    if check_node; then
        echo -e "${YELLOW}正在启动管理后台...${NC}"
        cd frontend/charging-admin
        
        if [ ! -d "node_modules" ]; then
            echo -e "${YELLOW}安装前端依赖（首次启动）...${NC}"
            npm install
        fi
        
        npm run dev > /dev/null 2>&1 &
        echo -e "${GREEN}✓ 管理后台启动完成 - http://localhost:3000${NC}"
        cd ../..
    fi
}

# 主流程
main() {
    check_docker
    start_infrastructure
    init_database
    compile_backend
    start_services
    start_frontend
    
    echo ""
    echo "========================================"
    echo "  🎉 启动完成！"
    echo "========================================"
    echo ""
    echo "📱 管理后台：http://localhost:3000"
    echo "🔧 Nacos 控制台：http://localhost:${NACOS_PORT:-8848}/nacos"
    echo "📊 Grafana: http://localhost:${GRAFANA_PORT:-3001}"
    echo "📈 Prometheus: http://localhost:${PROMETHEUS_PORT:-9090}"
    echo ""
    echo "停止服务：./scripts/stop.sh"
    echo "查看日志：tail -f logs/user-service.log"
    echo ""
}

main
