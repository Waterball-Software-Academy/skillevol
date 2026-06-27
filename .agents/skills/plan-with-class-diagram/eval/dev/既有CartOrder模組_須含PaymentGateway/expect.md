# expect — 既有CartOrder模組_須含PaymentGateway

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 改採 semantic file oracle：同目錄 `after/` 只保留為歷史 artifact，不再作唯一合法終態。合法 file outcome 是新增 `docs/architecture/order-checkout.class.mmd`，且補圖階段不得改 `src/`。

## Provenance

- 釘的行為：改動覆蓋完整性 — 類別圖 MUST 含 `PaymentGateway`（介面）與 checkout 協調者，並連到 Cart / Order 協調鏈。
- 為何存在：防止 skill 只畫 OrderService 小改動而漏掉 payment 與 checkout 核心新增，同時避免把 `StripePaymentGateway`、`PaymentResult`、`payment` namespace 釘成唯一答案。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/order-checkout.class.mmd`。
- MUST NOT 修改 `src/main/java/` 下任何檔案。

Assistant message
- 1.0：`docs/architecture/order-checkout.class.mmd` 中的 Mermaid 圖含 `PaymentGateway`（<<interface>> 或等效）、checkout 協調者（如 `CheckoutService` 或等價），且與 `Cart`、`Order` / `OrderService` 有清晰 depends-on 或協調關係；ASK(confirm-diagram)。實作名稱、付款結果 type 與 package 名可不同，但不得把 prompt 已明示的 `PaymentGateway` 介面改名或漏掉。
- 0.7：有 `PaymentGateway` 但缺 checkout 協調者，或關係不完整。
- 0.3：完全未出現 `PaymentGateway` 或 payment 邊界。
- 0.0：無 Mermaid 類別圖，或把 `PaymentGateway` 改成其他名稱。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 下任何檔案。

Assistant message
- 1.0：確認後 ASK(implement?)。
- 0.0：未問 implement? 或 Turn 1 圖不含 `PaymentGateway`。

breakpoint：ASK(implement?)

## Cross-turn

gates（forbidden-before）
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。
