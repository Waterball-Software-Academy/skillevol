# expect — skill-specify-內容運作正確_eval通過

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 after/ imply。

## Provenance

- 釘的行為：run-eval 對一個互動型 target unit（specify 的 login-happy）能正確端到端跑完——先建立 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/` sandbox，只把 login-happy 的 prompt 餵給 target subagent，依 unit 的 user.md 答 specify 的 clarify，用 skillevol-eval-judge 依 unit 的 expect 判分，寫出 verdict=pass 的報告，且 observation 含完整 sandbox/subagent/oracle-isolation provenance。
- 為何存在：run-eval 的第一個 happy 端到端案；同時實測它的 responder-policy（自答 specify 的 clarify、不轉交真人）、target subagent 隔離、sandbox-first 執行與 oracle 不外洩。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 重置 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/`，並把 specify `login-happy` 的 before/ 複製進該 sandbox。
- MUST 用 Task 啟動 subagent 執行 target skill /specify；subagent CWD 必須是 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/`。
- MUST 只把 specify `login-happy` 的 `prompt.md` 內容交給 target subagent；MUST NOT 交給 subagent 任何 expect、after、rubric、Provenance、expected verdict 或測試動機。
- MUST delegate to SKILL /specify（target，由 sandbox subagent 承載）
- MUST delegate to SKILL /skillevol-eval-judge（依 specify unit 的 expect 與 after 判分）
- MUST NOT 在主 agent inline 執行 /specify
- MUST NOT 向真人提問：specify 經 clarify 拋出的提問，由 run-eval 依 unit 的 user.md 自答（responder-policy），不轉交真人

Assistant message
- 1.0：回報已跑完 specify 的 login-happy、outer sandbox path、target 在 subagent 執行（含 subagent_id）、oracle isolation 為 none、判為 pass、報告寫到 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/eval-report.md`
- 0.3：跑了但沒講清楚結果或報告落點
- 0.0：宣稱跑了卻沒寫報告、把 oracle material 交給 target subagent，或把 specify 的 clarify 轉給真人

（檔案終態見 after/：after/ 是完整終態 fs snapshot，包含 before/ 原有 fixture，以及 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/`。該 sandbox 內含 copied target before fixture、target-generated specs、observation.md 與 eval-report.md；before/ 的 specify 包不被改動。）
