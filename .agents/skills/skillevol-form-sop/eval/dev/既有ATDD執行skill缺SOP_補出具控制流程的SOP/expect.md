# expect — 既有ATDD執行skill缺SOP_補出具控制流程的SOP

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply，不在這寫 file diff。

## Provenance

- 釘的行為：當既有 skill 已有 frontmatter、Purpose 與空的 `# SOP` 時，`skillevol-form-sop` 應補出具控制流程的 SOP，而不是產出線性 checklist 或重寫其他部位。
- 為何存在：這條 unit 防止 SOP form 只會要求單步句型與 delegate 格式，卻漏掉 loop orchestrator 必須具備的 gate、branch、back edge、exit condition。

## Run

過程是單輪。turn 只驗關鍵 tool calls 與 assistant message；檔案終態由同層 `after/` imply。

### Turn 1 — 結束方式：done

Tool calls
- MUST NOT 呼叫 askUserQuestion；prompt 與 before/ 已足夠補完 SOP。
- MUST NOT 建立 `.agents/skills/atdd-execution/` 以外的新 skill package。

Assistant message
- 1.0：明確表示已補完既有 `atdd-execution` 的 SOP，並指出 SOP 包含 intake gate、test design gate、red gate、per-AC 內圈、regression gate、refactor/reverify gate 與 final exit。
- 0.7：表示已補完 SOP，且提到 gate 或 loop，但漏提 ATDD 內圈或外圈其中之一。
- 0.3：只說已建立或更新 skill，沒有說明 SOP 的控制流程形狀。
- 0.0：把任務當成從零建立 skill，或只回報「寫測試、寫實作、跑測試」這種線性流程。

breakpoint：done，無互動斷點。
