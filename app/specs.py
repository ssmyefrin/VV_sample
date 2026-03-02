from __future__ import annotations

import json
from pathlib import Path
from typing import List, Optional, TypedDict, Any

from pydantic import BaseModel, Field, ValidationError


class VerifyCommand(BaseModel):
    name: str = Field(..., min_length=1)
    workdir: str = Field(default=".", min_length=1)
    cmd: List[str] = Field(..., min_length=1)
    timeout_sec: int = Field(default=900, ge=1)


class VerifySpec(BaseModel):
    commands: List[VerifyCommand] = Field(..., min_length=1)
    max_output_chars: int = Field(default=12000, ge=200, le=200_000)


class TaskSpec(BaseModel):
    task_id: str = Field(..., min_length=1)
    repo_root: str = Field(..., min_length=1)
    goal: str = Field(..., min_length=1)
    verify: VerifySpec

    @classmethod
    def parse_file(cls, path: str) -> "TaskSpec":
        p = Path(path)
        if not p.exists():
            raise FileNotFoundError(f"Task JSON not found: {path}")
        try:
            data = json.loads(p.read_text(encoding="utf-8"))
            return cls.model_validate(data)
        except json.JSONDecodeError as e:
            raise ValueError(f"Invalid JSON ({path}): {e}") from e
        except ValidationError as e:
            raise ValueError(f"TaskSpec validation failed: {e}") from e


# ----- VerifyReport -----
class CommandResult(BaseModel):
    name: str
    workdir: str
    cmd: List[str]
    resolved_cmd: List[str]
    exit_code: int
    duration_sec: float
    stdout_tail: str = ""
    stderr_tail: str = ""
    error_summary: Optional[str] = None


class VerifyReport(BaseModel):
    task_id: str
    ok: bool
    results: List[CommandResult]


# ----- FixPlan -----
class FixEdit(BaseModel):
    path: str
    change_summary: str = "(auto)"
    new_content: Optional[str] = None


class FixPlan(BaseModel):
    task_id: str
    diagnosis: str
    edits: List[FixEdit] = Field(default_factory=list)
    commands_to_rerun: List[List[str]] = Field(default_factory=list)
    confidence: int = Field(0, ge=0, le=100)
    notes: Optional[str] = None


# ----- LangGraph 공용 상태 -----
class GraphState(TypedDict, total=False):
    task: TaskSpec
    repo_root: str
    verify_report: VerifyReport
    verify_history: List[VerifyReport]
    attempt: int
    fix_plan: FixPlan
    next_action: str
    stop_reason: str
    apply_results: List[dict]
    did_apply: bool
    diff_path: str
