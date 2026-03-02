from __future__ import annotations

from dataclasses import dataclass
from pathlib import Path
from typing import List, Optional

from app.specs import FixPlan


@dataclass
class ApplyItemResult:
    path: str
    applied: bool
    reason: str
    backup_path: Optional[str] = None


def apply_fix_plan(repo_root: Path, plan: FixPlan) -> tuple[List[ApplyItemResult], Optional[str]]:
    """
    FixPlan.edits[].new_content를 실제 파일에 반영합니다.
    """
    repo_root = repo_root.resolve()
    results: List[ApplyItemResult] = []

    edits = plan.edits or []
    if not edits:
        return results, None

    for e in edits:
        rel = (e.path or "").strip()
        if not rel:
            results.append(ApplyItemResult(path="", applied=False, reason="empty path"))
            continue

        if not e.new_content:
            results.append(ApplyItemResult(path=rel, applied=False, reason="missing new_content"))
            continue

        try:
            target = (repo_root / rel).resolve()
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_text(e.new_content, encoding="utf-8")
            results.append(ApplyItemResult(path=rel, applied=True, reason="applied"))
        except Exception as ex:
            results.append(ApplyItemResult(path=rel, applied=False, reason=str(ex)))

    return results, None
