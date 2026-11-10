# 多阶段构建：阶段一 Maven+JDK8 编译 war；阶段二 官方 Tomcat9 镜像运行
# 技术基线：JavaEE8 (javax.*) · Tomcat 9 · JDK 8 · MySQL 8
# 构建：docker build -t lab-equipment:1.0 .
# 运行：docker run -d -p 8080:8080 --name lab-equipment lab-equipment:1.0

FROM maven:3.8-openjdk-8 AS build
WORKDIR /src
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q package -DskipTests

FROM tomcat:9.0.85-jdk8-openjdk
# 移除默认应用，保留 ROOT 空目录
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /src/target/demo.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
