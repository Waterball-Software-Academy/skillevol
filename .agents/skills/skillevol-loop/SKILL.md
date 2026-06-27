---
name: skillevol-loop
description: 以 eval 為 oracle 反覆演化目標 skill，先用白話 RCA 補上 failing test 與 red gate，再在關鍵 phase 等使用者確認，最後才進入持續 mutation。Use when 使用者要從既有 eval build up 新 skill、用 eval 驅動既有 skill 優化、或要求 loop until eval pass。SKIP when 只是要撰寫 eval（用 skillevol-define-evals）、只跑單一 eval（用 skillevol-run-eval）、只跑整組 benchmark（用 skillevol-run-benchmark），或只改某個已指定部位的 form/derive mutation。
---

# Purpose

Skill 很容易在沒有評量尺時被一次生成得過度完整，也容易在既有失敗上用補丁越疊越厚。
本 skill 在目標 skill 已有 eval oracle，且使用者要讓它從 eval 反推能力、持續演化到全綠時啟用。
它負責判斷何時補 eval、何時確認 red gate、何時選擇最低足夠 mutation、何時跑回歸與 final gate。
若缺少這個 orchestrator，agent 容易把 eval 當參考而不是法官，或在應該重組 skill 時只追加更多文字。

# SOP

1. read `GLOSSARY.md`。先對齊 eval 體系專有名詞與白話文，再進入後續 loop 步驟。
2. read 目標 skill、目標 skill 的 `eval/`、使用者 desired state、既有 `.skillevol/<target-skill>/loop/working-plan.md`（若存在）。
3. think 初始化 loop state。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 判定目標 skill identity 與 run scope。
   2. 判定 eval oracle 是否存在且可執行；若否，停止並回報應使用 `/skillevol-define-evals`。
   3. 判定 mode 為 build-up 或 evol。
   4. 判定本輪 eval scope 為 unit、dev benchmark 或 final gate。
   5. 判定目前是否卡在 `RCA 待確認`、`red gate 待確認`、或已可直接進入 mutation loop。
3. write `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md`。依 `templates/working-plan.template.md` 渲染或更新 working plan。
4. think 執行 eval oracle RCA。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 用白話文分析現有 eval 還缺什麼、為什麼目前測不出這次問題。
   2. 判定應擴充既有 dev scenario，還是新增 dev scenario。
   3. 將 RCA 與 failing-test 計畫寫回 working plan。
5. ask user confirm RCA 與 failing-test 計畫。
   1. 若使用者未確認，停止並等待修正後再進入 red gate。
   2. 若使用者確認，進入第 6 步。
6. think 執行 evol mode 的 eval adequacy gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 若 mode 是 build-up，直接進入第 9 步。
   2. 若現有 eval 已覆蓋此次 desired state，直接進入第 7 步。
   3. 若現有 eval 未覆蓋此次 desired state，進入第 7 步。
7. delegate to SKILL /skillevol-define-evals
   - input: 目標 skill、使用者 desired state、現有 eval coverage gap、RCA 結論、以及「擴充既有 dev」或「新增 dev」的提案
8. think 執行 red gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. delegate to SKILL /skillevol-run-eval
      - input: 本次新增或修改的 eval unit
   2. 若新增或修改的 eval 未形成合理 failure，回到第 4 步加嚴 RCA 與 failing-test 計畫。
   3. 若 failure 合理成立，將新寫出的 eval artifact 與 red gate 報告摘要寫回 working plan，進入第 9 步。
9. ask user confirm 新增或修改的 eval 與 red gate 結果。
   1. 必須把 eval 怎麼寫、red gate 抓到了什麼、報告路徑在哪裡用白話文公布給使用者看。
   2. 若使用者未確認，停止並等待他對 eval 或報告提出修正。
   3. 若使用者確認，將後續視為「在通過前不要停止」的 loop 授權，進入第 10 步。
10. delegate to SKILL /skillevol-run-benchmark
    - input: 迭代期間只跑 dev 或本輪相關 unit
11. think 進入 mutation loop。請嚴格遵守 `rules/mutation-selection.md` 來執行此步驟。
    1. 讀取 eval report 的 failure provenance。
    2. 將 failure 分類為 Trigger、Purpose、SOP、Rule、Template、Progressive Disclosure、Delegation、Boundary 或 Bloat failure。
    3. 選擇最低足夠 mutation level：LV1 優先，LV2 謹慎，LV3 最後。
    4. 選擇唯一的本輪 mutator 與 mutation scope。
    5. 若 provenance 不足以選 mutation，停止並回報需要補強 eval report 或 expect。
12. write 更新 `.skillevol/<target-skill>/loop/working-plan.md` 的本輪 iteration 區塊，記錄 failure、provenance、chosen level、chosen mutator、實際 delegate target、假設與下一個驗證點。
13. delegate to SKILL /<chosen-mutator>
    - input: 目標 skill、目標部位、failure provenance、mutation scope、本輪假設
14. delegate to SKILL /skillevol-run-eval
    - input: 本輪 mutation 對應的 failing unit
15. think 判斷 mutation loop 的下一個狀態。請嚴格遵守 `rules/oracle-and-loop.md` 與 `rules/mutation-selection.md` 來執行此步驟。
    1. 若本輪 unit 仍 fail 且 failure provenance 相同，回到第 11 步，重新選擇或升級 mutation。
    2. 若本輪 unit 仍 fail 但 eval provenance 不足，停止並要求補強 eval。
    3. 若本輪 unit pass，進入第 16 步。
    4. 若修復暴露 desired state 未被 eval 覆蓋，回到第 4 步。
16. delegate to SKILL /skillevol-run-benchmark
    - input: dev benchmark
17. think 判斷 dev benchmark gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
    1. 若 dev benchmark 仍有 failure，回到第 11 步。
    2. 若 dev benchmark 全綠，進入第 18 步。
18. delegate to SKILL /skillevol-run-benchmark
    - input: final gate，納入 holdout
    - skip: 使用者明確指定不跑 holdout
19. think 判斷 final gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
    1. 若 holdout fail 且顯示 dev eval coverage 不足，回到第 4 步。
    2. 若 holdout fail 且 failure provenance 可定位，回到第 11 步。
    3. 若 final gate pass，進入第 20 步。
20. write 收尾回報。依 `templates/final-report.template.md` 渲染此步驟的成品，並保留每輪 mutation 的 chosen mutator 與實際 delegate target 摘要，讓後續可直接 audit 是否真的委派到對應 mutator。
