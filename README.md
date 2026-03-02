# Agent Verifier — Sample

> LLM 기반 자동 빌드 검증 및 수정 에이전트 (샘플 버전)

빌드/테스트 실패를 자동으로 감지하고, LLM이 수정안을 생성·적용·검증하는 루프를 반복해 코드를 자동으로 고쳐주는 도구입니다.

> **Note**: 이 저장소는 아키텍처 구조를 소개하는 **샘플 버전**입니다.

---

## 기술 스택

| 분류 | 기술 |
|------|------|
| LLM | Llama-3.3-70b-versatile |
| LLM Inference | Groq (LPU) |
| Agent Framework | LangGraph |
| 모델 연동 | LangChain |
| 데이터 검증 | Pydantic v2 |

---

## 실행 방법

### 1. 환경 설정

```bash
pip install pydantic langgraph langchain-core langchain-groq python-dotenv
```

`.env` 파일에 Groq API 키를 설정합니다:

```
GROQ_API_KEY=your_api_key_here
```

### 2. Task JSON 작성

`tasks/` 폴더 아래에 Task 명세 파일을 작성합니다:

```json
{
    "task_id": "my-task-001",
    "repo_root": "/absolute/path/to/your/project",
    "goal": "빌드 오류 수정",
    "verify": {
        "max_output_chars": 12000,
        "commands": [
            {
                "name": "build",
                "workdir": ".",
                "cmd": ["./gradlew", "build", "-x", "test"],
                "timeout_sec": 900
            }
        ]
    }
}
```

### 3. 실행

```bash
python run_graph.py --task tasks/my_task.json
```

---

## 아키텍처

### 전체 흐름

```
run_graph.py
  ├── load_task_spec()   → Task JSON 파싱 + Pydantic 검증
  └── build_graph()      → LangGraph 실행

LangGraph 노드 흐름:

verifier_node (시작)
  └── route_after_verifier
        ├── OK    → END
        ├── STOP  → END
        └── FIX   → fixer_node

fixer_node
  └── route_after_fixer
        ├── STOP  → END
        └── APPLY → apply_node → verifier_node
```

---

## 모듈 설명

### `loader.py`
Task JSON을 로드하고 Pydantic으로 검증합니다.

---

### `verifier.py` / `verifier_node`
빌드·테스트 명령어를 실행하고 결과를 규격화합니다.

---

### `fixer_node` / `fixer.py`
LLM을 호출해 수정안(`FixPlan`)을 생성합니다.
에러 로그를 분석해 수정이 필요한 파일과 내용을 제안합니다.

---

### `apply_node` / `applier.py`
`FixPlan.edits[].new_content`를 실제 파일에 반영합니다.

---

### `specs.py`
프로젝트 전체의 데이터 스키마를 정의합니다.

- `TaskSpec` / `VerifySpec` / `VerifyCommand` — Task JSON 입력 스펙
- `CommandResult` / `VerifyReport` — 검증 결과 출력 스펙
- `FixEdit` / `FixPlan` — LLM 수정안 스펙
- `GraphState` — LangGraph 노드 간 공용 상태 공간

---

## 디렉토리 구조

```
verifier/
├── app/
│   ├── specs.py        # 전체 데이터 스키마 (Pydantic)
│   ├── loader.py       # Task JSON 로드 및 검증
│   ├── verifier.py     # 빌드/테스트 명령어 실행
│   ├── fixer.py        # LLM 수정안 생성
│   ├── applier.py      # 파일 수정 적용
│   └── graph.py        # LangGraph 워크플로우 정의
├── tasks/              # Task JSON 파일
├── run_graph.py        # 진입점
└── .env                # GROQ_API_KEY (gitignore됨)
```

---

## 출력 예시

```
=== HISTORY DUMP ===
[Attempt 1] Success: False
- failed command: build
- summary: NoticeController.java:85: error: cannot find symbol

[Attempt 2] Success: True

=== APPLY RESULTS ===
[1] src/main/.../NoticeController.java
  applied: True
```
