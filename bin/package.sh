#!/usr/bin/env bash
set -euo pipefail

repo_dir="$(cd "$(dirname "$0")/.." && pwd)"
cd "$repo_dir/web"
npm run build
cd "$repo_dir"
mvn clean compile -DskipTests
cp -R web/dist/. target/classes/static/
mvn package -DskipTests
printf '打包完成\n'
