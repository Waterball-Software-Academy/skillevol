# expect — 既有order-checkout-tdd-plan_完成checkout與payment實作及測試

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply，不在這寫 file diff。

## Provenance

- 釘的行為：當 `before/` 已含 `docs/tdd/order-checkout.tdd-plan.md` 且 prompt 已授權施工時，`implement-tdd-plan` 必須直接把 checkout orchestration、payment boundary、failure handling 與對應 tests 一次落成完整 repo 終態。
- 為何存在：防止 skill 收到已確認的 TDD plan 後仍停在 re-planning、只做局部 slice、只寫 production code 不補 tests，或回頭修改上游 docs。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 依 `docs/tdd/order-checkout.tdd-plan.md` 寫出完整實作與 tests。
- MUST NOT 修改 `docs/tdd/order-checkout.tdd-plan.md`、`docs/architecture/order-checkout.class.mmd` 或 `docs/order-checkout-requirements.md`。
- MUST NOT 再 ask `implement?`、confirm-plan，或把責任退回「先補一份更細的實作計畫」。

Assistant message
- 1.0：明確回報已完成 `order-checkout` 的實作，點出 `CheckoutService`、payment boundary 類別與對應 tests 已落地，並說明 checkout 仍維持 provider-agnostic、failure path 已被測試保護。
- 0.7：主要實作已完成，但摘要漏掉 tests、boundary contract 或 failure path 其中一項。
- 0.3：只回報完成部分 slice，或只說改了 code 沒說 tests 與 boundary/failure 落地。
- 0.0：重問是否施工、停在 re-planning，或承認只完成部分實作。

breakpoint：done
