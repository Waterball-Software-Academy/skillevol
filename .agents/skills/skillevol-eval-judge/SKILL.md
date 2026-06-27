---
name: skillevol-eval-judge
description: 判讀單一 eval run 的觀測、expect 與 after，回傳可被 run-eval 寫入報告的 pass/fail 評分結果。Use when /skillevol-run-eval 已完成 target skill run，需要統一 judge 判定各判準分數、verdict、違規定位與 provenance。SKIP when 要實際執行 target skill、選擇 benchmark units、撰寫 eval fixture，或直接修改 sandbox。
---

# Purpose

`skillevol-run-eval` 需要一個穩定的 judge，把被測 skill 的完整觀測結果、expect 判準與 after 終態比對成可落報告的評分結果。
本 skill 在 target run 已完成、caller 已收集 tool calls、assistant messages、event trace 與終態 fs 時啟用，只負責語意判斷與證據定位。
若缺少這個獨立 judge，run-eval 容易把「執行 target」和「判定 target 是否符合 eval」混在一起，造成 pass/fail 規則不穩或報告無法追溯。
本版採 LV1 最小型態，只寫 frontmatter、Purpose 與 SOP；只有當後續 eval 證明判準過重或輸出格式不穩時，才升級成 RuleFile、TemplateFile 或 Sub-SOP。

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
