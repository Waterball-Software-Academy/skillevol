# Rule 1 -- 僅從 Raw Idea 萃取，禁止腦補

## Good

Raw Idea: 「使用者登入後可以收藏文章」
分析結果只含: 登入、收藏文章；未提及的功能（例如忘記密碼、分享）不寫入 Story。

## Bad

Raw Idea 只提到「收藏文章」，分析卻新增「使用者可以建立收藏資料夾分類」——Raw Idea 未出現，屬腦補。

# Rule 2 -- 以 User Story 為單位區隔

## Good

Raw Idea: 「訪客可以瀏覽商品；會員可以加入購物車並結帳」
區隔為兩個 Story:
- 瀏覽商品（訪客）
- 加入購物車並結帳（會員）

## Bad

整份 Raw Idea 合併成單一 Story「使用者操作商品」，無法對應不同角色與能力邊界。

# Rule 3 -- 模糊處標 [待澄清]，不硬寫成確定規格

## Good

Raw Idea: 「登入失敗太多次要擋住」但未說次數與解鎖方式。
在相關 Story 內標記: `[待澄清] 登入失敗幾次鎖定？鎖定後如何解鎖？Raw Idea 未說明。`

## Bad

Raw Idea 未提次數，卻在分析中寫死「失敗 5 次鎖定 30 分鐘」。

# Rule 4 -- 每個 Scenario 草擬須含業務資料（Spec by Example）

## Good

Story「訂單退款」的 scenario 草擬:
- Given 訂單 `#ORD-2024-001` 狀態為「已出貨」，購買日為 2024-03-01，金額 1,200 元
- When 使用者於 2024-03-05 申請退款並填寫原因「商品瑕疵」
- Then 訂單狀態變為「退款審核中」，後台可見申請紀錄

使用者可從具體訂單編號、日期、金額判斷邏輯是否合理。

## Bad

- Given 使用者有一筆訂單
- When 使用者申請退款
- Then 系統處理退款

僅把 User Story 換句話說，無具體業務資料，使用者無法在短時間內判斷 Gherkin 邏輯對錯。

# Rule 5 -- 業務資料須貼近 Raw Idea 領域，缺值用合理占位並標記

## Good

Raw Idea 提到「VIP 會員免運」，草擬:
- Given 使用者「林小姐」為 VIP 等級「金卡」，購物車含商品「有機燕麥 500g」小計 890 元
- When 使用者選擇宅配結帳
- Then 運費為 0 元，訂單摘要顯示「VIP 免運」

Raw Idea 未給 VIP 等級名稱時:
- Given 使用者為 VIP 等級 `[待澄清: 等級名稱]` …

## Bad

Raw Idea 談電商退款，卻在 scenario 填入 `user_id: 12345`、`status: OK` 等與業務無關的技術占位，無法幫使用者想像流程。
