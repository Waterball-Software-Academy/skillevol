# expect — skill-specify-內容故意留下瑕疵_eval不通過

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 after/ imply。

## Provenance

- 釘的行為：當 target skill 面對自己的 unit 明確 fail 時，run-eval 仍須先建立 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/` sandbox，用 Task subagent 黑箱執行 target skill，並如實把 `/skillevol-eval-judge` 的 fail verdict 寫入報告，不得粉飾成 pass。
- 為何存在：這是 run-eval 的負向端到端案；防止 runner 只會產生 happy report，或在 target fail 時隱瞞 judge 結果；同時防止 inline 跑 target、target-owned workspace、或外洩 oracle material 繞過真實黑箱 eval。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 重置 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/`，並把 specify `login-needs-clarify` 的 before/ 複製進該 sandbox。
- MUST 用 Task 啟動 subagent 執行 target skill /specify；subagent CWD 必須是 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/`。
- MUST 只把 specify `login-needs-clarify` 的 `prompt.md` 內容交給 target subagent；MUST NOT 交給 subagent 任何 expect、after、rubric、Provenance、expected verdict 或測試動機。
- MUST delegate to SKILL /specify（target，由 sandbox subagent 承載）
- MUST delegate to SKILL /skillevol-eval-judge（依 specify unit 的 expect 與 after 判分）
- MUST NOT 在主 agent inline 執行 /specify
- MUST NOT 向真人提問：即使 target 沒有正確觸發 clarify，也只能把觀測交給 judge 判 fail

Assistant message
- 1.0：回報已跑完 specify 的 login-needs-clarify、outer sandbox path、target 在 subagent 執行（含 subagent_id）、oracle isolation 為 none、判為 fail、報告寫到 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/eval-report.md`，並點出 target 未完成 clarify 路徑
- 0.3：回報有 fail 或報告路徑，但沒講清楚 fail 來自 target 未完成 clarify
- 0.0：把 target fail 回報成 pass、沒有寫報告、把 oracle material 交給 target subagent，或要求真人補答登入鎖定規則

（檔案終態見 after/：after/ 是完整終態 fs snapshot，包含 before/ 原有 fixture，以及 `.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/`。該 sandbox 內含 copied target before fixture、target-generated outputs 或缺失 observation、observation.md 與 eval-report.md；before/ 的 specify 包不被改動。）
