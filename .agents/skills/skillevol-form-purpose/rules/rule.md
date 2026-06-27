# Purpose 規則

## Rule 1 — 只處理目標 skill 的 `# Purpose` 區塊

- 本 skill 只負責撰寫或改寫目標 SKILL.md 的 `# Purpose` 區塊。
- 除非使用者明確授權，否則不順手改 `description`、`# Rules`、`# SOP`、其他 H1、或新增其他規則檔。
- 目標檔原本沒有 `# Purpose` 時，應插在 frontmatter 之後、其他 H1 之前。

### Good

情境: 使用者只要求補寫 Purpose。

在 frontmatter 後插入 `# Purpose` 與三到五句正文；`# Rules`、`# SOP` 與其他檔案保持不變。

結果: 變更範圍乾淨，只動到 Purpose 本身。

### Bad

情境: 使用者只說「補 Purpose」。

改寫 Purpose 時，順手重寫 `description`、更新 SOP，或新增與 Purpose 無關的檔案。

預期改法:

- 回到單一職責，只保留 `# Purpose` 區塊的變更。

## Rule 2 — Purpose 要用三到五句交代 Why、When、Pain、Skip cost

- Purpose 應在三到五句內交代這個 skill 為何存在、何時啟用、在解什麼痛點、以及缺少這段脈絡會有什麼後果。
- 四種語意應融成連貫段落，不必用 `Why:`、`When:` 這種小標硬拆。
- 讀完 Purpose 後，不看 SOP 也應大致知道這個 skill 的脈絡與啟用條件。

### Good

情境: 為 pdf-merge 類型 skill 撰寫 Purpose。

使用者常需要把多份 PDF 合成一份再分享或存檔，但手動合併容易漏頁、順序錯亂，或不知道該用哪個工具與輸出檔名。
本 skill 在對方已明確要合併 PDF 且檔案路徑與順序可取得時啟用，負責把輸入清單、頁序與輸出路徑交代清楚再執行合併。
若沒有這段脈絡，AI 容易在只有「處理 PDF」的模糊需求下誤做拆分或 OCR，或在缺檔、順序未確認時直接覆寫原檔。

結果: 三句內就涵蓋 Why、When、Pain、Skip cost。

### Bad

情境: Purpose 只寫空泛摘要。

這個 skill 用於處理相關任務，讓流程更完整。

結果: 看不出何時該啟用、在解什麼問題、也看不出少了它會怎樣。

預期改法:

- 補齊 Why、When、Pain、Skip cost，收斂成三到五句具體陳述。

## Rule 3 — Purpose 只寫脈絡，不寫 SOP、Rules 或行銷文案

- Purpose 是脈絡段落，不是執行步驟、工具指令、Required Inputs 或規則清單。
- 不要出現「步驟 1」「read 某檔」「呼叫某工具」這類執行細節。
- 不要使用「超強」「完整又好用」這類行銷形容詞；用可觀察的情境與後果取代。

### Good

情境: 描述一個 skill 的啟用脈絡。

本 skill 在上游規格已大致定稿、但開頭缺少 Why 與觸發脈絡時啟用，負責把這段背景補回 skill 開頭，避免後續 agent 只照 SOP 機械執行。

結果: 文字描述取捨與場景，不混入操作步驟。

### Bad

情境: 把 SOP 寫進 Purpose。

請依下方 SOP 執行：步驟 1 確認檔案，步驟 2 呼叫工具，步驟 3 回報結果。本 skill 是最強的解決方案。

結果: Purpose 失去角色邊界，和 SOP/行銷文案混在一起。

預期改法:

- 刪掉步驟、工具與宣傳語，只保留存在理由、時機、痛點與後果。

## Rule 4 — 風格要精簡、可掃讀，且與目標 skill 語言一致

- 使用陳述句與純文字排版，不用粗體、斜體、底線、emoji 或表格。
- 句子要短而明確；若一口氣塞太多資訊，優先拆句，不靠裝飾強調。
- Purpose 的語言應和目標 skill 主要語言一致；目標檔是繁體中文就寫繁中，目標檔是英文就寫英文。

### Good

情境: 目標 skill 正文是繁體中文。

Purpose 用三到五句繁體中文陳述句，沒有星號裝飾，也沒有表格。

結果: 在 Text Mode 下就能直接掃讀與編修。

### Bad

情境: 為了強調重點加入裝飾與跨語切換。

```markdown
# Purpose

**This skill is powerful and useful.**
| Why | When |
| --- | --- |
| ... | ... |
```

結果: 裝飾造成噪音，表格不適合 Purpose，且語言與全檔風格脫節。

預期改法:

- 改回與目標 skill 一致的語言，用三到五句純文字陳述，不用表格與裝飾。

## Rule 5 — 無法保守推斷時先確認，不要臆測 domain

- 可從目標檔、現有 description、相鄰 skill 或工作流位置保守推斷的內容，才寫進 Purpose。
- 若看完仍無法判斷主要啟用時機、核心痛點、或最常見誤觸發，先問使用者一句聚焦問題。
- few-shot 只學結構與篇幅，不複製範例領域用語到別的 skill。

### Good

情境: 看完目標檔後，仍不確定這個 skill 最常在哪個節點啟用。

先問: `這個 skill 主要在什麼時機觸發？最常解決的痛點是什麼？`

結果: 寫出的 Purpose 有根據，不會把不存在的 domain 細節塞進去。

### Bad

情境: 只因為看過 PDF 範例，就替其他 skill 寫入「合併 PDF」「漏頁」等描述。

結果: Purpose 沿用錯誤 domain，和目標 skill 不一致。

預期改法:

- 刪掉無法驗證的領域詞；必要時先問使用者，再依答案重寫。
