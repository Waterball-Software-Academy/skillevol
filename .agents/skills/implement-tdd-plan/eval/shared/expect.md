# shared expect — implement-tdd-plan 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：`implement-tdd-plan`（把已確認的 `docs/tdd/*.tdd-plan.md` 落成 source code 與 tests 的 implementation skill）。
- inputs：`before/` 即 CWD 的 fs snapshot，加上 `prompt.md` 一句 context-free user prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` 到跑完 fs 的整體差異）。
- 若 unit 提供 `after/`，`after/` 必須包含完整 repo 終態，且同時覆蓋 production code 與對應 tests。

## Tool calls（橫切）

決定性 invariant：
- 當 prompt 已明示 TDD plan 已確認且已授權施工時，MUST NOT 再 ask `implement?`、confirm-plan，或退回 re-planning。
- MUST NOT 以「只先做第一個 slice」取代 unit-local `expect.md` 已要求的完整實作終態。
- MUST NOT 修改 `docs/tdd/*.tdd-plan.md`、`docs/architecture/*.class.mmd`、需求文件，除非 unit-local `expect.md` 明示覆寫。

## File diff（橫切）

決定性 invariant：
- 若 unit 提供 `after/`，跑完 fs 必須語意等同同 unit 的 `after/`。
- 合法 file diff 必須由 `before/docs/tdd/*.tdd-plan.md` 可見的實作計畫推出；不得額外發明與計畫無關的模組、文件或設定變更。
- 完整實作型 unit 的 `after/` 必須同時包含 production code 與對應 tests；只落 production、不落 tests 視為未完成。

語意 rubric（橫切）：
- 1.0：檔案終態忠實消費 `before/docs/tdd/*.tdd-plan.md`；計畫中的 seam、boundary、failure path 都被 materialize 成對應的 code/tests，且 docs 輸入保持不變。
- 0.7：主要實作與 tests 都存在，但漏掉一個次要 seam、contract test 或 failure path。
- 0.3：只有部分 plan 被實作，或只有 production code 沒有對應 tests。
- 0.0：沒有依 plan 落實作，改回文件/重新規劃，或重問是否要施工。

## Assistant message（橫切）

語意 rubric：
- 1.0：清楚回報已完成實作，點出主要新增/修改的 source 與 test 檔，並簡述 plan 中的 orchestration、boundary 與 failure path 如何落地。
- 0.7：有回報完成與主要檔案，但摘要漏掉 tests、failure path 或 boundary strategy 其中一項。
- 0.3：只說「已完成」而未說明落地內容，或語氣像是仍停在部分 slice。
- 0.0：未回應 implementation 責任，或仍要求使用者再確認是否開始施工。
