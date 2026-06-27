# expect — 既有CartOrder模組_Turn1禁止改src

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 改採 semantic file oracle：同目錄 `after/` 只保留為歷史 artifact，不再作唯一合法終態。合法 file outcome 是新增 `docs/architecture/order-checkout.class.mmd`，且 Turn 1/2 都不得改 `src/`。

## Provenance

- 釘的行為：先圖後碼 gate — Turn 1 絕對不得修改 `src/main/java/` 或新增 Java 實作檔。
- 為何存在：防止 skill 把「規劃」與「施工」混在同一 turn，或未確認就動 code。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/order-checkout.class.mmd`。
- MUST NOT Write、StrReplace 或任何方式修改 `src/main/java/` 下檔案。
- MUST NOT 新增 `CheckoutService.java`、`PaymentGateway.java` 等實作檔。

Assistant message
- 1.0：寫出 Mermaid class diagram 檔並 ASK(confirm-diagram)，且 Turn 1 完全未改 `src/`。
- 0.0：Turn 1 已建立或修改任何 Java 原始碼。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Assistant message
- 1.0：確認後才 ASK(implement?)；Turn 2 仍不得改 `src/`（本 eval 不驗實作），file outcome 仍只限 Mermaid 圖檔。
- 0.0：Turn 1 或 Turn 2 已改 `src/`。

breakpoint：ASK(implement?)

## Cross-turn

gates（forbidden-before）
- 任何 `src/main/java/` 變更不得出現在 ANSWER(confirm-diagram) 之前。
