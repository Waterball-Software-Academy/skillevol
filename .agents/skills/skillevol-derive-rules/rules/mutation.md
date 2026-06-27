# Mutation 規則

## Rule 1 — 只動指定 SOP 步驟與其對應 RuleFile

- 本 skill 的 mutation surface 只有兩塊：目標 SKILL.md 的指定 SOP 步驟，以及該步對應的 RuleFile。
- 未經使用者授權，不順手改 Purpose、其他 SOP 步驟、其他 rules 檔，或整份 skill 的 wording。

### Good

情境: 使用者指定 `specify/SKILL.md` 第 5 步。

只更新第 5 步與 `rules/api-doc-style.md`，其餘步驟與段落保持不變。

結果: 變更邊界清楚，維護者知道這次 derive 只影響哪一段。

### Bad

情境: 為了統一風格，順手把第 3、4、6 步也一起改寫。

結果: mutation surface 擴散，難以判斷哪些改動真與 RuleFile derive 有關。

預期改法:

- 回到單一步驟與單一 RuleFile 的最小變更。

## Rule 2 — RuleFile 要開在目標 skill 根下的正確位置

- 若指定步驟已 reference 某個 RuleFile，直接開那個既有檔案，不另起新檔。
- 若尚未 reference，則在目標 skill 根下的 `rules/` 建立或開啟一個語意清楚、kebab-case 的檔名。
- derive-rules 只決定開哪個檔與放在哪裡；RuleFile 的 form 由 `skillevol-form-rule-file` 負責。

### Good

情境: 目標 skill 為 `.agents/skills/specify/`，第 5 步尚未有 reference。

建立或開啟 `.agents/skills/specify/rules/api-doc-style.md`。

結果: RuleFile 與目標 skill 同根，路徑可由 SOP 用相對路徑穩定指向。

### Bad

情境: 在 repo 根目錄新建 `rules/rule1.md`，或明明已有 `rules/api-doc-style.md` 卻改開 `rules/api-style-v2.md`。

結果: 路徑漂移，SOP reference 與既有 rules 生態分裂。

預期改法:

- 若已有 reference 就沿用原檔；若無則在目標 skill 的 `rules/` 下用語意檔名建立。

## Rule 3 — SOP 的 mutation 形式固定為「主指令 + reference」

- 抽離完成後，指定步驟只保留主指令句與 reference 句，不保留已移入 RuleFile 的重複 bullet。
- reference 固定掛在該步指令句末，使用相對於目標 skill 的路徑。

### Good

情境: 第 5 步的五條無序規定已抽到 `rules/api-doc-style.md`。

```markdown
5. write API 文件至 spec/api.md。請嚴格遵守 `rules/api-doc-style.md` 來執行此步驟。
```

結果: SOP 一眼可掃讀，AI 也知道該步還要 read 哪個 RuleFile。

### Bad

情境: 抽離後仍保留原 bullet，或把 reference 另起一行。

```markdown
5. write API 文件至 spec/api.md。
（請參考 rules/api-doc-style.md）
   - 每個端點須含 HTTP 方法與路徑。
   - 請求與回應須各附 JSON 範例。
```

結果: SOP 與 RuleFile 內容重複，reference 句型也不穩定。

預期改法:

- 刪掉已抽離的 bullet，並把 reference 改回同一句的固定句型。

## Rule 4 — 不臆測新規則，只搬移或補入已提供內容

- RuleFile 內的規則內容只來自使用者提供、現有 SOP 內嵌規定、或經使用者確認的草稿。
- derive-rules 不負責發明新的業務規則；它只負責把內容移到對的位置。

### Good

情境: 使用者已提供四條 API 文件格式規定。

將這四條移入 RuleFile，並依既有資訊補上對應 reference。

結果: 這次變更可追溯到明確來源，沒有偷偷長出新要求。

### Bad

情境: 使用者只說「幫我展開 API 規則」。

未經確認就自行加上「每個端點須含 OAuth2 授權段落」。

結果: RuleFile 混入未授權規定，derive 變成創作。

預期改法:

- 只處理已知規則；缺內容時先確認，不自行擴寫 domain 規定。

## Rule 5 — 若內容不是 rule-type，就停止 derive

- derive-rules 只處理無序、可逐條驗收的原子規定。
- 若內容其實是固定骨架，應改走 TemplateFile；若內容有固定順序或子程序，應改走 Sub-SOP，而不是硬抽成 RuleFile。

### Good

情境: 三到五條格式規定彼此無順序，且每條都能寫 Good/Bad 驗收。

將其抽成 RuleFile，並在指定步驟補上 reference。

結果: derive 的對象與 RuleFile 形式匹配。

### Bad

情境: 內容其實是在教「先分析、再分類、最後輸出」的有序子程序。

仍硬把它們抽成 RuleFile。

結果: 本來應該是程序流的東西，被錯誤變成無序規章。

預期改法:

- 停止本 skill，改用對應的 TemplateFile 或 Sub-SOP 流程。
