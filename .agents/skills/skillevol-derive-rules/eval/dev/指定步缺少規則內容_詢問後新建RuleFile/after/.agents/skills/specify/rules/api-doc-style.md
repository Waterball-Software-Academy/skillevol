# API 文件格式

## Rule 1 — 每個端點必須含 HTTP 方法與路徑

- 端點的身分由方法與路徑共同決定。少了任一邊，讀者無法定位這支 API，client 也無從呼叫。

### Good

情境: 描述「建立訂單」端點

POST /orders

### Bad

建立訂單的端點

預期改法:

- 補上方法與路徑: 把「建立訂單的端點」寫成 POST /orders。

## Rule 2 — 請求與回應必須各附一個 JSON 範例

- 規格只列欄位名容易各自解讀；一個具體 JSON 範例把欄位型別、巢狀結構、實際值一次釘死，省掉來回確認。

### Good

情境: POST /orders 的請求與回應

請求: { "items": [{ "sku": "A1", "qty": 2 }] }
回應: { "orderId": "2406KX", "status": "created" }

### Bad

請求帶 items，回應帶 orderId 與 status

預期改法:

- 把欄位敘述換成具體 JSON: 請求 { "items": [...] }、回應 { "orderId": "...", "status": "..." } 各一段。

## Rule 3 — 錯誤碼至少標出 400 與 409 的語意

- 400 與 409 是寫入型 API 最常踩的兩種失敗。不標語意，client 無法分辨「是我傳錯」還是「狀態衝突」，錯誤處理就只能瞎猜。

### Good

情境: POST /orders 的錯誤碼

400: 參數錯（缺 items 或 qty 非正整數）
409: 狀態衝突（庫存不足）

### Bad

失敗時回傳適當的錯誤碼

預期改法:

- 至少把 400 與 409 各寫一行語意: 400 參數錯、409 狀態衝突。

## Rule 4 — path 用名詞複數，operationId 用 camelCase

- path 是資源導向、用名詞複數（/orders），動作交給 HTTP 方法；operationId 才放動詞、用 camelCase（createOrder）。兩者錯置會讓 path 長出動詞、operationId 失去可程式化的命名。

### Good

情境: 建立訂單

path: /orders
operationId: createOrder

### Bad

path: /createOrder
operationId: create_order

預期改法:

- path 去掉動詞改名詞複數 /orders；operationId 改 camelCase createOrder。
