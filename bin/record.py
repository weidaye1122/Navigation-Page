#!/usr/bin/env python3
"""Run one command in a newly reserved record directory, without reusing history."""
import argparse
from datetime import datetime, timezone
import json
import os
from pathlib import Path
import re
import subprocess
import sys
import urllib.error
import urllib.request

IMAGE = "weidaye1122/navigation-page"
RESERVED = {"command.json", "command.log", "result.json"}


def create_attempt(directory):
    # Only the grouping parent may already exist. Do not resolve a target symlink.
    directory = Path(os.path.abspath(directory))
    directory.parent.mkdir(parents=True, exist_ok=True)
    directory.mkdir(mode=0o700, exist_ok=False)
    return directory


def option_values(command, names):
    values = []
    for index, value in enumerate(command):
        if value in names:
            if index + 1 == len(command):
                raise ValueError("缺少参数值: " + value)
            values.append(command[index + 1])
        else:
            for name in names:
                if value.startswith(name + "="):
                    values.append(value[len(name) + 1:])
    return values


def validate_command(command, directory, fixed_tag):
    if not command:
        raise ValueError("必须提供 -- 后的命令")
    if fixed_tag and not re.fullmatch(re.escape(IMAGE) + r":\d+\.\d+\.\d+", fixed_tag):
        raise ValueError("固定标签必须为 " + IMAGE + ":X.Y.Z")
    is_docker = Path(command[0]).name == "docker"
    publishing = is_docker and any(
        value in {"--push", "push"} or "type=registry" in value or "push=true" in value
        for value in command[1:])
    if publishing and not fixed_tag:
        raise ValueError("镜像发布必须提供 --fixed-tag，先核对远端固定标签")
    if fixed_tag:
        tags = option_values(command, {"-t", "--tag"})
        if (not is_docker or command[1:3] != ["buildx", "build"]
                or "--push" not in command or len(tags) != 2
                or set(tags) != {fixed_tag, IMAGE + ":latest"}):
            raise ValueError("仅支持同一次 buildx --push 同时推送对应固定标签和 latest")
    if is_docker:
        for value in option_values(command, {"--metadata-file", "--iidfile"}):
            target = Path(os.path.abspath(value))
            if target.parent != directory or target.name in RESERVED:
                raise ValueError("Docker 元数据必须写入本次新目录，且不能占用记录工具文件名")


def check_fixed_tag(fixed_tag):
    """Only a confirmed tag 404 in an accessible project repository allows a push."""
    base = "https://hub.docker.com/v2/repositories/" + IMAGE + "/"
    with urllib.request.urlopen(base, timeout=30) as response:
        repository = json.load(response)
    if (not isinstance(repository, dict)
            or repository.get("namespace") != "weidaye1122"
            or repository.get("name") != "navigation-page"):
        raise ValueError("无法确认目标 Docker Hub 仓库，停止发布")
    version = fixed_tag.rsplit(":", 1)[1]
    try:
        with urllib.request.urlopen(base + "tags/" + version + "/", timeout=30):
            pass
    except urllib.error.HTTPError as error:
        if error.code == 404:
            return
        raise
    raise ValueError("远端固定标签已存在，禁止覆盖: " + fixed_tag)


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("directory", help="必须尚不存在的本次尝试目录")
    parser.add_argument("--fixed-tag", help="发布前必须检查不存在的 Docker Hub 固定标签")
    arguments = list(sys.argv[1:] if argv is None else argv)
    if "--" not in arguments:
        parser.error("目录和选项后必须用 -- 分隔实际命令")
    separator = arguments.index("--")
    options = parser.parse_args(arguments[:separator])
    command = arguments[separator + 1:]
    directory = Path(os.path.abspath(options.directory))
    try:
        validate_command(command, directory, options.fixed_tag)
        directory = create_attempt(directory)
    except (OSError, ValueError) as error:
        print("停止：记录目录未安全创建或命令无效；未写入记录、未执行命令。" + str(error), file=sys.stderr)
        return 1

    # Exclusive files are opened before any build/push. A write/open error never
    # falls through to command execution, and failed directories are left intact.
    try:
        with (directory / "command.json").open("x") as metadata:
            json.dump({"started_at": datetime.now(timezone.utc).isoformat(),
                       "command": command, "fixed_tag": options.fixed_tag},
                      metadata, ensure_ascii=False, indent=2)
            metadata.write("\n")
        with (directory / "command.log").open("xb") as log, \
                (directory / "result.json").open("x") as result:
            phase = "preflight"
            try:
                if options.fixed_tag:
                    check_fixed_tag(options.fixed_tag)
                phase = "command"
                environment = dict(os.environ, NAV_RECORD_DIR=str(directory))
                code = subprocess.run(command, stdout=log, stderr=subprocess.STDOUT,
                                      env=environment).returncode
                code = code if code >= 0 else 128 - code
                outcome = {"exit_code": code, "command_started": True}
            except (OSError, ValueError) as error:
                code = 1
                message = "停止：" + str(error)
                log.write((message + "\n").encode())
                print(message, file=sys.stderr)
                outcome = {"exit_code": code, "command_started": False,
                           "phase": phase, "error": str(error)}
            except KeyboardInterrupt:
                code = 130
                log.write(b"Interrupted; this attempt remains reserved.\n")
                outcome = {"exit_code": code, "command_started": phase == "command",
                           "phase": phase, "interrupted": True}
            outcome["finished_at"] = datetime.now(timezone.utc).isoformat()
            json.dump(outcome, result, ensure_ascii=False, indent=2)
            result.write("\n")
    except (OSError, ValueError) as error:
        print("停止：记录写入失败；不继续执行后续步骤。" + str(error), file=sys.stderr)
        return 1
    print("记录目录：" + str(directory))
    return code


if __name__ == "__main__":
    raise SystemExit(main())
