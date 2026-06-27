# expect — 已有TcpServer啟動骨架_語意驗RpcFramework責任分工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 不提供 `after/`：目的是以 semantic oracle 驗證 framework generalization，而不是用單一 golden 圖稿釘死唯一設計。

## Provenance

- 釘的行為：在非 HTTP 的 RPC framework 情境下，skill 應從 FR/NFR 推導 transport adapter、frame decode、method registry、interceptor / middleware、dispatcher、response encode 等責任邊界，而不是套用 MiniWeb 的 HttpServer / Router / HttpHandler traced checklist。
- 為何存在：這是抓 `SKILL.md` step 2.6 overfit 的 dev red gate。若 skill 仍把所有 internal plumbing 題都收斂成 `server/http/routing/middleware/handler/dispatch` + `JdkHttpHandlerAdapter`，本 unit 應 fail。

## Run

### Turn 1 — 結束方式：done 或 ASK(confirm-diagram)

Tool calls
- MUST 寫出一個 Mermaid `classDiagram` 檔案。
- MUST NOT 修改 `src/main/java/` 或 `docs/rpc-server-requirements.md`。
- MUST NOT 在 Turn 1 新增 Java framework 原始碼目錄或 class。

Assistant message
- 1.0：回報已完成 RPC framework 類別圖規劃；圖上清楚表達下列責任邊界：TCP server lifecycle / transport adapter、request/response context、frame codec、method registry、handler boundary、cross-cutting chain、dispatch coordinator；命名可不同，只要責任對齊即可。
- 0.7：主要責任邊界齊全，但漏一個次要責任（如 response encode 或 method lookup），或 namespace 分工略混。
- 0.3：有 Mermaid 圖，但只畫 server + handler 兩三個節點，或把 transport / dispatch / cross-cutting 其中兩項以上漏掉。
- 0.0：無 Mermaid 類別圖、Turn 1 已開始實作，或圖中出現與題目無關的 HTTP traced checklist（如 `HttpHandler`、`JdkHttpHandlerAdapter`、`RequestContext`、`ResponseWriter`、`Router`、`RouteRegistry`、`RouteMatch`）。

## File outcome（本 unit 覆寫 shared file-oracle）

- 合法 outcome：新增一個 Mermaid 類別圖檔於 `docs/` 或 `docs/architecture/` 下；檔名不必固定，但應語意對應 RPC framework。
- forbidden diff：不得修改 `src/`、需求文件、測試、設定檔。

## Red-gate signal

- 若終態圖檔出現 `HttpHandler`、`JdkHttpHandlerAdapter`、`RequestContext`、`ResponseWriter`、`Router`、`RouteRegistry`、`RouteMatch` 任一者，視為命中本 unit 預期的 overfit red gate。
