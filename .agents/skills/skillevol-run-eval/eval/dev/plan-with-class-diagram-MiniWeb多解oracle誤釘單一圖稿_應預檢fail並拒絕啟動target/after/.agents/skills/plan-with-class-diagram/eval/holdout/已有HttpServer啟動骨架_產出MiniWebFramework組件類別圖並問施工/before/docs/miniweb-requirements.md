# MiniWeb Framework — FR/NFR

現有系統只有 `Bootstrap` 直接啟動 JDK `com.sun.net.httpserver.HttpServer`，並把所有 request handling 寫在同一段 inline lambda 裡。請把它演進成一個可重用的小型 Web Framework，讓應用程式作者可以用 framework API 宣告路由與處理邏輯，而不是直接操作 JDK listener。

## Functional Requirements

1. 應用程式可以註冊不同 HTTP method 與 path pattern 的處理邏輯。
2. path pattern 需要支援簡單 path parameter，例如 `/users/{id}`。
3. 應用程式提供的處理邏輯不應直接依賴 JDK `HttpExchange`；framework 要提供較穩定的 request/response 操作介面。
4. 每次處理請求前後，framework 可以執行多個可重用的 cross-cutting behavior，例如 logging、exception mapping、auth check。
5. 找不到 route 時要能回傳 404；處理邏輯拋出錯誤時要能集中轉成 HTTP response。
6. 啟動程式應該只負責組裝 framework、註冊 route、啟動 server，不應保留 inline request handling 分支。

## Non-Functional Requirements

- 必須使用 JDK 內建 `HttpServer`，不得引入 Spring、Jetty、Netty 或 Servlet container。
- framework 內部職責要可測、可替換；JDK listener 相關細節應集中在邊界層。
- 架構應避免把 path/method 判斷、cross-cutting behavior、response writing、server lifecycle 全塞在單一 class。
- 類別圖要能看出 framework 內部 package 邊界與主要責任分工。
