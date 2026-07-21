# shared expect — aibdd-flows-specify 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準（local override）。

本檔不得宣告 `## Hidden oracle metadata`。hidden oracle metadata 只允許出現在 unit-local `expect.md`，正式 schema 以 `rules/hidden-oracle-metadata.md` 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：aibdd-flows-specify（把 plan package 的 pending 需求萃取成 api-wise action、編織 UAT flow 落成 `.activity` 與 rule-less `.feature` 骨架的 spec 建模 skill）。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 user-prompt；互動斷點（若有）由 responder 依 `user.md` 代答。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` ↔ 跑完的 fs）。

## Tool calls（橫切）

決定性 invariant：

- 產物一律落在 sandbox CWD 內；不得讀寫 CWD 之外的任何路徑。
- 向使用者提問只能經由 clarify 類互動斷點（由 responder 代答）；不得假裝使用者已回答而逕自往下。

## File diff（橫切）

決定性 invariant：

- 除該 unit 允許的產物（activities、features、reports 下的 impact-matrix.yml 等 unit-local expect 與 after/ 所示範圍）外，其餘檔案不得變動；fixture 不可污染：`.aibdd/`、`.specformula/`、`.agents/`（依賴 skill 目錄）、`.claude/` 一律不得改寫或刪除。plan spec.md 僅允許在合法 clarify 拍板後於澄清紀錄區 append-only 追寫（其他區段不得動；after/ 比對忽略澄清區）；不得就規格已明文事項發問後追寫。
- 產出的 `.activity` 必須是合法 DSL：能通過 aibdd-form-activity 的語法驗證（decoder 可解析、Actor 宣告與引用一致、圖可自 INITIAL 走到 FINAL）。
- 產出的 `.feature` 骨架必須 rule-less：只有 `# @ignore - ...` 註解行、`@ignore` 標籤行、`Feature: <業務意圖標題>` 三行；不得含 Rule、Background、Scenario、Examples 或 Step。
- 產物的相對目錄位置必須與 golden 一致（`.activity` 在所屬 package 的 activities/ 下、`.feature` 在 features/ 下、impact matrix 在 plan 的 reports/ 下）；檔名不要求與 golden 逐字相同（flows-specify 對命名有數種合理可能），但須符合專案規格語系 slug 命名規則，且能與 golden 檔案一一語意對應，數量與對應關係不得缺漏。

語意 rubric（after/ 比對，橫切）：

- 1.0：每個 golden 檔案都能找到語意等價的對應產物（內容語意等價、目錄正確），無多餘孤兒產物
- 0.7：對應齊全但個別檔案有不影響語意主張的偏差（如措辭、順序）
- 0.3：有 golden 檔案找不到對應產物，或出現大量未經授權的多餘產物
- 0.0：目錄位置錯置、產物缺漏過半，或以改寫 fixture 的方式偽造終態

## Assistant message（橫切）

語意 rubric：

- 1.0：依 SOP 逐步明示步驟編號推進，結案時具體列出本批產物與 impact 處置
- 0.7：有推進與結案說明，但步驟標示或產物清單不完整
- 0.3：只有籠統的完成宣稱，看不出 SOP 步驟軌跡
- 0.0：宣稱與實際 fs 終態不符
