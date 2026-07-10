# Navigation Page
Navigation Page 是一个面向团队场景的导航与资源管理系统，用于统一整理常用网站入口和业务资源。项目基于 Spring Boot 与 Vue 2 构建，支持分类导航、卡片管理、用户与角色管理、附件上传、静态网站托管，以及动态卡片和数据备份恢复等能力。
## 群晖 Compose 部署
先在群晖 File Station 中创建以下目录：
```text
/volume1/docker/Navigation-Page
/volume1/docker/Navigation-Page/config
/volume1/docker/Navigation-Page/logs
/volume1/docker/Navigation-Page/database
/volume1/docker/Navigation-Page/ext-resources
```
再把项目中的 `config/application.yml` 放到：
```text
/volume1/docker/Navigation-Page/config/application.yml
```
然后在 `/volume1/docker/Navigation-Page/compose.yaml` 中写入下面内容：
```yaml
services:
  Navigation-Page:
    image: weidaye1122/navigation-page:latest # Docker Hub 镜像，可按需改成指定版本标签
    container_name: Navigation-Page # 容器名称
    restart: unless-stopped # 开机自动重启，除非手动停止
    ports:
      - "8082:8080" # 群晖访问端口:容器内部端口
    environment:
      TZ: Asia/Shanghai # 容器时区
    volumes:
      - /volume1/docker/Navigation-Page/config/application.yml:/config/application.yml:ro # 挂载配置文件，只读
      - /volume1/docker/Navigation-Page/logs:/logs # 挂载日志目录
      - /volume1/docker/Navigation-Page/database:/database # 挂载数据库目录
      - /volume1/docker/Navigation-Page/ext-resources:/ext-resources # 挂载额外资源目录
```
在群晖 Container Manager 中进入“项目”，选择“新增”，项目路径选择 `/volume1/docker/Navigation-Page`，使用上面的 `compose.yaml` 创建并启动。启动后访问 `http://群晖IP:8082`。
