# TDD Plan — order-checkout

## Scope

- source diagram: `docs/architecture/order-checkout.class.mmd`
- goal: 讓 checkout 流程能先把 cart 轉成 order，再透過 `PaymentGateway` 扣款，而不把 provider 細節洩漏進 `CheckoutService`。
- out of scope for this cycle: persistence、UI、retry policy。

## Test Strategy

- primary seams:
  - `OrderService.createFromCart(cart)`：純 cart-to-order mapping。
  - `CheckoutService.checkout(cart)`：協調 order 建立與付款。
  - `PaymentGateway` / `StripePaymentGateway`：payment boundary contract。
- doubles strategy: 先對 pure mapping 不用 doubles；進到 checkout orchestration 時改用 spy `OrderService` 與 fake `PaymentGateway`；`StripePaymentGateway` 留在獨立 contract slice，不讓前面 slices 直接依賴 concrete provider。

## Slice Order

### Slice 1 — build order from cart

- user outcome: cart 可以被轉成包含相同 user 與 items 的 `Order`。
- red test: `OrderService.createFromCart(cart)` 會回傳從輸入 cart 導出的 `Order`。
- collaborators: `OrderService`、`Cart`、`CartItem`、`Order`
- test doubles: none
- implementation target: `OrderService.createFromCart`
- done when: mapping test 轉綠，且尚未碰 payment 行為。

### Slice 2 — checkout orchestrates order creation and payment

- user outcome: checkout 會先建立 order，再對 gateway 發起一次扣款並回傳建立好的 `Order`。
- red test: `CheckoutService.checkout(cart)` 會呼叫 `OrderService`，接著呼叫 `PaymentGateway.charge(request)`，並回傳建立出的 `Order`。
- collaborators: `CheckoutService`、`OrderService`、`PaymentGateway`、`PaymentRequest`、`PaymentResult`
- test doubles: spy `OrderService`、fake `PaymentGateway`
- implementation target: `CheckoutService.checkout`
- done when: 協調順序與回傳值都被測試明確保護。

### Slice 3 — payment request mapping stays at the boundary

- user outcome: checkout 送給 gateway 的是 boundary 專用的 `PaymentRequest`，而不是直接把 cart internals 洩漏出去。
- red test: `CheckoutService` 傳給 `PaymentGateway` 的 request 內容能從 order/checkout context 推導出來。
- collaborators: `CheckoutService`、`PaymentGateway`、`PaymentRequest`
- test doubles: capturing fake `PaymentGateway`
- implementation target: `CheckoutService` 內的 request-building logic
- done when: request contract 已被測試釘住，且不需要 concrete provider 就能驗。

### Slice 4 — concrete gateway keeps the same contract

- user outcome: `StripePaymentGateway` 能在不改 checkout 測試的前提下實作同一個 `PaymentGateway` contract。
- red test: 一個 contract test 對 `PaymentGateway` implementation 驗 `charge(request) -> PaymentResult` 的共同語意。
- collaborators: `PaymentGateway`、`StripePaymentGateway`、`PaymentRequest`、`PaymentResult`
- test doubles: fake Stripe client 或測試用 adapter seam（若真實 SDK 尚未引入）
- implementation target: `StripePaymentGateway`
- done when: concrete gateway 可以被換到相同 contract assertions 下驗證。

### Slice 5 — failed charge cannot look like success

- user outcome: gateway 失敗時，checkout 不能看起來像成功完成付款。
- red test: 當 gateway 回傳 failure result 或丟出錯誤時，checkout 會暴露 failure outcome，而不是回傳已付款成功的 order。
- collaborators: `CheckoutService`、`PaymentGateway`、`PaymentResult`
- test doubles: failing fake `PaymentGateway`
- implementation target: checkout failure-handling path
- done when: failure branch 有獨立 regression test，且在 provider integration 前就已被保護。

## Regression Gate

- 每完成一個 slice 就 rerun 先前已綠的 slices。
- `CheckoutService` tests 必須保持 provider-agnostic，不直接依賴 `StripePaymentGateway`。
- 在開始實作前，至少要有 mapping、orchestration、boundary contract、failure handling 四類切片都被規劃到。

## Open Questions

- payment 失敗時應阻止 order 建立，還是允許建立 pending/unpaid order？
- `PaymentRequest` 是否需要 idempotency 欄位，還是先留到 concrete gateway slice 再加？
