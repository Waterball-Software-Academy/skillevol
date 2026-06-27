# Mutation 規則

## Rule 1 — 只動指定 SOP 步驟與其對應 Sub-SOP

- 本 skill 的 mutation surface 只有兩塊: 目標 SKILL.md 的指定 SOP 步驟，以及該步對應的 Sub-SOP（目錄加 SOP.md）。
- 未經使用者授權，不順手改 Purpose、其他 SOP 步驟、其他 Sub-SOP，或整份 skill 的 wording。

### Good

情境: 使用者指定 `kickoff/SKILL.md` 第 2 步。

只更新第 2 步與 `01-ask-config/`（其 SOP.md），其餘步驟與段落保持不變。

結果: 變更邊界清楚，維護者知道這次 derive 只影響哪一段。

### Bad

情境: 為了統一風格，順手把第 3、5 步也一起改寫。

結果: mutation surface 擴散，難以判斷哪些改動真與 Sub-SOP derive 有關。

預期改法:

- 回到單一步驟與單一 Sub-SOP 的最小變更。

## Rule 2 — Sub-SOP 要開在目標 skill 根下的正確位置

- 若指定步驟已 reference 某個 Sub-SOP，直接開那個既有目錄，不另起新目錄。
- 若尚未 reference，則在目標 skill 根下建立一個語意清楚的 kebab-case 目錄與其 SOP.md；需與兄弟 Sub-SOP 排序時加 NN- 前綴。
- derive-subsop 只決定開哪個目錄與放在哪裡；Sub-SOP 的 form 由 `skillevol-form-subsop` 負責。

### Good

情境: 目標 skill 為 `.agents/skills/kickoff/`，第 2 步尚未有 reference，且與後續子程序有順序。

建立 `.agents/skills/kickoff/01-ask-config/SOP.md`。

結果: Sub-SOP 與目標 skill 同根、NN- 前綴標出執行序，parent 可用相對路徑穩定指向。

### Bad

情境: 在 repo 根新建 `sub1/SOP.md`，或明明已有 `01-ask-config/` 卻改開 `ask-config-v2/`。

結果: 路徑漂移，parent reference 與既有 Sub-SOP 生態分裂。

預期改法:

- 若已有 reference 就沿用原目錄；若無則在目標 skill 根下用語意目錄名建立。

## Rule 3 — parent 的 mutation 形式固定為「主指令 + invoke reference」

- 抽離完成後，指定步驟只保留主指令句與 invoke reference 句，不保留已移入 Sub-SOP 的重複子步。
- invoke reference 固定掛在該步指令句末，形式為「執行 `<dir>`。read `<dir>/SOP.md`。」，使用相對於目標 skill 的路徑。

### Good

情境: 第 2 步的十個子步已抽到 `01-ask-config/SOP.md`。

```markdown
2. 收集 kickoff 配置選項。執行 01-ask-config。read 01-ask-config/SOP.md。
```

結果: parent 一眼可掃讀，AI 也知道該步要去跑哪個 Sub-SOP。

### Bad

情境: 抽離後仍在 parent 保留全部子步，或 invoke reference 另起一行。

```markdown
2. 收集 kickoff 配置選項。
（見 01-ask-config）
   1. resolve 路徑
   2. 讀現有檔
   ...
```

結果: parent 與 Sub-SOP 內容重複，reference 句型也不穩定。

預期改法:

- 刪掉已抽離的子步，把 reference 改回同一句的固定句型「執行 `<dir>`。read `<dir>/SOP.md`。」。

## Rule 4 — 搬移時保持順序與依賴，並轉成法定-verb 自然語言步

- 子步移入 Sub-SOP 時必須保持原本的順序與前後依賴，不可重排成無序清單。
- 同時把任何 program-like 記號（$$ 暫存器、BRANCH / GOTO、PARSE / ASSERT 之類 pseudo-verb）改寫成自然語言加法定 verb 的一步一動作。

### Good

情境: 原步內嵌「先 resolve、再讀檔、再分析、最後回寫」。

移入 SOP.md 後維持這個順序，每步寫成 read / think / write 的白話步。

結果: 子程序語意不變，且符合 Sub-SOP form。

### Bad

情境: 搬移時把有序子步打散成無序 bullet，或原樣保留 `$x = PARSE(...); BRANCH ...`。

結果: 有序程序變成無序或殘留 program-like，違反 Sub-SOP form。

預期改法:

- 保持原順序與依賴，並把 program-like 記號改寫成自然語言法定-verb 步。

## Rule 5 — 不臆測新子步，只搬移或補入已提供內容

- Sub-SOP 內的子步只來自使用者提供、現有 SOP 內嵌子步、或經使用者確認的草稿。
- derive-subsop 不負責發明新的程序步驟；它只負責把子程序移到對的位置並補上 entry 與 exit。

### Good

情境: 使用者已提供五個有序子步。

將這五步移入 SOP.md，補上綁輸入的參數段與交產物的結尾步。

結果: 這次變更可追溯到明確來源，沒有偷偷長出新步驟。

### Bad

情境: 使用者只說「幫我把這步展開成 Sub-SOP」。

未經確認就自行加上「跑一輪整合測試」這個原本沒有的步。

結果: Sub-SOP 混入未授權步驟，derive 變成創作。

預期改法:

- 只處理已知子步；缺內容時先確認，entry / exit 之外不自行擴寫程序。

## Rule 6 — 若內容不是 subsop-type，就停止 derive

- derive-subsop 只處理有序、有前後依賴的子程序。
- 若內容其實是無序、可逐條驗收的原子規定，應改走 `skillevol-derive-rules`；若內容是固定產物骨架，應改走 `skillevol-derive-template`，而不是硬抽成 Sub-SOP。

### Good

情境: 一段「先 A、再 B、最後 C」且 C 吃 B 產物的有序流程。

將其抽成 Sub-SOP，並把 parent 步改成 invoke reference。

結果: derive 的對象與 Sub-SOP 形式匹配。

### Bad

情境: 內容其實是三條彼此無序的命名規定，或一份固定欄位的 yaml 骨架。

仍硬把它們抽成 Sub-SOP。

結果: 無序規章或固定骨架被錯誤變成程序流。

預期改法:

- 停止本 skill，改用 `skillevol-derive-rules`（無序規定）或 `skillevol-derive-template`（固定骨架）。
