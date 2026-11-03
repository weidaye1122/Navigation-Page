# syntax=docker/dockerfile:1

# 前端和 JAR 均在本机 Docker 中构建，不依赖宿主机已安装 Node / Maven。
# Vue CLI 4 沿用 Node 16；有锁文件时使用 npm ci。
FROM --platform=$BUILDPLATFORM node:16.20.2-bullseye-slim AS frontend
WORKDIR /build/web
COPY web/package*.json ./
RUN --mount=type=cache,id=navigation-page-npm,target=/root/.npm,sharing=locked \
    npm install --global npm@9.9.4 --prefer-offline --maxsockets=5 --no-audit --no-fund
RUN --mount=type=cache,id=navigation-page-npm,target=/root/.npm,sharing=locked \
    if [ -f package-lock.json ]; then npm ci --legacy-peer-deps --prefer-offline --maxsockets=5 --no-audit --no-fund; \
    else npm install --legacy-peer-deps --prefer-offline --maxsockets=5 --no-audit --no-fund; fi
COPY web/ ./
RUN npm run build

FROM --platform=$BUILDPLATFORM maven:3.9-eclipse-temurin-8 AS backend
WORKDIR /build
COPY pom.xml ./
COPY src/ ./src/
COPY --from=frontend /build/web/dist/ ./src/main/resources/static/
RUN --mount=type=cache,id=navigation-page-maven,target=/root/.m2,sharing=locked \
    mvn -B -ntp verify

# 保留 Java 8，使用同时支持 amd64 / arm64 的运行时。
FROM eclipse-temurin:8-jre-jammy AS runtime
ARG VERSION=dev
ARG REVISION=unknown
LABEL org.opencontainers.image.source="https://github.com/weidaye1122/Navigation-Page" \
      org.opencontainers.image.version="$VERSION" \
      org.opencontainers.image.revision="$REVISION"

WORKDIR /
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone

COPY --from=backend /build/target/NavigationPage.jar /app.jar
COPY LICENSE /LICENSE

# 暴露端口
EXPOSE 8080

# 存储卷
VOLUME ["/logs","/database","/ext-resources"]

# 设置环境变量
ENV PARAMS=""

#镜像入口
ENTRYPOINT ["sh", "-c", "exec java $PARAMS -jar app.jar"]
