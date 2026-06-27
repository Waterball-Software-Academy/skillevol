# expect — 既有CartOrder類別圖_產出結帳TDD計畫並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；file 終態由同層 `after/` imply。此 unit 的 `after/` 釘住唯一合法 file outcome：新增 `docs/tdd/order-checkout.tdd-plan.md`，且不得改動輸入 Mermaid 與需求檔。

## Provenance

- 釘的行為：在 checkout 類別圖與需求已明確時，skill 須先產 cover `OrderService`、`CheckoutService`、`PaymentGateway` 的 TDD 計畫，經 confirm-tdd-plan 後才問 implement。
- 為何存在：這是 `tdd-plan-from-class-diagram` 的 happy path golden，防止只給一般待辦清單、漏掉 payment boundary、或未確認就問施工。

## Run

### Turn 1 — 結束方式：ASK(confirm-tdd-plan)

Tool calls
- MUST 寫出 `docs/tdd/order-checkout.tdd-plan.md`。
- MUST NOT 修改 `docs/architecture/order-checkout.class.mmd` 或 `docs/order-checkout-requirements.md`。
- MUST NOT 在 Turn 1 新增 source 或 test 檔。

Assistant message
- 1.0：回報已寫出 `docs/tdd/order-checkout.tdd-plan.md`；計畫含 `OrderService`、`CheckoutService`、`PaymentGateway` / `StripePaymentGateway` 對應的切片，且明示 fake/spy 或 contract test 策略；說明確認前不會動 source/test；以 ASK(confirm-tdd-plan) 結束。
- 0.7：主要切片正確，但漏掉 concrete gateway contract slice 或 test double 策略其一。
- 0.3：有 TDD 計畫檔，但只列「先寫單元測試、再做整合測試」之類一般口號，未對齊圖上的 checkout/payment 邊界。
- 0.0：無 TDD 計畫檔、Turn 1 已開始寫測試/實作，或未 ASK(confirm-tdd-plan)。

breakpoint：ASK(confirm-tdd-plan)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `docs/architecture/order-checkout.class.mmd`、`docs/order-checkout-requirements.md` 或 `docs/tdd/order-checkout.tdd-plan.md` 以外的檔案。
- MUST NOT 新增 source 或 test 檔。

Assistant message
- 1.0：收到確認後，明確問是否按此 TDD 計畫開始施工；不自行開始寫測試或 source code。
- 0.7：有問施工意願，但未呼應使用者已確認 TDD 計畫。
- 0.3：未問施工意願。
- 0.0：跳過確認或 Turn 1/2 已改 code。

breakpoint：ASK(implement?)，eval 在此終止。

## Cross-turn

order（happens-before）
- ASK(confirm-tdd-plan) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/`、`test/`、`docs/architecture/order-checkout.class.mmd` 或 `docs/order-checkout-requirements.md` 的 WRITE 不得出現在 ANSWER(confirm-tdd-plan) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-tdd-plan) 之前。

liveness
- ASK(confirm-tdd-plan) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。
