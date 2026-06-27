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
5. write API 文件至 `spec/api.md`。請嚴格遵守 `rules/api-doc-style.md` 來執行此步驟。
   - 回應分頁時，必須回 total 與 page 兩個欄位。
6. write 測試清單到 `spec/tests.md`。
7. think 驗證三份產出彼此一致。
