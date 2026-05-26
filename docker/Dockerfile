FROM openjdk:11-jre-slim

WORKDIR /app

# 创建非 root 用户
RUN groupadd -r charging && useradd -r -g charging charging

# 复制 jar 包
COPY target/*.jar app.jar

# 设置权限
RUN chown charging:charging app.jar && \
    chmod 755 app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown charging:charging /app/logs

USER charging

# 暴露端口
EXPOSE 8081

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8081/actuator/health || exit 1

# JVM 参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
