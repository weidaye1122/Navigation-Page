# 本地开发与发布

规则面向自用和少量分享，命令均从代码根目录执行。保留现有 Java / Vue 结构；不设置团队审批、强制 PR 或自动发布。

## 开发与测试

1. 开始前读 README、CHANGELOG、AGENTS，检查 Git 和相关差异。2026-11-03 已恢复原仓库历史；若其他机器只有源码文件，先找回原历史并核对本地文件，不要为消除报错而 `git init`、覆盖文件或另造历史。
2. 修改范围尽量小，日常结果记入 CHANGELOG 的“未发布”。真实配置、数据和测试输出都与可提交源码分开；通用结论写回现有文档，逐次记录按下文的本地资料目录归档。
3. 只做与修改相符的检查，并记录限制。日常结束不暂存、提交、推送或打标签。用户另行要求 Git 检查点时，只执行该次明确要求的操作，不连带发布。

| 修改内容 | 基本检查 |
| --- | --- |
| 说明、目录与忽略规则 | 内容和本地链接、原文件差异；记录保护用 `python3 bin/test_record.py` 做临时目录和假命令验证 |
| Java 后端 | 安装 JDK 8、Maven 3 后运行 `mvn test`，再对改动行为做专用数据验证 |
| Vue 前端 | 在 `web/` 安装依赖后运行 `npm run lint`、`npm run build`，检查相关界面 |
| Dockerfile、依赖或打包 | 按 README 的新尝试目录构建命令执行，核对 JAR 内前端、应用版本和数据排除 |
| 安装、更新、数据保留、恢复或回退 | 额外在本地创建全新容器和数据，验证对应场景；不操作正在使用的实例 |

当前 Java / Vue 没有 `src/test/`、测试依赖或前端 `test` 脚本；`mvn test` / `verify` 会执行现有 Maven 生命周期，但不提供业务自动化功能覆盖。`bin/test_record.py` 只测试运维记录保护，使用临时目录、假 Docker/npm/Maven 与模拟远端，不构建或发布镜像。不要把这些检查写成真实环境验收。小改不必重复完整部署验证。

本机有 Docker / Buildx，整理时未找到可用宿主机 JDK、Maven、Node、npm，完整构建优先使用 Dockerfile。它在本机按顺序完成：Node 16 / npm 9.9.4 按锁文件安装依赖 → Vue 构建 → 将 `dist/` 复制到构建阶段的 `src/main/resources/static/` → Java 8 / Maven `verify` → 将 JAR 和许可证复制到 Java 8 JRE 镜像，不改写宿主机源码。npm 和 Maven 使用本项目专用构建缓存，不挂载宿主机凭据或业务数据。原 `jpack` Linux 分发已移除，正式产物仅 Docker Hub 镜像。

宿主机已有相应工具时可做前端热更新，先按 README 启动使用全新数据的本地后端，然后另开终端：

```sh
bash <<'BASH'
set -euo pipefail
cd web
npm ci --legacy-peer-deps
VUE_APP_PROXY_URL=http://127.0.0.1:18082 npm run dev -- --host 127.0.0.1 --port 18083
BASH
```

访问 <http://127.0.0.1:18083>。这里覆盖已有的 `localhost:1114` 开发代理，不改源文件和现用端口。Node 16 用于兼容当前 Vue CLI 4，npm 固定为 9.9.4，依赖使用 `web/package-lock.json`；`--legacy-peer-deps` 沿用旧发布流程的兼容模式，安装与生成锁文件必须使用同一模式。只有确实调整依赖时才运行 `npm install --legacy-peer-deps` 更新锁文件，日常构建使用 `npm ci --legacy-peer-deps`。不顺手迁移前端工具链。

独立 `mvn verify` 只构建 Java 及已有 resources，不自动运行 Vue；完整镜像统一走 Dockerfile，避免打出缺少首页的 JAR。已有 `web/start.sh`、宿主机 `web/dist/`、`target/`、数据和日志输出路径保留，不因规范迁移。

恢复原历史时保留了 `LICENSE`、原图标和内置 `/docs/` 帮助资源，`bin/package.sh` 也作为原有 JAR 辅助脚本保留；它不负责正式镜像发布。内置历史版本记录只作参考，当前版本和维护命令以根 README、CHANGELOG 和本文件为准。

## 本地资料目录

外层保持“代码目录 + 本地资料 + 项目说明.md”，本机资料不随源码 Git 同步。保存约定为：

```text
../本地资料/
├── README.md
├── 讨论/
├── 验证/
│   ├── 日期-任务/       本次记录、日志和相关截图
│   │   └── 尝试-…/      每次执行前必须全新创建
│   └── 发布/版本号/
│       └── 尝试-…/      构建、发布的日志和元数据
├── 备份/
└── 镜像归档/            实际产生离线镜像文件时再创建
```

任务目录使用 `YYYY-MM-DD-任务`，每次执行在其中新建尝试子目录，截图随该次尝试保存，不单独预留 `截图/`。构建、发布日志和元数据在 `验证/发布/版本号/` 中按尝试子目录保存；失败和未发布的构建也归入对应源码版本，目录存在不代表已发布。日期、版本父目录和 README 索引可以沿用；每次写记录的目标尝试目录必须是全新创建的。

`镜像归档/` 只在实际产生离线镜像文件时创建，不能用来存构建、发布日志和元数据。已有平铺记录、指纹、差异和检查快照原位保留，不为重试清空、删除或移动它们；历史命令和脚本只作查阅，不直接复跑。已有代码目录、开发环境、运行数据、`target/`、`web/dist/` 等构建输出和部署位置继续沿用，不为归档规则迁移。

## 记录目录与失败停止

需要保存记录的操作统一由 `python3 bin/record.py 新尝试目录 -- 命令…` 启动。工具只允许归档父目录已有，目标尝试目录以一次排他的 `mkdir` 原子创建；已存在的空目录、文件、符号链接，以及创建失败都直接返回失败，**不会写旧记录或运行命令**。不要先用 `mkdir -p` 创建目标尝试目录。

工具先排他创建 `command.json`、`command.log` 和 `result.json`，成功后才运行命令，并用 `NAV_RECORD_DIR` 把本次绝对路径传给子命令。记录文件打不开就停止，子命令失败或中断返回非零状态。Docker 的 `--metadata-file` / `--iidfile` 必须位于本次新目录，且不能占用上述工具文件名。工具不会清理失败目录或自动换名继续。

多条有依赖的命令放在独立 `bash` 的 `set -euo pipefail` 块中，不能只打印错误后继续。任一步失败，停止本轮后续写入、构建和发布；失败尝试及其已有日志、元数据完整保留。确需重试时，沿用日期/版本父目录，改用新的 `尝试-时间-序号` 子目录（例如从 `…-01` 改为 `…-02`），不能通过清空、删除或移动旧记录释放原名称；失败尝试即使为空也保留。

README 给出本地构建与试用的完整命令。记录工具只管理归档目录；现有编译缓存和构建输出位置不变。旧本地验证脚本若确需复用，先检查其入口同样要求新的任务尝试目录和记录尝试目录，目录保留失败时必须在清理/最终写回逻辑之前退出。

## 文件与打包边界

- `config/application.example.yml` 是样例；已有 `config/application.yml` 原位保留并忽略。实际部署复制样例到外部挂载位置，真实账号、令牌、数据库密码通过外置配置或运行环境提供。
- `src/main/resources/application*.yml` 是随程序打包的公开默认配置及数据库示例，保留既有默认行为；不能直接写入真实连接信息。前端 `.env.development`、`.env.production` 是公开构建设置，任何 `VUE_APP_*` 值都可能被浏览器读取。
- `.gitignore` 排除本机配置、运行数据、备份、日志、缓存和测试产物；`.dockerignore` 只允许 `pom.xml`、`src/`、`web/` 等必要输入，并再次排除其中的本机覆盖、凭据和产物。最终镜像只从构建阶段复制 JAR，外置 `config/`、运行目录、外层资料和旧 JAR 均不进入上下文。
- `web/public/` 和 `src/main/resources/` 的可发布资源会进入 JAR，不能把账号数据、真实导出、备份或私人截图放在里面。不要提交秘密后再依赖 `.dockerignore` 补救。
- Git 忽略规则不影响已跟踪文件。恢复原 Git 历史后须重新检查 `git ls-files` 和待发布差异，确认没有已跟踪的真实配置与数据；不能仅凭新增忽略规则宣布历史已清理。

## 正式发布

仅在用户明确要求正式发布时执行。GitHub 保存源码与 Git 标签；程序只上传 Docker Hub，不创建 GitHub Releases 附件、GHCR 镜像或远程构建工作流。

1. **核对来源与已有版本。** 原仓库为 `https://github.com/weidaye1122/Navigation-Page`。本机已恢复原历史；其他机器未带 `.git` 时，先在独立目录找回原仓库，与本地源码逐项审查后衔接。保护未提交工作，不强推、不新建空历史冒充原仓库。每次发布仍重新核对实际 remote、Git 标签、CHANGELOG、`pom.xml`、`web/package.json` 和 Docker Hub 标签，不能把旧观察结果当作永远最新。
2. **统一版本和范围。** 由 AI 根据实际已有版本递增，通常采用修订号，用户指定时按要求处理。同步 Maven 项目版本、前端版本、依赖锁文件和首页版本回退值，将“未发布”整理为正式日志；不要仅为文档整理升版本。应用版本来自 Maven 过滤后的 `@project.version@`，Docker 镜像参数 `VERSION` 和固定标签须与它一致。
3. **先在本地验证。** 审查本次版本全部改动，使用新尝试目录完成相关基本检查、本地镜像构建及产物核对。运行或数据行为变更再用新建测试数据做对应隔离验证；新增架构、基础镜像或安装方式变更要验证受影响平台，仿真启动不能代替真实硬件验收。确认本机 Docker 引擎和 builder 的 endpoint，不把当前选中项当作一定在本机。任一目录或检查失败即停止后续流程，已完成的步骤保留原记录。
4. **集中保存源码。** 仅暂存已核对的发布范围，保留其他未完成内容，提交源码并推送 GitHub。沿用 `vX.Y.Z` 代码标签，对应 Docker Hub 的 `X.Y.Z`。旧标签触发的 Docker Hub / GHCR 工作流已移除，推送源码和版本标签不会走这条旧流程；不新增 GitHub Release 或自动发布。
5. **一次本地构建上传。** 使用本机 Docker / Buildx 和既有 Docker Hub 登录；不要把登录配置、密码或令牌写入命令、日志或 Git。上传必须使用记录工具的 `--fixed-tag`：先确认仓库可访问，再检查实际要推送的固定标签。只有标签明确返回 404 才允许；已存在或鉴权、限流、网络/解析异常、状态不确定均停止。检查的固定标签必须与命令中的固定标签一致，另一个标签只能是本项目 `latest`。

下面整个代码块作为一次操作执行，变量从项目版本和 Git 读取。不要单独摘出 `--push` 命令绕过目录和远端检查。当前已发布的 `1.0.1` 会被拒绝；正式发布时应先按前述步骤确认新的版本并提交源码。

```sh
bash <<'BASH'
set -euo pipefail
release_version="$(python3 -c 'import xml.etree.ElementTree as E; print(E.parse("pom.xml").findtext("{http://maven.apache.org/POM/4.0.0}version"))')"
release_commit="$(git rev-parse HEAD)"
git_state="$(git status --porcelain)"
[ -z "$git_state" ]
attempt_dir="../本地资料/验证/发布/$release_version/尝试-$(date +%Y%m%d-%H%M%S)-上传-01"
python3 bin/record.py "$attempt_dir" \
  --fixed-tag "weidaye1122/navigation-page:$release_version" -- \
  docker buildx build \
  --platform linux/amd64,linux/arm64 \
  --build-arg VERSION="$release_version" \
  --build-arg REVISION="$release_commit" \
  --tag "weidaye1122/navigation-page:$release_version" \
  --tag weidaye1122/navigation-page:latest \
  --metadata-file "$attempt_dir/build.json" \
  --push .

# 上传成功后只读核对；各核对命令也先新建自己的记录子目录。
python3 bin/record.py "$attempt_dir/核对-固定标签" -- \
  docker buildx imagetools inspect "weidaye1122/navigation-page:$release_version"
python3 bin/record.py "$attempt_dir/核对-latest" -- \
  docker buildx imagetools inspect weidaye1122/navigation-page:latest
BASH
```

两种架构沿用本项目 Docker Hub 既有声明，两个标签来自同一次构建。更换本地尝试目录并不授权覆盖远端固定标签；上传失败或中断后，先只读核对远端状态，标签已存在或结果不确定时不能盲目重推、覆盖或另行晋级 `latest`。若仍有无关源码，先在独立发布工作区检出刚推送的提交再构建，不用脏工作区冒充已标记提交。远端检查不是并发发布锁，仍应避免同时发布同一版本。

6. **上传后只核对结果。** 查看两个标签的摘要、`linux/amd64` / `linux/arm64` 清单、版本和源码标签及推送结果，日志和元数据保留在本次新尝试目录，截图随对应日期任务的尝试保存。不追加第二轮功能验收或 `latest` 晋级，不删除旧固定版本。发布与更新现用程序分开，更新前按 README 核对数据、备份、恢复与回退版本。

现有应用的更新提示仍由 `VersionCheckTask` 读取 GitHub Releases 页面。仅推送 Git 标签和 Docker Hub 镜像不会更新该提示；本次保留它的行为，不为提示另建 GitHub Release。可用版本以 Docker Hub 固定标签及 CHANGELOG 为准，若以后要改更新提示来源，再作为功能任务处理。

## 收尾与清理

验证记录写清日期、目标与资源身份、专用数据位置、实际结果和未验证项，均按上文使用新尝试子目录。不单独预留 `截图/`。备份和日志可能含账号或连接信息，只保存在本地，不直接附到公共仓库或镜像。

仅清理本次明确创建且已用完的临时资源，先核对精确 ID、路径和挂载。失败/中断尝试及其记录不作为“多余空目录”清理，不通过清空、删除或移动它们重用原路径。完整备份、关键证据、唯一副本、未提交源码、在用和可回退版本保留。共享缓存和归属不清的资源保留，不做整机 `prune`、跨项目清理或 `compose down -v`；数据库、配置和卷从不当缓存。实际产生离线镜像文件时才创建镜像归档目录，不用它存日志或元数据。
