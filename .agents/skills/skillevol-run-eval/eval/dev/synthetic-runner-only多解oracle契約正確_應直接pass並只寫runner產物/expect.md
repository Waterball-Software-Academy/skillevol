# expect — synthetic-runner-only多解oracle契約正確_應直接pass並只寫runner產物

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply。此 unit 是 anti-overfit positive control：multi-valid contract 合法，但 oracle 明確是 runner-only，因此 outer `after/` 只應留下 deterministic runner artifacts，且 verdict 應為 pass。

## Hidden oracle metadata

- oracle_style: runner-only
- design_variance: multi-valid

## Provenance

- 釘的行為：當 nested target unit 明確宣告 `design_variance = multi-valid`，且 hidden oracle contract 是 `runner-only` 時，run-eval 必須接受這是合法 contract，直接以 deterministic runner report 判 pass，而不是因為看見 multi-valid 就一律 fail。
- 為何存在：positive control；防止 anti-overfit 實作退化成「multi-valid metadata 一出現就 fail」。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 讀取 nested target unit hidden oracle metadata，並判定 `multi-valid + runner-only` 為合法 contract。
- MUST 寫出 `.skillevol/skillevol-run-eval/run-evals/dev/synthetic-runner-only多解oracle契約正確_應直接pass並只寫runner產物/observation.md`。
- MUST 寫出 `.skillevol/skillevol-run-eval/run-evals/dev/synthetic-runner-only多解oracle契約正確_應直接pass並只寫runner產物/eval-report.md`。
- MUST NOT delegate to target skill `/synthetic-runner-only`。
- MUST NOT 產生 target subagent id。
- MUST NOT 向真人提問。

Assistant message
- 1.0：回報已在 preflight 確認 synthetic nested oracle 為合法的 `multi-valid + runner-only` contract，因此未啟動 target、整體 verdict = pass、報告寫到 `.skillevol/skillevol-run-eval/run-evals/dev/synthetic-runner-only多解oracle契約正確_應直接pass並只寫runner產物/eval-report.md`。
- 0.7：有說 runner-only pass 與 report path，但未明講 target 未啟動或未清楚指出 contract 合法。
- 0.3：只說 pass，沒指出是 runner-only positive control。
- 0.0：把本 unit 判 fail、仍啟動 target，或要求真人介入。
