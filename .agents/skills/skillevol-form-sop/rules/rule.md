# SOP 規則

## Rule 1 — 每步單一職責，且用單一動詞開頭

- SOP 的頂層步驟只用 `read`、`write`、`think`、`delegate` 這四類動詞開頭。
- 一個步驟只承載一個可獨立驗收的職責；不要把讀取、推理、寫入或驗收混成一句。

### Good

情境: 某 skill 需要先整理類別，再產出兩份文件。

```markdown
1. read 使用者提供的功能描述與既有 spec/ 目錄。
2. think 梳理核心實體與類別關係。
3. write 類別圖至 spec/class-diagram.mmd。
4. think 梳理 API 端點與請求回應結構。
5. write API 文件至 spec/api.md。
```

結果: 每步只做一件事，讀者與 agent 都能逐步驗收。

### Bad

情境: 同一步混了多個階段。

```markdown
1. 閱讀需求，梳理實體關係與 API 結構，並同時產出類別圖和 API 文件。
2. 確認內容正確後寫入 spec/ 並做品質檢查。
```

結果: read、think、write 混在一起，無法切開驗收。

預期改法:

- 拆成多個頂層步驟，讓每一步只保留單一動詞與單一職責。

## Rule 2 — 禁止單步要求多份獨立產出

- 同一個 `write` 步驟只對應一份產出物或一個區塊。
- 若需要產出兩份以上獨立成果，必須拆成多個頂層步驟，而不是塞進同一步的敘述或縮排。

### Good

情境: 類別圖與 API 文件都要更新。

將兩份產出拆成兩個 `write` 步，中間各自保留必要的 `think`。

結果: 每個產出各有自己的上下文與驗收邊界。

### Bad

情境: 把多份輸出塞進同一步。

```markdown
3. write 更新 SOP。
   - write 類別圖至 spec/class-diagram.mmd。
   - write API 文件至 spec/api.md。
```

結果: 同一步驟下藏了多個獨立 write 任務，顆粒度過粗。

預期改法:

- 將不同產出拆成各自的頂層 `write` 步驟。

## Rule 3 — Subflow 與 Rules 要分清楚

- 有固定執行順序的子流程，用縮排數字編號 `1. 2. 3.`。
- 無固定順序的補充規定，用 bullet。
- 同一指令下不要混用數字編號與 bullet。

### Good

情境: 某個 `think` 步內需要依固定順序盤點。

```markdown
2. think 盤點現有 SOP 的指令顆粒度。
   1. 列出所有含「並且」「同時」的步驟。
   2. 標記每個混步對應的獨立產出。
   3. 將混步拆成候選子步清單。
```

結果: 子流程順序清楚，讀者知道這是程序，不是規章。

### Bad

情境: 在同一指令下同時放 Subflow 與 Rules。

```markdown
2. think 梳理 API 端點結構。
   1. 列出所有資源名詞。
   2. 標記 CRUD 對應端點。
   - 不得捏造未在 spec 出現的欄位。
   - 路由須用複數名詞。
```

結果: 程序與規章混雜，難以辨識每個縮排的語義。

預期改法:

- 將有序子流程與無序規定拆開；必要時改成不同步驟。

## Rule 4 — 內嵌無序規定最多三項，超過就抽離

- 同一步底下的無序規定若三項以內，可用 bullet 內嵌。
- 超過三項時，應抽離到 `rules/*.md`，SOP 只保留主指令與 reference。
- 抽離後不可保留重複 bullet。

### Good

情境: API 文件有五條格式規定。

```markdown
5. write API 文件至 spec/api.md。請嚴格遵守 `rules/api-doc-style.md` 來執行此步驟。
```

結果: SOP 保持精簡，詳細規定移到對應 rules 檔。

### Bad

情境: 五條規定仍全塞在 SOP。

```markdown
5. write API 文件至 spec/api.md。
   - 每個端點須含 HTTP 方法與路徑。
   - 請求與回應須各附 JSON 範例。
   - 錯誤碼須列舉並附說明。
   - 欄位須標注型別與是否必填。
   - 文件語言與 spec 其餘正文一致。
```

結果: 規定過長，SOP 難掃讀，也不利後續維護。

預期改法:

- 把超過三項的無序規定抽到 `rules/*.md`，SOP 改回主指令加 reference。

## Rule 5 — delegate 用固定句型，不重述被委派 skill 細節

- `delegate` 步驟固定用 `delegate to SKILL /{skill-name}`。
- 補充 payload（如 `input`、`skip`）時，用 bullet，且盡量控制在三項內。
- 呼叫方 SOP 不重述被委派 skill 的執行細節。

### Good

情境: 某步需要委派給 `clarify`。

```markdown
7. delegate to SKILL /clarify
   - input: 第 6 步盤點的 `[待澄清]` 項目
   - input: specs/{package-name}/spec.md 全文
   - skip: 若無 `[待澄清]`
```

結果: 被委派對象與輸入都可機械辨識，邊界也清楚。

### Bad

情境: 用自然語言描述 delegate。

```markdown
7. delegate 至 clarify skill：傳入第 6 步盤點的 `[待澄清]` 項目與 spec 上下文；若無 `[待澄清]` 則跳過。
```

結果: 句型不穩定，對機械辨識與跨 skill 一致性都較差。

預期改法:

- 改回 `delegate to SKILL /clarify` 固定句型；其餘 payload 拆成 bullet。
