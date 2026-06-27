# Guideline

用於 `skillevol-loop` 完成或停止時的收尾回報。
若 loop 成功，重點放在 eval 結果與 mutation provenance。
若 loop 停止，重點放在停止原因、缺少的證據、以及應回到哪個上游 skill。
不要把完整 working-plan 複製到回報；只摘要足以讓使用者接續決策的資訊。

# Template

```markdown
## Result

{{FINAL_VERDICT}}

## Eval Evidence

- red gate: {{RED_GATE_SUMMARY}}
- dev benchmark: {{DEV_BENCHMARK_SUMMARY}}
- holdout gate: {{HOLDOUT_GATE_SUMMARY}}

## Mutations

- {{MUTATION_1_SUMMARY}}
- {{MUTATION_2_SUMMARY}}
- {{MUTATION_3_SUMMARY}}

## Mutation Audit

- iteration {{N1}}: chosen mutator = {{MUTATION_1_CHOSEN_MUTATOR}}, delegate target = {{MUTATION_1_DELEGATE_TARGET}}
- iteration {{N2}}: chosen mutator = {{MUTATION_2_CHOSEN_MUTATOR}}, delegate target = {{MUTATION_2_DELEGATE_TARGET}}
- iteration {{N3}}: chosen mutator = {{MUTATION_3_CHOSEN_MUTATOR}}, delegate target = {{MUTATION_3_DELEGATE_TARGET}}

## Remaining Risk

{{REMAINING_RISK_OR_NONE}}

## Next Step

{{NEXT_STEP_OR_DONE}}
```
