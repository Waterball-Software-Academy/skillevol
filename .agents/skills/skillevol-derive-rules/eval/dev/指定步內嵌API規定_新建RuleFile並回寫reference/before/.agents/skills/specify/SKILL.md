---
name: specify
description: 把需求轉成規格骨架、API 文件與測試清單。
---

# Purpose

依序產出規格骨架、API 文件、測試清單，三者互相對齊。

# SOP

1. read 需求輸入與既有 `spec/`。
2. think 對齊需求與既有規格，盤點差異。
3. write 規格骨架到 `spec/spec.md`。
4. think 盤點本輪要對外暴露的端點。
5. write API 文件至 `spec/api.md`。
   - 每個端點須含 HTTP 方法與路徑。
   - 請求與回應須各附一個 JSON 範例。
   - 錯誤碼至少標出 400（參數錯）與 409（狀態衝突）的語意。
   - path 用名詞複數，operationId 用 camelCase。
6. write 測試清單到 `spec/tests.md`。
7. think 驗證三份產出彼此一致。
