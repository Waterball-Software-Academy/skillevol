# Auth / Session Framework — FR/NFR

現有系統只有 `AuthBootstrap` 直接啟動 JDK `HttpServer`，並把登入狀態判斷、session 取出與 401 response 寫在同一段 inline handler 裡。請把它演進成一組可重用的 Auth / Session 架構，讓應用程式作者可以宣告登入、登出與受保護路由，而不是直接手寫 transport 與 session 判斷。

## Functional Requirements

1. 使用者登入後，系統要能建立可後續辨識的 authenticated session。
2. 受保護 route 要能解析目前 session / user，未登入時回 401。
3. 使用者登出後，既有 session 要能失效。
4. auth check 與 session load/unload 要能以可重用的 middleware / guard 形式掛到多個 route。
5. session transport 邊界必須可替換；cookie 或 token 都算合理方案，只要邊界清楚。
6. 啟動程式應只負責組裝 server、route 與 auth/session 組件，不應保留 inline auth logic。

## Non-Functional Requirements

- 必須使用 JDK 內建 `HttpServer`，不得引入 Spring Security、Servlet container 或第三方 framework。
- auth 驗證、session persistence 與 transport binding 要分開，避免全塞在單一 class。
- 類別圖要能清楚表達 auth、session、server 邊界與主要責任。
# Auth / Session Framework — FR/NFR

現有系統只有 `AuthBootstrap` 直接啟動 JDK `HttpServer`，並把登入狀態判斷、session 取出與 401 response 寫在同一段 inline handler 裡。請把它演進成一組可重用的 Auth / Session 架構，讓應用程式作者可以宣告登入、登出與受保護路由，而不是直接手寫 transport 與 session 判斷。

## Functional Requirements

1. 使用者登入後，系統要能建立可後續辨識的 authenticated session。
2. 受保護 route 要能解析目前 session / user，未登入時回 401。
3. 使用者登出後，既有 session 要能失效。
4. auth check 與 session load/unload 要能以可重用的 middleware / guard 形式掛到多個 route。
5. session transport 邊界必須可替換；cookie 或 token 都算合理方案，只要邊界清楚。
6. 啟動程式應只負責組裝 server、route 與 auth/session 組件，不應保留 inline auth logic。

## Non-Functional Requirements

- 必須使用 JDK 內建 `HttpServer`，不得引入 Spring Security、Servlet container 或第三方 framework。
- auth 驗證、session persistence 與 transport binding 要分開，避免全塞在單一 class。
- 類別圖要能清楚表達 auth、session、server 邊界與主要責任。
