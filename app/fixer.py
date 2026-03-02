from __future__ import annotations

import json
import os
import re
from pathlib import Path
from typing import Optional, List

from langchain_groq import ChatGroq
from langchain_core.messages import SystemMessage, HumanMessage

from app.specs import TaskSpec, VerifyReport, FixPlan


def make_fix_plan_llm(
    task: TaskSpec,
    repo_root: Path,
    report: VerifyReport,
    model: str = "llama-3.3-70b-versatile",
    **kwargs,
) -> FixPlan:
    if not os.environ.get("GROQ_API_KEY"):
        return FixPlan(
            task_id=task.task_id,
            diagnosis="GROQ_API_KEY is not set",
            edits=[],
            commands_to_rerun=[],
            confidence=0,
            notes="Set GROQ_API_KEY env var and rerun.",
        )

    # 에러 로그만 간단히 수집
    error_text = ""
    for r in report.results:
        if r.exit_code != 0:
            error_text += f"[{r.name}]\n{r.stderr_tail or r.stdout_tail or ''}\n"

    system_prompt = (
        "You are a build engineer. Analyze the build error and propose a fix.\n"
        "Output MUST be valid JSON only.\n"
        "Required fields: task_id, diagnosis.\n"
        "If you propose file edits, include edits[].path and edits[].new_content (full file content).\n"
        "edits[].path must be relative to repo_root.\n"
        "confidence must be an integer 0-100.\n"
    )

    user_payload = {
        "task_id": task.task_id,
        "goal": task.goal,
        "repo_root": str(repo_root),
        "error_log": error_text,
    }

    llm = ChatGroq(model=model, temperature=0)

    try:
        structured_llm = llm.with_structured_output(FixPlan)
        return structured_llm.invoke([
            SystemMessage(content=system_prompt),
            HumanMessage(content=json.dumps(user_payload, ensure_ascii=False, indent=2)),
        ])
    except Exception:
        resp = llm.invoke([
            SystemMessage(content=system_prompt),
            HumanMessage(content=json.dumps(user_payload, ensure_ascii=False, indent=2)),
        ])
        raw = resp.content if hasattr(resp, "content") else str(resp)
        # JSON 블록 추출
        raw = re.sub(r"^```json\s*", "", raw.strip())
        raw = re.sub(r"^```\s*", "", raw)
        raw = re.sub(r"\s*```$", "", raw)
        start = raw.find("{")
        end = raw.rfind("}")
        if start != -1 and end != -1:
            raw = raw[start:end + 1]
        return FixPlan.model_validate_json(raw)
