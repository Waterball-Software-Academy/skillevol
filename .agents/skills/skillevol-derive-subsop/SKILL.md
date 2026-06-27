---
name: skillevol-derive-subsop
description: 從目標 skill 的指定 SOP 步驟導出 Sub-SOP mutation：在正確位置建立或開啟一個 Sub-SOP 目錄與 SOP.md，把該步內嵌的有序子程序移入並轉成自然語言法定-verb 步，最後把 parent 步改成 invoke reference。Use when 已指定目標 skill 與步驟，且該步內嵌了一段有序、有前後依賴的子程序。SKIP when 內容其實是無序原子規定（改走 skillevol-derive-rules）、固定產物骨架（改走 skillevol-derive-template）、只改 Sub-SOP form、未指定目標步驟、或要從零發明程序。
---

# Purpose

SOP 某步內嵌了一整段有序子程序（前後有依賴、順序有語意）時，真正要做對的是 mutation：把那段子程序移到自己的 Sub-SOP 目錄與 SOP.md、轉成乾淨的有序步，再把 parent 步改成一句 invoke reference。

本 skill 在使用者已指定目標 skill 與步驟、且內容確屬 subsop-type（有序子程序）時啟用，負責完成這個 derive-and-attach mutation。

分工: Sub-SOP 的 form（目錄加 SOP.md、有序步、法定 verb、entry/exit、自洽子樹）交給 `skillevol-form-subsop`；本 skill 只決定開哪個目錄、移哪些子步、以及怎麼回寫 parent SOP。

全程嚴格遵守 `rules/mutation.md`。

# SOP

1. read 目標 SKILL.md（含 `# SOP` 與使用者指定的步驟編號或指令句），確認本次只動該步與其對應的 Sub-SOP。
2. think 確認必要輸入齊全（目標 skill、步驟、子程序內容），且內容確屬 subsop-type: 有序、有前後依賴的子程序。若其實是無序原子規定，停止並改走 `skillevol-derive-rules`；若是固定產物骨架，停止並改走 `skillevol-derive-template`。
3. think 決定 Sub-SOP 位置: 若該步已 reference 既有 Sub-SOP，直接開那個目錄；否則在目標 skill 根下建立一個語意清楚的 kebab-case 目錄（需與兄弟 Sub-SOP 排序時加 NN- 前綴）與其 SOP.md。
4. delegate Sub-SOP 的 form 給 `skillevol-form-subsop`；本 skill 不重述 Sub-SOP 應長什麼樣，只負責把本次子程序放進去。
5. write 子程序內容至該 Sub-SOP 的 SOP.md: 把指定步的有序子步移入、保持原順序與依賴、轉成自然語言法定-verb 步，並補上 entry（綁輸入）與 exit（交產物給 parent）；不臆測未被提供或未被確認的新步。
6. write 僅更新目標 SKILL.md 的指定 SOP 步驟: 移除已抽離的內嵌子步，保留主指令，並在句末補上 `執行 <dir>。read <dir>/SOP.md。`。
7. think 驗證 Sub-SOP 目錄與 SOP.md 存在、parent reference 與實際目錄名一致、子步有序且非 program-like、且 parent 步不再殘留已抽離的重複子步。
