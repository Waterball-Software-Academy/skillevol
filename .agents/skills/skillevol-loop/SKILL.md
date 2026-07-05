---
name: skillevol-loop
description: 以 eval 為 oracle 反覆演化目標 skill，先用白話 RCA 補上 failing test 與 red gate，再在關鍵 phase 等使用者確認，最後才進入持續 mutation。Use when 使用者要從既有 eval build up 新 skill、用 eval 驅動既有 skill 優化、或要求 loop until eval pass。SKIP when 只是要撰寫 eval（用 skillevol-define-evals）、只跑單一 eval（用 skillevol-run-eval）、只跑整組 benchmark（用 skillevol-run-benchmark），或只改某個已指定部位的 form/derive mutation。
---

# Purpose

Skill 很容易在沒有評量尺時被一次生成得過度完整，也容易在既有失敗上用補丁越疊越厚。
本 skill 在目標 skill 已有 eval oracle，且使用者要讓它從 eval 反推能力、持續演化到全綠時啟用。
它負責判斷何時補 eval、何時確認 red gate、何時選擇最低足夠 mutation、何時跑回歸與 final gate。
若缺少這個 orchestrator，agent 容易把 eval 當參考而不是法官，或在應該重組 skill 時只追加更多文字。

## PRINCIPLE: STRICT SOP

1. 依序不漏步：自底下列 SOP 逐一執行；每做一步，在訊息中「明示該步編號」。
2. 限縮延長推理：僅當 sub-SOP 當步明文標示須 **`think / reasoning`** 時，才拉長內省與推演；否則以最直接可做之 `READ`／`WRITE`／`DELEGATE` 工具呼叫達成該步，省略與該步授權範圍無關的冗長鋪墊，以降低往返等待時間。

## PRINCIPLE: 長流程待辦

長流程會跨多輪對話；在 conversation compact（對話摘要壓縮）之後，執行 AI 仍要靠**同一套待辦清單**還原：目前卡在哪個 **phase**，該 phase 內細項又到哪一格步驟。底下為**兩層**約定：**外層只列 phase**，**進入該 phase** 再把該 sub-SOP 第一層編號步驟拆成子項。尚未開始的 phase 不必預先展開成檔案級細項，以免待辦與實際 `SOP.md` 脫節。

- **必須工具化**：Tier 0／Tier 1 對應的勾選項，**要以執行環境提供的任務／待辦建立與更新能力實體化**（例如 **`TODOCREATE`**、**`TASKCREATE`** 等 tool；或宿主 IDE／Agent 內與之等效的待辦 API），在跑 sub-SOP **當下**就建好清單並隨步驟推進更新狀態。**禁止**只靠聊天裡口頭列點、不經工具建立的「心裡待辦」——壓縮後無法還原，也無法核對漏步。
- **Tier 0（phase）**：對應本檔 `# SOP` 最外層每一個 H2 Phase。這一層的勾選語意是「該 phase 的細項已全部展開**且**已執行完 Phase 其中所有子步驟。」。
- **Tier 1（phase 內細項）**：僅在目前執行中的 phase 建立；對應該 phase 裡**第一層編號步驟**拆解出的動作（`THINK` / `READ`／`WRITE`／`DELEGATE` 等）。編號建議：`(phase序)`、`(phase序-子序)`（例：`1`、`1-1`）；**進入該 phase 時**以 **`TODOCREATE`／`TASKCREATE`（或等效）** 補齊子項。

**(1)** 的子項全部完成後，以 **`TODOCREATE`／`TASKCREATE`（或等效）** 將 Tier 0 之 **(1)** 標為完成，再對 **(2)** 重複「展開 → 跑完」，依序往後。**未完成當前 phase** 前，**不要**為後續 phase 預開檔案層級的細項。


# SOP

## Phase 0 — Intake and Oracle Gate

先確認 target identity、eval oracle、run scope 與當前 loop 停在哪個 gate；缺少可執行 eval 時，這輪 loop 不得開始。
本 phase 的 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md` 必須先落盤；在這兩個檔案寫出前，不得開始 RCA 說明、不得 ask confirm，也不得進入後續 phase。

1. read `GLOSSARY.md`。先對齊 eval 體系專有名詞與白話文，再進入後續 loop 步驟。
2. read 目標 skill、目標 skill 的 `eval/`、使用者 desired state、既有 `.skillevol/<target-skill>/loop/working-plan.md`（若存在）。
   - 這裡的 eval 只指「目標 skill 的 eval oracle」，不是 `skillevol-loop` 自己的 self-eval fixture。
   - 除非使用者明確要求編修 `skillevol-loop/eval/**` fixture，否則執行 loop 時不得讀取本 skill 自己的 `eval/`、`expect.md`、`after/` 或其他 self-test golden。
3. think 初始化 loop state。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 判定目標 skill identity 與 run scope。
   2. 判定 eval oracle 是否存在且可執行；若否，停止並回報應使用 `/skillevol-define-evals`。
   3. 判定 mode 為 build-up 或 evol。
   4. 判定本輪 eval scope 為 unit、dev benchmark 或 final gate。
   5. 判定目前是否卡在 `RCA 待確認`、`red gate 待確認`、或已可直接進入 mutation loop。
4. MUST write `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md`。依 `templates/working-plan.template.md` 渲染或更新 working plan；這是 blocking step，未完成不得開始 RCA、不得 ask confirm。

## Phase 1 — RCA and Failing-Test Proposal

本 phase 的出口不是開始修 skill，而是把「現有測試缺什麼」與「要怎麼補 failing test」壓成可確認提案。
進入本 phase 前，`.skillevol/.gitignore` 與 working-plan 必須已存在；若缺失，先回到 Phase 0 step 4 補齊，不得直接輸出 RCA 文字。

5. think 執行 eval oracle RCA。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 用白話文分析現有 eval 還缺什麼、為什麼目前測不出這次問題。
   2. 判定應擴充既有 dev scenario，還是新增 dev scenario。
   3. 將 RCA 與 failing-test 計畫寫回 working plan。
6. ask user confirm RCA 與 failing-test 計畫。
   1. 若使用者未確認，停止並等待修正後再進入第 2 phase。
   2. 若使用者確認，進入第 7 步。

## Phase 2 — Eval Augmentation and Red Gate

本 phase 先判定現有 eval 是否已足夠充當本輪 red gate；若不足，先補 eval，再用 red gate 證明這條尺真的會抓到目標 failure。

7. think 執行 eval adequacy gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. 若 mode 是 build-up，直接採用現有對應 eval 作為本輪 red gate，進入第 9 步。
   2. 若 mode 是 evol 且現有 eval 已覆蓋此次 desired state，直接採用現有對應 eval 作為本輪 red gate，進入第 9 步。
   3. 若 mode 是 evol 且現有 eval 未覆蓋此次 desired state，進入第 8 步。
8. delegate to SKILL `/skillevol-define-evals`
   - input: 目標 skill、使用者 desired state、現有 eval coverage gap、RCA 結論、以及「擴充既有 dev」或「新增 dev」的提案
9. think 執行 red gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
   1. MUST delegate to SKILL `/skillevol-run-eval`
      - input: 本輪新增、修改或直接採用的 red gate unit
   2. 若新增或修改的 eval 未形成合理 failure，回到第 5 步加嚴 RCA 與 failing-test 計畫。
   3. 若直接採用既有 red gate unit 但 failure 不合理，停止並回報現有 eval 與 desired state 的對應不足。
   4. 若 failure 合理成立，將本輪 red gate 依據、eval artifact 與 red gate 報告摘要寫回 working plan，進入第 10 步。
10. ask user confirm 新增或修改的 eval 與 red gate 結果。
    1. 必須把 eval 怎麼寫、red gate 抓到了什麼、報告路徑在哪裡用白話文公布給使用者看。
    2. 若使用者未確認，停止並等待他對 eval 或報告提出修正。
    3. 若使用者確認，將後續視為「在通過前不要停止」的 loop 授權，進入第 11 步。

## Phase 3 — Mutation Loop

這個 phase 只在第二次確認後才可進入；它負責先鎖定目前要修的 failure，再選最低足夠 mutation，並用單點回歸決定 back edge。

11. delegate to SKILL /skillevol-run-benchmark
    - input: 迭代期間只跑 dev 或本輪相關 unit
12. think 進入 mutation loop。請嚴格遵守 `rules/mutation-selection.md` 來執行此步驟。
    1. 讀取 eval report 的 failure provenance。
    2. 將 failure 分類為 Trigger、Purpose、SOP、Rule、Template、Progressive Disclosure、Delegation、Boundary 或 Bloat failure。
    3. 選擇最低足夠 mutation level：LV1 優先，LV2 謹慎，LV3 最後。
    4. 選擇唯一的本輪 mutator 與 mutation scope。
    5. 若 provenance 不足以選 mutation，停止並回報需要補強 eval report 或 expect。
13. write 更新 `.skillevol/<target-skill>/loop/working-plan.md` 的本輪 iteration 區塊，記錄 failure、provenance、chosen level、chosen mutator、實際 delegate target、假設與下一個驗證點。
14. delegate to SKILL /<chosen-mutator>
    - input: 目標 skill、目標部位、failure provenance、mutation scope、本輪假設
15. delegate to SKILL /skillevol-run-eval
    - input: 本輪 mutation 對應的 failing unit
16. think 判斷 mutation loop 的下一個狀態。請嚴格遵守 `rules/oracle-and-loop.md` 與 `rules/mutation-selection.md` 來執行此步驟。
    1. 若本輪 unit 仍 fail 且 failure provenance 相同，回到第 12 步，重新選擇或升級 mutation。
    2. 若本輪 unit 仍 fail 但 eval provenance 不足，停止並要求補強 eval。
    3. 若本輪 unit pass，進入第 17 步。
    4. 若修復暴露 desired state 未被 eval 覆蓋，回到第 5 步。

## Phase 4 — Benchmark Gates and Close

單點回歸通過後，先跑 dev benchmark，再決定是否進 final gate；holdout 只在最後放行時使用，不回頭當每輪調參工具。

17. delegate to SKILL /skillevol-run-benchmark
    - input: dev benchmark
18. think 判斷 dev benchmark gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
    1. 若 dev benchmark 仍有 failure，回到第 12 步。
    2. 若 dev benchmark 全綠，進入第 19 步。
19. delegate to SKILL /skillevol-run-benchmark
    - input: final gate，納入 holdout
    - skip: 使用者明確指定不跑 holdout
20. think 判斷 final gate。請嚴格遵守 `rules/oracle-and-loop.md` 來執行此步驟。
    1. 若 holdout fail 且顯示 dev eval coverage 不足，回到第 5 步。
    2. 若 holdout fail 且 failure provenance 可定位，回到第 12 步。
    3. 若 final gate pass，進入第 21 步。
21. write 收尾回報。依 `templates/final-report.template.md` 渲染此步驟的成品，並保留每輪 mutation 的 chosen mutator 與實際 delegate target 摘要，讓後續可直接 audit 是否真的委派到對應 mutator。
