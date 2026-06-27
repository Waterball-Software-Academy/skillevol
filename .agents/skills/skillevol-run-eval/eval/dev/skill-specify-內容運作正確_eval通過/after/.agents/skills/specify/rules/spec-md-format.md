# spec.md Format

write 步驟產出 `spec.md` 時，依下列 template 填寫。

整體順序：多個 User Story（各為 H1）→ `# 非功能需求（NFR）` section（Raw Idea 無 NFR 時省略）→ `# 澄清 Q&A` section（位於檔案最底部；每題澄清後即時追加，每題一組 question / answer）。

每個 User Story：
- H1 為 Story 標題
- 標題下一句白話描述（不用 As / I want / So that 模板）
- 無法從 Raw Idea 判斷的項目，以 `[待澄清] …` 寫在描述段，不寫成確定的 GWT
- `## 驗收標準` 下，每個 scenario 一個 H3；其下固定三行 bullet：Given / When / Then
- GWT 須含具體業務資料（人名、金額、路徑、畫面文字等），讓使用者可快速判斷邏輯

NFR section 只收效能、資安、相容性等約束，不混進 Story 的 GWT，也不放在 Story 之前。

澄清 Q&A section（SOP 最後每題澄清後即時追加；Q&A 內容須符合 clarify skill 的 `rules/clarify-rules.md` Rule 6）：
- H1 固定為 `# 澄清 Q&A`，須在 NFR 之後、整份 spec.md 最底部
- 每題一個 H2，標題為 `Q{n}: {向 PM 提出的白話問題全文}`
- 標題下固定一行 `- Answer: {使用者選擇或自行說明的完整答案}`
- 依提問順序編號 Q1、Q2…，不得省略任一題
- 第 5 步初稿尚未澄清時，不須預留空白 Q&A section

澄清回寫（SOP 第 8 步；每收到一題答案就 write 一次，不得等全部問完才更新）：
- 替換或移除本題對應的 `[待澄清]` 標記，並將答案融入相關 Story 描述或 GWT
- 若 `# 澄清 Q&A` 尚不存在則新建；若已存在則在 section 末尾追加本題 Q&A
- 完成本題回寫後，呼叫方才繼續下一題澄清

```markdown
# {Story 標題}

{一句白話描述}

[待澄清] {Raw Idea 未提及、需後續澄清的項目}

## 驗收標準

### {Scenario 標題}
- Given {具體前置狀態與業務資料}
- When {具體操作或事件}
- Then {具體可觀察結果}

### {Scenario 標題}
- Given …
- When …
- Then …

# {下一個 Story 標題}

{一句白話描述}

## 驗收標準

### {Scenario 標題}
- Given …
- When …
- Then …

# 非功能需求（NFR）

- {NFR 項目 1}
- {NFR 項目 2}

# 澄清 Q&A

## Q1: {白話問題全文}
- Answer: {使用者答案}

## Q2: {白話問題全文}
- Answer: {使用者答案}
```


## Example

```markdown
# 會員登入

使用者要以 email 與密碼登入系統。

[待澄清] 登入失敗幾次鎖定帳號？Raw Idea 未提及。

## 驗收標準

### 成功登入
- Given 使用者「王小明」已註冊，email 為 `ming@example.com`，密碼為 `Pass1234`
- When 使用者以 `ming@example.com` / `Pass1234` 提交登入表單
- Then 系統導向 `/dashboard`，頁面顯示「歡迎，王小明」

### 密碼錯誤
- Given 使用者「王小明」已註冊，email 為 `ming@example.com`
- When 使用者以 `ming@example.com` / `WrongPass` 提交登入表單
- Then 登入頁顯示「帳號或密碼錯誤」，仍停留在 `/login`

# 訂單退款

使用者要在出貨後 7 天內申請訂單退款。

## 驗收標準

### 7 日內申請成功
- Given 訂單 `#ORD-2024-001` 於 2024-03-01 出貨，金額 1,200 元
- When 使用者於 2024-03-05 提交退款申請，原因填「商品瑕疵」
- Then 訂單狀態為「退款審核中」，後台可見該筆申請

### 超過 7 日拒絕
- Given 訂單 `#ORD-2024-002` 於 2024-02-01 出貨
- When 使用者於 2024-03-15 提交退款申請
- Then 系統顯示「已超過 7 日退款期限」，訂單狀態維持「已完成」

# 非功能需求（NFR）

- 登入 API 回應時間須在 200ms 內（p95）
- 系統須支援 1,000 併發使用者

# 澄清 Q&A

## Q1: 登入失敗太多次時，系統要怎麼處理？
- Answer: 失敗 5 次鎖定 30 分鐘，期間顯示剩餘時間；30 分鐘後自動解鎖。
```