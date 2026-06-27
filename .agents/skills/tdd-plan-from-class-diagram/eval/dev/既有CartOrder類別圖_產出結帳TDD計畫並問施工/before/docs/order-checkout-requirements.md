# Order Checkout — FR/NFR

Checkout 流程要把購物車內容轉成訂單，並透過 payment boundary 完成扣款。應用層不應直接依賴特定金流供應商實作，付款相關細節要留在 `PaymentGateway` 邊界之後。

## Functional Requirements

1. `OrderService` 負責把 `Cart` 與 `CartItem` 轉成 `Order`。
2. `CheckoutService` 負責協調訂單建立與付款，不應自己知道 `StripePaymentGateway` 的 provider 細節。
3. 付款請求需要透過 `PaymentRequest` 傳入 gateway，並回收 `PaymentResult`。
4. 付款失敗時，checkout 流程不得看起來像成功。

## Non-Functional Requirements

- 實作應以小迴圈 TDD 推進，不要一開始就寫大整合測試。
- 外部 payment boundary 需要可替換、可 fake。
- TDD 計畫要能看出先測哪個 seam、哪裡用 test double、哪裡留到 contract test。
