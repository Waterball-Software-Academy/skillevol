---
name: skillevol-derive-rules
description: 從目標 skill 的指定 SOP 步驟導出 RuleFile mutation：在正確位置建立或開啟 RuleFile，把該步的 rule-type 規定移入，並在步驟句末補上對 RuleFile 的 reference。Use when 已指定目標 skill 與步驟，且要把 SOP 內的無序原子規定抽成 RuleFile。SKIP when 內容其實是 TemplateFile、Sub-SOP、只改 RuleFile form、未指定目標步驟、或要從零發明規則。
---

# Purpose

SOP 某步累積太多無序規定時，真正要做對的是 mutation：找到正確步驟、把規定移到正確的 RuleFile、再把 reference 掛回該步。
本 skill 在使用者已指定目標 skill 與步驟、且內容確實屬於 rule-type 規定時啟用，負責完成這個 derive-and-attach mutation。
RuleFile 的 form 交給 `skillevol-form-rule-file`；本 skill 只負責決定開哪個檔、移哪些內容、以及怎麼回寫 SOP。

# SOP

1. read 目標 SKILL.md（含 `# SOP` 與使用者指定的步驟編號或指令句），並確認本次只動該步與其對應的 RuleFile。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
2. think 確認必要輸入齊全（目標 skill、步驟、規則內容），且內容確屬 rule-type：無序、可逐條驗收的原子規定；若其實是 TemplateFile 或 Sub-SOP，則停止並改走正確 skill。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
3. think 決定 RuleFile 位置：若該步已 reference 既有 RuleFile，直接開該檔；否則在目標 skill 根下的 `rules/` 選定一個語意清楚的 kebab-case 檔名，並在正確位置建立或開啟它。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
4. delegate RuleFile 的 form 給 `skillevol-form-rule-file`；本 skill 不重述 RuleFile 應長什麼樣，只負責把本次規定放進去。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
5. write 規則內容至目標 RuleFile，只移入本次指定步驟所需的 rule-type 規定，不臆測未被提供或未被確認的新規則。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
6. write 僅更新目標 SKILL.md 的指定 SOP 步驟：移除已抽離的內嵌 bullet，保留主指令，並在句末補上 `請嚴格遵守 \`rules/<檔名>.md\` 來執行此步驟。`。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
7. think 驗證 RuleFile 路徑存在、SOP reference 與實際檔名一致、且該步不再殘留已抽離的重複規定。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
