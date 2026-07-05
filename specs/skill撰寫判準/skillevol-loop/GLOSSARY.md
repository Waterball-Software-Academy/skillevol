# Skillevol Loop 詞彙表

本檔只收錄執行 `skillevol-loop` 時會反覆出現、且需要穩定稱呼的詞。
若某個詞只在單一步驟使用，優先在該步的 RuleFile 解釋，不放進本表。

## 驗證相關

eval:
一組可重跑、可評分、用來判斷 skill 是否做好的測試集合。白話說，就是「驗證集合」。

eval unit:
驗證集合中的單一測試。白話說，就是「一條測試」。

dev:
開發時會反覆跑的可見測試區。白話說，就是「開發用測試」。

holdout:
最後驗收才跑的測試區。白話說，就是「隱藏測試」。

benchmark:
一次跑一組測試並彙總結果。白話說，就是「整組驗證」。

eval-report.md:
單條測試跑完後的報告，應指出結果、失敗原因與違規位置。白話說，就是「測試報告」。

## 流程相關

working-plan:
`.skillevol/<target-skill>/loop/working-plan.md`。它記錄目前停在哪裡、正在等誰、下一步要驗證什麼。白話說，就是「工作計畫」。

active gate:
工作計畫中表示目前正在等待哪個關卡的欄位。白話說，就是「目前正在等什麼」。

confirm-failing-test-plan:
補測試前的等待點。白話說，就是「先問使用者：這條補測試方向對不對」。

confirm-enter-mutation:
開始修改前的等待點。白話說，就是「先問使用者：這個失敗是否真的代表要修的行為」。

red gate:
新增或選定測試後，先確認它會在目前 skill 上失敗，且失敗內容對應使用者要修的問題。白話說，就是「先證明測試真的抓得到現在的問題」。

final gate:
最後驗收關卡，通常包含隱藏測試。白話說，就是「最終驗證」。

## 修改相關

provenance:
測試報告指出的違規位置，例如哪個檔案、哪個步驟、哪條規則或哪個產物欄位。白話說，就是「報告指出哪裡出錯」。

mutation:
依測試報告對 skill 做的一次修改。白話說，就是「本輪修改」。

mutator:
被委派去執行修改的 skill，例如 `/skillevol-form-sop` 或 `/skillevol-form-template-file`。白話說，就是「修改用 skill」。

mutation scope:
本輪修改允許動到的範圍。白話說，就是「修改範圍」。

chosen mutator:
根據報告判斷應該使用的修改用 skill。白話說，就是「判斷上該用哪個 skill」。

actual delegate target:
本輪實際委派出去的 skill。白話說，就是「實際叫了哪個 skill」。

## 安全邊界

self-test answer material:
本 skill 自己的測試答案、預期結果、golden report 或 hidden oracle material。白話說，就是「本 skill 被測時不該偷看的答案」。

state carrier:
讓流程中斷後能接續的檔案組，主要是 `.skillevol/.gitignore` 與 working-plan。白話說，就是「可恢復狀態檔」。
