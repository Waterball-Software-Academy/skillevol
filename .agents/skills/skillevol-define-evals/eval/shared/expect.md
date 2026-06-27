# shared expect — skillevol-define-evals 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放 `skillevol-define-evals` 跨情境共通的期望；個別 unit 只補本情境特有的 naming contract 或 artifact contract。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段；決定性 invariant 交 working-plan 與 file diff 驗。

## 被測 skill 與通道

- 被測：`skillevol-define-evals`，負責用逐點驗證與 working-plan 歸檔的方式，為 target skill 定義 eval。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 user-prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`git diff (before/ ↔ 跑完的 fs)`）。

## Tool calls（橫切）

決定性 invariant：
- MUST 建立或更新 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/define-evals/working-plan.md`
- MUST NOT 在未確認當前 verification point 前，一次傾倒完整 `eval/` 樹
- MUST NOT 向真人逐題往返；若要確認一個點，必須把它壓成單一可 confirm/reject 的 naming 或 contract 提案

## File diff（橫切）

決定性 invariant：
- 允許的新增或修改只限 `.skillevol/**`，以及本 unit 明確確認後才允許的 `eval/**`
- 若本 unit 的 current point 尚未確認，不得先寫 target skill 的 `eval/dev/**`、`eval/holdout/**` 或 `shared/expect.md`
- `.skillevol/.gitignore` 在 run 結束後必須存在，且內容為 `**`

語意 rubric（橫切）：
- 1.0：working-plan 清楚寫出 queue、current point、下一步與修正歷史，且變更邊界與當前 verification point 完全一致
- 0.7：大方向正確，但 working-plan 或 assistant message 少了某個進度欄位
- 0.3：有建 working-plan，但點序、current point 或邊界含糊
- 0.0：沒有 working-plan、偷寫完整 eval 樹，或把未確認的 artifact 先落檔

## Assistant message（橫切）

語意 rubric：
- 1.0：只提出當前 verification point，給出具體 sample，讓使用者一句 confirm/reject 即可
- 0.7：有提當前點，但 sample 還不夠具體
- 0.3：談到很多方向，卻沒有單一可 confirm/reject 的當前點
- 0.0：一次傾倒多個 unit、shared、holdout 或多個互相耦合的決策
