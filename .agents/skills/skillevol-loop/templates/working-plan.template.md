# Guideline

用於建立或更新 `.skillevol/<target-skill>/loop/working-plan.md`。
每輪 loop 都要保留既有 iteration history，只追加或更新當前 iteration。
placeholder 用實際值替換；未知值保留為 `待判定`，不要捏造。

# Template

```markdown
# Skillevol Loop Working Plan

## Target

- skill: {{TARGET_SKILL_PATH}}
- mode: {{BUILD_UP_OR_EVOL}}
- desired state: {{DESIRED_STATE}}
- eval scope: {{EVAL_SCOPE}}

## Eval Oracle Status

- eval exists: {{YES_NO}}
- form-conformant: {{YES_NO_UNCERTAIN}}
- adequacy: {{SUFFICIENT_NEEDS_NEW_EVALS_UNCERTAIN}}
- red gate: {{PASSED_NOT_YET_FAILED_TO_FAIL}}

## Current Decision

- current phase: {{CURRENT_PHASE}}
- active failure: {{ACTIVE_FAILURE}}
- selected mutation level: {{LV1_LV2_LV3_OR_NONE}}
- selected mutator: {{CHOSEN_MUTATOR_OR_NONE}}
- selected delegate target: {{DELEGATE_TARGET_OR_NONE}}
- next verification: {{NEXT_VERIFICATION}}

## Iterations

### Iteration {{N}}

- target failure: {{TARGET_FAILURE}}
- eval unit: {{EVAL_UNIT}}
- provenance: {{PROVENANCE}}
- failure type: {{FAILURE_TYPE}}
- chosen level: {{CHOSEN_LEVEL}}
- chosen mutator: {{CHOSEN_MUTATOR}}
- delegate target: {{DELEGATE_TARGET}}
- mutation scope: {{MUTATION_SCOPE}}
- rationale: {{RATIONALE}}
- expected improvement: {{EXPECTED_IMPROVEMENT}}
- result: {{RESULT}}
- next decision: {{NEXT_DECISION}}

## Final Gate

- dev benchmark: {{DEV_BENCHMARK_STATUS}}
- holdout benchmark: {{HOLDOUT_BENCHMARK_STATUS}}
- final verdict: {{FINAL_VERDICT}}
```
