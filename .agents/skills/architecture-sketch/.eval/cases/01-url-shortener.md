# Case 01 — URL shortener service

## 埋藏陷阱
- **過度拆解**：`LinkRepository` 容易被展成 `create/get/update/delete` 四個方法盒（G1/G4）——
  沒有不同約束可掛，不該再細分。
- **約束當盒子**：rate-limit 是掛在 API 邊/節點上的 **design-constraint note**，不是一個獨立盒子。
- **範圍界線**：點擊分析（analytics）在此版本 out → **scope-bound note**，不該畫成實作盒。
- **缺引導層**：只畫 API→Service→DB 三盒而無任何 note（G5）。

## 需求原文
設計一個短網址服務的實作計畫之架構部分。使用者貼上長網址，服務產生一個短代碼並可由短代碼 302
轉址回原網址。短代碼需全域唯一且不可猜測。預期讀多寫少、需擋濫用（同一來源每分鐘建立上限）。
轉址路徑要低延遲，可加一層快取。資料存在關聯式資料庫。點擊統計分析這版先不做。
請產出架構 Sketch（plan YAML + SVG）。

## 預期 Sketch 應展現
- 物理層：API/Handler、ShortcodeService（產碼）、Cache、RelationalDB（datastore）等盒；每盒綁 impl 單元。
- `ShortcodeService` 掛 behavioral-contract：「短碼全域唯一且不可猜測」。
- rate-limit 為 **note**（design-constraint）錨在 API 節點或 create 邊，而非獨立盒。
- 快取以一個盒（或 panel）表示，邊上標 read-through/look-aside。
- analytics 以 **scope-bound note** 標 out（可錨 canvas 或 API）。
- 單一 focus（如「建立短碼 + 低延遲轉址鏈」）；雙層齊備。

## 不應出現（常見誤拆）
- `CreateLink` / `GetLink` / `UpdateLink` / `DeleteLink` 四個方法級盒子。
- `RateLimiter` 被畫成一個與 Service 並列的實作大盒（除非它真有自己的多條約束需下鑽）。
- `AnalyticsService` / `ClickTracker` 實作盒。
- 一張圖塞進「建立 + 轉址 + 快取 + 限流 + 分析」全部，無下鑽。
