# expect — 既有CartOrder模組_產出結帳類別圖並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 改採 semantic file oracle：同目錄 `after/` 只保留為歷史 artifact，不再作唯一合法終態。合法 file outcome 是新增 `docs/architecture/order-checkout.class.mmd`，且 `src/` 與既有檔案保持不變。

## Provenance

- 釘的行為：需求已清楚（Cart → Order 結帳 + PaymentGateway）時，skill 須先產出 cover 改動的 Mermaid class diagram，經 confirm-diagram 後才問 implement?；不得先改 code。
- 為何存在：這是 plan-with-class-diagram 的 happy path semantic oracle，防止跳過架構圖、漏改動節點、或未確認就實作，同時避免把單一 golden mmd 反教回 skill。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/order-checkout.class.mmd`。
- MUST NOT 修改 `src/main/java/` 下任何檔案。
- MUST NOT 在 Turn 1 delegate 實作型 skill 或直接 Write 新 Java class。

Assistant message
- 1.0：回報已寫出 `docs/architecture/order-checkout.class.mmd`；圖含有效 Mermaid `classDiagram`；namespace 區分 cart / order / payment（或等效 package 分組）；節點語意上 cover `Cart`、`Order`、既有 `OrderService`、結帳協調者（如 `CheckoutService` 或等價）、`PaymentGateway` 介面與至少一個具名實作；說明確認前不會動 source code；以 ASK(confirm-diagram) 結束。
- 0.7：圖大致正確但漏一個次要節點（如 `CartItem`、付款結果 type）或 package 分組不完整。
- 0.3：有 Mermaid 但缺結帳協調鏈或 `PaymentGateway` 邊界，或未請使用者確認。
- 0.0：無 Mermaid 類別圖，或 Turn 1 已開始實作/改檔。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 下任何檔案（eval 不驗實作階段，但仍須守先圖後碼 gate）。
- MUST NOT 修改 `docs/architecture/order-checkout.class.mmd` 以外的檔案。

Assistant message
- 1.0：收到確認後，明確問「要不要按此圖施工實作？」（或等價措辭），不自行開始實作。
- 0.7：有問施工意願，但未呼應使用者已確認架構圖。
- 0.3：未問施工意願，或暗示將直接開始寫 code 而未詢問。
- 0.0：跳過確認結論，直接實作或改檔。

breakpoint：ASK(implement?)，eval 在此終止，不要求 ANSWER(implement?)。

## Cross-turn

order（happens-before）
- ASK(confirm-diagram) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/main/java/` 的 WRITE 不得出現在 ANSWER(confirm-diagram) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。

liveness
- ASK(confirm-diagram) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。
