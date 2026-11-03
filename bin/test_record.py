#!/usr/bin/env python3
"""Temporary-fixture tests; never call a real Docker engine or publish an image."""
from contextlib import redirect_stderr, redirect_stdout
import importlib.util
import io
import json
import os
from pathlib import Path
import shlex
import shutil
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch
import urllib.error

SPEC = importlib.util.spec_from_file_location("nav_record", Path(__file__).with_name("record.py"))
record = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(record)


class RecordTests(unittest.TestCase):
    def setUp(self):
        self.temporary = tempfile.TemporaryDirectory(prefix="navigation-record-test-")
        self.addCleanup(self.temporary.cleanup)
        self.root = Path(self.temporary.name)
        self.attempt = self.root / "发布" / "1.0.2" / "尝试-01"
        self.marker = self.root / "command-called"
        self.command = [sys.executable, "-c",
                        "from pathlib import Path; import os; "
                        f"Path({str(self.marker)!r}).write_text(os.environ['NAV_RECORD_DIR']); "
                        "print('fixture output')"]
        self.docker = self.root / "docker"
        self.docker.write_text("#!" + sys.executable + "\nfrom pathlib import Path\n"
                               + f"Path({str(self.marker)!r}).write_text('fake docker only')\n")
        self.docker.chmod(0o755)
        self.tag = record.IMAGE + ":1.0.2"

    def invoke(self, directory=None, command=None, tag=None):
        args = [str(directory or self.attempt)]
        if tag:
            args += ["--fixed-tag", tag]
        with redirect_stdout(io.StringIO()), redirect_stderr(io.StringIO()):
            return record.main(args + ["--"] + (command or self.command))

    def publish_command(self, tag=None):
        return [str(self.docker), "buildx", "build", "--push", "--tag", tag or self.tag,
                "--tag", record.IMAGE + ":latest", "."]

    def snapshot(self, directory):
        return {str(p.relative_to(directory)): p.read_bytes()
                for p in directory.rglob("*") if p.is_file()}

    def test_new_directory_and_existing_grouping_parent(self):
        self.attempt.parent.mkdir(parents=True)
        self.assertEqual(self.invoke(), 0)
        self.assertEqual(self.marker.read_text(), str(self.attempt))
        self.assertIn("fixture output", (self.attempt / "command.log").read_text())
        self.assertEqual(json.loads((self.attempt / "result.json").read_text())["exit_code"], 0)

    def test_repeated_execution_preserves_every_record(self):
        self.assertEqual(self.invoke(), 0)
        before = self.snapshot(self.attempt)
        self.marker.unlink()
        self.assertNotEqual(self.invoke(), 0)
        self.assertFalse(self.marker.exists())
        self.assertEqual(before, self.snapshot(self.attempt))

    def test_existing_empty_directory_is_also_rejected(self):
        self.attempt.mkdir(parents=True)
        self.assertNotEqual(self.invoke(), 0)
        self.assertEqual(list(self.attempt.iterdir()), [])
        self.assertFalse(self.marker.exists())

    def test_existing_file_and_broken_symlink_are_rejected(self):
        for kind in ("file", "symlink"):
            target = self.root / kind
            if kind == "file":
                target.write_text("original")
            else:
                target.symlink_to(self.root / "missing")
            self.assertNotEqual(self.invoke(directory=target), 0)
            self.assertFalse(self.marker.exists())
        self.assertEqual((self.root / "file").read_text(), "original")
        self.assertTrue((self.root / "symlink").is_symlink())

    def test_parent_file_prevents_all_following_work(self):
        parent = self.root / "blocked"
        parent.write_text("original")
        self.assertNotEqual(self.invoke(directory=parent / "attempt"), 0)
        self.assertEqual(parent.read_text(), "original")
        self.assertFalse(self.marker.exists())

    def test_directory_creation_error_prevents_command_and_writes(self):
        with patch.object(record, "create_attempt", side_effect=PermissionError("fixture denied")):
            self.assertNotEqual(self.invoke(), 0)
        self.assertFalse(self.attempt.exists())
        self.assertFalse(self.marker.exists())

    def test_record_open_failure_prevents_command(self):
        original = Path.open

        def opening(path, *args, **kwargs):
            if path.name == "command.log":
                raise PermissionError("fixture denied")
            return original(path, *args, **kwargs)

        with patch.object(Path, "open", opening):
            self.assertNotEqual(self.invoke(), 0)
        self.assertFalse(self.marker.exists())

    def test_interrupted_attempt_keeps_its_directory_and_metadata(self):
        with patch.object(record.subprocess, "run", side_effect=KeyboardInterrupt):
            self.assertEqual(self.invoke(), 130)
        self.assertTrue(json.loads((self.attempt / "result.json").read_text())["interrupted"])
        before = self.snapshot(self.attempt)
        self.assertNotEqual(self.invoke(), 0)
        self.assertEqual(before, self.snapshot(self.attempt))

    def test_failed_command_is_preserved_and_retry_uses_new_subdirectory(self):
        command = [sys.executable, "-c", "print('failed fixture'); raise SystemExit(7)"]
        self.assertEqual(self.invoke(command=command), 7)
        before = self.snapshot(self.attempt)
        self.assertEqual(self.invoke(directory=self.attempt.with_name("尝试-02")), 0)
        self.assertEqual(before, self.snapshot(self.attempt))

    def test_strict_shell_does_not_continue_after_directory_failure(self):
        self.attempt.mkdir(parents=True)
        following = self.root / "following-command"
        args = [sys.executable, str(Path(record.__file__)), str(self.attempt), "--", *self.command]
        script = "set -euo pipefail\n" + shlex.join(args) + "\ntouch " + shlex.quote(str(following))
        result = subprocess.run(["bash", "-c", script], capture_output=True)
        self.assertNotEqual(result.returncode, 0)
        self.assertFalse(self.marker.exists())
        self.assertFalse(following.exists())

    def test_metadata_cannot_target_old_directory_or_tool_records(self):
        old = self.root / "old.json"
        old.write_text("original")
        for destination in [old, self.attempt / "command.log"]:
            command = [str(self.docker), "buildx", "build", "--metadata-file", str(destination), "."]
            self.assertNotEqual(self.invoke(command=command), 0)
        self.assertEqual(old.read_text(), "original")
        self.assertFalse(self.marker.exists())

    def test_publish_requires_checked_and_matching_fixed_tag(self):
        self.assertNotEqual(self.invoke(command=self.publish_command()), 0)
        with patch.object(record, "check_fixed_tag") as remote:
            self.assertNotEqual(self.invoke(command=self.publish_command(record.IMAGE + ":1.0.3"), tag=self.tag), 0)
            remote.assert_not_called()
        self.assertFalse(self.marker.exists())

    def test_existing_remote_tag_blocks_even_new_attempts(self):
        with patch.object(record, "check_fixed_tag", side_effect=ValueError("fixed tag exists")):
            for name in ["尝试-01", "尝试-02"]:
                target = self.attempt.with_name(name)
                self.assertNotEqual(self.invoke(target, self.publish_command(), self.tag), 0)
                self.assertFalse(json.loads((target / "result.json").read_text())["command_started"])
        self.assertFalse(self.marker.exists())

    def test_confirmed_absent_tag_allows_only_fake_publish_command(self):
        with patch.object(record, "check_fixed_tag") as remote:
            self.assertEqual(self.invoke(command=self.publish_command(), tag=self.tag), 0)
            remote.assert_called_once_with(self.tag)
        self.assertEqual(self.marker.read_text(), "fake docker only")

    def test_remote_404_requires_accessible_repository(self):
        repository = json.dumps({"namespace": "weidaye1122", "name": "navigation-page"}).encode()
        missing = urllib.error.HTTPError("https://fixture/tag", 404, "not found", {}, None)
        with patch.object(record.urllib.request, "urlopen", side_effect=[io.BytesIO(repository), missing]):
            record.check_fixed_tag(self.tag)
        with patch.object(record.urllib.request, "urlopen", side_effect=missing):
            with self.assertRaises(urllib.error.HTTPError):
                record.check_fixed_tag(self.tag)

    def test_uncertain_remote_state_blocks_publish(self):
        repository = json.dumps({"namespace": "weidaye1122", "name": "navigation-page"}).encode()
        for code in [401, 403, 429, 500]:
            target = self.attempt.with_name("尝试-http-" + str(code))
            error = urllib.error.HTTPError("https://fixture/tag", code, "fixture", {}, None)
            with patch.object(record.urllib.request, "urlopen", side_effect=[io.BytesIO(repository), error]):
                self.assertNotEqual(self.invoke(target, self.publish_command(), self.tag), 0)
        with patch.object(record.urllib.request, "urlopen", side_effect=urllib.error.URLError("fixture offline")):
            self.assertNotEqual(self.invoke(self.attempt, self.publish_command(), self.tag), 0)
        self.assertFalse(self.marker.exists())

    def test_package_helper_stops_before_maven_when_frontend_fails(self):
        repository = self.root / "fake repository"
        (repository / "bin").mkdir(parents=True)
        (repository / "web").mkdir()
        helper = repository / "bin/package.sh"
        shutil.copyfile(Path(__file__).with_name("package.sh"), helper)
        programs = self.root / "fake-bin"
        programs.mkdir()
        for name, code in [("npm", 7), ("mvn", 0)]:
            program = programs / name
            program.write_text("#!/bin/sh\n" + ("touch " + shlex.quote(str(self.marker)) + "\n" if name == "mvn" else "") + "exit " + str(code) + "\n")
            program.chmod(0o755)
        result = subprocess.run(["bash", str(helper)], cwd=self.root, capture_output=True,
                                env=dict(os.environ, PATH=str(programs) + os.pathsep + os.environ["PATH"]))
        self.assertEqual(result.returncode, 7)
        self.assertFalse(self.marker.exists())
        self.assertNotIn("打包完成", result.stdout.decode())


if __name__ == "__main__":
    unittest.main(verbosity=2)
