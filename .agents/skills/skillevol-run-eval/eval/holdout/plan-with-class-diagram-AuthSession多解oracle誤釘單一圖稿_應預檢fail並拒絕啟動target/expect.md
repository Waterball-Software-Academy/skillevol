# expect — plan-with-class-diagram-AuthSession多解oracle誤釘單一圖稿_應預檢fail並拒絕啟動target

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply。此 unit 驗的是 outer runner 的 anti-overfit holdout hard-fail；outer `after/` 只保留 deterministic runner artifacts，不把 target 設計 golden 變成 outer expected artifact。

## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid

## Provenance

- 釘的行為：當 nested `/plan-with-class-diagram` Auth / Session holdout unit 宣告 `design_variance = multi-valid`，卻仍以 `exact-after-single-golden` 把單一類別圖當成唯一合法 target outcome 時，run-eval 必須在 target launch 前 hard-fail。
- 為何存在：這是新的 anti-overfit holdout generalization 案；防止 runner 只記住 MiniWeb fixture 名稱，卻無法在另一個多解架構域上拒絕 single-golden overfit oracle。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 讀取 nested target unit hidden oracle metadata，並判定 `multi-valid + exact-after-single-golden` 為 preflight overfit risk。
- MUST 寫出 `.skillevol/skillevol-run-eval/run-evals/holdout/plan-with-class-diagram-AuthSession多解oracle誤釘單一圖稿_應預檢fail並拒絕啟動target/observation.md`。
- MUST 寫出 `.skillevol/skillevol-run-eval/run-evals/holdout/plan-with-class-diagram-AuthSession多解oracle誤釘單一圖稿_應預檢fail並拒絕啟動target/eval-report.md`。
- MUST NOT delegate to target skill `/plan-with-class-diagram`。
- MUST NOT 產生 target subagent id。
- MUST NOT 向真人提問。

Assistant message
- 1.0：回報已在 preflight 偵測 Auth / Session nested oracle 為 `multi-valid + exact-after-single-golden`，因此未啟動 `/plan-with-class-diagram`、整體 verdict = fail、報告寫到 `.skillevol/skillevol-run-eval/run-evals/holdout/plan-with-class-diagram-AuthSession多解oracle誤釘單一圖稿_應預檢fail並拒絕啟動target/eval-report.md`。
- 0.7：有說 preflight fail 與 report path，但未清楚指出是 multi-valid overfit risk，或未明講 target 未啟動。
- 0.3：只說 fail，沒指出 oracle contract 與 preflight hard-fail 邊界。
- 0.0：仍啟動 target、把問題轉問真人，或把 overfit 只當 warning 不 fail。
