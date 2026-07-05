---
name: skillevol-eval-judge
description: 判讀單一 eval run 的觀測、expect 與 after，回傳可被 run-eval 寫入報告的 pass/fail 評分結果。Use when /skillevol-run-eval 已完成 target skill run，需要統一 judge 判定各判準分數、verdict、違規定位與 provenance。SKIP when 要實際執行 target skill、選擇 benchmark units、撰寫 eval fixture，或直接修改 sandbox。
---

# Purpose

`skillevol-run-eval` 需要一個穩定的 judge，把被測 skill 的完整觀測結果、expect 判準與 after 終態比對成可落報告的評分結果。
本 skill 在 target run 已完成、caller 已收集 tool calls、assistant messages、event trace 與終態 fs 時啟用，只負責語意判斷與證據定位。
若缺少這個獨立 judge，run-eval 容易把「執行 target」和「判定 target 是否符合 eval」混在一起，造成 pass/fail 規則不穩或報告無法追溯。
本版採 LV1 最小型態，只寫 frontmatter、Purpose 與 SOP；只有當後續 eval 證明判準過重或輸出格式不穩時，才升級成 RuleFile、TemplateFile 或 Sub-SOP。


## PRINCIPLE: STRICT SOP

1. 依序不漏步：自底下列 SOP 逐一執行；每做一步，在訊息中「明示該步編號」。
2. 限縮延長推理：僅當 sub-SOP 當步明文標示須 **`think / reasoning`** 時，才拉長內省與推演；否則以最直接可做之 `READ`／`WRITE`／`DELEGATE` 工具呼叫達成該步，省略與該步授權範圍無關的冗長鋪墊，以降低往返等待時間。

## PRINCIPLE: 長流程待辦

長流程會跨多輪對話；在 conversation compact（對話摘要壓縮）之後，執行 AI 仍要靠**同一套待辦清單**還原：目前卡在哪個 **phase**，該 phase 內細項又到哪一格步驟。底下為**兩層**約定：**外層只列 phase**，**進入該 phase** 再把該 sub-SOP 第一層編號步驟拆成子項。尚未開始的 phase 不必預先展開成檔案級細項，以免待辦與實際 `SOP.md` 脫節。

- **必須工具化**：Tier 0／Tier 1 對應的勾選項，**要以執行環境提供的任務／待辦建立與更新能力實體化**（例如 **`TODOCREATE`**、**`TASKCREATE`** 等 tool；或宿主 IDE／Agent 內與之等效的待辦 API），在跑 sub-SOP **當下**就建好清單並隨步驟推進更新狀態。**禁止**只靠聊天裡口頭列點、不經工具建立的「心裡待辦」——壓縮後無法還原，也無法核對漏步。
- **Tier 0（phase）**：對應本檔 `# SOP` 最外層每一個 H2 Phase。這一層的勾選語意是「該 phase 的細項已全部展開**且**已執行完 Phase 其中所有子步驟。」。
- **Tier 1（phase 內細項）**：僅在目前執行中的 phase 建立；對應該 phase 裡**第一層編號步驟**拆解出的動作（`THINK` / `READ`／`WRITE`／`DELEGATE` 等）。編號建議：`(phase序)`、`(phase序-子序)`（例：`1`、`1-1`）；**進入該 phase 時**以 **`TODOCREATE`／`TASKCREATE`（或等效）** 補齊子項。

**(1)** 的子項全部完成後，以 **`TODOCREATE`／`TASKCREATE`（或等效）** 將 Tier 0 之 **(1)** 標為完成，再對 **(2)** 重複「展開 → 跑完」，依序往後。**未完成當前 phase** 前，**不要**為後續 phase 預開檔案層級的細項。

# SOP

1. read caller 提供的 judge input：target skill 名稱、unit 路徑、觀測紀錄、before/、after/、unit expect.md，以及繼承的 shared/expect.md。
2. think 將所有需要評分的判準整理成 judge worklist。請嚴格遵守 `rules/criteria-normalization.md` 來執行此步驟。
3. think 只根據 judge worklist 中每個判準對應的可觀測證據打分。
   1. 用 after/ 作為終態檔案 oracle，不把 after/ 內容抄成被測輸出。
   2. 對 MUST 違反、MUST NOT 發生、或終態與 after/ 矛盾的判準給 0.0。
   3. 不要求、引用或推測被測 agent 的 hidden reasoning trace。
4. think 推導整體 verdict。
   - pass: 所有 MUST 或 veto 判準通過，且沒有關鍵 provenance 缺口。
   - fail: 任一 MUST 或 veto 判準失敗，或觀測終態與 after/ 明確矛盾。
   - uncertain: caller 沒提供足夠觀測，導致無法判定關鍵判準。
5. write judge result 給 caller，不直接寫出任何 report 檔案。
   - include: verdict、overall、criterion_scores、violations、missing_evidence、provenance。
   - cite: 每個分數使用的 expect 條目、觀測事件、檔案路徑或 after/ 對照。
   - mutate: 不修改 sandbox、fixture、before/、after/ 或 caller 的 report 檔。
