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
   - 先掃描需求裡出現的所有端點，列出原始清單。
   - 再把每個端點分類成 CRUD 型或命令型。
   - 最後依分類順序，逐類產出 openapi 區塊。
6. write 測試清單到 `spec/tests.md`。
7. think 驗證三份產出彼此一致。
