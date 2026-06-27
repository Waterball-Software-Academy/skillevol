# expect — 既有CartOrder類別圖_須先confirm計畫再問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；file 終態由同層 `after/` imply。此 unit 的 `after/` 釘住唯一合法 file outcome：新增 `docs/tdd/order-checkout.tdd-plan.md`，且確認與施工詢問階段不得改動輸入檔。

## Provenance

- 釘的行為：互動 gate 順序 — 必須 ASK(confirm-tdd-plan) 並收到 ANSWER 後，才能 ASK(implement?)。
- 為何存在：防止 skill 在同一 turn 同時丟計畫與問施工，或未確認就問 implement。

## Run

### Turn 1 — 結束方式：ASK(confirm-tdd-plan)

Tool calls
- MUST 寫出 `docs/tdd/order-checkout.tdd-plan.md`。
- MUST NOT 修改 `docs/architecture/order-checkout.class.mmd` 或 `docs/order-checkout-requirements.md`。
- MUST NOT 在 Turn 1 新增 source 或 test 檔。

Assistant message
- 1.0：寫出 TDD 計畫檔；Turn 1 結尾 MUST 是請使用者確認 TDD 計畫（confirm-tdd-plan）；Turn 1 MUST NOT 出現 implement? 或「要不要開始施工」。
- 0.3：有計畫但 Turn 1 同時問 implement?。
- 0.0：未 ASK(confirm-tdd-plan)。

breakpoint：ASK(confirm-tdd-plan)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `docs/architecture/order-checkout.class.mmd` 或 `docs/order-checkout-requirements.md`。
- MUST NOT 新增 source 或 test 檔。

Assistant message
- 1.0：Turn 2 才問 implement?；Turn 1 不得問 implement?。
- 0.0：Turn 1 已問 implement?，或 Turn 2 未問 implement?。

breakpoint：ASK(implement?)

## Cross-turn

order（happens-before）
- ASK(confirm-tdd-plan) 與 ANSWER(confirm-tdd-plan) 先於 ASK(implement?)。

gates（forbidden-before）
- ASK(implement?) 不得出現在 ANSWER(confirm-tdd-plan) 之前。
