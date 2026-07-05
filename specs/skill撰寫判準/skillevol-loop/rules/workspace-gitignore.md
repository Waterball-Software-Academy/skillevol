# 暫存資料夾隔離規則

本檔只規範 `.skillevol/.gitignore` 的內容與寫入時機。

## Rule 1 — `.skillevol/.gitignore` 必須先於任何分析或修改寫出

- `.skillevol/.gitignore` 是用來隔離本流程暫存資料的檔案，內容固定為兩個星號一行。
- Phase 0 未寫出此檔前，不得開始分析測試缺口、不得詢問使用者確認、不得跑測試、不得修改 skill。
- 若檔案已存在但內容不是兩個星號，必須改回兩個星號。

### Good

情境: loop 剛確認要改哪個 skill。

```
.skillevol/.gitignore
**
```

結果: 暫存測試報告、工作計畫與 sandbox 不會意外變成正式產物。

### Bad

情境: loop 直接先分析測試缺口。

```
先說明現有測試缺口，尚未建立 .skillevol/.gitignore
```

結果: 後續產物沒有隔離邊界，也違反 Phase 0 的第一個檔案要求。

預期改法:

- 先寫 `.skillevol/.gitignore`，內容固定為兩個星號，再進入後續分析或委派。
