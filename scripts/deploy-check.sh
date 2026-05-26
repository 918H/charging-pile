#!/bin/bash

echo "========================================"
echo "  充电桩平台 - 生产环境部署检查清单"
echo "========================================"

SUCCESS=0
FAILED=0

check() {
    local name=$1
    local cmd=$2
    
    if eval $cmd &> /dev/null; then
        echo "  ✓ $name"
        ((SUCCESS++))
        return 0
    else
        echo "  ✗ $name"
        ((FAILED++))
        return 1
    fi
}

echo ""
echo "1. 系统要求"
echo "--------------------------------------"
check "Docker" "docker --version"
check "Docker Compose" "docker-compose --version"
check "Java 11+" "java -version"
check "Maven" "mvn --version"
check "Node.js 16+" "node --version"

echo ""
echo "2. 端口检查"
echo "--------------------------------------"
echo "  检查以下端口是否未被占用:"
echo "  - 3306 (MySQL)"
echo "  - 6379 (Redis)"
echo "  - 8848 (Nacos)"
echo "  - 3000 (Grafana)"
echo "  - 8081-8086 (微服务)"

echo ""
echo "3. 配置文件"
echo "--------------------------------------"
if [ -f "docker/.env" ]; then
    echo "  ✓ Docker 环境变量配置"
    ((SUCCESS++))
else
    echo "  ✗ Docker 环境变量配置"
    ((FAILED++))
fi

if [ -f "backend/order-service/src/main/resources/application.yml" ]; then
    echo "  ✓ 应用配置存在"
    ((SUCCESS++))
else
    echo "  ✗ 应用配置存在"
    ((FAILED++))
fi

echo ""
echo "4. 支付配置 (生产环境)"
echo "--------------------------------------"
echo "  请确认以下配置:"
echo "  - 微信商户号配置"
echo "  - API 密钥配置"
echo "  - 回调地址配置"

echo ""
echo "5. 数据库"
echo "--------------------------------------"
if mysql -h localhost -u root -proot -e "SELECT 1" &> /dev/null; then
    echo "  ✓ MySQL 连接正常"
    ((SUCCESS++))
    
    DB_COUNT=$(mysql -h localhost -u root -proot -e "SHOW DATABASES" | grep -E "charging|pile" | wc -l)
    if [ $DB_COUNT -gt 0 ]; then
        echo "  ✓ 数据库已创建"
        ((SUCCESS++))
    else
        echo "  ✗ 数据库未创建"
        ((FAILED++))
    fi
else
    echo "  ✗ MySQL 连接异常"
    ((FAILED++))
fi

echo ""
echo "========================================"
echo "检查结果：$SUCCESS 通过，$FAILED 失败"
echo "========================================"

if [ $FAILED -eq 0 ]; then
    echo -e "\n\033[0;32m✓ 生产环境检查通过，可以部署\033[0m"
    exit 0
else
    echo -e "\n\033[0;31m✗ 存在未通过项，请检查后重新部署\033[0m"
    exit 1
fi
