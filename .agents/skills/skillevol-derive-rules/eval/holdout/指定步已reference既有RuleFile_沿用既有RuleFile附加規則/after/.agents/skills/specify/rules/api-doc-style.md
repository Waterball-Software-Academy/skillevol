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

## Rule 3 — 列表回應分頁時必須回 total 與 page

- 分頁列表只回當頁陣列，client 無法知道總筆數與目前頁次，無從畫頁碼或判斷是否還有下一頁。total 與 page 兩欄把分頁狀態講清楚。

### Good

情境: GET /orders 第 2 頁

回應: { "items": [...], "total": 128, "page": 2 }

### Bad

回應: [ ... ]

預期改法:

- 在回應外層補 total 與 page: { "items": [...], "total": 128, "page": 2 }。
