#!/bin/bash

# 充电桩系统压力测试脚本
# 使用 Apache JMeter 进行压力测试

BASE_URL="${1:-http://localhost:8081}"
THREADS="${2:-100}"
DURATION="${3:-300}"

echo "======================================"
echo "  充电桩系统压力测试"
echo "======================================"
echo "基础 URL: $BASE_URL"
echo "并发线程数：$THREADS"
echo "持续时间：$DURATION 秒"
echo ""

# 检查 JMeter 是否安装
if ! command -v jmeter &> /dev/null; then
    echo "错误：JMeter 未安装，请先安装 JMeter"
    echo "下载地址：https://jmeter.apache.org/download_jmeter.cgi"
    exit 1
fi

# 执行压力测试
jmeter -n -t test/pressure-test.jmx \
    -JBASE_URL=$BASE_URL \
    -JTHREADS=$THREADS \
    -JDURATION=$DURATION \
    -l test/results/jtl/pressure-test-$(date +%Y%m%d-%H%M%S).jtl \
    -e -o test/results/html/pressure-test-$(date +%Y%m%d-%H%M%S)

echo ""
echo "测试完成！报告已生成到 test/results/html/"
echo "请打开生成的 HTML 报告查看测试结果"
