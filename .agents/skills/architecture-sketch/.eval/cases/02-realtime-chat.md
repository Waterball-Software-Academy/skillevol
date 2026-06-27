# Case 02 — real-time chat / notification delivery

## 埋藏陷阱
- **缺引導層（G5）**：容易只畫 Gateway→Service→Queue→DB 的純物理圖，漏掉交付語意。
- **行為契約**：「至少一次交付（at-least-once）+ 用戶端去重」是 behavioral-contract note，不是盒。
- **範圍界線**：本版不存歷史訊息 → scope-bound note。
- **可擴充設計約束（OCP）**：傳輸層要可換（WebSocket / SSE / 之後的行動推播）→ design-constraint，
  畫成一個 transport 抽象節點 + note，而非為每種傳輸各開一盒。

## 需求原文
設計一個即時訊息推播系統的架構部分。使用者連線後會即時收到送給他的訊息；送出的訊息要保證最終
一定送達線上的收件者（短暫斷線重連後也要補收）。連線量大、需可水平擴充，連線狀態與訊息暫存可用
佇列／記憶體存放。傳輸方式初期用 WebSocket，但日後可能換成 SSE 或接行動推播，介面要能換。
這版不保存歷史訊息（重開不回溯）。請產出架構 Sketch（plan YAML + SVG）。

## 預期 Sketch 應展現
- 物理層：ConnectionGateway、DeliveryService、Queue/Broker、PresenceStore 等盒，綁 impl 單元。
- 一個 `Transport`（抽象/port）節點，掛 **design-constraint**：「Open for SSE / push vendor」（OCP）。
- DeliveryService 或交付邊掛 **behavioral-contract**：「at-least-once + client dedup」。
- **scope-bound note**：「不存歷史訊息」。
- 雙層齊備、單一 focus（如「上線→保證送達線上收件者的鏈」）。

## 不應出現（常見誤拆）
- 為 WebSocket / SSE / Push 各開一個並列實作盒（應是一個 port + note）。
- 純物理圖、零 note。
- `MessageHistory` / `Archive` 實作盒。
