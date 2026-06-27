---
name:
description:
---

# Purpose

使用者每次迭代規格驅動開發時，往往先有一段未整理的自然語言 Raw Idea；若直接進澄清或任務拆解，容易造成排版雜亂、錯字或意圖遺失，下游也缺少可追蹤的單一規格起點。
本 skill 在對方已提供（或更新）功能 Raw Idea、且準備開始新一輪規格驅動開發迭代時啟用，負責整理原文排版與錯字後寫入規格檔，作為整條工作流的固定入口。
若跳過這步，AI 容易在模糊輸入上誤觸發後續 skill，或各階段各自解讀同一段需求，造成規格分裂與重複澄清。

# SOP

1. read 使用者提供的 Raw Idea 與 specs/ 目錄現況。
2. think 依 Raw Idea 推導本次迭代的 package name。請嚴格遵守 `rules/package-name.md` 來執行此步驟。
3. write 向使用者回報本次 spec package 結構。
   - spec root 為 specs/。
   - 每次迭代開新 package：specs/{package-name}/。
   - 產出檔為 spec.md。
4. think 分析 Raw Idea、區隔 User Story 並草擬驗收情境。請嚴格遵守 `rules/raw-idea-analysis.md` 來執行此步驟。
5. write 整理後需求至 specs/{package-name}/spec.md。請嚴格遵守 `rules/spec-md-format.md` 來執行此步驟。
6. read specs/{package-name}/spec.md，盤點 `[待澄清]` 項目。
7. delegate to SKILL /clarify
   - input: 第 6 步盤點中下一個未澄清的 `[待澄清]` 項目（每次只傳一項）
   - input: specs/{package-name}/spec.md 全文
   - skip: 若無 `[待澄清]`
8. write 依 clarify 回報的單題 Q&A 即時回寫至 specs/{package-name}/spec.md。請嚴格遵守 `rules/spec-md-format.md` 來執行此步驟。
9. read specs/{package-name}/spec.md，盤點是否仍有 `[待澄清]`；若有則回到第 7 步。
