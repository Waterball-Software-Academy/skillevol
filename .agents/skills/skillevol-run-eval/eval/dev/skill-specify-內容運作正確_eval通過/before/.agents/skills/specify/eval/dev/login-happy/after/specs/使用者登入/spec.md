# 會員登入

使用者要以 email 與密碼登入系統；登入失敗多次會鎖定帳號。

## 驗收標準

### 成功登入
- Given 使用者「王小明」已註冊，email 為 `ming@example.com`，密碼為 `Pass1234`
- When 使用者以 `ming@example.com` / `Pass1234` 提交登入表單
- Then 系統導向 `/dashboard`，頁面顯示「歡迎，王小明」

### 密碼錯誤
- Given 使用者「王小明」已註冊，email 為 `ming@example.com`
- When 使用者以 `ming@example.com` / `WrongPass` 提交登入表單
- Then 登入頁顯示「帳號或密碼錯誤」，仍停留在 `/login`

### 連續失敗鎖定
- Given 使用者「王小明」已連續 4 次輸入錯誤密碼
- When 使用者第 5 次以錯誤密碼提交登入
- Then 帳號鎖定 30 分鐘，登入頁顯示「帳號已鎖定，請於 30 分鐘後再試」並顯示剩餘時間

# 澄清 Q&A

## Q1: 登入失敗太多次時，系統要怎麼處理？
- Answer: 失敗 5 次鎖定 30 分鐘，鎖定期間顯示剩餘時間，30 分鐘後自動解鎖。
