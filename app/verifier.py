from __future__ import annotations

import subprocess
import time
from pathlib import Path
from typing import List, Optional

from app.specs import TaskSpec, VerifyReport, CommandResult


def _tail(text: str, max_chars: int) -> str:
    if not text:
        return ""
    return text if len(text) <= max_chars else text[-max_chars:]


def run_verify(task: TaskSpec, repo_root: Path) -> VerifyReport:
    if not repo_root.exists():
        raise RuntimeError(f"repo_root not found: {repo_root}")
    if not repo_root.is_dir():
        raise RuntimeError(f"repo_root is not a directory: {repo_root}")

    results: List[CommandResult] = []
    ok = True

    for c in task.verify.commands:
        workdir = Path(c.workdir) if Path(c.workdir).is_absolute() else (repo_root / c.workdir).resolve()

        if not workdir.exists() or not workdir.is_dir():
            ok = False
            results.append(CommandResult(
                name=c.name, workdir=c.workdir, cmd=c.cmd, resolved_cmd=[],
                exit_code=2, duration_sec=0.0, stdout_tail="", stderr_tail="",
                error_summary=f"workdir not found: {workdir}",
            ))
            continue

        resolved = list(c.cmd)

        start = time.time()
        try:
            proc = subprocess.run(
                resolved,
                cwd=str(workdir),
                capture_output=True,
                text=True,
                encoding="utf-8",
                errors="replace",
                timeout=c.timeout_sec,
                shell=False,
            )
            duration = time.time() - start

            stdout_tail = _tail(proc.stdout or "", task.verify.max_output_chars)
            stderr_tail = _tail(proc.stderr or "", task.verify.max_output_chars)

            err_summary: Optional[str] = None
            if proc.returncode != 0:
                ok = False
                err_summary = (stderr_tail or stdout_tail)[-500:] or "command failed"

            results.append(CommandResult(
                name=c.name, workdir=c.workdir, cmd=c.cmd, resolved_cmd=resolved,
                exit_code=proc.returncode, duration_sec=round(duration, 3),
                stdout_tail=stdout_tail, stderr_tail=stderr_tail,
                error_summary=err_summary,
            ))

        except subprocess.TimeoutExpired as e:
            duration = time.time() - start
            ok = False
            results.append(CommandResult(
                name=c.name, workdir=c.workdir, cmd=c.cmd, resolved_cmd=resolved,
                exit_code=124, duration_sec=round(duration, 3),
                stdout_tail=_tail(getattr(e, "stdout", "") or "", task.verify.max_output_chars),
                stderr_tail=_tail(getattr(e, "stderr", "") or "", task.verify.max_output_chars),
                error_summary=f"timeout after {c.timeout_sec}s",
            ))

    return VerifyReport(task_id=task.task_id, ok=ok, results=results)
