# MiniWeb Framework — FR/NFR

現有系統想從單一 inline `HttpServer` handler 演進成一個可重用的小型 Web Framework，讓應用程式作者用 framework API 註冊路由與 handler，而不是直接碰 JDK `HttpExchange`。

## Functional Requirements

1. 應用程式可以註冊不同 HTTP method 與 path pattern 的處理邏輯。
2. path pattern 要支援簡單 path parameter，例如 `/users/{id}`。
3. framework 要提供較穩定的 request/response abstraction，handler 不應直接依賴 `HttpExchange`。
4. 請求前後要能串多個 reusable middleware，例如 logging、auth、exception mapping。
5. 找不到 route 時要 404；handler 丟錯時要能集中轉成 HTTP response。

## Non-Functional Requirements

- JDK listener 細節應集中在 transport adapter。
- routing、middleware、dispatch、handler boundary 不可全塞在同一個大整合測試起手式裡。
- TDD 計畫要能看出 pure logic、framework orchestration、boundary contract 與 failure mapping 的切片順序。
