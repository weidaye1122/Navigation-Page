# Navigation Page

自用、也可分享给少量朋友的导航与资源管理页面。基于 Spring Boot 2.7、Java 8 和 Vue 2，支持分类、卡片、用户角色、附件、静态网站托管、动态卡片和数据备份恢复。

开发和测试在本地完成，程序仅通过 [Docker Hub](https://hub.docker.com/r/weidaye1122/navigation-page) 分发。开发约定见 [AGENTS.md](AGENTS.md)，测试与正式发布步骤见 [维护说明](docs/maintenance.md)，版本变化见 [CHANGELOG.md](CHANGELOG.md)。

本机资料放在代码目录旁的 `../本地资料/`：记录、日志和截图随任务放入 `验证/日期-任务/尝试-…/`，不单独预留 `截图/`；构建、发布日志和元数据放入 `验证/发布/版本号/尝试-…/`。每次执行由 [记录工具](bin/record.py) 原子创建全新尝试目录；目录已存在（包括空目录）或创建/写入失败就停止，不继续构建或发布。重试换新的尝试子目录，不能清空、删除或移动旧记录来复用路径。实际产生离线镜像文件时才创建 `镜像归档/`，具体见 [记录目录与失败停止](docs/maintenance.md#记录目录与失败停止)。

## 本地构建与试用

从代码根目录执行，需要 Docker、Buildx 和 Python 3（记录工具仅用标准库）。Dockerfile 在本机容器内构建前端，再将其打入 JAR；宿主机无需预装 Node、Java 或 Maven。下面整个代码块作为一次操作执行，任一步失败即停止：

```sh
bash <<'BASH'
set -euo pipefail
build_version="$(python3 -c 'import xml.etree.ElementTree as E; print(E.parse("pom.xml").findtext("{http://maven.apache.org/POM/4.0.0}version"))')"
attempt_dir="../本地资料/验证/发布/$build_version/尝试-$(date +%Y%m%d-%H%M%S)-本地构建-01"
python3 bin/record.py "$attempt_dir" -- \
  docker buildx build --load -t navigation-page:dev \
  --metadata-file "$attempt_dir/build.json" .
BASH
```

下面只创建全新试用数据，访问 <http://127.0.0.1:18082>。先确认该本地端口空闲；首次打开按页面提示初始化管理员。`dev` 是本地标签，不上传。

```sh
bash <<'BASH'
set -euo pipefail
attempt_dir="../本地资料/验证/$(date +%Y-%m-%d)-试用/尝试-$(date +%H%M%S)-01"
python3 bin/record.py "$attempt_dir" -- bash -euo pipefail -c '
  test_root="$NAV_RECORD_DIR/专用数据"
  mkdir "$test_root"
  mkdir "$test_root/database" "$test_root/ext-resources" "$test_root/logs"
  docker run --rm --name "navigation-page-local-$(date +%s)-$$" \
    -p 127.0.0.1:18082:8080 \
    --mount "type=bind,source=$test_root/database,target=/database" \
    --mount "type=bind,source=$test_root/ext-resources,target=/ext-resources" \
    --mount "type=bind,source=$test_root/logs,target=/logs" \
    navigation-page:dev
'
BASH
```

按 Ctrl+C 停止试用；容器自动移除，本次日志、结果和专用数据保留，记录位置由工具输出。重试使用新的尝试子目录，不删除旧目录来复用名称。前端热更新及基本检查见 [开发与测试](docs/maintenance.md#开发与测试)。

## 群晖 Compose 使用

以下沿用本项目已有路径、容器名和 `8082:8080` 映射，是使用示例，不代表当前已部署。先创建目录：

```text
/volume1/docker/Navigation-Page
/volume1/docker/Navigation-Page/config
/volume1/docker/Navigation-Page/logs
/volume1/docker/Navigation-Page/database
/volume1/docker/Navigation-Page/ext-resources
```

把 [配置样例](config/application.example.yml) 复制到下列位置，再按需修改。真实配置留在部署目录，仓库中的样例不填写真实密码或账号。

```text
/volume1/docker/Navigation-Page/config/application.yml
```

在 `/volume1/docker/Navigation-Page/compose.yaml` 中写入：

```yaml
services:
  Navigation-Page:
    image: weidaye1122/navigation-page:latest # 需要可回退更新时，改用已发布的固定版本
    container_name: Navigation-Page
    restart: unless-stopped
    ports:
      - "8082:8080"
    environment:
      TZ: Asia/Shanghai
    volumes:
      - /volume1/docker/Navigation-Page/config/application.yml:/config/application.yml:ro
      - /volume1/docker/Navigation-Page/logs:/logs
      - /volume1/docker/Navigation-Page/database:/database
      - /volume1/docker/Navigation-Page/ext-resources:/ext-resources
```

在群晖 Container Manager 中新增项目，路径选择 `/volume1/docker/Navigation-Page`。启动后访问 `http://群晖IP:8082`，空数据库首次访问会进入管理员初始化。

## 数据、更新与备份恢复

| 内容 | Docker 内位置 | 说明 |
| --- | --- | --- |
| 外置配置 | `/config/application.yml` | 由原部署挂载；不会打入新镜像 |
| 默认 H2 数据库 | `/database/` | `data` 数据库及其辅助文件，包含用户、配置和导航数据 |
| 文件资源 | `/ext-resources/` | 附件、图片、托管模块及备份中间文件 |
| 日志 | `/logs/` | 排查问题用，可能包含私人信息 |
| 上传临时文件 | `/temp/` | 容器工作目录中的临时文件，保留原有行为 |

原生 Java 运行时，这些相对路径按**进程启动目录**解析，并非固定按 JAR 所在目录。已有数据、脚本输出和开发环境继续留在原位。使用外部 MySQL / PostgreSQL 时，数据在对应数据库中，不能只备份 `/database/`。内置数据库配置是开发默认值，实际连接信息通过外置配置或运行环境设置。

更新现用程序是单独操作：先核对原 Compose 项目、挂载、数据库类型及旧固定镜像，再备份配置、数据库和完整 `ext-resources/`；在原部署目录拉取指定固定版本并重建原服务，保留原映射和挂载。发布镜像不会自动更新已有程序，本次整理不执行这些操作。

完整恢复或回退使用**同一时点的配置、数据库和文件资源备份**：H2 应先停写、停止原程序后复制整个数据库目录；外部数据库用其原生一致性备份。先在全新本地目录验证恢复，用备份对应的旧固定镜像启动；确认后才安排现用实例的恢复。不让旧版本直接写入被新版本升级过的数据库。

管理页面的“数据备份”会导出数据库 JSON 及 `attachments/`、`images/`、`modules/` 为 ZIP，不包含外置配置；运行目录还会生成 `ext-resources.zip` 和 `ext-resources/data-backup.json`，均按私人数据保管。“还原备份”会解压文件，并仅插入数据库中尚不存在的 ID，已有记录会跳过，文件可能覆盖。因此页面导出用于数据迁移或补充恢复，不能替代更新前的完整备份；恢复试验只对专门创建的空数据执行。
