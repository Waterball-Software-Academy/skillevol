# 修改選擇與簡化規則

本檔規範 `skillevol-loop` 如何根據測試報告選擇最小修改。測試報告指出的違規位置，在工作計畫中可記為 `provenance`；負責執行修改的 skill，在工作計畫中可記為 `mutator`。

## Rule 1 — 修改必須來自報告指出的違規位置

- 每輪修改都必須能追溯到測試報告指出的檔案、步驟、規則、樣板、委派輸入、工作計畫欄位、回報欄位或產物差異。
- 若報告沒有指出違規位置，先補強測試報告或評分準則，不得把「看起來可以更完整」當作修改理由。
- 工作計畫可以把違規位置記為 `failure provenance`，但對使用者回報時應說「報告指出哪裡出錯」。

### Good

情境: 測試報告指出 `SOP Phase 2 step 9` 沒有停下來詢問使用者是否開始修改。

```
違規位置: SKILL.md Phase 2 step 9
失敗類型: SOP failure
要委派的修改 skill: /skillevol-form-sop
修改範圍: 只調整 Phase 2 的使用者確認步驟
```

結果: 修改範圍由報告證據決定，不會順手重寫整份 skill。

### Bad

情境: 測試報告只說 unit fail。

```
我順手新增 rules、templates、Sub-SOP，讓 skill 更完整。
```

結果: 修改無法追溯到失敗證據，容易增加 instruction bloat。

預期改法:

- 先補強測試報告或 expect，讓報告指出違規位置，再選擇修改。

## Rule 2 — 每輪只選一個修改層級、一個修改 skill、一個範圍

- 每輪修改決策必須包含修改層級、要委派的修改 skill、實際委派對象、修改範圍、預期改善與下一個驗證點。
- 同一輪不得同時改 SOP、RuleFile、TemplateFile 與 Sub-SOP，除非測試報告指出同一個不可拆分的產物合約必須同時更新。
- 工作計畫與 final report 必須分開記錄「判斷上該用哪個修改 skill」與「實際委派了哪個 skill」。

### Good

情境: 測試報告指出 working-plan 沒記錄實際委派對象。

```
修改層級: LV2
要委派的修改 skill: /skillevol-derive-template
實際委派對象: /skillevol-derive-template
修改範圍: templates/working-plan.template.md 的 Current Decision 與 Iterations 欄位
```

結果: 實際委派對象與選擇理由可 audit。

### Bad

情境: 同一 failure。

```
chosen mutation: 改一下 loop 文件
```

結果: 沒有層級、修改 skill、實際委派對象與範圍，後續無法驗證是否真的照判斷修改。

預期改法:

- 補齊修改層級、要委派的修改 skill、實際委派對象、修改範圍與下一個驗證點。

## Rule 3 — 主流程缺口先改 SKILL.md

- LV1 指 frontmatter、Purpose 與主 SOP；它負責觸發邊界、存在理由與主流程。
- 若失敗是缺少階段、確認點、分支、回到哪一步、停止條件或委派動作，優先修主 SOP。
- LV1 不是把所有細節塞回 `SKILL.md`；若細節超過三條無序規定，應改放 RuleFile 或 TemplateFile。

### Good

情境: 測試報告指出新 skill 無驗證集合時仍先寫 skill 正文。

```
修改層級: LV1
要委派的修改 skill: /skillevol-form-sop
修改範圍: Phase 0 的入口判斷與 Phase 0B 的先建驗證集合分支
```

結果: 缺的是主流程，因此先修 SOP，不先新增 rules。

### Bad

情境: 同一 failure。

```
新增 rules/eval-bootstrap.md，但 SKILL.md 仍沒有 Phase 0B。
```

結果: 規則存在但主流程不會執行，failure 仍可能發生。

預期改法:

- 先把「先建立驗證集合，不寫 skill 正文」分支寫進主 SOP；只有分支已存在但細部判準過長時，才抽 RuleFile。

## Rule 4 — 規則檔與樣板檔只承載當步需要的細節

- RuleFile 用於無序、可逐條驗收的規則。
- TemplateFile 用於固定產物骨架。
- RuleFile 與 TemplateFile 必須由某個 SOP step 消費；不得成為未被主流程引用的資料倉庫。
- 有順序依賴的程序不屬於 RuleFile；行為規則不屬於 TemplateFile。

### Good

情境: SOP 已有「根據報告選擇本輪修改」步驟，但失敗顯示它常把固定報告格式誤判成一般規則問題。

```
修改層級: LV2
要委派的修改 skill: /skillevol-derive-rules
修改範圍: rules/mutation-selection.md 中失敗類型與修改 skill 對應判準
```

結果: 主流程保持簡潔，細部分類規則掛回對應 think step。

### Bad

情境: 需要的是固定 final report 章節。

```
把 final report 的章節骨架寫進 rules/final-report.md。
```

結果: 產物骨架被放進 RuleFile，消費者不知道要渲染哪個固定形狀。

預期改法:

- 固定章節骨架放進 `templates/final-report.template.md`；SOP step 只 reference template。

## Rule 5 — 只有有序子程序才抽成 Sub-SOP

- Sub-SOP 用於有順序、有前後依賴、可獨立呼叫的子程序。
- 若內容只是無序禁止事項，使用 RuleFile；若內容只是固定產物骨架，使用 TemplateFile。
- 抽 Sub-SOP 的目的，是讓主 SOP 回到流程總覽，而不是讓目錄看起來更完整。

### Good

情境: 多個測試失敗都指向同一段「讀報告、分類失敗、選修改 skill、寫本輪紀錄」的有序程序，且主 SOP 因此過長。

```
修改層級: LV3
要委派的修改 skill: /skillevol-derive-subsop
修改範圍: mutation-planning/SOP.md
預期改善: 主 SOP Phase 3 只保留呼叫子程序
```

結果: 有序子程序被封裝，主 SOP 更容易掃讀。

### Bad

情境: 只有五條無序禁令。

```
新增 mutation-guard/SOP.md，把五條禁令編號成子流程。
```

結果: 把規則型內容誤抽成 Sub-SOP，增加不必要層級。

預期改法:

- 將無序禁令寫成 RuleFile，並由對應 SOP step reference。

## Rule 6 — 失敗類型必須對應正確的修改 skill

- 觸發時機錯誤時，使用 `/skillevol-form-frontmatter`。
- Purpose 說不清存在理由或適用時機時，使用 `/skillevol-form-purpose`。
- 主流程缺階段、分支、確認點或停止條件時，使用 `/skillevol-form-sop`。
- 規則內容錯誤或掛錯步驟時，使用 `/skillevol-derive-rules` 或 `/skillevol-form-rule-file`。
- 固定產物骨架錯誤時，使用 `/skillevol-derive-template` 或 `/skillevol-form-template-file`。
- 有序子程序錯誤時，使用 `/skillevol-derive-subsop` 或 `/skillevol-form-subsop`。
- 自動化腳本錯誤時，使用 `/skillevol-derive-script` 或 `/skillevol-form-script`。
- 測試本身錯誤時，停止並回到 `/skillevol-define-evals` 或 `/skillevol-form-eval`。

### Good

情境: 報告指出 `templates/working-plan.template.md` 缺少 `active gate` 欄位。

```
失敗類型: Template failure
要委派的修改 skill: /skillevol-form-template-file
修改範圍: working-plan template 的 Current Decision 區塊
```

結果: 修改落在真正的產物骨架，不會改錯 SOP。

### Bad

情境: 同一 failure。

```
失敗類型: SOP failure
要委派的修改 skill: /skillevol-form-sop
修改範圍: 在 SOP 補一句「working-plan 要完整」
```

結果: SOP 增加提醒，但 template 仍缺欄位，測試仍會 fail。

預期改法:

- 依報告指出的違規位置，直接修模板欄位。

## Rule 7 — 通過測試時仍要刪掉沒有行為價值的文字

- 修改不只允許新增，也允許刪除、重組與移動。
- 若文字與目前合法動作無關、與 RuleFile/TemplateFile/Sub-SOP 重複、或只是歷史脈絡，應刪除。
- 判準不是「這段是否有點道理」，而是「留下它是否讓下一次執行更容易正確通過測試」。

### Good

情境: 已把工作計畫寫入規則抽到 `rules/working-plan-state.md`，但 SKILL.md 還留著整段長流程待辦原文。

```
action: 刪除 SKILL.md 的重複長段，SOP 只 reference rules/working-plan-state.md。
```

結果: 單一規則來源清楚，主 SOP 更容易掃讀。

### Bad

情境: 抽出 RuleFile 後保留同一批規則。

```
SKILL.md 與 rules/working-plan-state.md 同時描述 TODO Tier 0/Tier 1 的完整規定。
```

結果: 雙重 SSOT，未來兩邊可能衝突。

預期改法:

- 保留 RuleFile 版本，SKILL.md 僅留下 reference 與必要主流程指令。

## Rule 8 — 升級修改層級必須寫出下一個可驗證假設

- 從主 SOP 升到 RuleFile 或 TemplateFile，或從 RuleFile/TemplateFile 升到 Sub-SOP 時，工作計畫必須記錄升級原因、預期改善的測試、以及若仍失敗要檢查的下一個假設。
- 沒有下一個可驗證假設的升級，通常只是把複雜度往外搬。

### Good

情境: 主 SOP 修正後，final report 測試仍 fail，報告指向固定章節缺失。

```
升級: LV1 to LV2
要委派的修改 skill: /skillevol-form-template-file
預期改善: final report 包含 Mutation Audit 與 Remaining Risk
若仍失敗: 檢查 template placeholder mapping
```

結果: 升級有明確原因與下一個驗證點。

### Bad

情境: 主 SOP 修正後還有 failure。

```
我先把所有可能規則都抽出去。
```

結果: 升級沒有對應假設，修改面擴大且不可驗證。

預期改法:

- 只升級與報告違規位置對應的部位，並在工作計畫寫出預期改善與下一個驗證點。
