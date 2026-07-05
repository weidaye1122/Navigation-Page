# 团队导航服务

团队导航服务是一个轻量级的网址导航和团队资源管理系统，后端使用 Spring Boot，前端使用 Vue 2 和 Element UI。项目支持分类导航、卡片管理、通知公告、用户和角色管理、附件上传、静态网站上传、动态卡片和数据备份还原。

默认使用 H2 数据库，无需额外数据库服务即可运行。容器内默认端口为 `8080`，默认管理员账号为 `admin`，默认密码为 `123456`。

## 群晖 Compose 部署

在群晖 File Station 中创建目录：

```text
/volume1/docker/team-nav
/volume1/docker/team-nav/config
/volume1/docker/team-nav/logs
/volume1/docker/team-nav/database
/volume1/docker/team-nav/ext-resources
```

把本项目的 `config/application.yml` 放到：

```text
/volume1/docker/team-nav/config/application.yml
```

在 `/volume1/docker/team-nav/compose.yaml` 写入下面内容。`image` 可以使用 Docker Hub 镜像，也可以换成 GitHub Container Registry 镜像 `ghcr.io/weidaye1122/teamnav2:latest`。

```yaml
services:
  team-nav:
    image: weidaye1122/teamnav2:latest
    container_name: team-nav
    restart: unless-stopped
    ports:
      - "8082:8080"
    environment:
      TZ: Asia/Shanghai
    volumes:
      - /volume1/docker/team-nav/config/application.yml:/config/application.yml:ro
      - /volume1/docker/team-nav/logs:/logs
      - /volume1/docker/team-nav/database:/database
      - /volume1/docker/team-nav/ext-resources:/ext-resources
```

在群晖 Container Manager 中进入“项目”，选择“新增”，项目路径选择 `/volume1/docker/team-nav`，使用上面的 `compose.yaml` 创建并启动。

启动后访问：

```text
http://群晖IP:8082
```

需要修改站点名称、端口、管理员密码等内容时，编辑 `/volume1/docker/team-nav/config/application.yml` 后重启容器。
