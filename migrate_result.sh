#!/bin/bash

set -e

echo "=========================================="
echo "  Result -> R 迁移脚本"
echo "=========================================="
echo ""

cd /workspace

# 备份
echo "📦 步骤 1: 备份代码..."
git add -A 2>/dev/null || true
git commit -m "backup: 迁移 Result 到 R 前的备份" 2>/dev/null || true
git branch backup-before-result-migration 2>/dev/null || true
echo "✅ 备份完成"
echo ""

# 替换导入
echo "🔄 步骤 2: 替换导入语句..."
cd /workspace/backend
find . -name "*.java" -type f -exec sed -i 's/import com\.charging\.[a-z]*\.common\.Result;/import com.charging.common.core.response.R;/g' {} \;
echo "✅ 导入替换完成"
echo ""

# 替换方法调用
echo "🔄 步骤 3: 替换方法调用..."
find . -name "*.java" -type f -exec sed -i 's/Result\.success(/R.ok(/g' {} \;
find . -name "*.java" -type f -exec sed -i 's/Result\.error(/R.fail(/g' {} \;
find . -name "*.java" -type f -exec sed -i 's/Result\.fail(/R.fail(/g' {} \;
echo "✅ 方法调用替换完成"
echo ""

# 删除重复文件
echo "🗑️  步骤 4: 删除重复的 Result.java..."
rm -f user-service/src/main/java/com/charging/user/common/Result.java
rm -f charging-service/src/main/java/com/charging/charging/common/Result.java
rm -f coupon-service/src/main/java/com/charging/coupon/common/Result.java
rm -f payment-service/src/main/java/com/charging/payment/common/Result.java
rm -f order-service/src/main/java/com/charging/order/common/Result.java
rm -f common-service/src/main/java/com/charging/common/common/Result.java
echo "✅ Result.java 删除完成 (6 个文件)"
echo ""

echo "🗑️  步骤 5: 删除重复的 GlobalExceptionHandler..."
rm -f user-service/src/main/java/com/charging/user/config/GlobalExceptionHandler.java
rm -f charging-service/src/main/java/com/charging/charging/config/GlobalExceptionHandler.java
rm -f coupon-service/src/main/java/com/charging/coupon/config/GlobalExceptionHandler.java
rm -f payment-service/src/main/java/com/charging/payment/config/GlobalExceptionHandler.java
rm -f order-service/src/main/java/com/charging/order/config/GlobalExceptionHandler.java
rm -f common-service/src/main/java/com/charging/common/config/GlobalExceptionHandler.java
echo "✅ GlobalExceptionHandler 删除完成 (6 个文件)"
echo ""

echo "=========================================="
echo "✅ 迁移完成！"
echo "=========================================="
echo ""
echo "请运行以下命令验证："
echo "  cd /workspace/backend"
echo "  mvn clean compile -DskipTests"
echo ""
