# shared expect — tdd-plan-from-class-diagram 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：`tdd-plan-from-class-diagram`（從 Mermaid 類別圖推導 TDD 計畫的 planning skill）。
- inputs：`before/` 即 CWD 的 fs snapshot，加上 `prompt.md` 一句 context-free user prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` 到跑完 fs 的整體差異）。
- 若 unit 提供 `after/`，`after/` 必須包含規劃階段產出的 TDD plan 檔，並作為終態檔案 oracle。
- 若 unit 不提供 `after/`，則由 unit-local `expect.md` 明示允許的 plan path 與語意 rubric；judge 不得把缺少 `after/` 當成失敗。
- 所有 unit 的合法 file diff 都只限新增或更新 `docs/tdd/*.tdd-plan.md`；`src/`、`test/`、需求文件、Mermaid 圖稿與設定檔必須語意等同 `before/`，除非 unit-local `expect.md` 明示覆寫。

## 先計畫後施工 gate（橫切）

決定性 invariant：
- 在收到使用者對 TDD 計畫的確認（ANSWER(confirm-tdd-plan)）之前，MUST NOT 修改任何原始碼、測試、需求文件、Mermaid 圖稿或設定檔。
- Turn 1 MUST 寫出 TDD 計畫檔至 `docs/tdd/*.tdd-plan.md`。
- MUST NOT 在 Turn 1 直接開始實作、寫測試或 delegate 實作型 skill。

語意 rubric：
- 1.0：明確先產 TDD 計畫檔並請使用者確認，且說明確認前不會動 source code 或測試。
- 0.7：有產計畫並暫停，但未清楚說明先確認再施工的 gate。
- 0.3：有提到 TDD，但同 turn 已開始寫測試、安排直接實作，或沒把計畫落成檔案。
- 0.0：跳過 TDD 計畫，直接進入測試或實作。

## 切片品質（橫切）

語意 rubric（TDD plan）：
- 1.0：計畫明確把類別圖拆成可執行的 red-green slices；每個 slice 都含 user outcome、red test、collaborators、test doubles、implementation target 與 done evidence；順序先小迴圈再外部邊界。
- 0.7：主要 slices 合理，但漏一個欄位、少一個關鍵 seam，或切片順序略大。
- 0.3：只有一般工作清單或測試口號，缺少 red test/test doubles/implementation target 三者中的兩項以上。
- 0.0：內容與類別圖脫節，或根本不是 TDD 計畫。

## 外部邊界隔離（橫切）

語意 rubric：
- 1.0：若圖上有 interface 與具名 implementation，計畫清楚先用 fake、spy 或 contract test 隔離該邊界，再決定何時碰 concrete impl。
- 0.7：有提到 fake 或 contract test，但沒有明說對應哪個邊界。
- 0.3：把 concrete impl 直接排在第一個 slice，或完全不提 test double 策略。
- 0.0：外部邊界直接和 domain/application slice 混成單一大整合測試。

## 確認後才問施工（橫切）

決定性 invariant：
- ASK(implement?) 不得出現在 ANSWER(confirm-tdd-plan) 之前。

語意 rubric：
- 1.0：使用者確認 TDD 計畫後，才問「要不要按此計畫施工？」類問題。
- 0.7：有問施工意願，但時序或措辭含糊。
- 0.3：未確認就問是否實作，或未問施工意願就暗示開始動手。
- 0.0：確認與施工 gate 皆缺失。

## Assistant message（橫切）

語意 rubric：
- 1.0：清楚呈現 TDD plan 檔路徑、切片摘要、確認 gate、施工詢問（若已到該步）。
- 0.7：計畫與流程大致正確，但缺少切片摘要或 gate 說明之一。
- 0.3：只有口頭說會做 TDD，沒有落檔或沒有互動 gate。
- 0.0：未回應 diagram-driven TDD planning 責任，直接討論實作細節。

## File diff（橫切）

決定性 invariant：
- 若 unit 提供 `after/`，跑完至 ASK(implement?) 時，終態 fs 必須語意等同同 unit 的 `after/`。
- 若 unit 不提供 `after/`，跑完至 ASK(implement?) 時，終態 fs 必須符合 unit-local `expect.md` 明示的 plan path、allowed diff 與 forbidden diff。
- file outcome 必須只包含 `docs/tdd/*.tdd-plan.md` 計畫檔；不得新增、刪除或修改 `src/`、`test/`、`docs/architecture/*.class.mmd`、需求文件或設定檔，除非 unit-local `expect.md` 明示覆寫。
- 本 skill 的 eval 不驗實作階段；ASK(implement?) 前後都不得產生 source 或 test 檔。
