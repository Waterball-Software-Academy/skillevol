---
name: skillevol-run-eval
description: 跑單一 eval unit 並評分。Use when 要對某 skill 的某個 eval unit 實際執行、收集 observation、交給 judge 評分並產出 eval-report。SKIP when 要跑整組 benchmark（用 skillevol-run-benchmark）、撰寫 eval（用 skillevol-form-eval 或 skillevol-define-evals）、或只想人工閱讀 eval 結果。
---

# Purpose

單一 eval unit 的價值不只在於跑 target skill，而在於把一次執行轉成可重放、可評分、可追溯 provenance 的觀測紀錄。
本 skill 在已經選定某個 eval unit 時啟用，負責先建立 run-eval 自己的 outer sandbox，再從 sandbox 以 prompt-only 的 Task subagent 黑箱執行 target skill，依 user.md 回答互動斷點，固化 observation，再交給 `/skillevol-eval-judge` 評分。
它只處理一個 unit；選哪些 unit、dev/holdout 排程與整體 pass rate 彙總由 `/skillevol-run-benchmark` 負責。
若缺少這個 transaction boundary，agent 容易把 fixture、run workspace、target identity、benchmark subject、judge input 與 target subagent input 混在一起，導致 oracle 外洩或評分結果不可追溯。


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

## Phase 0 — Intake and Run Bootstrap

先解析 outer unit、target skill、target unit、fixture contract 與既有 run state；這個 phase 只負責定界與恢復，不建立 sandbox、不啟動 target subagent。

1. read 目標 skill、目標 eval unit、unit 的 `before/`、`prompt.md`、`expect.md`、選用 `user.md`／`after/`、可繼承的 `shared/expect.md`，以及既有 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/run-state.md`（若存在）。
2. think resolve run identity。請嚴格遵守 `rules/target-skill-resolution.md` 來執行此步驟。
   1. 判定 `<run-owner>`：本輪 run workspace 的 owner；若正在評估 `skillevol-run-eval` 自身，owner 必須是 `skillevol-run-eval`。
   2. 判定 `<outer-unit>`：本輪被跑的 outer eval unit 名稱。
   3. 判定 `<target-skill>`：本輪要實際執行的 immediate target skill。
   4. 判定 `<target-unit>`：target skill 內部被執行的 eval unit。
   5. 判定 `<phase>` 為 dev 或 holdout。
   6. 若 run-owner、outer-unit、target-skill、target-unit 或 phase 無法判定，停止並回報缺少的 evidence。
3. think validate unit contract。
   1. 必須存在 `before/`、`prompt.md`、`expect.md`。
   2. `user.md` 與 `after/` 是選用檔；缺少時不得自行捏造。
   3. 若有 `shared/expect.md`，必須明確納入本輪 criteria。
   4. 若必要檔案缺失、fixture 結構不完整、或 expect 無法解析，停止且不建立 run workspace。
4. write 建立或更新 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/run-state.md`。
   1. 記錄 run-owner、outer-unit、target-skill、target-unit、phase、fixture 路徑與當前 phase。
   2. 若既有 run-state 存在，優先從 run-state 恢復，不重新發明已確定的 run identity。
   3. 此步只同步 run state；不得預先建立 outer sandbox 或 judge artifact。

## Phase 1 — Oracle Preflight and Sandbox Setup

先決定 oracle contract 是否允許 launch，再依決策建立 outer sandbox；這個 phase 不準備 target opening input，也不處理 responder loop。

5. think 執行 oracle contract preflight。請嚴格遵守 `rules/oracle-contract-preflight.md` 來執行此步驟。
   1. 讀 outer unit `expect.md` 與 nested target `expect.md`（在 fixture `before/` 內）的 `## Hidden oracle metadata`。
   2. Hidden oracle metadata 的正式 schema、合法值、優先序與決策表，一律以 `.agents/skills/skillevol-form-eval/rules/hidden-oracle-metadata.md` 為準。
   3. 依該 schema 判定 `target_run.launch_decision` 與 `oracle_contract.preflight_check`。
   4. 若 `launch_decision = skipped`，只允許建立 deterministic runner artifacts 所需的 outer sandbox 形狀；不得偷跑 target launch。
   5. 若無 metadata 或 `launch_decision = launched`，進入第 6 步。
6. write 重建 outer sandbox 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。
   1. 只刪除或清空該 outer sandbox。
   2. 不修改 eval fixture。
   3. 若 `launch_decision = launched`：將 target eval unit 的 `before/` 複製到 outer sandbox，作為 target subagent 的唯一 CWD；複製後立刻剝離 sandbox 內的 target eval oracle material。請嚴格遵守 `rules/target-skill-resolution.md` Rule 5。
   4. 若 `launch_decision = skipped`：不複製 nested target `before/`；只保留之後要寫的 deterministic runner artifacts。
   5. responder 用的 `user.md` 只從 fixture 的 target unit 路徑讀取，不得從 sandbox 讀取。
   6. 不在 target subagent 執行前把 `expect.md`、`shared/expect.md`、`after/`、judge payload、rubric、expected verdict 或 `user.md` 留在 target subagent 可讀的位置。
7. think 執行 launch branch gate。
   1. 若 `launch_decision = launched`，同步 run-state 後進入第 8 步。
   2. 若 `launch_decision = skipped`，同步 run-state 後進入第 12 步。
   3. 若 launch decision 與 sandbox 形狀不一致，停止並回報 oracle isolation 失敗。

## Phase 2 — Target Launch Gate

只負責準備 minimal opening input 並委派 target subagent；這個 phase 不處理 responder 回答，也不寫 observation。

8. think 準備 target subagent input。
   1. 讀取 target unit 的 `prompt.md` 內容。
   2. 依 `rules/target-skill-resolution.md` Rule 3 渲染 minimal opening input，只包含 sandbox CWD envelope 與 target unit 的 `prompt.md` 原文。
   3. 不得加入 target skill SOP 摘要、expected tool calls、互動腳本、constraints、return schema、觀測需求或任何 parent runner 指令。
   4. 若需要 responder，主 agent 只依 `user.md` 在互動斷點被問到的 topic 回答；不得把整份 `user.md` 提供給 target subagent。
   5. 若任何 expect、after、rubric、provenance、expected verdict、golden output、測試動機或非 minimal envelope 指令會進入 target subagent input，停止並回報 oracle isolation 失敗。
9. delegate to SKILL /<target-skill>
   - input: 透過 Task subagent 執行 target skill；禁止在主 agent context inline 執行 target。
   - input: `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/` 作為 target subagent CWD。
   - input: 第 8 步的 minimal opening input 作為唯一開場 user 輸入。
   - output: 記錄 Task 回傳的 `subagent_id` 供 observation 與 report 引用。

## Phase 3 — Responder Breakpoint Loop

只處理 target subagent 執行期間的互動斷點；每次 resume 只允許送出 answer 本身，不得重貼 runner 包裝或順手洩漏 oracle material。

10. think 判定 target subagent 是否進入互動斷點。請嚴格遵守 `rules/responder-policy.md` 來執行此步驟。
   1. 若 target subagent 沒有互動斷點且已完成，進入第 12 步。
   2. 若 target 只是停止或宣告下一步但未實際發問，MUST NOT 以 user.md 主動 resume；記錄 missing breakpoint 後進入第 12 步。
   3. 若 target subagent 在 clarify 或其他互動斷點停下來、或回傳待答問題，進入第 11 步。
11. think 依 fixture 的 `user.md` 回答 current breakpoint 並 resume Task。
   1. 只回答被問到的 topic，且每次 resume Task 的輸入只放該次 answer 本身（option 值或 free-text 字面值），不得重貼 CWD、prompt、runner 說明或任何包裝前綴。
   2. 若 target subagent 問到 `user.md` 沒有的 topic，依 fallback 回答，不自編值。
   3. 若 target 在未經 parent responder turn 的情況下產出本應由 `user.md` 提供的答案，記錄 `target_run.leaked_oracle_material` 為 suspected，本輪不得 pass。
   4. 若缺少必要 answer key，記錄 missing evidence，不向真人提問。
   5. 每次 resume 後回到第 10 步，直到 target 完成或無法繼續。

## Phase 4 — Observation Realization Gate

不論 target 有沒有 launch，都要把本輪可觀測事實固化成 observation；這個 phase 不做 judge，只處理事實收集與完整性檢查。

12. write observation artifact 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/observation.md`。
   1. 記錄 run-owner、outer-unit、target-skill、target-unit、phase 與 outer sandbox path。
   2. 記錄 `oracle_contract.*`（若 preflight 有讀 metadata）、`target_run.launch_decision`、`target_run.skip_reason`（若 skipped）、`target_run.subagent_id`、`target_run.cwd`、`target_run.prompt_source`、`target_run.opening_input`、`target_run.opening_input_shape` 與 `target_run.leaked_oracle_material`。
   3. 記錄 target assistant messages、tool calls、互動斷點問題、responder answers，以及每次 resume Task 的 exact input 與 `answer-only | non-minimal` 形狀判定。
   4. 記錄終態 fs snapshot。
   5. 記錄缺失 evidence，例如無法取得 event trace、target skill 未呼叫工具、沒有 user.md 但 target skill 發問、遺失 `target_run.subagent_id`，或 oracle material 外洩。
   6. 不記錄 target skill 的 extended thinking 或 reasoning trace。
13. think validate observation artifact。
   1. 若 observation 缺 outer sandbox path、target identity、prompt source、`target_run.subagent_id`、`target_run.cwd`、`target_run.opening_input`、`target_run.opening_input_shape`、`target_run.leaked_oracle_material`、終態 fs、互動紀錄，或任何 responder turn 的 exact resume input / input shape，先補齊可觀測資料。
   2. 若 `target_run.opening_input_shape` 不是 `minimal-cwd-and-prompt-only`，本輪 run-eval verdict 不得 pass。
   3. 若任一 responder turn 的 resume input shape 不是 `answer-only`，本輪 run-eval verdict 不得 pass。
   4. 若 `target_run.leaked_oracle_material` 不是 `none`，本輪 run-eval verdict 不得 pass。
   5. 若缺失無法補齊，保留 missing evidence 註記，不得捏造。
   6. 若 target skill 執行中止，仍進入後續 completion gate，但 observation 必須標明中止點與已取得 evidence。

## Phase 5 — Judge Dispatch Gate

只有 launched run 會建立 judge payload 並委派 judge；若 preflight 已判定 skipped，這個 phase 只負責明示 judge 未被呼叫的理由與後續報告來源。

14. think 執行 judge branch gate。
   1. 若 `launch_decision = launched`，進入第 15 步。
   2. 若 `launch_decision = skipped`，不得呼叫 `/skillevol-eval-judge`；只把 preflight 結論、skip reason 與 deterministic runner evidence 帶進 completion gate。
15. write normalized judge payload 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/judge-input.md`。（僅 `launch_decision = launched`）
   1. 合併 `shared/expect.md` 與 unit `expect.md`。
   2. 列出每條 criteria、rubric、來源檔案與 provenance label。
   3. 包含 observation、target opening input 與 target 終態 fs 路徑。
   4. 不附 target skill 的 extended thinking 或 reasoning trace。
16. delegate to SKILL /skillevol-eval-judge（僅 `launch_decision = launched`）
   - input: `judge-input.md`
   - input: `observation.md`
   - input: outer sandbox 終態 fs
   - input: eval unit 的 `before/` 與選用 `after/`
   - input: 不附 target skill 的 extended thinking 或 reasoning trace

## Phase 6 — Completion Gate

統一收束本輪 observation、judge 結果、oracle isolation 判定與 missing evidence；這個 phase 才負責把 transaction closure 寫成最終 eval-report。

17. think 執行 completion gate。
   1. 若 `launch_decision = launched`，檢查 judge output、observation、outer sandbox provenance 與 target subagent provenance 是否一致。
   2. 若 `launch_decision = skipped`，檢查 eval-report 會明示 judge 未被呼叫，且 skip reason、preflight 結論與 deterministic runner evidence 可追溯。
   3. 檢查 oracle isolation 判定、criteria payload 來源與 known missing evidence 是否完整。
   4. 若任何 provenance 或 report basis 不一致，停止並先補齊，不得捏造最終 verdict。
18. write 評分報告至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/eval-report.md`。
   1. 寫入本輪最終 verdict；若 judge 未執行，必須明示 verdict basis 來自 preflight / deterministic runner evidence，而非 judge。
   2. 寫入各 criteria 分數、pass/fail、違規定位與 failure provenance；若 judge 未執行，明示哪些欄位不存在與原因。
   3. 寫入 outer sandbox provenance、target subagent provenance 與 oracle isolation 判定。
   4. 寫入 judge 使用的 criteria payload 來源，或明示 judge branch 被跳過。
   5. 寫入 observation artifact 路徑與 known missing evidence。
