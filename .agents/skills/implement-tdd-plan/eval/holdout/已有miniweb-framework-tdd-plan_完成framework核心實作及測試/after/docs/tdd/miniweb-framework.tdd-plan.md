# TDD Plan — miniweb-framework

## Scope

- source diagram: `docs/architecture/miniweb-framework.class.mmd`
- goal: 先用可觀察測試把 MiniWeb 的路由解析、middleware 協調、dispatcher 轉派與 transport adapter 邊界固定下來，再進入具體 framework 實作。
- out of scope for this cycle: `Bootstrap` 啟動 wiring、真實 JDK listener 生命週期、logging/auth 等具體 middleware 實作、非必要的端到端整合測試。

## Test Strategy

- primary seams:
  - `routing.Router.match(method, path)` 作為 pure routing seam，先固定 static path 與 path parameter 行為。
  - `middleware.MiddlewareChain.next(context)` 作為 cross-cutting seam，先固定 middleware 執行順序與 short-circuit 能力。
  - `dispatch.RequestDispatcher.dispatch(context)` 作為 framework 協調者，串起 route resolve、middleware chain 與 `RequestHandler.handle(context)`。
  - `server.JdkHttpHandlerAdapter.handle(exchange)` 作為 transport boundary seam，隔離 `HttpExchange` 與 framework 的 `RequestContext` / `ResponseWriter` abstraction。
- doubles strategy: 先用 fake `RouteRegistry` / `RouteMatch` 固定 routing 輸出；用 spy `RequestHandler`、spy/fake `Middleware` 驗證 dispatcher 與 chain 的呼叫順序；在 adapter slice 用 fake exchange 與 spy dispatcher / spy `ResponseWriter` 驗證邊界 contract；失敗情境以 throwing handler 或 throwing middleware 驅動。

## Slice Order

### Slice 1 — Router 先固定 method/path 與 path parameter 的 pure matching

- red test: `Router.match()` 在已註冊 route 下，能依 HTTP method 與 path 找到對應 `RouteMatch`，且 `/users/{id}` 能抽出 path parameter；未命中時回傳明確 miss。
- collaborators: `Router`, `RouteRegistry`, `RouteMatch`
- test doubles: fake `RouteRegistry`
- implementation target: 先把 route registration 與 matching 演算法做成 pure logic，不碰 transport、middleware 或真實 handler 執行。
- done when: route lookup 可區分 method、static path、path parameter；呼叫端可從 `RouteMatch` 取得後續 dispatch 所需資訊，且 miss path 不會被誤判成命中。

### Slice 2 — MiddlewareChain 固定順序、包裹與 short-circuit 語意

- red test: `MiddlewareChain.next(context)` 會依註冊順序執行多個 `Middleware.intercept(context, chain)`，並在 middleware 不呼叫 `chain.next()` 時停止往後傳遞。
- collaborators: `MiddlewareChain`, `Middleware`, `RequestContext`, `RequestHandler`
- test doubles: spy `Middleware`, terminal spy `RequestHandler`
- implementation target: 先實作 chain traversal 與 terminal handler handoff，讓 cross-cutting 行為可獨立測，不需要先有完整 dispatcher。
- done when: 可驗證 middleware 前後包裹順序、terminal handler 只被執行一次、short-circuit 時後續 middleware 與 handler 都不被觸發。

### Slice 3 — RequestDispatcher 串起 route resolve、chain 與 handler dispatch

- red test: `RequestDispatcher.dispatch(context)` 在 route 命中時，會先透過 `Router` 取得 `RouteMatch`，再用 `MiddlewareChain` 包住目標 `RequestHandler`，最後把同一個 `RequestContext` 送進 handler。
- collaborators: `RequestDispatcher`, `Router`, `RouteMatch`, `MiddlewareChain`, `RequestHandler`
- test doubles: fake `Router`, fake `RouteMatch`, spy `MiddlewareChain`, spy `RequestHandler`
- implementation target: 把 framework 內部的主協調流程固定下來，避免一開始就寫大型整合測試。
- done when: route 命中流程的 collaborator handoff 明確、呼叫順序可觀察、handler 不直接依賴 transport 細節，且 `RequestDispatcher` 本身不承擔 route matching 細節。

### Slice 4 — Dispatcher 先處理 404，再把成功路徑交給 middleware/handler

- red test: 當 `Router` 回傳 miss 時，`RequestDispatcher` 會產生 404 response，且不會啟動 `MiddlewareChain` 或 `RequestHandler`。
- collaborators: `RequestDispatcher`, `Router`, `ResponseWriter`, `RequestContext`
- test doubles: fake miss `Router`, spy `ResponseWriter`, spy `MiddlewareChain`, spy `RequestHandler`
- implementation target: 先把最基本的 failure mapping 固定為 framework orchestration 行為，而不是留到 transport adapter 或端到端測試才驗。
- done when: 404 成為 dispatcher 的可觀察 contract；miss route 時不會錯誤落入 handler；response status / body 可從 `ResponseWriter` 驗證。

### Slice 5 — JdkHttpHandlerAdapter 固定 transport boundary，最後補 handler/middleware 失敗映射

- red test: `JdkHttpHandlerAdapter.handle(exchange)` 會把 `HttpExchange` 轉成 `RequestContext` 與 `ResponseWriter` 後委派給 framework；另外當 handler 或 middleware 丟出例外時，framework 會集中轉成 HTTP error response，而不是把例外直接洩漏到 adapter 外。
- collaborators: `JdkHttpHandlerAdapter`, `RequestContext`, `ResponseWriter`, `RequestDispatcher`, `MiddlewareChain`, `RequestHandler`
- test doubles: fake exchange, spy `RequestDispatcher`, throwing `RequestHandler` / throwing `Middleware`, spy `ResponseWriter`
- implementation target: 最後才碰 transport adapter；例外映射先以 framework boundary contract 表達，可在實作時決定落在 dispatcher 保護層或專責 exception-mapping middleware，但測試先固定 observable outcome。
- done when: adapter 不把 `HttpExchange` 洩漏給 handler；成功請求會正確委派；例外請求能穩定轉成 HTTP response；transport 細節仍侷限在 adapter。

## Regression Gate

- 保留 `Router.match()` 對 static path、path parameter、HTTP method 的回歸測試，避免 route precedence 漂移。
- 保留 `MiddlewareChain` 的順序與 short-circuit 測試，避免 cross-cutting 行為被重構打壞。
- 保留 `RequestDispatcher` 的 hit / miss 分流測試，確保 404 與成功 dispatch 不會混線。
- 保留 `JdkHttpHandlerAdapter` 的 boundary contract 測試，確保 handler 仍只看 `RequestContext` / `ResponseWriter`。
- 保留 handler / middleware 例外轉 HTTP response 的失敗映射測試，避免錯誤直接洩漏成未處理例外。

## Open Questions

- 例外映射最後是由 `RequestDispatcher` 直接保護，還是以一個預設 `exception-mapping middleware` 注入 `MiddlewareChain`？本計畫先固定 observable outcome，不先綁死內部落點。
