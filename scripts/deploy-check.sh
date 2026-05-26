#!/bin/bash

echo "=============================================="
echo "  生产环境部署检查清单"
echo "=============================================="
echo ""

# 颜色定义
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

check_pass() {
    echo -e "${GREEN}✓${NC} $1"
}

check_fail() {
    echo -e "${RED}✗${NC} $1"
}

check_warn() {
    echo -e "${YELLOW}⚠${NC} $1"
}

echo "1. 系统环境检查"
echo "----------------------------------------"

# 检查 Java 版本
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -1 | cut -d'"' -f2)
    if [[ $JAVA_VERSION == "11"* ]]; then
        check_pass "Java 版本：$JAVA_VERSION"
    else
        check_warn "Java 版本：$JAVA_VERSION (推荐 11)"
    fi
else
    check_fail "Java 未安装"
fi

# 检查 Maven
if command -v mvn &> /dev/null; then
    MVN_VERSION=$(mvn -version | head -1)
    check_pass "Maven: $MVN_VERSION"
else
    check_fail "Maven 未安装"
fi

# 检查 Docker
if command -v docker &> /dev/null; then
    DOCKER_VERSION=$(docker --version)
    check_pass "Docker: $DOCKER_VERSION"
else
    check_warn "Docker 未安装 (可选)"
fi

echo ""
echo "2. 配置文件检查"
echo "----------------------------------------"

# 检查环境变量
if [ ! -z "$NACOS_SERVER_ADDR" ]; then
    check_pass "NACOS_SERVER_ADDR 已配置"
else
    check_warn "NACOS_SERVER_ADDR 未配置"
fi

if [ ! -z "$MYSQL_HOST" ]; then
    check_pass "MYSQL_HOST 已配置"
else
    check_warn "MYSQL_HOST 未配置"
fi

if [ ! -z "$REDIS_HOST" ]; then
    check_pass "REDIS_HOST 已配置"
else
    check_warn "REDIS_HOST 未配置"
fi

echo ""
echo "3. 数据库连接检查"
echo "----------------------------------------"

if command -v mysql &> /dev/null && [ ! -z "$MYSQL_HOST" ]; then
    if mysql -h "$MYSQL_HOST" -u root -p"$MYSQL_PASSWORD" -e "SELECT 1" &> /dev/null; then
        check_pass "MySQL 连接正常"
    else
        check_fail "MySQL 连接失败"
    fi
else
    check_warn "无法检查 MySQL 连接"
fi

if command -v redis-cli &> /dev/null && [ ! -z "$REDIS_HOST" ]; then
    if redis-cli -h "$REDIS_HOST" -a "$REDIS_PASSWORD" ping &> /dev/null; then
        check_pass "Redis 连接正常"
    else
        check_fail "Redis 连接失败"
    fi
else
    check_warn "无法检查 Redis 连接"
fi

echo ""
echo "4. 安全检查"
echo "----------------------------------------"

if [ -f "backend/user-service/src/main/resources/application.yaml" ]; then
    if grep -q "secret:" backend/user-service/src/main/resources/application.yaml; then
        if grep -q "JWT_SECRET" backend/user-service/src/main/resources/application.yaml; then
            check_pass "JWT 密钥使用环境变量"
        else
            check_fail "JWT 密钥硬编码在配置文件中"
        fi
    fi
fi

echo ""
echo "5. 服务状态检查"
echo "----------------------------------------"

SERVICES=(
    "user-service:8081"
    "charging-service:8082"
    "order-service:8083"
    "payment-service:8084"
)

for service in "${SERVICES[@]}"; do
    name="${service%%:*}"
    port="${service##*:}"
    
    if curl -s "http://localhost:$port/actuator/health" | grep -q "UP"; then
        check_pass "$name 运行正常"
    else
        check_fail "$name 未响应"
    fi
done

echo ""
echo "6. 监控检查"
echo "----------------------------------------"

if curl -s "http://localhost:9090/-/healthy" | grep -q "Healthy"; then
    check_pass "Prometheus 运行正常"
else
    check_warn "Prometheus 未启动"
fi

if curl -s "http://localhost:3001/api/health" | grep -q "ok"; then
    check_pass "Grafana 运行正常"
else
    check_warn "Grafana 未启动"
fi

echo ""
echo "=============================================="
echo "  检查完成"
echo "=============================================="
echo ""
echo "建议："
echo "1. 确保所有服务使用生产环境配置"
echo "2. 数据库定期备份 (建议每天)"
echo "3. 配置日志收集系统 (ELK)"
echo "4. 设置告警通知 (邮件/短信)"
echo "5. 准备回滚方案"
echo ""
