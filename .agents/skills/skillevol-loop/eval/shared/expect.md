# shared expect — skillevol-loop 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放 `skillevol-loop` 跨情境共通的期望；個別 unit 只補本情境特有的 RCA、red gate 或 confirmation gate。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段；決定性 invariant 交 working-plan 與關鍵 gate 驗。

## 被測 skill 與通道

- 被測：`skillevol-loop`，負責先補齊 eval 壓力、再根據 fail 反覆演化 target skill。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 user-prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`git diff (before/ ↔ 跑完的 fs)`）。

## Tool calls（橫切）

決定性 invariant：
- MUST 建立或更新 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md`
- 在使用者第二次確認「可以進 mutation loop」之前，MUST NOT delegate mutator、MUST NOT 跑 `/skillevol-run-benchmark`、MUST NOT 開始改 target skill 本體
- 若當前 phase 是 RCA 或 red-gate review，Assistant message 必須停在 ASK(...) 的確認點，不能自行越過 gate

## File diff（橫切）

決定性 invariant：
- 在 pre-mutation 的確認 phase，允許的新增或修改只限 `.skillevol/**`，以及個別 unit 明示允許的 red gate 報告快照
- `.skillevol` 之外的 target skill 檔案在第二次確認前必須保持 byte-identical
- `.skillevol/.gitignore` 在 run 結束後必須存在，且內容為 `**`

語意 rubric（橫切）：
- 1.0：working-plan 清楚記錄現在卡在哪個確認 gate、下一步是什麼、以及為什麼還不能進 mutation
- 0.7：有記錄 gate 與下一步，但 RCA 或 red gate 摘要略不完整
- 0.3：知道要停下來，但 working-plan 沒有把 gate 與原因寫清楚
- 0.0：在未確認前就開始 mutation，或 working-plan 完全沒有反映 gate 狀態

## Assistant message（橫切）

語意 rubric：
- 1.0：用白話文說明缺口或 red gate 結果，不濫用 skillevol 行話，並把本輪確認點壓成單一可回答問題
- 0.7：大致白話，也有確認點，但仍殘留少量內部術語
- 0.3：提到很多內部術語，或一次丟多個耦合決策給使用者
- 0.0：直接說「我現在開始修」，或把 RCA / report 省略成內部黑箱
