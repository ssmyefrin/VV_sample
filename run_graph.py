from __future__ import annotations

import argparse
import sys
from dotenv import load_dotenv

load_dotenv()

from app.loader import load_task_spec
from app.graph import build_graph

def _print_history_dump(final_state: dict) -> None:
    print("\n=== HISTORY DUMP ===")
    history = final_state.get("verify_history", [])

    for i, report in enumerate(history, start=1):
        print(f"\n[Attempt {i}] Success: {report.ok}")
        if not report.ok:
            # 첫 result만 보지 말고, 요약만 뽑는 게 더 안전함(여러 커맨드일 수 있음)
            for r in report.results:
                if r.exit_code != 0:
                    msg = r.error_summary or r.stderr_tail or r.stdout_tail
                    print(f"- failed command: {r.name}")
                    print(f"- summary:\n{msg}")
                    break


def _print_debug_state(final_state: dict) -> None:
    next_action = final_state.get("next_action")
    stop_reason = final_state.get("stop_reason")
    if next_action or stop_reason:
        print("\n=== FLOW DEBUG ===")
        if next_action:
            print(f"- next_action: {next_action}")
        if stop_reason:
            print(f"- stop_reason: {stop_reason}")

    results = final_state.get("apply_results")
    if results:
        print("\n=== APPLY RESULTS ===")
        for i, r in enumerate(results, start=1):
            print(f"[{i}] {r.get('path')}")
            print(f"  applied: {r.get('applied')}")
            print(f"  reason : {r.get('reason')}")


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--task", required=True)
    ap.add_argument("--override", action="append", default=None, help="key=value (repeatable)")
    args = ap.parse_args()

    task = load_task_spec(args.task, overrides=args.override)

    app = build_graph()
    init_state = {"task": task, "attempt": 0, "verify_history": []}
    final_state = app.invoke(init_state)

    # 여기: final_state 받은 직후에 “관측성 출력” 몰아서
    _print_history_dump(final_state)
    _print_debug_state(final_state)

    report = final_state.get("verify_report")
    if report is None:
        print("verify_report missing in final_state", file=sys.stderr)
        raise SystemExit(2)

    print("\n=== VERIFY REPORT ===")
    print(report.model_dump_json(indent=2))

    if not report.ok:
        plan = final_state.get("fix_plan")
        if plan is not None:
            print("\n=== FIX PLAN ===")
            print(plan.model_dump_json(indent=2, exclude_none=True))
        raise SystemExit(1)

    raise SystemExit(0)


if __name__ == "__main__":
    main()
