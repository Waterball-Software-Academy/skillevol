---
description: 接收 planner 的批次疑問 payload，先做共通 schema 檢查與白話文轉譯，再依可用工具分流。優先走 fork；若無 fork，改走 AskUserQuestion／AskQuestion 類互動工具，問完把答案回傳 caller。任何 planner 要向使用者提問都必須 DELEGATE 此 skill，不得自行逐題提問。
metadata:
  source: project-level dogfooding
  user-invocable: true
name: clarify
---

# clarify

外層 `SKILL.md` 只負責 intake、白話文轉譯與 routing，不承載 route-specific SOP。互動細節全部在 `sub-skill/`。答案回傳 caller 後，由 caller 自行寫回 spec.md 澄清區與重推規格，本 skill 不改任何規格 artifact。

## §1 SOP (Router Only)

### Phase 1 — INTAKE payload

1. READ `references/payload-schema.md` 的共通核心欄位。
2. 依此 payload 為要問的每題準備精準的定位提問，每題 options 在 2..4 範圍內。

### Phase 2 — NORMALIZE questions

1. SORT questions per `references/question-priority.md`（依 `priority_score` 與 `dependencies`）。
2. TRANSLATE `context`／`question`／`options`／`recommendation_rationale`，嚴格遵照 `references/simplified-wording.md` 的白話文友善用字。
3. 產出 `translated_questions`（僅資料準備，不派發工具）。

### Phase 3 — DETECT tooling + ROUTE + route payload validate

1. DETECT runtime tool capabilities（由系統工具清單判斷）。
2. ROUTE 規則（固定優先序）：
   1. IF 可用工具包含 `fork`：route=`specformula-fork`。
   2. ELSE IF 可用工具包含 `AskUserQuestion` 或 `AskQuestion`（或等價 ask-question tool）：route=`ask-user-question`。
   3. ELSE：RETURN `{status: "unsupported_tooling", required: ["fork", "AskUserQuestion|AskQuestion"]}`。
3. IF route=`specformula-fork`，VALIDATE normalized payload against `sub-skill/SpecFormula-Fork/references/payload-schema.md`；IF 不符，RETURN `{status: "incomplete", route: "specformula-fork", missing: [...]}`，不自動降級。
4. IF route=`ask-user-question`，VALIDATE normalized payload against `sub-skill/AskUserQuestion/references/payload-schema.md`；IF 不符，RETURN `{status: "incomplete", route: "ask-user-question", missing: [...]}`。

### Phase 4 — DELEGATE to sub-skill

1. IF route=`specformula-fork`，DELEGATE `sub-skill/SpecFormula-Fork/SKILL.md` with normalized payload。
2. IF route=`ask-user-question`，DELEGATE `sub-skill/AskUserQuestion/SKILL.md` with normalized payload。
3. 透傳子技能回傳的答案給 caller。

## §2 FAILURE CONTRACT

1. `incomplete`：payload 欄位不齊或 route-specific 契約不成立。
2. `unsupported_tooling`：環境缺少可用互動工具。
3. `route_failed`：子技能執行失敗，附上 `route` 與 `reason`。

## §3 CROSS-REFERENCES

1. 不限定 caller。description 已涵蓋「任何 planner 要向使用者提問都必須 DELEGATE 此 skill」；分流規則由 Phase 3.2 的 runtime capability detection 一錘定音，與 caller 身分無關。
2. 本 skill 只回傳答案，不寫任何規格 artifact 或 spec.md；寫回澄清區與重推規格是 caller 的責任。
