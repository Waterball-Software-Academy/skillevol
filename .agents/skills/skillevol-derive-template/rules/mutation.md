# Mutation 規則

## Rule 1 — 只動指定 SOP 步驟與其對應 TemplateFile

- 本 skill 的 mutation surface 只有兩塊: 目標 SKILL.md 的指定 SOP 步驟，以及該步對應的 TemplateFile。
- 未經使用者授權，不順手改 Purpose、其他 SOP 步驟、其他 templates 檔，或整份 skill 的 wording。

### Good

情境: 使用者指定 `specify/SKILL.md` 第 4 步。

只更新第 4 步與 `templates/openapi-skeleton.yml`，其餘步驟與段落保持不變。

結果: 變更邊界清楚，維護者知道這次 derive 只影響哪一段。

### Bad

情境: 為了統一風格，順手把第 2、5 步也一起改寫。

結果: mutation surface 擴散，難以判斷哪些改動真與 TemplateFile derive 有關。

預期改法:

- 回到單一步驟與單一 TemplateFile 的最小變更。

## Rule 2 — TemplateFile 要開在目標 skill 根下的正確位置

- 若指定步驟已 reference 某個 TemplateFile，直接開那個既有檔案，不另起新檔。
- 若尚未 reference，則在目標 skill 根下的 `templates/` 建立或開啟一個語意清楚、kebab-case 的檔名。
- derive-template 只決定開哪個檔與放在哪裡；TemplateFile 的 form 由 `skillevol-form-template-file` 負責。

### Good

情境: 目標 skill 為 `.agents/skills/specify/`，第 4 步尚未有 reference。

建立或開啟 `.agents/skills/specify/templates/openapi-skeleton.yml`。

結果: TemplateFile 與目標 skill 同根，路徑可由 SOP 用相對路徑穩定指向。

### Bad

情境: 在 repo 根目錄新建 `templates/t1.yml`，或明明已有 `templates/openapi-skeleton.yml` 卻改開 `templates/openapi-v2.yml`。

結果: 路徑漂移，SOP reference 與既有 templates 生態分裂。

預期改法:

- 若已有 reference 就沿用原檔；若無則在目標 skill 的 `templates/` 下用語意檔名建立。

## Rule 3 — 依消費者選對 raw 或 guideline 變體

- 消費者是 script 或 generator 逐字消費的，選 raw 變體: 整檔就是範本本體，byte-exact，無 Guideline。
- 需要 LLM 看指導才填得對的，選 guideline 變體: `# Guideline` 講怎麼填、`# Template` 放骨架。
- 選錯的代價: raw 包了指導會被一起吐進產物；guideline 漏給 LLM 需要的指導則填不對。

### Good

情境: 第 4 步的骨架是要 append 進 arguments.yml、由 generator 逐字消費。

選 raw 變體: `templates/python-e2e.tail.yml` 整檔只有範本本體。

結果: generator 逐字 append，不會吐出多餘標題。

### Bad

情境: 同樣是 generator 逐字消費的 tail，卻抽成 guideline 兩段框。

結果: `# Guideline` 與 `# Template` 標題會被一起吐進 arguments.yml，污染產物。

預期改法:

- 依消費者重選: script 或 generator 逐字消費用 raw，LLM 渲染用 guideline。

## Rule 4 — SOP 的 mutation 形式固定為「主指令 + 依 template 產出」

- 抽離完成後，指定步驟只保留主指令句與 reference 句，不保留已移入 TemplateFile 的重複骨架。
- reference 固定掛在該步指令句末，使用相對於目標 skill 的路徑，動詞用「產出」或「渲染」，不用「遵守」: TemplateFile 是拿來填出成品的，不是拿來遵守的。

### Good

情境: 第 4 步的 OpenAPI 骨架已抽到 `templates/openapi-skeleton.yml`。

```markdown
4. write OpenAPI 文件至 spec/api.yml。依 `templates/openapi-skeleton.yml` 產出此步驟的成品。
```

結果: SOP 一眼可掃讀，AI 也知道該步要拿哪個 TemplateFile 當骨架。

### Bad

情境: 抽離後仍保留整段內嵌骨架，或 reference 另起一行、用「請遵守」。

```markdown
4. write OpenAPI 文件至 spec/api.yml。
（請遵守 templates/openapi-skeleton.yml）
   openapi: 3.0.0
   info:
     title: ${TITLE}
```

結果: SOP 與 TemplateFile 內容重複，reference 句型也不穩定，且把「填骨架」誤寫成「遵守」。

預期改法:

- 刪掉已抽離的骨架，把 reference 改回同一句的固定句型「依 `templates/<檔名>` 產出此步驟的成品。」。

## Rule 5 — 不臆測新骨架，只搬移或補入已提供內容

- TemplateFile 內的骨架只來自使用者提供、現有 SOP 內嵌骨架、或經使用者確認的草稿。
- derive-template 不負責發明新的欄位或結構；它只負責把骨架移到對的位置與變體，並標好 placeholder。

### Good

情境: 使用者已提供一份四欄位的 yaml 成品骨架。

將骨架原樣移入，把要填處標成 `${...}` placeholder。

結果: 這次變更可追溯到明確來源，沒有偷偷長出新欄位。

### Bad

情境: 使用者只說「幫我展開 API 範本」。

未經確認就自行加上 `security: [OAuth2]` 區段。

結果: TemplateFile 混入未授權結構，derive 變成創作。

預期改法:

- 只處理已知骨架；缺內容時先確認，不自行擴寫產物結構。

## Rule 6 — 若內容不是 template-type，就停止 derive

- derive-template 只處理有固定形狀、會被逐字產出的輸出骨架。
- 若內容其實是無序、可逐條驗收的原子規定，應改走 `skillevol-derive-rules`；若內容有固定順序或子程序，應改走 Sub-SOP，而不是硬抽成 TemplateFile。

### Good

情境: 一份欄位固定的 yaml 成品骨架，整體就是要被逐字產出的形狀。

將其抽成 TemplateFile，並在指定步驟補上 reference。

結果: derive 的對象與 TemplateFile 形式匹配。

### Bad

情境: 內容其實是在教「先掃描、再分類、最後輸出」的有序子程序。

仍硬把它們抽成 TemplateFile。

結果: 本來應該是程序流的東西，被錯誤變成固定骨架。

預期改法:

- 停止本 skill，改用 `skillevol-derive-rules`（無序規定）或 Sub-SOP（有序子程序）。
