# shared expect — plan-with-class-diagram 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：`plan-with-class-diagram`（實作前先產架構類別圖的 orchestrator skill）。
- inputs：`before/` 即 CWD 的 fs snapshot，加上 `prompt.md` 一句 context-free user prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` 到跑完 fs 的整體差異）。
- 若 unit 提供 `after/`，`after/` 必須包含規劃階段產出的 Mermaid 類別圖檔，並作為終態檔案 oracle。
- 若 unit 不提供 `after/`，則由 unit-local `expect.md` 明示允許的 diagram path 與語意 rubric；judge 不得把缺少 `after/` 當成失敗。
- 所有 unit 的合法 file diff 都只限新增或更新 `docs/architecture/*.class.mmd`；`src/`、需求文件、測試與設定檔必須語意等同 `before/`，除非 unit-local `expect.md` 明示覆寫。

## 先圖後碼 gate（橫切）

決定性 invariant：
- 在收到使用者對架構圖的確認（ANSWER(confirm-diagram)）之前，MUST NOT 修改任何原始碼、測試、需求文件或設定檔。
- Turn 1 MUST 寫出 Mermaid 類別圖檔至 `docs/architecture/*.class.mmd`。
- MUST NOT 在 Turn 1 直接開始實作、寫測試或 delegate 實作型 skill。

語意 rubric：
- 1.0：明確先產架構類別圖檔並請使用者確認，且說明確認前不會動 source code。
- 0.7：有產圖並暫停，但未清楚說明先確認再實作的 gate。
- 0.3：有提到架構，但同 turn 已開始改檔或寫實作步驟。
- 0.0：跳過類別圖，直接實作或只列文字步驟而無 Mermaid 圖。

## 改動覆蓋完整性（橫切）

語意 rubric（Mermaid class diagram）：
- 1.0：圖上節點與關係 cover 本次需求所有新增/修改的架構部分（含協調者、介面、被依賴的既有 class 若會被改動）；用 namespace 或等效分組表達 package 邊界。
- 0.7：主要新增 class 有入圖，但漏一個次要改動或 package 分組不清。
- 0.3：只畫 1-2 個 class，明顯漏掉 Payment、Checkout 或 Order 協調鏈中的關鍵節點。
- 0.0：圖與需求無關，或只列 bullet 而無 Mermaid classDiagram。

## 類別圖品質（橫切）

語意 rubric：
- 1.0：Mermaid `classDiagram` 語法有效；只含架構有意義的 class；有 namespace 分 package；關係精簡（implements / owns / depends-on 或等效）；非 DTO/helper inventory。
- 0.7：語法有效且精簡，但 namespace 或關係略混亂。
- 0.3：可渲染但過度列舉 getter/setter 或無關 class。
- 0.0：非 Mermaid 類別圖，或無法視為架構閱讀工具。

## 確認後才問施工（橫切）

決定性 invariant：
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。

語意 rubric：
- 1.0：使用者確認架構圖後，才問「要不要按此圖施工實作？」類問題。
- 0.7：有問施工意願，但時序或措辭含糊。
- 0.3：未確認就問是否實作，或未問施工意願就宣稱開始實作。
- 0.0：確認與施工 gate 皆缺失。

## Assistant message（橫切）

語意 rubric：
- 1.0：清楚呈現 Mermaid 類別圖檔路徑、改動摘要、確認 gate、施工詢問（若已到該步）。
- 0.7：圖與流程大致正確，但缺少改動摘要或 gate 說明之一。
- 0.3：只有口頭描述架構，圖不完整或缺少互動 gate。
- 0.0：未回應架構規劃責任，直接進入實作討論。

## File diff（橫切）

決定性 invariant：
- 若 unit 提供 `after/`，跑完至 ASK(implement?) 時，終態 fs 必須語意等同同 unit 的 `after/`。
- 若 unit 不提供 `after/`，跑完至 ASK(implement?) 時，終態 fs 必須符合 unit-local `expect.md` 明示的 diagram path、allowed diff 與 forbidden diff。
- file outcome 必須只包含 `docs/architecture/*.class.mmd` 類別圖檔；不得新增、刪除或修改 `src/`、測試、設定檔或需求文件，除非 unit-local `expect.md` 明示覆寫。
- 本 skill 的 eval 不驗實作階段；ASK(implement?) 前後都不得產生 Java 實作檔。
