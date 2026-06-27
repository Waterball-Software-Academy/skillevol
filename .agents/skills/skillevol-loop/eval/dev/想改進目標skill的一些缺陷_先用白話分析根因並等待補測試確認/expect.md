# expect — 想改進目標skill的一些缺陷_先用白話分析根因並等待補測試確認

繼承 `../../shared/expect.md`。本檔只補「補測試前先白話分析根因並等確認」這個 unit 的特有期望。

## Provenance

- 釘的行為：當使用者想改進目標 skill 的某個缺陷、而現有 eval 抓不到時，`skillevol-loop` 會先用白話分析根因，說清楚現有測試缺什麼、為什麼測不出來，並提出「擴充既有 dev」或「新增 dev」的補測試計畫，等使用者確認後才進 red gate。
- 為何存在：若 loop 沒先把根因翻成白話、也沒在補測試前先等確認，就很容易把錯方向的 failing test 鎖進系統。
- 檔案終態：見同層 `after/`。`after/` 是完整終態 fs snapshot，包含 `before/` 原有 `demo-plan-skill` fixture，外加新增的 `.skillevol/` workspace；`before/` 的 target skill 包不被改動。

## Run

過程只有一個 turn；此 unit 會停在補 failing test 計畫的確認點。

### Turn 1 — 結束方式：ASK(confirm-failing-test-plan) 交還 user

Tool calls
- MUST 寫出 `.skillevol/.gitignore`
- MUST 寫出 `.skillevol/demo-plan-skill/loop/working-plan.md`
- MUST NOT delegate `/skillevol-define-evals`
- MUST NOT delegate `/skillevol-run-eval`
- MUST NOT delegate `/skillevol-run-benchmark`
- MUST NOT delegate 任何 mutator

Assistant message
- 1.0：用白話說清楚「現有測試只會看有沒有產出計畫，沒有看它會不會停下來等確認，所以現在抓不到這個問題」；並明確提議新增一條 dev scenario，而不是硬塞進既有 happy path；最後只 ask `confirm-failing-test-plan`
- 0.7：有講缺口與補法，也有 ask confirm，但還殘留少量內部術語
- 0.3：只說「現有 eval 不足」或「要補 oracle」，沒有把缺口翻成日常語言，也沒講清楚是擴充既有 dev 還是新增新的 dev
- 0.0：直接進 red gate、直接開始修 skill，或把補測試方向說成黑箱結論

breakpoint：ASK(confirm-failing-test-plan) 交還 user；responder 依 user.md 作答
