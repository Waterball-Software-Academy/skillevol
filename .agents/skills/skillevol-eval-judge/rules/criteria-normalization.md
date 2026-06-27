# 判準消費規則

本檔規範 judge 消費既有 expect 時，如何把判準整理成可追溯的 judge worklist item。

## Rule 1 — judge worklist item 必須使用固定欄位

- judge worklist item 必須使用固定欄位，讓每個待判項都能追到原始 expect、需要的觀測、已取得的證據、缺失證據與目前結果。欄位不固定時，後續打分與 report 會失去穩定輸入。
格式:
```
worklist item:
- source: 原始 expect 條目的來源位置，例如 unit expect.md / Tool calls 或 shared/expect.md / Cross-turn
- criterion: 原始 expect 條目，保留原意，不改寫成新 rubric
- required evidence: 判定此條目需要的觀測類型
- available evidence: caller 已提供、可支撐判斷的觀測
- missing evidence: 缺少的必要觀測；沒有缺口時寫 none
- result: pass、fail、uncertain 或 pending；整理階段可先寫 pending，打分後再更新
```

### Good

情境: judge 正在整理一條 tool-call 判準。

worklist item:
- source: unit expect.md / Tool calls
- criterion: MUST delegate to SKILL /specify
- required evidence: tool calls
- available evidence: pending
- missing evidence: pending
- result: pending

結果: 六個固定欄位都存在，後續 judge 能直接填入證據並更新結果。

### Bad

情境: judge 用自由格式記錄同一條判準。

MUST delegate /specify，等一下看 tool trace。

結果: 缺少 source、required evidence、available evidence、missing evidence 與 result，後續無法穩定追溯。

預期改法:

- 改成固定欄位的 worklist item，補齊 source、criterion、required evidence、available evidence、missing evidence 與 result。

## Rule 2 — expect 與 shared expect 必須作為判準 SSOT

- judge 只消費 caller 提供的 unit expect.md 與 shared/expect.md，不重新設計 eval 結構、不補寫 rubric，也不把 expect 改寫成另一套表單。判準整理的目的只是讓後續打分可追溯回原始 expect 條目。

### Good

情境: expect 已寫明「MUST delegate to SKILL /specify」與「MUST NOT 向真人提問」。

worklist item 1:
- source: unit expect.md / Tool calls
- criterion: MUST delegate to SKILL /specify
- required evidence: tool calls
- available evidence: pending
- missing evidence: pending
- result: pending

worklist item 2:
- source: unit expect.md / Tool calls
- criterion: MUST NOT 向真人提問
- required evidence: interaction trace
- available evidence: pending
- missing evidence: pending
- result: pending

結果: judge 的待判清單直接來自 expect，沒有新增 form 或改寫原意。

### Bad

情境: expect 只有兩條 tool-call 要求。

worklist item:
- source: judge invented
- criterion: Response Quality
- required evidence: assistant message
- available evidence: pending
- missing evidence: pending
- result: pending

結果: judge 把 eval authoring 的責任接過來，產生 expect 沒授權的評分面向。

預期改法:

- 刪除自行新增的評分面向，只保留 expect 與 shared expect 明示或可直接繼承的判準。

## Rule 3 — 判準整理必須只產生 judge worklist

- 整理判準時只記錄原始 expect 條目、需要的觀測、可用證據與缺失證據。這份 worklist 是 judge 的工作清單，不是 eval form，也不是 report template。

### Good

情境: expect 要求寫出 eval-report.md，caller 提供終態 fs snapshot。

worklist item:
- source: unit expect.md / Assistant message and after/
- criterion: CWD 根新增 eval-report.md
- required evidence: filesystem terminal state
- available evidence: final fs snapshot includes eval-report.md
- missing evidence: none
- result: pending

結果: judge 能依既有觀測判分，且每個結論都能追回 expect 與 evidence。

### Bad

情境: 同一條 report 檔案要求。

worklist item:
- source: judge invented section
- criterion: 所有 eval 之後都必須採用 File terminal vs after/ 章節名稱
- required evidence: expect document structure
- available evidence: pending
- missing evidence: pending
- result: pending

結果: judge 把消費端整理誤變成 eval authoring 端規範，混淆了判分與撰寫 eval 的責任。

預期改法:

- 改回 worklist item，只描述本次判斷需要哪些 evidence，不規定未來 expect 應使用什麼章節或名稱。

## Rule 4 — 缺少觀測證據時必須回報缺口

- caller 沒提供足夠觀測時，judge 要在 missing_evidence 中標出缺口，並在必要時給 uncertain。不得用 assistant 宣稱替代 tool calls、event trace 或 fs snapshot；也不得把缺證據直接判 fail，除非 expect 明寫缺證據本身就是違規。

### Good

情境: expect 要驗 tool call，但 caller 只提供 assistant message。

worklist item:
- source: unit expect.md / Tool calls
- criterion: MUST delegate to SKILL /specify
- required evidence: tool calls
- available evidence: assistant message 宣稱已執行
- missing evidence: tool calls 紀錄未提供
- result: uncertain

結果: judge 沒有把宣稱當成觀測事實，caller 可回頭補 tool call trace。

### Bad

情境: 同一條 tool-call 要求，caller 仍未提供 tool calls。

worklist item:
- source: unit expect.md / Tool calls
- criterion: MUST delegate to SKILL /specify
- required evidence: tool calls
- available evidence: assistant message 宣稱已執行
- missing evidence: none
- result: pass

結果: judge 用不可驗證的宣稱取代觀測證據，可能把未發生的行為判成通過。

預期改法:

- 將該項標為 missing_evidence；若此缺口影響關鍵 verdict，整體 verdict 改為 uncertain。
