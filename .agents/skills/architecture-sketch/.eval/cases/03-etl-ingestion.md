# Case 03 — multi-source ETL ingestion pipeline

## 埋藏陷阱
- **同質可擴充集合**：N 個來源連接器應以 `multiplicity:N`（堆疊）+ 一條 OCP design-constraint 表示，
  不是為每個來源各開一盒。
- **行為契約**：load 階段「冪等（idempotent）：重跑不重複寫入」是 behavioral-contract note。
- **單盒過載 → 下鑽（G2）**：Transform 階段同時承載 schema 對映、清洗、驗證、欄位推導等 >3 條約束，
  必須 `zoom:` 成自己的 C1 圖，而不是在一個盒上堆 5+ notes。
- **內部技術細節（G4）**：retry/back-off 等細節若無獨立約束，併入邊註記，不另開盒。

## 需求原文
設計一個資料匯入管線的架構部分。系統定期從多個外部來源（目前有 CRM、金流、客服三個，未來會再接更多）
抓資料，做轉換後寫入資料倉儲。轉換階段要做：來源 schema 對映、髒資料清洗、必填驗證、以及由既有欄位
推導新欄位——這塊邏輯最複雜、規則最多。寫入要可重跑而不產生重複資料。抓取失敗要能重試。
請產出架構 Sketch（plan YAML + SVG）；複雜的部分請用下鑽另開圖。

## 預期 Sketch 應展現
- 物理層：SourceConnector（`multiplicity:N`，掛 OCP design-constraint「Open for new sources」）、
  Extractor、Transform、Loader、Warehouse(datastore)，各綁 impl 單元。
- Loader 或 load 邊掛 **behavioral-contract**：「idempotent load，重跑不重複」。
- `Transform` 節點 `zoom:` 到一張 C1 圖，把 schema-map / clean / validate / derive 拆成各自承載
  約束的子元素（呼應 G2 把過載下鑽）。
- 至少兩張 sketch（C2 管線 + C1 Transform），跨圖以 id 對齊。

## 不應出現（常見誤拆）
- `CrmConnector` / `PaymentConnector` / `SupportConnector` 三個並列實作盒（應是一個 N 堆疊）。
- 在單一 `Transform` 盒上堆 5+ notes 而不下鑽。
- 為 retry/back-off 另開盒。
