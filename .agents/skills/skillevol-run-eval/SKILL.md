---
name: skillevol-run-eval
description: 跑單一 eval unit 並評分。Use when 要對某 skill 的某個 eval unit 實際執行、收集 observation、交給 judge 評分並產出 eval-report。SKIP when 要跑整組 benchmark（用 skillevol-run-benchmark）、撰寫 eval（用 skillevol-form-eval 或 skillevol-define-evals）、或只想人工閱讀 eval 結果。
---

# Purpose

單一 eval unit 的價值不只在於跑 target skill，而在於把一次執行轉成可重放、可評分、可追溯 provenance 的觀測紀錄。
本 skill 在已經選定某個 eval unit 時啟用，負責先建立 run-eval 自己的 outer sandbox，再從 sandbox 以 prompt-only 的 Task subagent 黑箱執行 target skill，依 user.md 回答互動斷點，固化 observation，再交給 `/skillevol-eval-judge` 評分。
它只處理一個 unit；選哪些 unit、dev/holdout 排程與整體 pass rate 彙總由 `/skillevol-run-benchmark` 負責。
若缺少這個 transaction boundary，agent 容易把 fixture、run workspace、target identity、benchmark subject、judge input 與 target subagent input 混在一起，導致 oracle 外洩或評分結果不可追溯。

# SOP

1. read 目標 skill、目標 eval unit、unit 的 `before/`、`prompt.md`、`expect.md`、選用 `user.md`／`after/`，以及可繼承的 `shared/expect.md`。
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
4. think 執行 oracle contract preflight。請嚴格遵守 `rules/oracle-contract-preflight.md` 來執行此步驟。
   1. 讀 outer unit `expect.md` 與 nested target `expect.md`（在 fixture `before/` 內）的 `## Hidden oracle metadata`。
   2. Hidden oracle metadata 的正式 schema、合法值、優先序與決策表，一律以 `.agents/skills/skillevol-form-eval/rules/hidden-oracle-metadata.md` 為準。
   3. 依該 schema 判定 `target_run.launch_decision` 與 `oracle_contract.preflight_check`。
   4. 若 `launch_decision = skipped`，完成第 5 步 sandbox 重置後跳至第 9 步，不執行第 6–8、11–12 步。
   5. 若無 metadata 或 `launch_decision = launched`，進入第 5 步後繼續第 6 步。
5. write 重建 outer sandbox 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。
   1. 只刪除或清空該 outer sandbox。
   2. 不修改 eval fixture。
   3. 若 `launch_decision = launched`：將 target eval unit 的 `before/` 複製到 outer sandbox，作為 target subagent 的唯一 CWD；複製後立刻剝離 sandbox 內的 target eval oracle material。請嚴格遵守 `rules/target-skill-resolution.md` Rule 5。
   4. 若 `launch_decision = skipped`：不複製 nested target `before/`；只保留之後要寫的 deterministic runner artifacts。
   5. responder 用的 `user.md` 只從 fixture 的 target unit 路徑讀取，不得從 sandbox 讀取。
   6. 不在 target subagent 執行前把 `expect.md`、`shared/expect.md`、`after/`、judge payload、rubric、expected verdict 或 `user.md` 留在 target subagent 可讀的位置。
6. think 準備 target subagent input。（僅 `launch_decision = launched`）
   1. 讀取 target unit 的 `prompt.md` 內容。
   2. 依 `rules/target-skill-resolution.md` Rule 3 渲染 minimal opening input，只包含 sandbox CWD envelope 與 target unit 的 `prompt.md` 原文。
   3. 不得加入 target skill SOP 摘要、expected tool calls、互動腳本、constraints、return schema、觀測需求或任何 parent runner 指令。
   4. 若需要 responder，主 agent 只依 `user.md` 在互動斷點被問到的 topic 回答；不得把整份 `user.md` 提供給 target subagent。
   5. 若任何 expect、after、rubric、Provenance、expected verdict、golden output、測試動機或非 minimal envelope 指令會進入 target subagent input，停止並回報 oracle isolation 失敗。
7. delegate to SKILL /<target-skill>（僅 `launch_decision = launched`）
   - input: 透過 Task subagent 執行 target skill；禁止在主 agent context inline 執行 target。
   - input: `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/` 作為 target subagent CWD。
   - input: 第 5 步的 minimal opening input 作為唯一開場 user 輸入。
   - output: 記錄 Task 回傳的 `subagent_id` 供 observation 與 report 引用。
8. think 處理 target subagent 的互動斷點。（僅 `launch_decision = launched`）請嚴格遵守 `rules/responder-policy.md` 來執行此步驟。
   1. 若 target subagent 沒有互動斷點，進入第 9 步。
   2. 若 target 只是停止或宣告下一步但未實際發問，MUST NOT 以 user.md 主動 resume；記錄 missing breakpoint 後進入第 9 步。
   3. 若 target subagent 在 clarify 或其他互動斷點停下來、或回傳待答問題，依 fixture 的 `user.md` 只回答被問到的 topic，且每次 resume Task 的輸入只放該次 answer 本身（option 值或 free-text 字面值），不得重貼 CWD、Prompt、runner 說明或任何包裝前綴；之後 resume Task 直到 target 完成或無法繼續。
   3. 若 target subagent 問到 `user.md` 沒有的 topic，依 fallback 回答，不自編值。
   4. 若 target 在未經 parent responder turn 的情况下產出本應由 `user.md` 提供的答案，記錄 `target_run.leaked_oracle_material` 為 suspected，本輪不得 pass。
   5. 若缺少必要 answer key，記錄 missing evidence，不向真人提問。
9. write observation artifact 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/observation.md`。
   1. 記錄 run-owner、outer-unit、target-skill、target-unit、phase 與 outer sandbox path。
   2. 記錄 `oracle_contract.*`（若 preflight 有讀 metadata）、`target_run.launch_decision`、`target_run.skip_reason`（若 skipped）、`target_run.subagent_id`、`target_run.cwd`、`target_run.prompt_source`、`target_run.opening_input`、`target_run.opening_input_shape` 與 `target_run.leaked_oracle_material`。
   3. 記錄 target assistant messages、tool calls、互動斷點問題、responder answers，以及每次 resume Task 的 exact input 與 `answer-only | non-minimal` 形狀判定。
   4. 記錄終態 fs snapshot。
   5. 記錄缺失 evidence，例如無法取得 event trace、target skill 未呼叫工具、沒有 user.md 但 target skill 發問、遺失 `target_run.subagent_id`，或 oracle material 外洩。
   6. 不記錄 target skill 的 extended thinking 或 reasoning trace。
10. think validate observation artifact。
   1. 若 observation 缺 outer sandbox path、target identity、prompt source、`target_run.subagent_id`、`target_run.cwd`、`target_run.opening_input`、`target_run.opening_input_shape`、`target_run.leaked_oracle_material`、終態 fs、互動紀錄，或任何 responder turn 的 exact resume input / input shape，先補齊可觀測資料。
   2. 若 `target_run.opening_input_shape` 不是 `minimal-cwd-and-prompt-only`，本輪 run-eval verdict 不得 pass。
   3. 若任一 responder turn 的 resume input shape 不是 `answer-only`，本輪 run-eval verdict 不得 pass。
   4. 若 `target_run.leaked_oracle_material` 不是 `none`，本輪 run-eval verdict 不得 pass。
   5. 若缺失無法補齊，保留 missing evidence 註記，不得捏造。
   6. 若 target skill 執行中止，仍進入 judge，但 observation 必須標明中止點與已取得 evidence。
11. write normalized judge payload 至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/judge-input.md`。（僅 `launch_decision = launched`）
    1. 合併 `shared/expect.md` 與 unit `expect.md`。
    2. 列出每條 criteria、rubric、來源檔案與 provenance label。
    3. 包含 observation、target opening input 與 target 終態 fs 路徑。
    4. 不附 target skill 的 extended thinking 或 reasoning trace。
12. delegate to SKILL /skillevol-eval-judge（僅 `launch_decision = launched`）
    - input: `judge-input.md`
    - input: `observation.md`
    - input: outer sandbox 終態 fs
    - input: eval unit 的 `before/` 與選用 `after/`
    - input: 不附 target skill 的 extended thinking 或 reasoning trace
13. write 評分報告至 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/eval-report.md`。
    1. 寫入 verdict、各 criteria 分數、pass/fail、違規定位與 failure provenance。
    2. 寫入 outer sandbox provenance、target subagent provenance 與 oracle isolation 判定。
    3. 寫入 judge 使用的 criteria payload 來源。
    4. 寫入 observation artifact 路徑。
    5. 寫入 known missing evidence。
