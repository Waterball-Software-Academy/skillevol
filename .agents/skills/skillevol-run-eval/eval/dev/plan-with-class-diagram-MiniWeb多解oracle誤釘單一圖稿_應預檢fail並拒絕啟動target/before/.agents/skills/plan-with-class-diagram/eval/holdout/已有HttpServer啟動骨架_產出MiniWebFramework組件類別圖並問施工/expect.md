# expect — 已有HttpServer啟動骨架_產出MiniWebFramework組件類別圖並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；本 fixture copy 不提供 target `after/`，單一 golden contract 只由 hidden metadata 編碼，避免把 target 設計答案直接洩漏進 outer anti-overfit fixture。

## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid

## Provenance

- 釘的行為：在 JDK `HttpServer` 最小骨架與 FR/NFR 輸入上規劃 MiniWeb framework 時，此 fixture 以 hidden metadata 明確宣告「若用單一 golden 圖稿當唯一合法 target outcome，該 contract 屬 overfit」。
- 為何存在：它是 anti-overfit 測試用的故意過窄 hidden oracle；MiniWeb framework 的 package / component 切法其實是 multi-valid，outer runner 應在 preflight 擋下這種 single-golden contract，而不是黑箱驅動 target。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/miniweb-framework.class.mmd`。
- MUST NOT 修改 `src/main/java/` 或 `docs/miniweb-requirements.md`。
- MUST NOT Turn 1 新增 framework 原始碼目錄或 Java class。

Assistant message
- 1.0：回報已寫出 `docs/architecture/miniweb-framework.class.mmd`；檔內含有效 Mermaid `classDiagram`；從 FR/NFR 推導出 framework package 邊界，用 namespace 區分至少 `server`、`http`、`routing`、`middleware`、`handler`（或等價 package）；節點 cover 應新增的 framework 組件：MiniWebServer（或等價 server 協調者）、RequestContext、ResponseWriter、Router、RouteRegistry、RouteMatch、RequestHandler、HandlerRegistry、Middleware、MiddlewareChain、RequestDispatcher（或等價 dispatch 協調者）、JdkHttpHandlerAdapter（或等價 JDK HttpHandler 橋接）；並標示與既有 Bootstrap/HttpServer 的關係；說明確認前不動 source code；以 ASK(confirm-diagram) 結束。
- 0.7：主要 framework 組件齊，但漏一個次要節點（如 RouteMatch）或 namespace 不完整。
- 0.3：只畫 Router + Handler 兩三個節點，漏 middleware pipeline 或 dispatch 協調者，或誤用 Cart/Order/Payment 業務 class。
- 0.0：無 Mermaid 類別圖，或 Turn 1 已開始實作。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 或 `docs/miniweb-requirements.md`（eval 不驗實作階段）。
- MUST NOT 修改 `docs/architecture/miniweb-framework.class.mmd` 以外的檔案。

Assistant message
- 1.0：收到確認後，明確問是否按此 framework 架構圖施工實作；不自行開始寫 Java。
- 0.7：有問施工意願，但未呼應 framework 架構已確認。
- 0.3：未問施工意願。
- 0.0：跳過確認或 Turn 1/2 已改 code。

breakpoint：ASK(implement?)，eval 在此終止。

## Cross-turn

order（happens-before）
- ASK(confirm-diagram) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/` 或 `docs/miniweb-requirements.md` 的 WRITE 不得出現在 ANSWER(confirm-diagram) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。

liveness
- ASK(confirm-diagram) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。

## 改動覆蓋完整性（本 unit 覆寫 shared 0.3 錨點）

- 0.3：只畫 1-2 個 framework class，明顯漏掉 Router、MiddlewareChain、RequestDispatcher 或 JdkHttpHandlerAdapter 其中兩項以上；或把業務分層（Service/Repository）當成 framework 圖。
