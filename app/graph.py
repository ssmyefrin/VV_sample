from __future__ import annotations

from pathlib import Path

from langgraph.graph import StateGraph, END

from app.specs import GraphState
from app.loader import resolve_repo_root
from app.verifier import run_verify
from app.fixer import make_fix_plan_llm
from app.applier import apply_fix_plan

MAX_ATTEMPTS = 3


def verifier_node(state: GraphState) -> GraphState:
    task = state["task"]
    repo_root = resolve_repo_root(task.repo_root)
    report = run_verify(task, repo_root)

    history = list(state.get("verify_history", []))
    history.append(report)

    return {
        "repo_root": str(repo_root),
        "verify_report": report,
        "verify_history": history,
        "attempt": state.get("attempt", 0) + 1,
        "next_action": None,
        "stop_reason": None,
    }


def route_after_verifier(state: GraphState) -> str:
    report = state.get("verify_report")
    attempt = state.get("attempt", 0)

    if report and report.ok:
        return "ok"
    if attempt >= MAX_ATTEMPTS:
        return "stop"
    return "fix"


def fixer_node(state: GraphState) -> GraphState:
    task = state["task"]
    report = state["verify_report"]
    repo_root = Path(state["repo_root"])

    plan = make_fix_plan_llm(task=task, repo_root=repo_root, report=report)

    if any((e.new_content or "").strip() for e in (plan.edits or [])):
        return {"fix_plan": plan, "next_action": "apply", "stop_reason": None}

    return {
        "fix_plan": plan,
        "next_action": "stop",
        "stop_reason": "FixPlan has no edits with new_content",
    }


def route_after_fixer(state: GraphState) -> str:
    return state.get("next_action") or "stop"


def apply_node(state: GraphState) -> GraphState:
    repo_root = Path(state["repo_root"])
    plan = state["fix_plan"]

    results, diff_path = apply_fix_plan(repo_root, plan)

    return {
        "apply_results": [r.__dict__ for r in results],
        "diff_path": diff_path,
        "did_apply": True,
        "next_action": None,
        "stop_reason": None,
    }


def build_graph():
    g = StateGraph(GraphState)

    g.add_node("verifier", verifier_node)
    g.add_node("fixer", fixer_node)
    g.add_node("apply", apply_node)

    g.set_entry_point("verifier")

    g.add_conditional_edges(
        "verifier",
        route_after_verifier,
        {"ok": END, "fix": "fixer", "stop": END},
    )
    g.add_conditional_edges(
        "fixer",
        route_after_fixer,
        {"apply": "apply", "stop": END},
    )
    g.add_edge("apply", "verifier")

    return g.compile()
