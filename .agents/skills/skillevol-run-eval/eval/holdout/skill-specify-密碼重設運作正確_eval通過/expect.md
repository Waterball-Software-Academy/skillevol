# expect — skill-specify-密碼重設運作正確_eval通過

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 after/ imply。

## Provenance

- 釘的行為：run-eval 對 dev 未出現過的 target unit（password-reset-happy）能端到端跑完——先建立 `.skillevol/skillevol-run-eval/run-evals/holdout/skill-specify-密碼重設運作正確_eval通過/` sandbox，只把 password-reset-happy 的 prompt 餵給 target subagent，並如實把 `/skillevol-eval-judge` 的 pass verdict 寫入報告；observation 含完整 sandbox/subagent/oracle-isolation provenance。
- 為何存在：holdout 案；防止 runner 對 dev 的 login fixture、固定 unit 名稱、固定 target-owned workspace 或固定 report 文案過度擬合；同時驗證 holdout 仍遵守 sandbox-first 與 target subagent 黑箱隔離。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 重置 `.skillevol/skillevol-run-eval/run-evals/holdout/skill-specify-密碼重設運作正確_eval通過/`，並把 specify `password-reset-happy` 的 before/ 複製進該 sandbox。
- MUST 用 Task 啟動 subagent 執行 target skill /specify；subagent CWD 必須是 `.skillevol/skillevol-run-eval/run-evals/holdout/skill-specify-密碼重設運作正確_eval通過/`。
- MUST 只把 specify `password-reset-happy` 的 `prompt.md` 內容交給 target subagent；MUST NOT 交給 subagent 任何 expect、after、rubric、Provenance、expected verdict 或測試動機。
- MUST delegate to SKILL /specify（target，由 sandbox subagent 承載）
- MUST delegate to SKILL /skillevol-eval-judge（依 specify unit 的 expect 與 after 判分）
- MUST NOT 在主 agent inline 執行 /specify
- MUST NOT 向真人提問：specify 經 clarify 拋出的提問，由 run-eval 依 unit 的 user.md 自答，不轉交真人

Assistant message
- 1.0：回報已跑完 specify 的 password-reset-happy、outer sandbox path、target 在 subagent 執行（含 subagent_id）、oracle isolation 為 none、判為 pass、報告寫到 `.skillevol/skillevol-run-eval/run-evals/holdout/skill-specify-密碼重設運作正確_eval通過/eval-report.md`
- 0.3：跑了但沒講清楚結果或報告落點
- 0.0：宣稱跑了卻沒寫報告、把 oracle material 交給 target subagent，或把 specify 的 clarify 轉給真人

（檔案終態見 after/：after/ 是完整終態 fs snapshot，包含 before/ 原有 fixture，以及 `.skillevol/skillevol-run-eval/run-evals/holdout/skill-specify-密碼重設運作正確_eval通過/`。該 sandbox 內含 copied target before fixture、target-generated specs、observation.md 與 eval-report.md；before/ 的 specify 包不被改動。）
