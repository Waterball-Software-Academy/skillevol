---
name: skillevol-loop
description: 先建立可重跑的驗證集合，再依驗證結果反覆改進 skill。Use when 使用者要建立新 skill、補強既有 skill、或要求持續修到驗證通過。SKIP when 只是撰寫驗證案例、只跑單一驗證、只跑整組驗證、或只改某個已指定的 skill 部位。
---

# Purpose

Skill 如果先寫正文、後補驗證，很容易變成看起來完整但無法判斷好壞的指令堆。
本 skill 在使用者要新建或改進 skill 時啟用，先確認是否已有可重跑的驗證集合，這組驗證集合在本流程中稱為 `eval`。
若還沒有 `eval`，本 skill 只啟動建立驗證集合的流程，不先寫 skill 正文。
若已經有 `eval`，本 skill 會先找出現有驗證抓不到的問題，等使用者確認測試方向，再依失敗報告選擇最小修改，直到單點驗證、開發驗證與最終驗證通過。

# SOP

## Phase 0 — 確認目標與建立狀態檔

1. read `GLOSSARY.md`、使用者想達成的結果、目標 skill 路徑或使用者指令中的目標名稱。
2. think 判斷目標 skill 是否明確。
   1. 若目標不明確，停止並詢問使用者要改哪個 skill。
   2. 若目標明確，進入第 3 步。
3. read 目標 skill 狀態、目標 skill 的驗證集合狀態、既有 `.skillevol/<target-skill>/loop/working-plan.md`。
   1. 若目標 skill 存在，讀取目標 skill；否則記錄目標 skill 不存在。
   2. 若目標 skill 的驗證集合存在，讀取該驗證集合；否則記錄缺少驗證集合。
   3. 若既有工作計畫存在，讀取工作計畫；否則記錄目前從入口開始。
   4. 不得讀取 `skillevol-loop/eval/**`、本 skill self-test 的 `expect.md`、`after/`、golden report 或 hidden oracle material。
4. write `.skillevol/.gitignore`。請嚴格遵守 `rules/workspace-gitignore.md` 來執行此步驟。
5. write `.skillevol/<target-skill>/loop/working-plan.md`。請依 `templates/working-plan.template.md` 渲染或更新本檔，並嚴格遵守 `rules/working-plan-state.md`。
6. think 判斷是否要接續舊進度。
   1. 若工作計畫顯示正在等待使用者確認測試方向，進入 Phase 2。
   2. 若工作計畫顯示正在等待使用者確認是否開始修改，進入 Phase 3。
   3. 若工作計畫顯示已在修改迴圈，進入 Phase 3 第 4 步。
   4. 若工作計畫顯示已在整組驗證，進入 Phase 4。
   5. 否則進入第 7 步。
7. think 判斷本輪入口。
   1. 若目標 skill 不存在，進入 Phase 0B。
   2. 若目標 skill 存在但沒有可信的驗證集合，進入 Phase 0B。
   3. 若目標 skill 有可信的驗證集合，且使用者要建立初始能力，進入 Phase 1。
   4. 若目標 skill 有可信的驗證集合，且使用者要改進既有缺陷，進入 Phase 1。

## Phase 0B — 先建立驗證集合，不寫 skill 正文

1. write 工作計畫，記錄目前要先建立驗證集合。
   1. current phase 設為 `Phase 0B — 先建立驗證集合，不寫 skill 正文`。
   2. 目前等待欄位設為 `define-evals-first`。
   3. selected delegate target 設為 `/skillevol-define-evals`。
   4. next verification 設為 `等待 define-evals 的第一個 verification point`。
2. delegate to SKILL /skillevol-define-evals
   - input: target skill name/path
   - input: 使用者想達成的結果
   - input: 目前沒有可信的驗證集合，且本輪不得建立或改寫目標 skill 正文
3. read `.skillevol/<target-skill>/define-evals/working-plan.md`。
4. write 工作計畫，記錄委派結果。
5. think 檢查建立驗證集合的工作計畫是否已出現。
   1. 若 define-evals working-plan 已存在，進入第 6 步。
   2. 若 define-evals working-plan 不存在，停止並回報委派後沒有產出預期狀態檔。
6. write 對使用者的回報。
   1. 說明目標 skill 目前沒有驗證集合。
   2. 說明已進入先建立驗證集合的流程。
   3. 說明本輪沒有先寫 Purpose、SOP 或 skill 正文。
   4. 點名 define-evals working-plan 路徑。

## Phase 1 — 找出現有驗證抓不到的問題

1. think 檢查現有驗證是否涵蓋使用者想修的問題。請嚴格遵守 `rules/coverage-gap.md` 來執行此步驟。
   1. 判斷現有驗證已經覆蓋哪些行為。
   2. 判斷使用者想達成的結果中，哪些還沒有被驗證覆蓋。
   3. 若現有驗證已足夠覆蓋本輪需求，進入 Phase 2 第 4 步。
   4. 若現有驗證不足，進入第 2 步。
2. think 用白話分析現有驗證缺口。請嚴格遵守 `rules/coverage-gap.md` 來執行此步驟。
   1. 說明現有測試已經測了什麼。
   2. 說明現有測試沒測到什麼。
   3. 說明因此漏抓哪個錯誤行為。
   4. 判斷要擴充既有測試，還是新增一條測試。
3. write 工作計畫，記錄缺口分析與補測試提案。
4. write 對使用者的確認問題。
   1. 公布現有驗證缺口。
   2. 公布補測試方向。
   3. 詢問使用者：這條補測試方向是否真的在測你在意的問題。

## Phase 2 — 補測試並確認它真的抓到問題

1. read 使用者對補測試方向的回答。
2. think 判斷使用者是否確認補測試方向。
   1. 若使用者未確認，停止並等待使用者修正缺口分析或補測試方向。
   2. 若使用者已確認，進入第 3 步。
3. delegate to SKILL /skillevol-define-evals
   - input: target skill、使用者想達成的結果、現有驗證缺口、缺口分析結論
   - input: 新增或擴充測試的決策
   - skip: 若 Phase 1 判定現有驗證已覆蓋本輪需求
4. think 選定要先跑的測試。請嚴格遵守 `rules/first-failing-test.md` 來執行此步驟。
   1. 若本輪新增或修改測試，選新增或修改的測試。
   2. 若本輪直接採用既有測試，選能代表使用者需求的既有測試。
   3. 若找不到可代表使用者需求的測試，停止並回報驗證集合與需求對不上。
5. delegate to SKILL /skillevol-run-eval
   - input: 第 4 步選定的測試
   - input: 只跑這一條測試
6. read 該測試的 `eval-report.md` 與必要觀測紀錄。
7. think 判斷測試是否真的抓到問題。請嚴格遵守 `rules/first-failing-test.md` 來執行此步驟。
   1. 若測試失敗，且失敗內容對應使用者要修的問題，進入第 8 步。
   2. 若新增或修改的測試沒有抓到合理失敗，回到 Phase 1 第 2 步。
   3. 若直接採用既有測試但失敗內容不合理，停止並回報既有測試不足以代表使用者需求。
8. write 工作計畫，記錄這條測試與失敗結果。
9. write 對使用者的確認問題。
   1. 公布這條測試在驗什麼。
   2. 公布它抓到什麼失敗。
   3. 點名測試報告路徑。
   4. 詢問使用者：這個失敗是否真的代表你要修的行為，若是才開始自動修改。

## Phase 3 — 依失敗報告選最小修改

1. read 使用者對是否開始修改的回答。
2. think 判斷使用者是否同意開始修改。
   1. 若使用者未確認，停止並等待使用者修正測試或失敗判斷。
   2. 若使用者已確認，進入第 3 步。
3. write 工作計畫，記錄使用者已同意開始修改。
4. delegate to SKILL /skillevol-run-benchmark
   - input: 開發用測試，或本輪相關測試
   - input: 找出目前仍失敗的項目
5. read 目前失敗項目的測試報告或整組報告。
6. think 根據報告選擇本輪修改。請嚴格遵守 `rules/mutation-selection.md` 來執行此步驟。
   1. 讀取報告指出的違規位置。
   2. 分類失敗類型。
   3. 選擇唯一修改層級。
   4. 選擇唯一要委派的修改 skill。
   5. 選擇唯一修改範圍。
   6. 若報告沒有指出足夠違規位置，停止並回報需要補強測試報告或評分準則。
7. write 工作計畫，追加本輪修改紀錄。請依 `templates/working-plan.template.md` 追加本輪 iteration。
8. delegate to SKILL /<第 6 步選定的修改用 skill>
   - input: 目標 skill、目標部位、報告指出的違規位置
   - input: 修改範圍、預期改善
9. delegate to SKILL /skillevol-run-eval
   - input: 目前正在修的失敗測試
   - input: 只跑這一條測試
10. read 該測試的 `eval-report.md`。
11. think 判斷單條測試結果。
    1. 若測試通過，進入 Phase 4。
    2. 若測試仍失敗，且違規位置相同，回到第 6 步。
    3. 若測試仍失敗，但違規位置已改變，回到第 5 步。
    4. 若測試仍失敗，且報告沒有指出足夠違規位置，停止並回報需要補強測試報告或評分準則。
    5. 若修改後發現使用者需求沒有被測試覆蓋，回到 Phase 1 第 2 步。

## Phase 4 — 跑整組驗證並收尾

1. delegate to SKILL /skillevol-run-benchmark
   - input: 開發用整組測試
2. read 開發用整組測試報告。
3. think 判斷開發用測試是否全過。
   1. 若仍有失敗，回到 Phase 3 第 5 步。
   2. 若全部通過，進入第 4 步。
4. delegate to SKILL /skillevol-run-benchmark
   - input: 最終驗證，包含最後才跑的隱藏測試
   - skip: 使用者明確指定不跑隱藏測試
5. read 最終驗證報告。
6. think 判斷最終驗證結果。請嚴格遵守 `rules/stop-conditions.md` 來執行此步驟。
   1. 若隱藏測試失敗，且顯示開發用測試缺口不足，回到 Phase 1 第 2 步。
   2. 若隱藏測試失敗，且報告足以定位問題，回到 Phase 3 第 5 步。
   3. 若最終驗證通過，進入第 7 步。
7. write final report。請依 `templates/final-report.template.md` 渲染。
8. write 對使用者的收尾回報。
