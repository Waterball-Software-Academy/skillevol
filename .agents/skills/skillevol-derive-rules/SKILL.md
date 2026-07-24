---
name: skill-derive-rule
description: 給訂一個已經撰寫好 SOP 的 SKILL，使用者想要針對某個步驟，去展開該步驟需要遵守的規則，那此時就必須呼叫此 Skill。
---

# SOP

## Phase 1 -- 收斂規則展開範圍

1. READ 讀取目標 SOP skill 內容，定位使用者指定的 phase 與 step，確認要展開的是哪個步驟的規則。
2. THINK 若需要判斷哪些要求應抽成可按需載入的 RuleFile、哪些內容仍應留在原 step，先讀取 `rules/規則展開邊界.md`，再根據該 step 的目的、輸入輸出與常見失誤收斂規則邊界，避免把規則展開誤寫成流程改寫。

## Phase 2 -- 展開規則並掛回 SOP

1. DELEGATE 若已收斂出規則範圍，呼叫 `/skill-form-rule` 撰寫或修改目標 skill `rules/` 下對應的 RuleFile，交付該步驟需要額外把關的規則內容。
2. WRITE 回寫目標 SOP skill 的指定 step，補上何時按需讀取該 RuleFile 的說明，讓主流程維持最小可用、規則在需要時才載入。
3. READ 若需要檢查回寫後的指定 step 是否已把新 RuleFile 的讀取時機與路徑嵌回原句，先讀取 `rules/回寫後仍可執行.md`，再回頭檢查新 RuleFile 與回寫後的 SOP 是否一致：指定 step 仍描述當下動作、RuleFile 只在實際讀取時被點名，且流程已形成「先有 SOP、再逐步展開規則」的結構；若不符合，立即修正。
