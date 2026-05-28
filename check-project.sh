#!/bin/bash

echo "🔍 充电桩平台项目检查"
echo "========================"

# 检查 Java 环境
echo -n "Java 环境："
if command -v java &> /dev/null; then
    java -version 2>&1 | head -1
else
    echo "❌ 未安装 Java"
fi

# 检查 Maven
echo -n "Maven 环境："
if command -v mvn &> /dev/null; then
    mvn -version 2>&1 | head -1
else
    echo "❌ 未安装 Maven"
fi

# 检查 Node
echo -n "Node 环境："
if command -v node &> /dev/null; then
    node -v
else
    echo "❌ 未安装 Node"
fi

# 检查 Docker
echo -n "Docker 环境："
if command -v docker &> /dev/null; then
    docker --version
else
    echo "❌ 未安装 Docker"
fi

# 检查微服务目录
echo ""
echo "📦 微服务检查:"
for service in user charging order payment coupon statistics message support marketing finance monitor common; do
    if [ -f "backend/${service}-service/pom.xml" ]; then
        echo "  ✅ ${service}-service"
    else
        echo "  ❌ ${service}-service (缺失)"
    fi
done

# 检查前端
echo ""
echo "🌐 前端检查:"
if [ -f "frontend/charging-admin/package.json" ]; then
    echo "  ✅ charging-admin"
else
    echo "  ❌ charging-admin (缺失)"
fi

# 检查数据库脚本
echo ""
echo "🗄️ 数据库脚本:"
for sql in init.sql V1__user.sql V2__charging_order.sql V3__payment_coupon.sql V4__new_features.sql; do
    if [ -f "database/$sql" ]; then
        echo "  ✅ $sql"
    else
        echo "  ❌ $sql (缺失)"
    fi
done

# 检查文档
echo ""
echo "📚 文档检查:"
for doc in README.md PROJECT_DELIVERY.md API_TEST_COLLECTION.md QUICK_REFERENCE.md; do
    if [ -f "$doc" ]; then
        echo "  ✅ $doc"
    else
        echo "  ❌ $doc (缺失)"
    fi
done

# 统计
echo ""
echo "📊 统计信息:"
echo "  Java 文件：$(find backend -name '*.java' 2>/dev/null | wc -l)"
echo "  Vue 文件：$(find frontend -name '*.vue' 2>/dev/null | wc -l)"
echo "  配置文件：$(find . -name '*.yaml' -o -name '*.yml' 2>/dev/null | wc -l)"
echo "  文档文件：$(find . -name '*.md' 2>/dev/null | wc -l)"
echo "  Git 提交：$(git log --oneline 2>/dev/null | wc -l)"

echo ""
echo "========================"
echo "检查完成!"
