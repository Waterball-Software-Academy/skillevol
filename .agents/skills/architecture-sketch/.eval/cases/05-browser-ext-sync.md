# Case 05 — browser extension + sync backend

## 埋藏陷阱
- **泳道分層**：瀏覽器端 vs 後端是兩條 lane；別把兩側壓進一欄。
- **異質內部分面（panels）**：擴充功能內的 content-script 與 background-worker 是 **panels**
  （不同職責），不是 `multiplicity:N` 堆疊，也不是兩個無關大盒。
- **單盒過載 / 多焦點（G6）**：把「擷取頁面 + 衝突合併同步」塞進一張圖會雙焦點；同步衝突解析應自成
  一張下鑽圖。
- **純物理誘惑（G5）**：容易只畫 extension→API→DB 而漏掉「離線可用 / 最後寫入者勝」等約束 note。

## 需求原文
設計一個瀏覽器擴充功能加後端的架構部分。擴充功能會在使用者瀏覽時擷取目前頁面的標註，存到本機並
同步到後端，讓使用者在不同裝置看到同一份標註。擴充功能內部分成兩塊：在頁面上跑、負責抓取與注入 UI
的部分，以及在背景跑、負責儲存與連線後端的部分。離線時要能照常標註，連線後再同步；多裝置同時改同
一筆時的衝突，這版用「最後寫入者勝」。同步衝突的細節邏輯較多，請另開圖說明。
請產出架構 Sketch（plan YAML + SVG）。

## 預期 Sketch 應展現
- 兩條 lane：Browser / Backend。
- Extension 節點以 **panels** 表示 content-script 與 background-worker 兩個分面。
- behavioral-contract note：「離線可標註，連線後同步」；衝突策略 note：「last-write-wins」。
- 同步衝突解析以 `zoom:` 另開一張圖（避免單圖雙焦點，G6）。
- 各盒綁 impl 單元；雙層齊備。

## 不應出現（常見誤拆）
- content-script / worker 被畫成 `multiplicity:N` 堆疊（它們是異質分面，不是同型集合）。
- 一張圖同時詳述「擷取同步」與「衝突合併」兩個焦點。
- 純物理三盒圖，無離線/衝突約束 note。
