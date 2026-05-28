#!/bin/bash
set -e

echo "🚀 启动充电桩平台..."

# 启动基础设施
echo "📦 启动 MySQL, Redis, Nacos..."
docker-compose up -d mysql redis nacos

# 等待基础设施就绪
echo "⏳ 等待服务启动..."
sleep 30

# 构建并启动微服务
echo "🔧 构建微服务..."
docker-compose build

echo "🚀 启动 12 个微服务..."
docker-compose up -d

# 启动监控
echo "📊 启动 Prometheus 和 Grafana..."
docker-compose up -d prometheus grafana

echo "✅ 所有服务已启动!"
echo ""
echo "服务访问地址:"
echo "  - Nacos: http://localhost:8848/nacos"
echo "  - Prometheus: http://localhost:9090"
echo "  - Grafana: http://localhost:3000 (admin/admin123)"
echo ""
echo "查看日志：docker-compose logs -f [service-name]"
echo "停止服务：docker-compose down"
