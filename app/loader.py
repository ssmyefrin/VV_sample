from __future__ import annotations

from pathlib import Path
from typing import Any, Dict, List, Optional
from app.specs import TaskSpec


def resolve_repo_root(repo_root: str, base_dir: Optional[Path] = None) -> Path:
    """
    repo_root가 상대경로면 base_dir 기준으로 해석, 절대경로면 그대로.
    base_dir 기본은 현재 작업 디렉토리.
    """
    p = Path(repo_root)
    if p.is_absolute():
        return p
    base = base_dir or Path.cwd()
    return (base / p).resolve()


def apply_overrides(task: TaskSpec, overrides: List[str]) -> TaskSpec:
    """
    dotted path override (간단 버전)
    예: verify.max_output_chars=20000
    """
    data: Dict[str, Any] = task.model_dump()

    for ov in overrides:
        if "=" not in ov:
            raise ValueError(f"override must be key=value, got: {ov}")
        key, raw = ov.split("=", 1)
        key, raw = key.strip(), raw.strip()

        if raw.lower() in ("true", "false"):
            value: Any = raw.lower() == "true"
        else:
            try:
                value = int(raw)
            except ValueError:
                try:
                    value = float(raw)
                except ValueError:
                    value = raw

        cur = data
        parts = key.split(".")
        for p in parts[:-1]:
            if p not in cur or not isinstance(cur[p], dict):
                cur[p] = {}
            cur = cur[p]
        cur[parts[-1]] = value

    return TaskSpec.model_validate(data)


def load_task_spec(path: str, overrides: Optional[List[str]] = None) -> TaskSpec:
    task = TaskSpec.parse_file(path)
    if overrides:
        task = apply_overrides(task, overrides)
    return task
