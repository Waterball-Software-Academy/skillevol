---
name: skillevol-derive-template
description: 從目標 skill 的指定 SOP 步驟導出 TemplateFile mutation：判定 raw 或 guideline 變體，在正確位置建立或開啟 TemplateFile，把該步的固定產物骨架移入，並在步驟句末補上對 TemplateFile 的 reference。Use when 已指定目標 skill 與步驟，且要把 SOP 內固定形狀的輸出骨架抽成 TemplateFile。SKIP when 內容其實是無序原子規定（改走 skillevol-derive-rules）、有序子程序（改走 Sub-SOP）、只改 TemplateFile form、未指定目標步驟、或要從零發明骨架。
---

# Purpose

SOP 某步內嵌了一整塊固定的產物骨架（輸出範本）時，真正要做對的是 mutation：判定它該是 raw 還是 guideline 變體、把骨架移到正確的 TemplateFile、再把 reference 掛回該步。

本 skill 在使用者已指定目標 skill 與步驟、且內容確屬 template-type（有固定形狀、會被逐字產出的輸出骨架）時啟用，負責完成這個 derive-and-attach mutation。

分工: TemplateFile 的 form（raw 與 guideline 兩變體、placeholder 規範、逐字即成品）交給 `skillevol-form-template-file`；本 skill 只決定變體、開哪個檔、移哪些內容、以及怎麼回寫 SOP。

全程嚴格遵守 `rules/mutation.md`。

# SOP

1. read 目標 SKILL.md（含 `# SOP` 與使用者指定的步驟編號或指令句），確認本次只動該步與其對應的 TemplateFile。
2. think 確認必要輸入齊全（目標 skill、步驟、骨架內容），且內容確屬 template-type: 有固定形狀、會被逐字產出的輸出骨架。若其實是無序原子規定，停止並改走 `skillevol-derive-rules`；若是有序子程序，停止並改走 Sub-SOP derive。
3. think 判定變體: 消費者是 script 或 generator 逐字消費，選 raw（無 guideline，整檔即範本本體）；需 LLM 看指導才填得對，選 guideline（`# Guideline` 加 `# Template` 兩段）。
4. think 決定 TemplateFile 位置: 若該步已 reference 既有 TemplateFile，直接開該檔；否則在目標 skill 根下的 `templates/` 選定一個語意清楚的 kebab-case 檔名，建立或開啟它。
5. delegate TemplateFile 的 form 給 `skillevol-form-template-file`；本 skill 不重述 TemplateFile 應長什麼樣，只依 step 3 的變體把本次骨架放進去。
6. write 骨架內容至目標 TemplateFile，只移入指定步驟的固定產物骨架，placeholder 用固定記號，不臆測未被提供或未被確認的欄位與結構。
7. write 僅更新目標 SKILL.md 的指定 SOP 步驟: 移除已抽離的內嵌骨架，保留主指令，並在句末補上 `依 templates/<檔名> 產出此步驟的成品。`（raw 變體）或 `依 templates/<檔名> 渲染此步驟的成品。`（guideline 變體）。
8. think 驗證 TemplateFile 路徑存在、SOP reference 與實際檔名一致、變體選擇與消費者相符、且該步不再殘留已抽離的重複骨架。
