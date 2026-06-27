# expect — 已有HttpServer啟動骨架_語意驗MiniWeb責任分工並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 不提供 `after/`：file outcome 以 semantic rubric 判定，不用單一 golden mmd 釘死唯一設計。唯一合法新增檔案是 `docs/architecture/miniweb-framework.class.mmd`。

## Provenance

- 釘的行為：在 JDK `HttpServer` 最小骨架與 FR/NFR 輸入上規劃 MiniWeb framework 時，skill 應從責任分工推導架構圖，而不是背出某一張固定 golden 圖稿。
- 為何存在：這是 semantic holdout；驗 `plan-with-class-diagram` 是否能在不靠 exact-after 的情況下，仍畫出 cover 路由、邊界 adapter、cross-cutting、dispatch 與 handler boundary 的 framework 類別圖。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/miniweb-framework.class.mmd`。
- MUST NOT 修改 `src/main/java/` 或 `docs/miniweb-requirements.md`。
- MUST NOT 在 Turn 1 新增 framework 原始碼目錄或 Java class。

Assistant message
- 1.0：回報已寫出 `docs/architecture/miniweb-framework.class.mmd`；檔內含有效 Mermaid `classDiagram`；圖上明確表達下列責任邊界：server lifecycle coordinator、JDK transport adapter、request/response abstraction、route matching/registry、application handler boundary、cross-cutting chain、dispatch coordinator；並標示與既有 `Bootstrap` 的關係、說明確認前不動 source code、以 ASK(confirm-diagram) 結束。命名可不同，只要責任對齊即可。
- 0.7：主要責任邊界齊全，但漏一個次要責任（如 route match 或 response abstraction），或 namespace 分工略混。
- 0.3：有 Mermaid 圖，但只畫 Router + Handler 兩三個節點，或把 listener adapter / dispatch / cross-cutting chain 其中兩項以上漏掉。
- 0.0：無 Mermaid 類別圖、Turn 1 已開始實作，或把需求收斂成單一 traced golden（例如只會背某張既有 MiniWeb checklist，卻無法說明為何那些責任由 FR/NFR 推出）。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 或 `docs/miniweb-requirements.md`（eval 不驗實作階段）。
- MUST NOT 修改 `docs/architecture/miniweb-framework.class.mmd` 以外的檔案。

Assistant message
- 1.0：收到確認後，明確問是否按此 framework 架構圖施工實作；不自行開始寫 Java。
- 0.7：有問施工意願，但未呼應使用者已確認架構圖。
- 0.3：未問施工意願。
- 0.0：跳過確認或 Turn 1/2 已改 code。

breakpoint：ASK(implement?)，eval 在此終止。

## Cross-turn

order（happens-before）
- ASK(confirm-diagram) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/main/java/` 或 `docs/miniweb-requirements.md` 的 WRITE 不得出現在 ANSWER(confirm-diagram) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。

liveness
- ASK(confirm-diagram) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。
