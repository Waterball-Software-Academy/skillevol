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

<TODO>