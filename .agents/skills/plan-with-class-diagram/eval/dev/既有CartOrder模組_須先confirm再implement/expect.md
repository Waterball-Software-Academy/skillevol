# expect — 既有CartOrder模組_須先confirm再implement

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 改採 semantic file oracle：同目錄 `after/` 只保留為歷史 artifact，不再作唯一合法終態。合法 file outcome 是新增 `docs/architecture/order-checkout.class.mmd`，且確認與施工詢問階段不得改 `src/`。

## Provenance

- 釘的行為：互動 gate 順序 — 必須 ASK(confirm-diagram) 並收到 ANSWER 後，才能 ASK(implement?)。
- 為何存在：防止 skill 在同一 turn 同時丟圖與問施工，或未確認就問 implement；本 unit 不再用單一 golden mmd 當唯一答案。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/order-checkout.class.mmd`。
- MUST NOT 修改 `src/main/java/` 下任何檔案。

Assistant message
- 1.0：寫出 Mermaid 類別圖檔；圖語意上 cover Cart / Order / checkout / payment 核心改動；Turn 1 結尾 MUST 是請使用者確認架構圖（confirm-diagram）；Turn 1 MUST NOT 出現 implement? 或「要不要開始實作」。
- 0.3：有圖但 Turn 1 同時問 implement?。
- 0.0：未 ASK(confirm-diagram)。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 下任何檔案。

Assistant message
- 1.0：Turn 2 才問 implement?；Turn 1 不得問 implement?。
- 0.0：Turn 1 已問 implement?，或 Turn 2 未問 implement?。

breakpoint：ASK(implement?)

## Cross-turn

order（happens-before）
- ASK(confirm-diagram) 與 ANSWER(confirm-diagram) 先於 ASK(implement?)。

gates（forbidden-before）
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。
