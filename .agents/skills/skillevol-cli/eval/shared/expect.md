# shared expect — skillevol-cli 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡只放 setup 與 reset 共通的期望；個別 scenario 再補自己的 command-specific 斷言。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段；決定性 invariant 交 file diff 與關鍵 tool call 驗。

## 被測 skill 與通道

- 被測：`skillevol-cli`，負責管理 `.skillevol` workspace 的 `setup` 與 `reset`。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 user-prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`git diff (before/ ↔ 跑完的 fs)`）。

## Tool calls（橫切）

決定性 invariant：
- MUST 依本次 command 只執行一支對應的 local script：`scripts/setup.py` 或 `scripts/reset.py`。
- MUST NOT 同一輪同時執行 `setup` 與 `reset`。
- MUST NOT 向 user 追問或 delegate 其他 skill；兩個 command 都能從 cwd 內的 `.skillevol` 狀態直接決定。

## File diff（橫切）

決定性 invariant：
- 只允許修改 `.skillevol/**`。
- `.skillevol/.gitignore` 在 run 結束後必須存在，且內容為 `**`。
- `.skillevol` 之外的檔案必須保持 byte-identical。

語意 rubric（橫切）：
- 1.0：只 materialize 本次 command 要求的 workspace 變更，不多刪、不多建，也不碰 `.skillevol` 之外的檔案。
- 0.7：主要結果正確，但 workspace 邊界或保留項的表達略含糊。
- 0.3：大方向猜對，但多動了不該動的路徑，或 `.gitignore` 契約不穩。
- 0.0：改到 `.skillevol` 外，或 setup / reset 做反。

## Assistant message（橫切）

語意 rubric：
- 1.0：明確說出執行的是 `setup` 或 `reset`、`.skillevol/.gitignore` 狀態，以及新增、保留或刪除了哪些路徑。
- 0.7：有回報 command 與主要結果，但缺少保留 / 刪除細節。
- 0.3：只籠統說完成，沒有交代 workspace 現況。
- 0.0：誤報 command、誤報結果，或宣稱未發生的改動。
