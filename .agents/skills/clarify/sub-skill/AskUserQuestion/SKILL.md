---
name: ask-user-question
description: "clarify AskUserQuestion route。"
metadata:
  user-invocable: false
  source: project-level
---

# clarify sub-skill: AskUserQuestion

此文件只定義 ask-question 路徑。外層 router 已完成 payload intake 與白話文重寫，這裡承接互動、回收答案並回傳 caller。本 route 只問與收答，不改任何規格 artifact。

## Tool preference in this route

1. 優先使用 `AskUserQuestion`。
2. 若環境沒有 `AskUserQuestion`，可用 `AskQuestion`（或等價 ask-question tool）承接同一批問題。
3. 若 ask-question 工具不可用，回傳：

```yaml
status: unsupported_tooling
route: ask-user-question
required:
  - AskUserQuestion
  - AskQuestion
```

## Inputs

1. `normalized_payload.questions[]`
2. route-specific 欄位：
   1. `location`（ask route 使用 `file:line` 或可回溯定位字串）。
   2. 可選 `OTHER` / `free_text: true`（若 caller 提供）。

## References

1. `references/payload-schema.md`：AskUserQuestion route 專屬 payload 契約。
2. `references/ask-user-question-usage.md`。
3. `references/round-lifecycle.md`。
4. `references/config-schema.md`。

## SOP

### Phase A — Batch ask

1. 依 `references/ask-user-question-usage.md` 做 Sub-round batching，每個 tool call 最多 4 題。
2. [USER INTERACTION] 觸發 ask-question tool，等待使用者回覆。

### Phase B — Parse + return

1. 解析答案並還原 option id（含 `OTHER` free-text 情況）。
2. 依 `references/round-lifecycle.md` append Sub-round log（crash-safe）。
3. 全部 Sub-round 完成後 RETURN：

```yaml
status: completed
route: ask-user-question
answers:
  Q1: <label | {choice, text}>
```
