# skillevol-cli adoption research

## Goal

盤點目前哪些 skillevol skills 應改用 `skillevol-cli` 來管理 `.skillevol` workspace。

## Direct adopters

### skillevol-define-evals

- Evidence: `SKILL.md` 第 3 步明寫建立或更新 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/define-evals/working-plan.md`。
- Recommended migration: 先呼叫 `skillevol-cli setup` 建立 workspace root 與 `.gitignore`，再由 `skillevol-define-evals` 自己寫 `working-plan.md`。
- Why: 可移除重複的 workspace bootstrap 邏輯，讓 skill 聚焦在 verification point 與 eval artifact。

### skillevol-loop

- Evidence: `SKILL.md` 第 3 步明寫建立 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md`。
- Recommended migration: 與 `skillevol-define-evals` 相同，先呼叫 `skillevol-cli setup`，再由 `skillevol-loop` 維護自己的 loop working plan。
- Why: 這支 skill 只需要保證 workspace root 存在，不需要自己重寫 setup 細節。

## Not a direct adopter now

### skillevol-run-eval

- Evidence: `SKILL.md` 第 5 步只重建 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/` 這個 outer sandbox。
- Decision: 目前不要改用 `skillevol-cli reset`。
- Why: `skillevol-cli reset` 的責任是清空整個 `.skillevol/` 但保留 `.gitignore`；`skillevol-run-eval` 要的是局部 sandbox reset，範圍更窄，直接沿用現有 per-sandbox 行為較正確。

## Future follow-up

### skillevol-run-benchmark

- Evidence: `benchmark-report.md` 路徑目前只在 `skillevol-run-eval/rules/target-skill-resolution.md` 被引用，`skillevol-run-benchmark/SKILL.md` 尚未把 workspace bootstrap 寫明。
- Decision: 先不接 `skillevol-cli`。
- Follow-up: 等 `skillevol-run-benchmark` 明確寫出 `.skillevol/<target-skill>/benchmark-report.md` 的建立流程後，再評估是否先做 `setup` 再寫報告。
