---
name: plan-with-class-diagram
description: 在實作前先分析既有 code 與需求，產出涵蓋改動範圍的 Mermaid class diagram 檔，請使用者確認架構，再詢問是否按圖施工實作。Use when 使用者已提出具體程式功能需求、希望先看 class diagram 再決定是否實作，或明確下 `/plan-with-class-diagram`。SKIP when 純問概念、純除錯、只要直接 coding、或需求仍不足以畫出改動邊界。
---

# Purpose

功能需求在進入實作前，最容易直接跳到寫 code，結果把改動邊界、協調者、依賴方向與 package 切分全藏進腦中；等到實作一半才發現架構歪了，修正成本會很高。
本 skill 在使用者已有具體功能需求，且希望先看程式架構如何改動時啟用。
它先讀既有 code 與需求，推導出只涵蓋改動部分的 Mermaid class diagram，將圖檔落到 `docs/architecture/*.class.mmd`，請使用者確認；確認後再問是否按圖施工。
若缺少這個 planning gate，agent 容易直接改 source code、漏掉關鍵改動點，或把架構圖只留在對話訊息而不是可追蹤的檔案 artifact。

# SOP

1. read 使用者需求、相關程式碼、需求文件與現有 package 邊界。
   1. 只讀與此次功能需求相關的檔案；不要為了畫圖把整個 repo inventory 化。
   2. 若需求已明確指到某個路徑、模組或文件，優先以那些輸入為準。
2. think 判定架構改動範圍。
   1. 找出這次需求會新增、修改、重新協調的 class、interface 與 package。
   2. 只保留有架構意義的節點：協調者、主要依賴、介面邊界、被依賴且會受影響的既有 class。
   3. 不把 DTO、getter/setter、helper inventory 畫進主圖；class 方塊只留對架構閱讀有用的 public 方法簽名，不列欄位。
   4. 需求若提到外部邊界（如 PaymentGateway、Repository、Client），圖上 MUST 同時含該 interface 與至少一個具名實作（如 StripePaymentGateway）；若流程有 request/result 語意，把對應 type 一併入圖。
   5. 新增的協調者（如 CheckoutService）放在它所協調的子域 package（如 order），不要為單一 class 另開同層 package。
   6. 若 FR/NFR 描述的是 framework / internal plumbing（routing、middleware pipeline、server lifecycle、handler 不直接依賴 JDK API），而非業務 domain：
      1. namespace 用 `server`、`http`、`routing`、`middleware`、`handler`；dispatch 協調者可獨立 `dispatch` namespace。
      2. 節點 MUST cover：server 協調者（MiniWebServer 或等價）、JDK HttpHandler 橋接（JdkHttpHandlerAdapter 或等價）、RequestContext、ResponseWriter、Router、RouteRegistry、RouteMatch、RequestHandler、HandlerRegistry、Middleware、MiddlewareChain、RequestDispatcher；既有 Bootstrap 留在 `server` 並標示與 server 協調者的關係。
      3. 勿用 `app` / `pipeline` 等額外 wrapper namespace 取代上述分域；middleware 與 handler 不得合併成單一 package。
3. think 決定圖檔產物與命名。
   1. 將規劃產物固定落到 `docs/architecture/`。
   2. 依此次功能或子系統主題，取一個簡潔 kebab-case 檔名，寫成 `<feature-scope>.class.mmd`。
   3. 若 `docs/architecture/` 不存在，可以建立；除此之外不得修改 `src/`、測試、設定或需求檔。
4. write 產出 Mermaid class diagram 到 `docs/architecture/<feature-scope>.class.mmd`。依 `templates/class-diagram.class.mmd` 渲染此步驟的成品。
   1. 保留 template 的 `direction LR`、短 namespace id、標準關係箭頭（`..>`、`*--`、`<|..`）；禁止自創 wrapper return type。
   2. 圖上節點與關係 MUST cover step 2 判定的所有改動部分，不得只畫局部 happy path。
   3. interface 與其具名實作之間 MUST 畫 `implements`；協調者方法回傳領域實體，不回傳包裝型別。
   4. 關係線 MUST 帶 `: <責任動詞>` 標籤（如 `: contains`、`: reads`、`: creates`、`: accepts`、`: charges`、`: implements`）；composition 用 `"1" *-- "0..*"`.
5. assistant-message 回報架構規劃結果並請使用者確認。
   1. 明確指出已寫出的圖檔路徑。
   2. 簡短說明此次改動範圍與為何這些 class / interface 需要入圖。
   3. 在這一步結尾詢問使用者是否確認這張架構圖。
   4. 在收到確認前，不得修改任何 source code。
6. think 判定使用者對架構圖的回應。
   1. 若使用者確認，進入第 7 步。
   2. 若使用者要求修圖，回到第 2 步，依回饋調整圖檔後再請確認。
   3. 若使用者指出需求仍不足以畫出邊界，先補問必要資訊，再回到第 2 步。
7. assistant-message 詢問是否按圖施工實作。
   1. 只有在架構圖已確認後，才可問是否開始實作。
   2. 若使用者尚未授權實作，不得自行改寫 Java / TypeScript / 其他 source 檔。
8. think 判定是否進入實作。
   1. 若使用者同意按圖施工，才可開始後續實作工作。
   2. 若使用者拒絕或暫停，停止在已確認的圖檔與規劃摘要，不再往下改 code。
