#!/bin/bash

echo "========================================"
echo "  充电桩平台 - 停止所有服务"
echo "========================================"

# 颜色定义
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

# 停止 Docker
echo -e "${YELLOW}[1/3] 停止 Docker 服务...${NC}"
cd docker
docker-compose down
echo -e "${GREEN}✓ Docker 服务已停止${NC}"

# 停止后端
echo ""
echo -e "${YELLOW}[2/3] 停止后端服务...${NC}"
pkill -f "charging-order"
pkill -f "charging-user"
pkill -f "charging-payment"
echo -e "${GREEN}✓ 后端服务已停止${NC}"

# 停止管理后台
echo ""
echo -e "${YELLOW}[3/3] 停止管理后台...${NC}"
pkill -f "charging-admin"
echo -e "${GREEN}✓ 管理后台已停止${NC}"

echo ""
echo -e "${GREEN}========================================"
echo "✓ 所有服务已停止"
echo "========================================${NC}"
