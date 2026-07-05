# Guideline

用於 `skillevol-loop` 完成、停止、或交還使用者確認時的收尾回報。
不要複製整份 working-plan；只摘要足以讓使用者判斷目前停在哪裡、有哪些測試證據、改了什麼、還缺什麼。
若 loop 成功，重點放在測試結果、修改紀錄與剩餘風險。
若 loop 停止，重點放在停止原因、缺少的證據，以及應回到哪個上游 skill。

# Template

```markdown
## Result

{{FINAL_VERDICT_OR_STOP_STATE}}

## Current State

- phase: {{CURRENT_PHASE}}
- waiting for: {{ACTIVE_GATE_OR_NONE}}
- status: {{GATE_STATUS}}
- next verification: {{NEXT_VERIFICATION}}

## Test Evidence

- verification set: {{EVAL_ORACLE_STATUS}}
- gap analysis: {{RCA_SUMMARY_OR_NONE}}
- first failing test: {{RED_GATE_SUMMARY_OR_NONE}}
- first failing test report: {{RED_GATE_REPORT_PATH_OR_NONE}}
- development benchmark: {{DEV_BENCHMARK_SUMMARY_OR_NONE}}
- development benchmark report: {{DEV_BENCHMARK_REPORT_PATH_OR_NONE}}
- final verification: {{HOLDOUT_GATE_SUMMARY_OR_NONE}}
- final verification report: {{FINAL_GATE_REPORT_PATH_OR_NONE}}

## Changes

- iteration {{N1}}: {{MUTATION_1_SUMMARY}}
- iteration {{N2}}: {{MUTATION_2_SUMMARY}}
- iteration {{N3}}: {{MUTATION_3_SUMMARY}}

## Change Audit

- iteration {{N1}}: chosen level = {{MUTATION_1_LEVEL}}, chosen change skill = {{MUTATION_1_CHOSEN_MUTATOR}}, actual delegated skill = {{MUTATION_1_ACTUAL_DELEGATE_TARGET}}, scope = {{MUTATION_1_SCOPE}}
- iteration {{N2}}: chosen level = {{MUTATION_2_LEVEL}}, chosen change skill = {{MUTATION_2_CHOSEN_MUTATOR}}, actual delegated skill = {{MUTATION_2_ACTUAL_DELEGATE_TARGET}}, scope = {{MUTATION_2_SCOPE}}
- iteration {{N3}}: chosen level = {{MUTATION_3_LEVEL}}, chosen change skill = {{MUTATION_3_CHOSEN_MUTATOR}}, actual delegated skill = {{MUTATION_3_ACTUAL_DELEGATE_TARGET}}, scope = {{MUTATION_3_SCOPE}}

## Files Changed

- {{FILE_1}}: {{CHANGE_SUMMARY_1}}
- {{FILE_2}}: {{CHANGE_SUMMARY_2}}
- {{FILE_3}}: {{CHANGE_SUMMARY_3}}

## Stop Or Risk

- stopped: {{YES_NO}}
- stop reason: {{STOP_REASON_OR_NONE}}
- missing evidence: {{MISSING_EVIDENCE_OR_NONE}}
- remaining risk: {{REMAINING_RISK_OR_NONE}}
- recommended upstream skill: {{UPSTREAM_SKILL_OR_NONE}}

## Next Step

{{NEXT_STEP_OR_DONE}}
```
