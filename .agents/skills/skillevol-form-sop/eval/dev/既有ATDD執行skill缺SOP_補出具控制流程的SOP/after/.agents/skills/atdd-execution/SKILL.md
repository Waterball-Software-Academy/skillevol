---
name: atdd-execution
description: 執行 Acceptance Test-Driven Development，以驗收測試驅動功能從可測需求到通過回歸。Use when 使用者要用 ATDD 實作 feature、要求先寫驗收測試再實作、或需要逐一讓 acceptance criteria 綠燈。SKIP when 只是要補單一 unit test、純重構、或沒有可觀察驗收標準的探索討論。
---

# Purpose

ATDD 容易被誤寫成「寫測試、寫程式、跑測試」的線性清單，但真正的風險在於需求不可測、測試沒有先 red、或 regression 破壞既有行為時仍繼續往下做。
本 skill 在 feature 已有或正在形成 acceptance criteria，且使用者想用驗收測試驅動實作時啟用。
它把每個 acceptance criterion 放進 red-green-refactor 內圈，同時用外圈 gate 管住可測性、整體回歸、重構後再驗證與最終交付。
若缺少這個控制流程，agent 容易跳過 red gate、把 implementation detail 當驗收、或在測試失敗時靠猜測繼續改。

# SOP

1. read feature request、acceptance criteria、既有 spec、既有測試、目前實作狀態與使用者指定的驗證命令。
2. think 執行 intake gate。
   1. 若缺少 feature request 或 acceptance criteria，停止並要求補齊。
   2. 若 acceptance criteria 不是使用者可觀察行為，停止並要求改寫成可驗收描述。
   3. 若 acceptance criteria 缺少輸入、輸出、範例或驗收邊界，停止並要求澄清。
   4. 若 criteria 可測，進入第 3 步。
3. think 建立 acceptance test plan。
   1. 將每個 acceptance criterion 對應到一個或多個 acceptance tests。
   2. 標出每個 test 是自動化測試、人工驗收，或需要 LLM judge 的語意驗收。
   3. 若 test 只驗 implementation detail 而非 user outcome，回到本步重新設計。
   4. 若仍有 criterion 無法對應 test，回到第 2 步要求澄清。
4. write 建立或更新 acceptance tests，只覆蓋目前 plan 中的驗收行為，不寫無關測試。
5. think 執行 red gate。
   1. 跑新增或更新的 acceptance tests。
   2. 若 test fail 且 failure 對應尚未實作的 acceptance behavior，進入第 6 步。
   3. 若 test pass，判斷功能已存在、測試太弱、測錯層級或 fixture 錯；若測試太弱、測錯層級或 fixture 錯，回到第 3 步。
   4. 若無法判斷為什麼沒有 red，停止並回報 red gate 不成立。
6. think 進入每個 acceptance criterion 的內圈。
   1. 選定下一個未通過的 acceptance criterion。
   2. 若所有 acceptance criteria 都有 passing acceptance evidence，進入第 10 步。
   3. 否則進入第 7 步。
7. write 做足以推進目前 acceptance criterion 的最小實作，不做無關 refactor。
8. think 驗證目前 criterion。
   1. 跑目前 criterion 對應的 acceptance test。
   2. 若 pass，記錄 evidence，回到第 6 步選下一個 criterion。
   3. 若 fail 且 failure 指向 test design 或 fixture，回到第 3 步。
   4. 若 fail 且 failure 指向需求不可判定，回到第 2 步。
   5. 若 fail 且 failure 指向 implementation，回到第 7 步。
9. think 檢查內圈是否卡住。
   1. 若同一 criterion 多輪後 failure provenance 沒有變化，停止並回報 blocker。
   2. 若 failure provenance 有新資訊，回到第 8 步重新分類。
10. think 執行 regression gate。
    1. 跑相關 regression、unit、lint、typecheck 或專案既有驗證。
    2. 若 regression fail，進入 impact analysis，判斷是新功能破壞既有行為、測試基準需更新，或實作方向錯。
    3. 若是實作破壞既有行為，回到第 7 步修正。
    4. 若是 acceptance test plan 缺口，回到第 3 步。
    5. 若 regression 全綠，進入第 11 步。
11. think 執行 refactor gate。
    1. 若沒有必要整理，進入第 13 步。
    2. 若需要 refactor，只做不改變行為的整理。
    3. refactor 後回到第 10 步重跑 acceptance 與 regression 驗證。
12. think 檢查未解決項。
    1. 若存在未解釋 skip、未覆蓋 acceptance criterion、未解決問題或未跑的必要驗證，回到對應 gate。
    2. 若所有 evidence 完整，進入第 13 步。
13. write 收尾回報：列出每個 acceptance criterion、對應 test evidence、red gate 結果、最終 regression 結果、refactor 是否執行，以及仍需人工驗收的項目。
