#!/bin/bash

BASE_URL="http://localhost"

echo "🧪 开始 API 测试..."

# 测试 statistics-service
echo "Testing statistics-service..."
curl -s "${BASE_URL}:8087/statistics/dashboard" | head -c 100
echo ""

# 测试 message-service
echo "Testing message-service..."
curl -s "${BASE_URL}:8088/message/list?userId=1" | head -c 100
echo ""

# 测试 support-service
echo "Testing support-service..."
curl -s "${BASE_URL}:8089/support/ticket/list" | head -c 100
echo ""

# 测试 marketing-service
echo "Testing marketing-service..."
curl -s "${BASE_URL}:8090/marketing/activity/list" | head -c 100
echo ""

# 测试 finance-service
echo "Testing finance-service..."
curl -s "${BASE_URL}:8091/finance/balance?userId=1" | head -c 100
echo ""

# 测试 monitor-service
echo "Testing monitor-service..."
curl -s "${BASE_URL}:8092/monitor/piles/status" | head -c 100
echo ""

echo "✅ 所有服务 API 测试完成!"
