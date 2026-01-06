# 使用官方的OpenJDK 11镜像作为基础镜像
FROM openjdk:11.0-jre-buster

# 设置工作目录
WORKDIR /app

# 将构建好的jar文件复制到容器中
COPY target/WAF-0.0.1-SNAPSHOT.jar app.jar

# 暴露应用运行的端口
EXPOSE 8081

# 运行应用
CMD ["java", "-jar", "app.jar"]