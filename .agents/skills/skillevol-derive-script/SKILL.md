---
name: skillevol-derive-script
description: 從目標 skill 的指定 SOP 步驟導出 ScriptFile mutation：在正確位置建立或開啟 `scripts/*.py`，把同職責、可一起自動化的 1..* 個步驟群移入，並把 parent 步改成 script invoke reference。Use when 已指定目標 skill 與步驟群，且內容確屬 script-type。SKIP when 內容其實是無序原子規定、固定產物骨架、語意判讀子程序、只改 ScriptFile form、未指定目標步驟，或要從零發明自動化流程。
---

# Purpose

SOP 某段步驟若承載的是同職責、可一起自動化的 mechanical 工作，真正要做對的是 mutation：把這群步驟抽到正確層級的 `scripts/*.py`，再把 parent SOP 改成穩定的 script invoke reference。

本 skill 在使用者已指定目標 skill 與 1..* 個步驟、且內容確屬 script-type 時啟用，負責完成這個 derive-and-attach mutation。

分工: ScriptFile 的 form（PEP 723、單檔 Python、entrypoint、顯性輸入輸出）交給 `skillevol-form-script`；本 skill 只決定抽哪群步驟、開哪個 script、移哪些內容、以及怎麼回寫 SOP。

# SOP

1. read 目標 `SKILL.md`（含 `# SOP` 與使用者指定的 1..* 個步驟編號或指令句），確認本次只動該步驟群與其對應的單一 ScriptFile。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
2. think 確認必要輸入齊全（目標 skill、步驟群、步驟內容），且內容確屬 script-type：同職責、可一起自動化、核心是 mechanical 工作。若其實是無序原子規定，停止並改走 `skillevol-derive-rules`；若是固定產物骨架，停止並改走 `skillevol-derive-template`；若是語意判讀或不應自動化的有序子程序，停止並改走 `skillevol-derive-subsop` 或保留 inline。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
3. think 決定 ScriptFile 位置：若指定步驟群已 reference 既有 ScriptFile，直接開該檔；否則在該 SOP 所屬層級的 `scripts/` 選定一個語意清楚的 kebab-case 檔名，建立或開啟它。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
4. delegate ScriptFile 的 form 給 `skillevol-form-script`：必須以 Task subagent 委派 `skillevol-form-script`，不得自行 read 其 rules 後 inline 寫 ScriptFile；本 skill 不重述 ScriptFile 應長什麼樣，只負責把本次自動化工作放進去。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
5. write 目標 ScriptFile：只在第 4 步 delegate 完成後才寫檔；只搬入指定步驟群內已存在、且可被穩定自動化的工作；腳本需符合 PEP 723 與單檔 Python form，不臆測新的 workflow、隱性輸入或第三方依賴。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
6. write 僅更新目標 `SKILL.md` 的指定步驟群：移除已抽離的細步，折成一個單一步驟，使用 `run` verb 並以 `run \`scripts/<檔名>.py\` 完成 <共同職責>。` 的形式掛回 parent。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
7. think 驗證 ScriptFile 路徑存在、SOP reference 與實際檔名一致、變更邊界只限指定步驟群與單一 ScriptFile、且 parent 不再殘留已抽離的重複細步。請嚴格遵守 `rules/mutation.md` 來執行此步驟。
