# oracle contract preflight 規則

本檔規範 `skillevol-run-eval` 在啟動 target subagent 前，如何讀 hidden oracle metadata 並決定 `launch_decision`。
hidden oracle metadata 的正式 schema、合法值、優先序與禁止欄位，一律以 `.agents/skills/skillevol-form-eval/rules/hidden-oracle-metadata.md` 為準；本檔只定義 `skillevol-run-eval` 如何消費該 schema。

## Rule 1 — metadata 來源

- 先讀 outer unit 的 `expect.md` 是否含 `## Hidden oracle metadata`。
- 再從 outer unit 的 `before/` 內找 nested target 的 `expect.md`（路徑形如 `before/.agents/skills/<target-skill>/eval/<phase>/<target-unit>/expect.md`）。
- 若 nested expect 也有 metadata，以 nested expect 作為 `oracle_contract.source`；否則用 outer expect。
- 若兩者皆無 metadata，設定 `launch_decision = launched`，跳過本檔其餘規則，走既有 target launch 流程。
- metadata 只供 parent runner 讀取；不得寫入 target subagent input，也不得留在 sandbox 供 target 讀取。

## Rule 2 — 欄位解析

- 若 unit 宣告 metadata 但缺少 form schema 要求的正式欄位，停止並在 report 標記 missing evidence；本輪不得 pass。
- `preflight_policy`、`target_launch_policy`、`target_artifact_family` 不屬於正式 preflight schema；consumer 不得依賴這些欄位做決策。
- `exact-target-after` 不是合法值；遇到此舊 alias 應視為 fixture 不合規，而不是默默接受成新值。

## Rule 3 — launch 決策表

- `skillevol-run-eval` 必須依 form schema 的決策表執行，不得自行發明另一套 launched / skipped 邏輯。

| design_variance | oracle_style | launch_decision | preflight_check | run-eval verdict |
|---|---|---|---|---|
| multi-valid | exact-after-single-golden | skipped | failed | fail |
| multi-valid | runner-only | skipped | passed | pass |
| unique | * | launched | passed | 依 target judge |
| 未宣告 | * | launched | not-applicable | 依 target judge |

- `multi-valid + exact-after-single-golden` 代表題目多解但 oracle 只接受單一 golden；這是非法 contract，MUST hard-fail，MUST NOT 啟動 target。
- `multi-valid + runner-only` 代表合法 contract；MUST NOT 因 multi-valid 就一律 fail；MUST NOT 啟動 target。

## Rule 4 — skipped launch 的 sandbox 與產物

- 仍 MUST 重置 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。
- MUST NOT 複製 nested target `before/` 到 sandbox（`copied_target_before: no`）。
- MUST 寫 `observation.md` 與 `eval-report.md`；不呼叫 target subagent，不產生 `target_run.subagent_id`。
- observation MUST 含：
  - `oracle_contract.source`
  - `oracle_contract.oracle_style`
  - `oracle_contract.design_variance`
  - `oracle_contract.preflight_check`
  - `oracle_contract.overfit_risk`
  - `target_run.launch_decision: skipped`
  - `target_run.skip_reason`
  - `target_run.subagent_id: none`
  - `target_run.opening_input: none`
  - `target_run.opening_input_shape: not-applicable-no-launch`
- skipped hard-fail 的 `eval-report.md` 頂行 MUST 是 `verdict: fail`；skipped runner-only pass MUST 是 `verdict: pass`。
- skipped unit 可不 delegate `/skillevol-eval-judge`；以 shared + unit expect 的 preflight 判準直接寫 report。

## Rule 5 — launched 路徑不變

- `launch_decision = launched` 時，繼續 sandbox copy、oracle strip、minimal opening input、Task subagent、responder、judge 的既有流程。
- 既有未宣告 metadata 的 unit 完全不受影響。
