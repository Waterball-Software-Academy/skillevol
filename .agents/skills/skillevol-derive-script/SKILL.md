---
name: skill-derive-script
description: 給訂一個已經撰寫好 SOP 的 SKILL，使用者想要針對某個步驟，去展開該步驟需要委派的 Python script，那此時就必須呼叫此 Skill。
---

# SOP

## Phase 1 -- 收斂腳本委派範圍

1. READ 讀取目標 SOP skill 內容，定位使用者指定的 phase 與 step，確認要展開的是哪個步驟的 script。
2. THINK 若需要判斷哪些職責應委派給可按需執行的 Python script、哪些內容仍應留在原 step，先讀取 `rules/腳本委派邊界.md`，再根據該 step 的目的、輸入輸出與可自動化程度收斂 script 邊界，避免把 script 展開誤寫成流程改寫。

## Phase 2 -- 展開腳本並掛回 SOP

1. DELEGATE 若已收斂出 script 範圍，呼叫 `/skill-form-script` 撰寫或修改目標 skill `scripts/` 下對應的 Python script，交付該步驟需要自動化執行的腳本。
2. WRITE 回寫目標 SOP skill 的指定 step，補上何時執行該 Python script、script 路徑與預設執行方式的說明，讓主流程維持最小可用、script 只在需要自動化時才執行。
3. READ 若需要檢查回寫後的指定 step 是否已把 script 的執行時機、路徑與執行方式嵌回原句，先讀取 `rules/回寫時寫清 script 路徑與執行方式.md`，再回頭檢查新 script 與回寫後的 SOP 是否一致：指定 step 仍描述當下動作、script 只在實際執行時被點名，且流程已形成「先有 SOP、再逐步展開 script」的結構；若不符合，立即修正。
