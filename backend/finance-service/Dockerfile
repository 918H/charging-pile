FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/*.jar app.jar
ENV JAVA_OPTS="-Xms512m -Xmx512m"
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
