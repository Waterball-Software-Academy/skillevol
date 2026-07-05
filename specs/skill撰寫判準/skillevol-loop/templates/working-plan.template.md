# Guideline

用於建立或更新 `.skillevol/<target-skill>/loop/working-plan.md`。
這份檔案讓後續 agent 不用靠聊天記憶，也能知道目前停在哪裡、正在等誰、下一步要驗證什麼。
每次寫入都保留既有修改紀錄與關卡紀錄，只更新目前狀態，並追加新的觀測、決策或修改紀錄。
placeholder 用實際值替換；未知值保留為 `待判定`，不要捏造。

# Template

```markdown
# Skillevol Loop Working Plan

## 目標

- target skill: {{TARGET_SKILL_PATH}}
- target identity source: {{USER_PROMPT_OR_PATH_OR_WORKING_PLAN}}
- desired state: {{DESIRED_STATE}}
- mode: {{EVAL_BOOTSTRAP_OR_BUILD_UP_OR_EVOL}}
- run scope: {{UNIT_OR_DEV_BENCHMARK_OR_FINAL_GATE}}
- run owner: {{RUN_OWNER}}

## 目前停在哪裡

- current phase: {{CURRENT_PHASE}}
- active gate: {{ACTIVE_GATE}}
- gate status: {{WAITING_FOR_USER_OR_READY_OR_BLOCKED_OR_PASSED}}
- next action owner: {{USER_OR_SKILLEVOL_LOOP_OR_DELEGATED_SKILL}}
- next verification: {{NEXT_VERIFICATION}}
- can change target skill: {{YES_NO}}
- can run full benchmark: {{YES_NO}}

## 已讀取的輸入

- target skill exists: {{YES_NO}}
- target verification set exists: {{YES_NO}}
- target verification set runnable: {{YES_NO_UNCERTAIN}}
- allowed inputs read: {{ALLOWED_INPUTS_READ}}
- forbidden self-test answer material read: {{YES_NO}}
- notes: {{INPUT_NOTES_OR_NONE}}

## 驗證集合狀態

- existing tests cover: {{WHAT_EXISTING_EVAL_COVERS}}
- missing coverage: {{WHAT_EXISTING_EVAL_DOES_NOT_COVER}}
- test plan proposal: {{ADD_DEV_OR_EXTEND_DEV_OR_USE_EXISTING}}
- selected first test: {{RED_GATE_UNIT_OR_NONE}}
- first test result: {{NOT_RUN_OR_FAILED_REASONABLY_OR_FAILED_UNREASONABLY_OR_PASSED_UNEXPECTEDLY}}
- first test report: {{RED_GATE_REPORT_PATH_OR_NONE}}

## 使用者確認

### 補測試方向

- machine gate id: confirm-failing-test-plan
- required: {{YES_NO}}
- status: {{NOT_ASKED_OR_WAITING_OR_CONFIRMED_OR_REJECTED}}
- user-facing summary: {{RCA_AND_TEST_PLAN_SUMMARY}}
- user answer: {{USER_ANSWER_OR_NONE}}

### 是否開始修改

- machine gate id: confirm-enter-mutation
- required: {{YES_NO}}
- status: {{NOT_ASKED_OR_WAITING_OR_CONFIRMED_OR_REJECTED}}
- user-facing summary: {{FIRST_TEST_FAILURE_SUMMARY}}
- user answer: {{USER_ANSWER_OR_NONE}}

## 目前正在處理的失敗

- active test: {{ACTIVE_EVAL_UNIT_OR_NONE}}
- active report: {{ACTIVE_REPORT_PATH_OR_NONE}}
- failure summary: {{FAILURE_SUMMARY_OR_NONE}}
- report points to: {{FAILURE_PROVENANCE_OR_NONE}}
- failure type: {{FAILURE_TYPE_OR_NONE}}
- report evidence sufficient: {{SUFFICIENT_OR_INSUFFICIENT}}

## 本輪修改決策

- selected change level: {{LV1_OR_LV2_OR_LV3_OR_NONE}}
- chosen change skill: {{CHOSEN_MUTATOR_OR_NONE}}
- actual delegated skill: {{ACTUAL_DELEGATE_TARGET_OR_NONE}}
- change scope: {{MUTATION_SCOPE_OR_NONE}}
- expected improvement: {{EXPECTED_IMPROVEMENT_OR_NONE}}
- next verification after change: {{NEXT_VERIFICATION_OR_NONE}}

## 修改紀錄

### Iteration {{N}}

- status: {{PLANNED_OR_DELEGATED_OR_VERIFIED_OR_ESCALATED_OR_REVERTED}}
- target failure: {{TARGET_FAILURE}}
- test: {{EVAL_UNIT}}
- report path: {{REPORT_PATH}}
- report points to: {{PROVENANCE}}
- failure type: {{FAILURE_TYPE}}
- chosen level: {{CHOSEN_LEVEL}}
- chosen change skill: {{CHOSEN_MUTATOR}}
- actual delegated skill: {{ACTUAL_DELEGATE_TARGET}}
- change scope: {{MUTATION_SCOPE}}
- rationale: {{RATIONALE}}
- expected improvement: {{EXPECTED_IMPROVEMENT}}
- files changed: {{FILES_CHANGED}}
- single-test result: {{UNIT_REGRESSION_RESULT}}
- next decision: {{NEXT_DECISION}}

## 整組驗證

- development benchmark status: {{NOT_RUN_OR_PASS_OR_FAIL}}
- development benchmark report: {{DEV_BENCHMARK_REPORT_OR_NONE}}
- hidden tests included: {{YES_NO}}
- final verification status: {{NOT_RUN_OR_PASS_OR_FAIL}}
- final verification report: {{FINAL_GATE_REPORT_OR_NONE}}
- final verdict: {{FINAL_VERDICT_OR_PENDING}}

## 關卡紀錄

### Event {{N}}

- phase: {{PHASE}}
- gate: {{GATE}}
- event: {{ASKED_OR_CONFIRMED_OR_REJECTED_OR_PASSED_OR_FAILED_OR_STOPPED}}
- evidence: {{EVIDENCE_PATH_OR_MESSAGE_SUMMARY}}
- timestamp: {{TIMESTAMP_OR_UNKNOWN}}

## 停止狀態

- stopped: {{YES_NO}}
- stop reason: {{STOP_REASON_OR_NONE}}
- missing evidence: {{MISSING_EVIDENCE_OR_NONE}}
- recommended upstream skill: {{UPSTREAM_SKILL_OR_NONE}}
- resume entrypoint: {{PHASE_AND_STEP_TO_RESUME}}
```
