---
name: skillevol-define-evals
description: 與使用者階段性協作，逐點驗證地為某個目標 skill 定義出遵守 skillevol-form-eval 的 eval（golden benchmark），全程把計畫與進度歸檔。Use when 要為一個 skill 從零建立或補一組 eval、希望逐步確認而非一次傾倒、要把 eval 定義過程歸檔追蹤，或使用者想跑 eval 但目標 skill 尚無 eval。SKIP when 只是要跑既有 eval（用 skillevol-run-eval 或 skillevol-run-benchmark）、只改 eval 的 form（用 skillevol-form-eval）、或要改的是別的 skill 部位。
---

# Purpose

eval 是 skill 自主迭代的 fitness oracle，但定義一把可信的 eval 假設很多、很容易整套理解錯；若一股腦把整棵 eval 樹產出再交付，使用者得一次否決一大堆，痛苦之餘還會逼出「勉強接受校歪的尺」，驗證力當場崩潰。
本 skill 在要為某個目標 skill 從零建立或補一組 eval、且希望與使用者階段性對齊而非一次傾倒時啟用，負責把定義 eval 改造成逐點驗證、有計畫、有進度歸檔的協作，最終產出一組遵守 skillevol-form-eval 的 eval。
若少了這段協作脈絡，agent 容易在根本假設還沒對齊前就大量產出 unit，等使用者發現框架就錯了，已經堆出一堆得整批 reject 的東西。


## PRINCIPLE: STRICT SOP

1. 依序不漏步：自底下列 SOP 逐一執行；每做一步，在訊息中「明示該步編號」。
2. 限縮延長推理：僅當 SOP 當步明文標示須 **`think / reasoning`** 時，才拉長內省與推演；否則以最直接可做之 `READ`／`WRITE`／`DELEGATE` 工具呼叫達成該步，省略與該步授權範圍無關的冗長鋪墊，以降低往返等待時間。

## PRINCIPLE: 長流程待辦

長流程會跨多輪對話；在 conversation compact（對話摘要壓縮）之後，執行 AI 仍要靠**同一套待辦清單**還原：目前卡在哪個 **phase**，該 phase 內細項又到哪一格步驟。底下為**兩層**約定：**外層只列 phase**，**進入該 phase** 再把該 sub-SOP 第一層編號步驟拆成子項。尚未開始的 phase 不必預先展開成檔案級細項，以免待辦與實際 `SOP.md` 脫節。

- **必須工具化**：Tier 0／Tier 1 對應的勾選項，**要以執行環境提供的任務／待辦建立與更新能力實體化**（例如 **`TODOCREATE`**、**`TASKCREATE`** 等 tool；或宿主 IDE／Agent 內與之等效的待辦 API），在跑 sub-SOP **當下**就建好清單並隨步驟推進更新狀態。**禁止**只靠聊天裡口頭列點、不經工具建立的「心裡待辦」——壓縮後無法還原，也無法核對漏步。
- **Tier 0（phase）**：對應本檔 `# SOP` 最外層每一個 H2 Phase。這一層的勾選語意是「該 phase 的細項已全部展開**且**已執行完 Phase 其中所有子步驟。」。
- **Tier 1（phase 內細項）**：僅在目前執行中的 phase 建立；對應該 phase 裡**第一層編號步驟**拆解出的動作（`THINK` / `READ`／`WRITE`／`DELEGATE` 等）。編號建議：`(phase序)`、`(phase序-子序)`（例：`1`、`1-1`）；**進入該 phase 時**以 **`TODOCREATE`／`TASKCREATE`（或等效）** 補齊子項。

**(1)** 的子項全部完成後，以 **`TODOCREATE`／`TASKCREATE`（或等效）** 將 Tier 0 之 **(1)** 標為完成，再對 **(2)** 重複「展開 → 跑完」，依序往後。**未完成當前 phase** 前，**不要**為後續 phase 預開檔案層級的細項。

# SOP

## Phase 0 — Intake and Bootstrap

先判定目標 skill、desired state 與既有 eval 狀態，並建立可跨輪恢復的 working-plan；這個 phase 只負責啟動與定界，不預先落任何 eval artifact。

1. read 目標 skill 的 `SKILL.md`、既有 spec/proposal、既有 `eval/`（若存在）、`skillevol-form-eval` 的 form，以及既有 `.skillevol/<target-skill>/define-evals/working-plan.md`（若存在）。
2. think 執行 intake gate。
   1. 判定目標 skill identity。
   2. 判定本次目標是從零建立 eval、補強既有 eval、還是修正 eval coverage gap。
   3. 若目標 skill 沒有 `eval/`，將本次目標判定為從零建立 eval，進入第 3 步；不得因 eval 缺失 reject。
   4. 判定使用者 desired state 是否足以排出第一批驗證點，尤其要先看得出 output channel 是 file artifact、assistant message、both 還是 no file outcome。
   5. 若目標 skill 或 desired state 不足以定義 eval，停止並回報缺少的資訊。
   6. 若既有 working-plan 存在，優先從 working-plan 恢復狀態，不重新發明點序。
3. delegate to SKILL /skillevol-cli
   - input: command = setup
   - skip: 若 `.skillevol/.gitignore` 已存在且內容為 `**`
4. write 建立或更新 `.skillevol/<target-skill>/define-evals/working-plan.md`。依 `templates/working-plan.template.md` 渲染或保留既有進度，並維護 `Artifact Contract Matrix`。

## Phase 1 — Verification-Point Queue Design

外圈 loop 的唯一真相是 verification-point queue。這個 phase 只負責排序、修正與同步 queue；同一輪只允許一個 current verification point 進入確認或落檔判斷。

5. think 建立或修正 verification-point queue。請嚴格遵守 `rules/verification-points.md` 來執行此步驟。
   1. 依 working-backward 排序，最根本、錯了會讓後面全白做的假設排最前。
   2. 若目標 skill 涵蓋多個可選 subcommand，且 chosen subcommand 會影響 unit identity、expected artifact path 或 artifact family，必須先單獨對齊 unit naming contract：採 `<subcommand動作>-<情境設定概述>_<預期結果概述>`，把 chosen subcommand 放在 before-segment 最前面。
   3. 若目標 skill 預期產出 file artifact，必須在 naming contract（若適用）已釘住之後、且在「可跑骨架」之前排出一個獨立的 artifact output contract 驗證點，先對齊 output channel、expected artifact path、allowed diff、forbidden diff、target-visible inputs 與 hidden oracle 邊界。
   4. 若某條 unit 需要 runner / judge 在 target launch 前先做契約判讀，必須額外確認是否使用 `expect.md` 的可選區塊 `## Hidden oracle metadata`，並對齊 metadata schema、消費者與不可外洩邊界。
   5. 每個 verification point 只承載一個可 confirm/reject 的假設。
   6. 若使用者曾 reject 某點，修正該點與所有依賴它的後續點。
   7. 將 queue、狀態、依賴關係同步回 working-plan。
6. think 進入 verification-point loop。
   1. 若存在狀態為 `pending-confirmation` 的最上游 verification point，選它並進入第 7 步。
   2. 若沒有待確認點，但存在 `confirmed` 或 `artifact-pending` 的點，進入第 10 步。
   3. 若所有點都已確認且 artifact 已產出，進入第 13 步。
   4. 若 queue 為空或狀態不一致，停止並回報 working-plan 需要修正。

## Phase 2 — Current Point Confirmation Gate

這個 phase 只處理 current verification point 的確認結果：confirm 就進 artifact gate，reject 就回頭修 queue，partial answer 就只修當前點草案，不得順手展開其他 verification points。

7. delegate to SKILL /clarify
   - input: current verification point 的具體假設
   - input: 讓使用者能一句 confirm/reject 的草案、選項、圖示或片段
   - input: working-plan 中此點的上游依賴與目前狀態
8. think 分類使用者對 current verification point 的回應。請嚴格遵守 `rules/no-dump-protocol.md` 來執行此步驟。
   1. 若使用者 confirm，將此點標記為 `confirmed`，進入第 10 步。
   2. 若使用者 reject，進入第 9 步。
   3. 若使用者 partial answer 或提出新限制但未確認，更新此點草案後回到第 7 步。
   4. 若使用者改變 desired state，回到第 5 步重排 queue。
9. think 處理 rejected verification point。
   1. 定位被 reject 的上游假設。
   2. 修正 current point。
   3. 標記受影響的下游 verification points 為需重排或待重驗。
   4. 將「原本以為、其實是、改成」寫入 working-plan 修正歷史。
   5. 回到第 5 步。

## Phase 3 — Artifact Realization Gate

只在 current verification point 已確認、且其依賴也已確認時，才可落對應 artifact；若此點只是在對齊概念形狀，就只更新 working-plan，不得預先傾倒 eval 樹。

10. think 執行 artifact realization gate。
    1. 判定剛確認的 verification point 是否足以產出 eval artifact。
    2. 若此點只確認 conceptual shape，僅更新 working-plan，回到第 6 步。
    3. 若此點確認的是 artifact output contract，先更新 `Artifact Contract Matrix`，再判定 downstream unit 是否已具備寫 before/prompt/expect/after 的前提；若 target skill 含多個 subcommand，也要在 expected artifact path 與 sample unit name 中明確帶入 chosen subcommand；若使用 hidden oracle metadata，也要同步更新 metadata schema 與 consumer 欄位。
    4. 若此點依賴的上游假設尚未確認，停止，不得產出 artifact。
    5. 若此點可產出 artifact，進入第 11 步。
11. write 只產出 current verification point 對應的 form-eval-conformant eval artifact。
    1. 不產出尚未確認的 dev unit、holdout unit、shared expect 或 after fixture。
    2. 每個 artifact 必須可追溯到已確認的 verification point。
    3. 若某 unit 的 output channel 是 `file artifact` 或 `both`，`after/` 必須存在，且要完整 materialize expected artifact paths；不得只在 expect.md 寫「MUST 產出某檔」卻省略 after。
    4. 若該 unit 已確認使用 `## Hidden oracle metadata`，只在 `expect.md` 或 `shared/expect.md` 產出該 metadata；不得把同內容複寫到 before/、prompt.md 或 user.md。
    5. 產出後同步 working-plan 的該點狀態、artifact 路徑、`Artifact Contract Matrix` 與下一步。
12. think 判斷是否繼續逐點確認。
    1. 若仍有待確認 verification point，回到第 6 步。
    2. 若仍有已確認但未產出 artifact 的 verification point，回到第 10 步。
    3. 若全部點已確認且 artifact 已產出，進入第 13 步。

## Phase 4 — Completion Gate

所有 verification points 與 artifact 都就位後，才可檢查整組 eval 是否 form-conformant、traceable，且 hidden oracle 邊界沒有外洩。

13. think 執行 completion gate。
    1. 檢查 eval 是否符合 `skillevol-form-eval` 的目錄與內容 form。
    2. 檢查每個 unit 是否可追溯到已確認 verification point。
    3. 檢查 shared expect、dev、holdout、prompt、before、after、user.md 與選用的 `Hidden oracle metadata` 是否與已確認假設一致。
    4. 檢查 `Artifact Contract Matrix` 是否完整：不得有 `after status = missing` 或 `verification status = violated`。
    5. 檢查每個 `output channel = file artifact | both` 的 unit 是否真的有 after/，且 after/ 內含 `expected artifact paths` 列出的全部檔案。
    6. 檢查 hidden oracle material 沒有外洩到 before/、prompt.md 或 user.md。
    7. 檢查所有已宣告的 `Hidden oracle metadata` 都有對應的 consumer（例如 runner / judge / outer evaluator），且未宣告 metadata 的 unit 不被硬套用 metadata-dependent 判準。
    8. 若任何檢查失敗，回到第 5 步或第 10 步處理對應缺口。
    9. 若全部通過，進入第 14 步。
14. write 收尾回報。
    1. 回報 eval 已完整且 form-conformant。
    2. 回報每個 artifact 對應的 verification point。
    3. 回報 working-plan 已反映完整確認、修正與產出軌跡。
