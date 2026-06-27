# Case 04 — checkout / payments flow

## 埋藏陷阱
- **第三方 + 廠商可換**：外部金流商畫成 `kind: external` + `«3rd»`，掛 **design-constraint**
  「Open for different PSP vendor」（OCP）——不是把 Stripe 細節攤成多盒。
- **行為契約**：「以 idempotency key 防重複扣款」是 behavioral-contract note。
- **範圍界線**：退款（refunds）這版不做 → scope-bound note。
- **參照範例**：「對帳/收據格式參考某成熟產品」是 reference-exemplar note，不是實作盒。

## 需求原文
設計一個結帳付款流程的架構部分。使用者送出訂單後，系統建立一筆付款並交給外部金流商收款，成功後
標記訂單已付並寄出收據。目前接 Stripe，但合約要求日後能換成別家金流商，所以介接要能換。網路重試
或使用者重複點擊不能造成重複扣款。退款與部分退款這一版先不做。收據與對帳報表的格式希望比照成熟
電商的做法。請產出架構 Sketch（plan YAML + SVG）。

## 預期 Sketch 應展現
- 物理層：CheckoutService、PaymentGateway(port)、OrderStore、ReceiptSender；外部 PSP 節點
  `external`+`«3rd»`；各綁 impl 單元。
- PSP/Gateway 掛 **design-constraint**：「Open for different PSP vendor」（OCP，可標 ★）。
- 付款建立節點/邊掛 **behavioral-contract**：「idempotency key → 不重複扣款」。
- **scope-bound note**：「退款 / 部分退款 MVP-out」。
- **reference-exemplar note**：「收據 / 對帳格式比照成熟電商」。
- 單一 focus（如「下單→收款→標記已付→收據」），雙層齊備。

## 不應出現（常見誤拆）
- 把 Stripe API 攤成 `CreatePaymentIntent` / `ConfirmIntent` / `Webhook` 等方法級盒。
- `RefundService` 實作盒。
- 廠商可換性被忽略（無 port、無 OCP note），直接把 Stripe 綁死成核心盒。
