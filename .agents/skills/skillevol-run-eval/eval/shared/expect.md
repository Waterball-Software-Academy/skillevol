# shared expect — skillevol-run-eval 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡只放 run-eval 外層責任的跨情境共用期望；個別 scenario 只補該 target unit 的特有期望，衝突時以個別 scenario 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分。若 unit 提供 `after/`，檔案終態由 `after/` imply；若 unit 不提供 outer `after/`，則只驗行為與實跑觀測，不把預期檔案結果手寫成 diff。

本 eval 的核心不是「看起來有呼叫 subagent」，而是 run-eval 必須先建立自己的 outer sandbox，再依 local contract 決定「真的黑箱啟動 target subagent」或「在 preflight 就 hard-fail / runner-only pass」。任何把 target prompt、expect、after、rubric 或測試動機一起交給 subagent 的做法，都不是有效 eval run。
target subagent opening input 只能是 minimal CWD envelope 加 target unit `prompt.md` 原文；額外的 Instructions、Constraints、Return to parent、expected tool calls、ASK 腳本或 artifact 指示都會把 target behavior 變成被提示出來，而不是被測出來。

## 被測 skill 與通道

- 被測：`skillevol-run-eval`，負責把單一 eval unit 轉成可追溯的 runner transaction：建立 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/`，記錄 preflight 判定，必要時才在該 sandbox 內啟動 target subagent，最後寫 observation 與評分報告。
- inputs：`before/` 即 CWD 的 fs snapshot，加上 `prompt.md` 一句 user-prompt；若 target unit 互動，回答來源是該 target unit 自帶的 `user.md`。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` 與跑完後 fs 的差異）。
- 若 unit-local `expect.md` 宣告 `oracle_style` 與 `design_variance`，run-eval 還必須先做 hidden-oracle contract preflight；舊 unit 未宣告 metadata 時，不受此條件式 oracle gate 約束。

## Sandbox execution（橫切，高權重）

決定性 invariant：
- MUST 在每次執行 outer unit 前刪除或清空 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/`。
- 對 `launch_decision = launched` 的 unit：MUST 把 target eval unit 的 `before/` input fs 複製進 outer sandbox 作為唯一 target run CWD。
- 對 `launch_decision = launched` 的 unit：MUST 在複製後自 sandbox 刪除 target skill package 下的整個 `eval/` 樹，使 nested `user.md`、`expect.md`、`after/` 不可被 target subagent 從 CWD 讀取；responder 用的 `user.md` 只從 fixture target unit 路徑讀取。
- 對 `launch_decision = launched` 的 unit：MUST 從 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/` 啟動 Task subagent 執行 target skill；不得從 repo root、outer fixture `before/`、或 `.skillevol/<target-skill>/run-evals/<phase>/<target-unit>/` 啟動。
- MUST NOT 直接把 target run 寫到 fixture `before/`，也不得把 target run 寫到 `.skillevol/<target-skill>/run-evals/<phase>/<target-unit>/` 這種 target-owned workspace。
- MUST NOT 在主 agent context inline 執行 target skill；不得略過 Task 直接在主 agent 內完成 target 的 read/think/write/delegate。

語意 rubric：
- 1.0：outer sandbox 先被重置；若 unit 需要 target launch，target before 被複製進 sandbox，且 target subagent 的 CWD 是該 sandbox；若 unit 明定 skip-launch，sandbox 仍被正確建立，且只留下 deterministic runner artifacts。
- 0.7：sandbox transaction 大致正確，但 provenance 略缺，例如 observation 未完整列出 reset / copy 或 skip 細節。
- 0.3：有建立某種 run workspace，但不是 `skillevol-run-eval` 自己的 outer sandbox，或 launched / skipped 的 transaction boundary 不清楚。
- 0.0：在主 agent inline 跑 target、直接污染 fixture `before/`、沿用 `.skillevol/<target-skill>/...` 當主要執行 sandbox，或在 skip-launch unit 仍偷偷啟動 target。

## Oracle isolation（橫切，高權重）

決定性 invariant：
- 對 `launch_decision = launched` 的 unit：MUST 只把 minimal CWD envelope 與 target unit 的 `prompt.md` 原文作為 target subagent 的開場輸入。合法形狀固定為 `CWD (ONLY work here, never read files out of here):`、絕對 sandbox path、空行、`Prompt:`、`prompt.md` 原文。
- MUST NOT 在 target subagent prompt 中附加 target skill SOP 摘要、expected tool calls、互動腳本、constraints、return schema、觀測需求或 parent runner 指令。
- MUST NOT 把 outer unit 的 `expect.md`、`shared/expect.md`、`after/`、Provenance、rubric、expected verdict、測試動機、golden output 或任何評分提示交給 target subagent。
- MUST NOT 在 target subagent prompt 中暗示本 unit 想驗「應該 pass」「應該 fail」「需要問 clarify」「不要問真人」或任何 eval oracle 結論。
- MUST NOT 讓 target subagent 從 sandbox filesystem 讀到 nested eval 的 `user.md`、`expect.md` 或 `after/`。
- MUST 在 `observation.md` 記錄 `target_run.launch_decision`、`target_run.prompt_source` 與 `target_run.leaked_oracle_material`。
- 對 `launch_decision = launched` 的 unit：`observation.md` 還 MUST 記錄 `target_run.opening_input`、`target_run.opening_input_shape` 與 `sandbox_oracle_stripped`；若沒有外洩，`target_run.leaked_oracle_material` 必須是 `none`，`target_run.opening_input_shape` 必須是 `minimal-cwd-and-prompt-only`。
- 對 `launch_decision = skipped` 的 unit：`target_run.opening_input` 必須是 `none`，`target_run.opening_input_shape` 必須是 `not-applicable-no-launch`。

語意 rubric：
- 1.0：launched unit 的 target subagent opening input 是 minimal CWD envelope + nested target `prompt.md` 原文；skip-launch unit 則明確記錄為 not-applicable，且 oracle material 外洩為 none。
- 0.7：oracle boundary 大致正確，但 observation 未完整記錄 opening input 或 skip reason，仍可由其他 evidence 推回。
- 0.3：subagent input 混入非答案級的測試脈絡或 runner 說明，例如「這是 run-eval 的 happy path」或「請回傳你的工具呼叫」；或 skip-launch unit 的 not-applicable 邊界不清楚。
- 0.0：subagent input 含 expect、after、rubric、Provenance、expected verdict、golden output、target SOP 操作提示、ASK 腳本、artifact 指示、return schema，或 skip-launch unit 仍讓 target 看到 hidden oracle。

## Anti-overfit oracle contract（條件式，高權重）

本節只在 unit-local `expect.md` 明示 hidden oracle metadata 時生效；沒有宣告 metadata 的既有 unit 完全不受本節約束，避免破壞既有 3 個 unit 的判準。
hidden oracle metadata 的正式 schema、合法值、優先序與決策表，一律以 `.agents/skills/skillevol-form-eval/rules/hidden-oracle-metadata.md` 為準；本節只驗 `skillevol-run-eval` 是否正確遵守那份契約。

決定性 invariant：
- 當 `design_variance = multi-valid` 且 `oracle_style = exact-after-single-golden` 時，run-eval MUST 將此 oracle contract 判定為非法，並在 target launch 之前 preflight hard-fail。
- 對上述非法 contract，run-eval MUST NOT 啟動 target subagent；MUST NOT 把單一 golden target 設計當成唯一合法 outcome 去黑箱驅動 target。
- 當 `design_variance = multi-valid` 且 `oracle_style = runner-only` 時，run-eval MUST 視為合法 contract；不得因看見 multi-valid metadata 就一律 fail。
- 對所有 `design_variance = multi-valid` 的 unit：outer `after/` MUST NOT 含 target design golden；outer expected artifact 只能保留 deterministic runner artifacts，外加必要的 outer sandbox 結構。
- 適用本節的 unit，`observation.md` MUST 記錄 `oracle_contract.source`、`oracle_contract.oracle_style`、`oracle_contract.design_variance`、`oracle_contract.preflight_check`、`oracle_contract.overfit_risk` 與 `target_run.launch_decision`。

語意 rubric：
- 1.0：正確讀到 metadata，能分辨「題目多解但 oracle 只接受單一 golden」必須 hard-fail，與「題目多解但 oracle 是 runner-only」可以 pass，並把 launched / skipped 決策寫清楚。
- 0.7：有做 metadata 判讀，但 overfit risk 或 launch decision 的說明略缺。
- 0.3：看見 multi-valid metadata 卻只做模糊風險揭露，沒有形成 deterministic hard-fail / pass 決策。
- 0.0：把 multi-valid + exact-after-single-golden 當成正常 target run、或把 multi-valid + runner-only 一律判 fail。

## Tool calls（橫切）

決定性 invariant：
- MUST 依 scenario `prompt.md` 解析 immediate target skill 與 target unit。
- 對 `launch_decision = launched` 的 unit：MUST delegate to scenario `prompt.md` 指定的 target skill。
- 對 `launch_decision = launched` 的 unit：MUST delegate to SKILL /skillevol-eval-judge，並把觀測、終態 fs、unit expect、繼承的 shared expect 與 after/ 一併交給 judge。
- 對 `launch_decision = skipped` 的 unit：MUST NOT delegate to target skill；是否仍呼叫 `/skillevol-eval-judge` 由 local expect 明示，否則以 deterministic runner report 為準。
- MUST NOT 向真人提問。target skill 的互動斷點必須依該 target unit 的 `user.md` 與 responder-policy 作答；若缺少必要 answer key，應在報告中判定 run-eval 無法完成該 unit，而不是臨時問真人。
- MUST NOT 將 target skill 或 judge 的 extended thinking / reasoning trace 傳入評分輸入或報告。

## File diff（橫切）

決定性 invariant：
- MUST 在 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/observation.md` 寫出觀測 artifact，且其中 MUST 含 `outer_sandbox.path`、`target_run.launch_decision`、`target_run.prompt_source`、`target_run.leaked_oracle_material`。
- 對 `launch_decision = launched` 的 unit：`observation.md` 還 MUST 含 `target_run.subagent_id`、`target_run.cwd`、`target_run.opening_input`、`target_run.opening_input_shape`。
- 對 `launch_decision = skipped` 的 unit：`observation.md` 還 MUST 含 `target_run.subagent_id: none`、`target_run.opening_input: none`、`target_run.opening_input_shape: not-applicable-no-launch` 與 `target_run.skip_reason`。
- MUST 在 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/eval-report.md` 寫出評分報告，除非個別 scenario 明確指定不同報告路徑。
- MUST 保持 fixture `before/` byte-identical；target skill 的實際改檔只能發生在 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/`，不得污染 fixture 或寫入 target-owned workspace。
- `eval-report.md` MUST 含頂行 `verdict: pass` 或 `verdict: fail`。
- `eval-report.md` MUST 含觀測摘要、sandbox provenance、oracle isolation 判定、各判準分數、違規定位與 provenance。
- 對 `launch_decision = launched` 的 unit：`eval-report.md` 的 verdict MUST 與 `/skillevol-eval-judge` 對 target unit expect 的整體判定一致；不得在 judge fail 時回報 pass，也不得在 judge pass 時回報 fail。
- 對 `launch_decision = launched` 的 unit：缺少 outer sandbox、缺少 `target_run.subagent_id`、`target_run.opening_input_shape` 不是 `minimal-cwd-and-prompt-only`、或 `target_run.leaked_oracle_material` 不是 `none` 時，整體 run-eval verdict MUST fail，即使 target unit 本身產物正確。
- 對 `launch_decision = skipped` 且 local contract 要求 hard-fail 的 unit：整體 verdict MUST fail。
- 對 `launch_decision = skipped` 且 local contract 標明 runner-only pass 的 unit：整體 verdict MUST pass。

語意 rubric（橫切）：
- 1.0：報告清楚分開「run-eval 自身是否正確執行」與「target 是否有被啟動或被 preflight 擋下」，並用 provenance 指到 outer sandbox、target skill、unit 名稱、prompt source、expect 來源與 after/ 比對來源。
- 0.7：報告 verdict 正確且有主要分數，但 provenance 或觀測摘要略少，仍可追到 launched / skipped 決策。
- 0.3：報告有 verdict，但分數、違規定位或 provenance 缺漏，使失敗原因需要人工猜測。
- 0.0：沒有報告、verdict 與判準矛盾、缺 outer sandbox、外洩 oracle 給 target subagent，或把 target 的互動問題轉問真人。

## Assistant message（橫切）

語意 rubric：
- 1.0：回報已完成哪個 target skill 的哪個 unit、outer sandbox path、target 是 launched（含 subagent id）或 skipped（含 skip reason）、oracle isolation 結果、整體 verdict、報告路徑 `.skillevol/skillevol-run-eval/run-evals/<phase>/<outer-unit>/eval-report.md`，並簡短說明是否有 overfit 或其他違規。
- 0.7：回報 verdict 與報告路徑，且提到 sandbox 與 launched / skipped，但 sandbox path、subagent id 或 skip reason 略缺。
- 0.3：只籠統說「已完成」或「有寫報告」，沒有明確 verdict、路徑，或未說明 target 是否真的在 sandbox subagent 執行。
- 0.0：宣稱完成但未寫報告、在主 agent inline 跑 target、向 target subagent 外洩 oracle、要求真人回答 target 的互動問題，或隱瞞 judge fail / preflight fail。
