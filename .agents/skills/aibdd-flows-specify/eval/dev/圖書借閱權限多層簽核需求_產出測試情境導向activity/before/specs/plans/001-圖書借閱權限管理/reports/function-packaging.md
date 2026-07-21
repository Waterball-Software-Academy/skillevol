# Function Packaging

## `packages/01-圖書借閱權限管理` — added

- rationale: 本 plan 全新需求 L01 圖書借閱權限管理落成單一對外主線——承辦館員送出新讀者借閱權限申請或借閱額度變更 → 依申請借閱額度逐級審核 → 最後一關核准才建立/更新生效借閱權限（駁回即終止），並提供申請清單、審核明細與讀者借閱權限清單/詳情查詢。新讀者申請與借閱額度變更共用同一套逐級審核流程與申請單／讀者借閱權限資料／審核紀錄的狀態生命週期，拆開會連坐改規格；查詢本身不擁有狀態、僅組合此主線的生效資料，故不獨立成 package。boundary 內原無任何 package 可承載，須新開此目錄；本批次 17 筆 pending impact（flows／rules／spec-by-example／api-plan／data-plan／dependency-plan）皆歸此 package 由下游落成 UAT flow 與 feature。
