# Frontmatter 規則

## Rule 1 — 只處理 `name` 與 `description`

- 本 skill 只負責 SKILL.md frontmatter 的 `name` 與 `description`。若需求其實是改正文、改其他 frontmatter 欄位、或 rename skill 資料夾，先停下來說明邊界。
- 目標檔已有其他 frontmatter 欄位時，只更新這兩個欄位，其餘原樣保留。

### Good

情境: 使用者只要求補寫 frontmatter，目標檔另有 `metadata`。

```yaml
---
name: skill-write-purpose
description: 為 SKILL.md 撰寫或改寫開頭的 Purpose 段落。Use when 新建 skill 缺 Purpose、既有 Purpose 過時或空洞、或 workflow 上游剛定稿需要補 Why 段落。SKIP when 使用者要改 SOP、Rules、frontmatter 或整份 skill。
metadata:
  user-invocable: true
---
```

結果: 只動 `name` 與 `description`，其他 frontmatter 與正文保持不變。

### Bad

情境: 使用者只說「補 frontmatter」。

改完 `name` 與 `description` 後，順手重寫 `# Purpose`、刪掉 `metadata`，或自行 rename 資料夾。

預期改法:

- 回到 frontmatter 邊界，只保留 `name` 與 `description` 的修改。

## Rule 2 — `name` 以目標 skill 資料夾名為 SSOT

- `name` 必須和目標 SKILL.md 的 parent directory 一致，使用小寫 kebab-case。
- 若使用者想改 skill 名稱但尚未 rename 資料夾，先要求確認是否連資料夾一起改；不要留下名稱與路徑不一致的半套狀態。

### Good

情境: 目標檔位於 `.agents/skills/skill-form-frontmatter/SKILL.md`。

```yaml
---
name: skill-form-frontmatter
description: 當要撰寫 Skill 的 frontmatter 部位時，必須遵守此 Skill 之規範撰寫。
---
```

結果: `name` 與資料夾名一致，可作為穩定識別字。

### Bad

情境: 同一路徑卻寫成別的名字。

```yaml
---
name: skill-write-frontmatter
description: 當要撰寫 Skill 的 frontmatter 部位時，必須遵守此 Skill 之規範撰寫。
---
```

結果: skill 識別字與路徑脫節，容易造成誤引用或路由混亂。

預期改法:

- 將 `name` 改回目標 skill 的資料夾名。

## Rule 3 — `description` 用固定 routing 句型，且保持精簡

- `description` 固定寫成「做什麼。Use when ... SKIP when ...」。
- `Use when` 要同時涵蓋使用者會怎麼描述需求，以及檔案本身會出現的狀態。
- `SKIP when` 只收緊真正相鄰的邊界，不把 Purpose 的背景故事或過多情境塞進來。

### Good

情境: 為一個只處理 Purpose 的 skill 撰寫 `description`。

`為 SKILL.md 撰寫或改寫開頭的 Purpose 段落。Use when 新建 skill 缺 Purpose、既有 Purpose 過時或空洞、或 workflow 上游剛定稿需要補 Why 段落。SKIP when 使用者要改 SOP、Rules、frontmatter 或整份 skill。`

結果: 只看 `description` 就知道職責、觸發時機與邊界。

### Bad

情境: `description` 只寫摘要，沒有路由資訊。

`處理 skill metadata。`

結果: 看不出何時該載入，也看不出何時不該載入。

預期改法:

- 改成「做什麼。Use when ... SKIP when ...」完整句型，補齊時機與邊界。

## Rule 4 — 無法保守推斷時先確認，不要臆測

- 可從目標檔、資料夾名與相鄰 skill 保守推斷的內容才寫進 frontmatter。
- 若看完仍無法判斷主要時機、最常見誤觸發、或 `SKIP when` 邊界，先問使用者一句聚焦問題，不要硬編。

### Good

情境: 看完目標檔後，仍不知道它和相鄰 skill 的分界。

先問: `這個 skill 主要在什麼時機啟用？最常見會和哪個 skill 撞邊界？`

結果: `description` 依使用者答案收斂，不會亂寫 routing metadata。

### Bad

情境: 只因 skill 名像 `specify`，就自行寫成 GitHub PR 建立後才啟用。

結果: frontmatter 寫入檔內完全驗證不出的外部流程，造成誤觸發。

預期改法:

- 刪掉無法從檔案或使用者資訊驗證的情境；必要時先提問再寫。

## Rule 5 — frontmatter 必須位於檔首，且維持合法 YAML

- frontmatter 必須位於檔首，用成對的 `---` 包住。
- 若原本已有 frontmatter，更新後仍要保持合法 YAML；frontmatter 後面立刻接正文，不把 `name:`、`description:` 寫進正文段落。

### Good

情境: 目標檔原本沒有 frontmatter。

```yaml
---
name: pdf-processing
description: 處理 PDF 的讀取、合併、拆分、旋轉、OCR 與表單填寫。Use when 使用者提到 PDF 擷取、合併文件、頁面拆分、掃描檔 OCR 或 PDF 表單處理。SKIP when 需求與 PDF 無關。
---
```

結果: frontmatter 位置正確，解析器可正常讀取。

### Bad

情境: 把 `name:`、`description:` 直接插進正文。

```markdown
# Purpose

name: pdf-processing
description: 處理 PDF。
```

結果: frontmatter 失效，metadata 不會被正確載入。

預期改法:

- 將 `name` 與 `description` 移到檔首的 YAML frontmatter，並補上成對的 `---`。
