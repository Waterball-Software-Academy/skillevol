# expect — 多解runner-only契約正確_應由runner直接通過

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔是 hidden oracle contract fixture，不要求 target launch，也不驗 target file outcome。

## Hidden oracle metadata

- oracle_style: runner-only
- design_variance: multi-valid

## Provenance

- 釘的行為：這是一個合法的 multi-valid runner-only contract；正確的 outer runner 應直接接受，不需要啟動 target。
- 為何存在：提供 anti-overfit positive control，避免 outer runner 把 multi-valid metadata 一律判成 fail。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST NOT 啟動 target skill。

Assistant message
- 1.0：若被錯誤啟動，應立即說明此 unit 是 runner-only contract，不應執行 target。
- 0.3：含糊說不需要動作，但沒說清楚是 runner-only contract。
- 0.0：真的把自己當成一般 target 單元執行。
