# working-plan — 為 <目標 skill> 建 eval

## 目標

用 skillevol-form-eval 的 form，為 <目標 skill> 建一組 eval（shared/expect.md + dev/ + 選用 holdout/）。

## 方法（鐵律）

最小成本、逐步驗證、一點一驗。每個驗證點先跟使用者確認假設，confirm 才產出對應 artifact。使用者說「不」= 某個假設錯了，修正計畫、記錄於修正歷史、再續。絕不一次傾倒讓使用者整批 reject。

## State

- mode: create | extend | repair-coverage
- current point: <VP 編號與名稱>
- current status: pending-confirmation | rejected | confirmed | artifact-pending | artifact-written
- blocked reason: <若無則填 none>

## Verification Point Queue（依最根本、最可能整套錯排序）

### 驗證點 1 — <名稱>
- <要對齊的假設，一句>
- status: pending-confirmation | rejected | confirmed | artifact-pending | artifact-written
- depends on: <上游驗證點；若無則填 none>
- artifact: <產物路徑；若尚未產出則填 none>
- last user response: <最近一次使用者回應摘要；若無則填 none>

### 驗證點 2 — <名稱>
- <…>
- status: pending-confirmation | rejected | confirmed | artifact-pending | artifact-written
- depends on: <…>
- artifact: <…>
- last user response: <…>

## Artifact Contract Matrix

### Unit — <unit path 或名稱>
- output channel: file artifact | assistant message | both | no file outcome
- expected artifact paths: <path1, path2；若無則填 none>
- after status: required-written | optional-none | missing
- allowed diff: <只允許的 diff；若純行為則填 none>
- forbidden diff: <不允許的 diff；若無則填 none>
- target-visible inputs: <before/prompt/user 內可見的資訊>
- hidden oracle metadata: <若 unit 使用 `## Hidden oracle metadata`，在此列 schema 與 consumer；若無則填 none>
- hidden oracle judge-only: <只能留在 expect/shared/judge 的 rubric、golden rationale、expected verdict；若無則填 none>
- verification status: pending | confirmed | violated

## 進度

- 現階段：<驗證點 N — 正在做什麼、在等什麼>
- 下一步：<下一個驗證點或動作>

## Artifact Trace

- <artifact path>: <由哪個已確認驗證點產出；若尚無 artifact 則填 none>

## 修正歷史

- <日期>：<原本以為…，其實是…，改成…>
