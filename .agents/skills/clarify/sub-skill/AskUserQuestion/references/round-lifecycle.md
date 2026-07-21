# Round Lifecycle

Round Opener、Question 與 Sub-question、Sub-round batching、crash-safe 紀錄格式。本 route 只問與收答，不掃描或改動任何規格 artifact。

## Round Opener

Phase A 呼叫 ask-question tool 前，主 agent 先 inline 呈現一行前導敘述（不單獨彈卡片）：

```
--- Round <R>：本回合處理 <N> 題 ---
```

## Question vs Sub-question

1. Question：為了推進一個新的待解疑問而提出，計入 `MAX_QUESTIONS_PER_ROUND`。
2. Sub-question：仍在解決同一個疑問的延伸互動，不計入上限。
   1. ask-question tool 回傳含 `OTHER`（free-text）→ 追問該題的自由文字。
   2. 使用者回答模糊 → 追問釐清。
3. 判定原則：仍在解決同一疑問 → Sub-question；切換至新疑問 → 下一 Question。

## Sub-round Batching + Log

Round 內若因 ask-question tool 硬限（每 call ≤ 4 題）而拆批，每批為一個 Sub-round。每個 Sub-round 完成（Phase B 解析回傳）時立即 append log：

```markdown
## Round <R>

### Sub-round <R>.1
- Q1 (@<location>): <question> → <answer>
- Q2 ...

### Sub-round <R>.2
- Q5 ...
```

crash / compaction 後可從 session 檔恢復已完成進度；未 log 的 Sub-round 視為未完成，需重問。

## Round Closer

全部 Sub-round 完成後呈現：

```
--- Round <R> 完成：已回收 <A> 題答案 ---
```

## 紀錄格式

Session 檔位於 `${CLARIFY_DIR}/<YYYY-MM-DD-HHMM>.md`，首次 Round 開始時建立，檔頭寫入呼叫方傳入的 idea 全文（逐字不改寫）：

```markdown
# Clarify Session <YYYY-MM-DD HH:MM>

## Idea

<使用者原始 idea 全文，逐字複製>

## Round 1

- Q1 (@activities/結帳.mmd:19): 訪客觸發登入歸屬哪個資料夾 → B（features/checkout/）
- Q2 (@activities/結帳.mmd:53): 訂單何時建 → A（送出即建）
```
