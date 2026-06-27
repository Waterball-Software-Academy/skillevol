# Mutation selection 與簡化規則

本檔規範 `skillevol-loop` 如何根據 eval failure provenance 選擇最低足夠的 mutation level，並避免把 skill 越改越胖。

## Rule 1 — mutation 必須對應到 failure provenance

- 每輪 mutation 都必須能追溯到一個或多個 eval failure provenance。provenance 可以指向 frontmatter、Purpose、SOP 步驟、RuleFile、TemplateFile、Sub-SOP、delegate payload 或產物形狀。
- 若找不到 provenance，先修 eval report 或 expect，不得把「看起來可以更完整」當作 mutation 理由。
- working-plan 與 final report 必須分開記錄 `chosen mutator` 與實際 `delegate target`。若本輪是 self-mutation，也要明寫 `self`，不得只寫成模糊的「LV1 SOP」。

### Good

情境: eval report 指出 `SOP step 4` 沒有委派 `/skillevol-derive-rules`，導致既有 RuleFile 沒被沿用。

```
failure provenance: SKILL.md SOP step 4
failure type: Delegation Failure
candidate mutation: /skillevol-form-sop
scope: 只調整該 SOP 步驟與 delegate payload
```

結果: mutation 範圍由 failure 證據決定。

### Bad

情境: eval report 只顯示某 unit fail。

```
我順手新增 rules/、templates/、三個 Sub-SOP，讓 skill 更完整。
```

結果: mutation 無法追溯到 failure，增加不必要結構。

預期改法:

- 先定位 failure provenance，再選單一最低足夠 mutation。

## Rule 2 — LV1 必須先於 LV2 與 LV3

- 只要 failure 能透過 frontmatter、Purpose 或主 SOP 解決，就不得升級到 RuleFile、TemplateFile 或 Sub-SOP。
- LV1 不是把所有細節塞進 `SKILL.md`；它只負責觸發邊界、存在理由與主流程。

### Good

情境: eval failure 來自 skill 沒有先檢查 eval oracle。

```
chosen level: LV1
chosen mutator: /skillevol-form-sop
reason: 缺少主流程步驟，不是缺少 RuleFile。
```

結果: 主流程缺口由 SOP 修正，沒有提前建立 rules/。

### Bad

情境: 同一個主流程缺口。

```
新增 rules/oracle-check.md，裡面寫一堆 gate 規則，但 SKILL.md SOP 仍沒有先檢查 eval。
```

結果: 細節檔存在，主流程仍不會執行該行為。

預期改法:

- 先把主流程步驟寫進 SOP；只有步驟存在但細部規則過多時，才抽 RuleFile。

## Rule 3 — LV2 只承載 SOP 某步必要的規則或產物骨架

- RuleFile 用於無序、可逐條驗收的原子規則。TemplateFile 用於固定產物骨架。兩者都必須掛回某個 SOP 步驟，且不得成為未被主流程引用的資料倉庫。
- 如果內容有順序依賴，不屬於 RuleFile；如果內容是行為規則，不屬於 TemplateFile。

### Good

情境: SOP 已有「think 選擇 mutation」步驟，但 eval 顯示它常把 template failure 誤判成 rule failure。

```
chosen level: LV2
chosen mutator: /skillevol-derive-rules
target step: mutation selection
rule content: RuleFile 與 TemplateFile 的判準差異
```

結果: SOP 保持主流程，細部判斷被掛到對應步驟。

### Bad

情境: 需要的是固定 final report 形狀。

```
把 final report 的章節骨架寫進 rules/final-report.md。
```

結果: RuleFile 被用來裝產物骨架，消費者不知道要逐字渲染哪個形狀。

預期改法:

- 固定產物骨架改放 TemplateFile，SOP 步驟改成依該 template 渲染。

## Rule 4 — LV3 只在 progressive disclosure 失效時使用

- Sub-SOP 只用於有序、有前後依賴、可獨立執行的子程序。它的目的，是讓主 SOP 回到 orchestration，而不是讓目錄看起來完整。
- 若只是無序規則，用 RuleFile；若只是固定 artifact skeleton，用 TemplateFile。

### Good

情境: 多個 eval failure 都指向同一段「讀 report、分類 failure、排序 mutation、寫 plan」的有序流程，且主 SOP 已因此過長。

```
chosen level: LV3
chosen mutator: /skillevol-derive-subsop
scope: 抽出 mutation-planning/SOP.md
reason: 該段是有序子程序，且已造成主 SOP progressive disclosure 失效。
```

結果: 主 SOP 只留下 invoke reference，子流程有自己的執行順序。

### Bad

情境: 只有五條無序禁止事項。

```
新增 escalation/SOP.md，把五條禁止事項寫成子流程。
```

結果: 把 rule-type 內容誤抽成 Sub-SOP，增加不必要層級。

預期改法:

- 將無序禁止事項放入 RuleFile，並由對應 SOP 步驟 reference。

## Rule 5 — 通過 eval 時仍要刪除沒有行為價值的 instruction bloat

- mutation 不只允許新增，也允許刪除與重組。若舊文字與目前合法 action set 無關、與 RuleFile/TemplateFile/Sub-SOP 重複、或只是在保留歷史脈絡，應刪除。
- 判準不是「這段文字是否有點道理」，而是「留下它是否讓 skill 更容易通過 eval 並正確執行」。

### Good

情境: 抽出 `rules/mutation-selection.md` 後，`SKILL.md` 還留著同樣的八條 mutation 判斷 bullet。

```
動作: 刪除 SKILL.md 中重複 bullet，SOP 步驟只保留 reference。
```

結果: instruction surface 變小，單一規則來源更清楚。

### Bad

情境: 已抽 RuleFile，但保留原內嵌規則。

```
SKILL.md 和 rules/mutation-selection.md 同時描述同一套 LV1/LV2/LV3 判斷。
```

結果: 未來修改會產生雙重 SSOT，agent 也可能讀到互相衝突的版本。

預期改法:

- 主 SOP 只留下指令與 RuleFile reference，移除重複細節。

## Rule 6 — 升級層級必須寫出下一個可驗證假設

- 每次從 LV1 升級到 LV2，或從 LV2 升級到 LV3，都必須在 working-plan 記錄升級原因、預期改善的 eval unit、以及若仍失敗要檢查的下一個假設。
- 沒有下一個可驗證假設的升級，通常只是把複雜度往外搬。

### Good

情境: LV1 SOP 修正後，`dev/template-output-shape` 仍 fail，provenance 指出 final report 缺固定章節。

```
escalation: LV1 to LV2
chosen mutator: /skillevol-derive-template
expected improvement: final report contains required sections
if still fail: inspect template placeholder mapping
```

結果: 升級有明確假設與驗證點。

### Bad

情境: LV1 修正後還有 failure。

```
我先把所有可能規則都抽出去，應該會更清楚。
```

結果: 升級沒有對應假設，容易擴大修改面。

預期改法:

- 只升級與 failure provenance 對應的部位，並寫出可驗證的下一個假設。
