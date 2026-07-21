# AskUserQuestion Usage

Phase A 必須透過 Claude Code 內建的 AskUserQuestion tool 呈現批次提問，不得改用純文字對話。

## 載入（首次使用）

AskUserQuestion 是 deferred tool；每個 session 首次使用前需先載入：

```
ToolSearch(query="select:AskUserQuestion", max_results=1)
```

載入成功後才可呼叫。

## 呼叫約定

1. 一個 Sub-round 呼叫一次 AskUserQuestion，帶該批白話文重寫後的題目。
2. 每個 Sub-round ≤ 4 題（工具硬限）。
3. 每題 options ≤ `${MAX_OPTIONS_PER_QUESTION}`（4，含 `OTHER`）。

## 題目組裝

把 payload 翻譯成 AskUserQuestion 的 input：

```yaml
questions:
  - question: |
      Q1: 「訪客觸發登入」的檔案，要跟誰放一起？

      你剛才同意：訪客按「加入購物車」時，要被導到登入頁。

      推薦：B — 訪客被擋下來是因為他想買東西，這更像是「結帳流程的守門員」。

      📍 activities/結帳.mmd:19
    multiSelect: false
    options:
      - label: "A · 跟登入/註冊一起（auth 資料夾）"
        description: "好處：認證類集中；壞處：購物流程被切散"
      - label: "B · 跟結帳一起（checkout 資料夾）  ⭐ 推薦"
        description: "好處：購物旅程完整；壞處：未來獨立登入 feature 會散落"
      - label: "C · 其他（自行填寫）"
        description: "選後於後續訊息補充"
```

## 標註約定

1. 推薦選項 `label` 末尾加 `  ⭐ 推薦`（兩個半形空格 + 星號）。
2. 選項 id 以 label 前綴 `A ·` / `B ·` / `OTHER ·` 表示；解析時以此還原 option id。
3. description 使用「好處：X；壞處：Y」格式。

## 「其他」(OTHER) 選項

payload 含 `id=OTHER` 且 `free_text: true`：

1. `label`：`其他（自行填寫）`。
2. `description`：`選後於後續訊息補充`。
3. 使用者選後，clarify 於 AskUserQuestion 回傳後追問一題 Sub-question（純文字訊息，不計入題數上限）：
   ```
   你選了 Q1 的「其他」，請用一句話描述你的想法：
   ```
4. 取得 free-text 後，併入 `answers.Q1 = {choice: "OTHER", text: "<使用者輸入>"}`。

## 回傳解析

AskUserQuestion 回傳每題所選 label。解析流程：

1. 從 label 前綴抽 option id（`A · ...` → `A`）。
2. 對應 payload 的 option schema。
3. 建構 answers：

```yaml
answers:
  Q1: B
  Q2: A
  Q3:
    choice: OTHER
    text: "希望 callback 失敗時發 email 通知 admin 即可"
```

## Sub-round Batching

1. AskUserQuestion 硬限：每 call ≤ 4 題、每題 options ≤ 4（含 `OTHER`）。
2. 若本 Round 題數 > 4：
   1. 依 `../../../references/question-priority.md` 的 `priority_score` 遞減排序，每 4 題切為一個 Sub-round。
   2. 後續 Sub-round 按序送出：前一 Sub-round 完成 Phase B（append log）後才發下一批。
   3. Sub-round 編號記為 `round_id.sub_round_id`（例 `1.1`、`1.2`）。
3. 超出硬限時主動拆批，不得退回純文字對話。
