# Rule 粒度

## Rule 1 — 流程型內容不得抽成 RuleFile

- 若內容描述的是先後順序、分支、停止條件或回到哪一步，應留在 SOP 或 Sub-SOP。把流程藏進 RuleFile，讀者必須跳檔案才知道下一步去哪。

### Good

情境: 入口判斷有三個分支

```markdown
2. think 判斷本輪入口。
   1. 若目標 skill 不存在，進入 Phase 0B。
   2. 若目標 skill 存在但沒有可信的驗證集合，進入 Phase 0B。
   3. 若目標 skill 有可信的驗證集合，進入 Phase 1。
```

### Bad

情境: 把同一段分支抽成 RuleFile

```markdown
2. think 判斷本輪入口。請嚴格遵守 `rules/entry-mode.md`。
```

預期改法:

- 將分支條件直接寫回 SOP；只有無序、可逐條驗收的判準才保留在 RuleFile。

## Rule 2 — 跨多個 concern 的共用背景不得抽成單一大 RuleFile

- RuleFile 應服務單一 step 的當下職責。若同一檔同時約束 read、think、write、resume routing 等多種 concern，就會變成背景廣播，讓每一步載入太多無關規則。

### Good

情境: 寫 `.skillevol/.gitignore`

```markdown
4. write `.skillevol/.gitignore`。請嚴格遵守 `rules/workspace-gitignore.md`。
```

### Bad

情境: 多個不同步驟都引用同一個大規則檔

```markdown
1. read 使用者需求。請嚴格遵守 `rules/runtime-state.md`。
2. think 判斷目標 skill。請嚴格遵守 `rules/runtime-state.md`。
3. write `.skillevol/.gitignore`。請嚴格遵守 `rules/runtime-state.md`。
```

預期改法:

- 拆回 step-local 規則；若內容其實是流程，直接寫回 SOP。

## Rule 3 — 不得因多個判斷而機械拆成多個 RuleFile

- 多個判斷不等於多個 RuleFile。若判斷本身能用 SOP 流程清楚表達，留在 SOP；只有同一步需要反覆消費的無序判準，才抽成 RuleFile。

### Good

情境: 目標判斷可直接用 SOP 表達

```markdown
2. think 判斷目標 skill 是否明確。
   1. 若目標不明確，停止並詢問使用者要改哪個 skill。
   2. 若目標明確，進入第 3 步。
```

### Bad

情境: 每個小判斷都拆一個檔

```text
rules/
  target-identity.md
  intake-inputs.md
  resume-routing.md
  entry-mode.md
```

預期改法:

- 先判斷內容是流程還是規則；流程留在 SOP，真正的無序判準才抽成 RuleFile。

## Rule 4 — 術語或 protocol label 不應包成規則

- 若術語或 protocol label 沒有降低歧義，應改成白話流程或刪除該詞。把 `active gate`、`mutation authorization` 這類標籤包成規則，只會增加 glossary 負擔。

### Good

情境: 直接寫使用者要確認什麼

```markdown
2. write 對使用者的確認問題。
   - 詢問使用者：這個失敗是否真的代表你要修的行為，若是才開始自動修改。
```

### Bad

情境: 把 protocol label 當成規則

```markdown
2. write active gate = confirm-enter-mutation。
3. read mutation authorization。
```

預期改法:

- 改成白話流程句；只有需要跨檔穩定引用的詞才進 glossary 或 RuleFile。
