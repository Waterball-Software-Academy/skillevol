# RPC Framework — FR/NFR

現有系統只有 `Bootstrap` 直接使用 Java `ServerSocket` 接收連線，並把 frame decode、method lookup、interceptor、錯誤處理與 handler 執行寫在同一段流程裡。請把它演進成一個可重用的小型 RPC Framework，讓應用程式作者可以註冊 RPC method handler，而不是直接操作 raw socket 與 byte frame。

## Functional Requirements

1. 應用程式可以依 RPC method name 註冊不同的處理邏輯。
2. 應用程式提供的處理邏輯不應直接依賴 `Socket` 或 raw byte frame；framework 要提供較穩定的 request / response context 介面。
3. 每次 RPC 呼叫前後，framework 可以執行多個可重用的 cross-cutting behavior，例如 logging、auth check、metrics。
4. 找不到 method 時要能集中回傳標準錯誤；handler 拋出例外時要能集中轉成 RPC error response。
5. 啟動程式應該只負責組裝 framework、註冊 handler、啟動 TCP server loop，不應保留 inline decode / dispatch / error handling 分支。

## Non-Functional Requirements

- 必須使用 Java 原生 `ServerSocket` / `Socket`，不得假裝成 HTTP server。
- transport 細節應集中在邊界層，方便之後替換不同 wire protocol。
- 架構應避免把 socket accept、frame decode、method dispatch、cross-cutting、response encode 全塞在單一 class。
- 類別圖要能看出 framework 內部 package 邊界與主要責任分工。
