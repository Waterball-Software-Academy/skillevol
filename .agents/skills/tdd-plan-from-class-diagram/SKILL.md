---
name: tdd-plan-from-class-diagram
description: 依 Mermaid 類別圖規劃 TDD 計畫並在確認後再詢問是否開始施工。Use when 使用者已有 Mermaid 類別圖、想把設計切成可執行的 red-green slices、或明確下 `/tdd-plan-from-class-diagram`。SKIP when 還沒有類別圖、要直接實作、或需求不足以推導可觀察測試行為。
---

# Purpose

`plan-with-class-diagram` 解決的是先把改動邊界畫清楚，但圖本身還不會告訴後續實作應該先測哪個 seam、哪裡該 fake、哪裡該留到 contract test。
本 skill 在使用者已經有 Mermaid 類別圖，且想把設計轉成可落地的 TDD 推進順序時啟用。
它會從類別圖與相鄰需求中推導出最小可觀察切片，先把 TDD 計畫寫進 `docs/tdd/*.tdd-plan.md`，請使用者確認後，再問是否按此計畫施工。
若缺少這個 planning gate，agent 容易直接跳去寫測試或程式碼、把整合測試排在最前面，或讓外部邊界與 fake/spy 策略全留在腦中。

# SOP

1. read 使用者提供的 Mermaid 類別圖（檔案路徑或 inline Mermaid）、相關需求文件、既有測試/原始碼（若存在）與 package 邊界。
   1. 優先讀 Mermaid 類別圖與同主題需求文件；只有在計畫需要落到具體 seam 時才補讀 source/test。
   2. 若 prompt 指到 `docs/architecture/*.class.mmd`，以該圖為 primary source。
2. think 執行 intake gate。
   1. 判定是否已拿到可解析的 Mermaid 類別圖。
   2. 若只有類別關係而缺少可觀察行為、需求或輸出邊界，停止並要求補齊必要上下文。
   3. 若類別圖已明顯過時、與相鄰需求檔互相衝突，停止並指出要先回到 `/plan-with-class-diagram` 修圖或補需求。
   4. 若可推導測試切片，進入第 3 步。
3. think 判定 TDD 計畫範圍與切片順序。
   1. 從類別圖找出 public seams、協調者、外部邊界、具名實作與 cross-cutting chain。
   2. 依「最小可觀察行為 -> 協調流程 -> 外部邊界 contract -> 失敗/回歸」排序 slices；不要先排大而全整合測試。
   3. 每個 slice 都要標出 red test、主要 collaborators、需要的 test doubles、實作目標與 done evidence。
   4. 若某個 slice 只能靠 implementation detail 驗證，回到本步重新切分。
4. think 決定計畫產物與命名。
   1. 將規劃產物固定落到 `docs/tdd/`。
   2. 若輸入是 `docs/architecture/<feature-scope>.class.mmd`，輸出檔名用 `<feature-scope>.tdd-plan.md`。
   3. 若輸入是 inline Mermaid，從圖主題推導簡潔 kebab-case 檔名；若無法穩定推導，停止並詢問使用者。
   4. 在收到計畫確認前，不得修改 `src/`、`test`、`spec` 或設定檔。
5. write 產出 TDD 計畫到 `docs/tdd/<feature-scope>.tdd-plan.md`。依 `templates/tdd-plan.md` 渲染此步驟的成品。
   - 計畫 MUST 對齊類別圖上的協調者、介面邊界與具名實作，不得只列抽象測試口號。
   - 每個 slice MUST 含 red test、test doubles、implementation target 與 done when。
   - 若圖上有外部邊界 interface 與具名 impl，計畫 MUST 明示先用 fake/spy 或 contract test 隔離，再決定何時碰具體整合。
6. assistant-message 回報 TDD 規劃結果並請使用者確認。
   1. 明確指出已寫出的計畫檔路徑。
   2. 簡短說明切片順序與為何先測這些 seams。
   3. 在這一步結尾詢問使用者是否確認這份 TDD 計畫。
7. think 判定使用者對 TDD 計畫的回應。
   1. 若使用者確認，進入第 8 步。
   2. 若使用者要求調整切片、測試層級或順序，回到第 3 步。
   3. 若使用者補充新的需求或限制，回到第 1 步。
8. assistant-message 詢問是否按此 TDD 計畫開始施工。
   1. 只有在 TDD 計畫已確認後，才可問是否開始實作或寫第一個 red test。
   2. 若使用者尚未授權實作，不得自行新增測試或修改 source code。
9. think 判定是否進入實作。
   1. 若使用者同意按計畫施工，才可開始後續實作工作。
   2. 若使用者拒絕或暫停，停止在已確認的計畫檔與規劃摘要，不再往下改 code。
