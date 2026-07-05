# 工作計畫寫入規則

本檔只規範 `.skillevol/<target-skill>/loop/working-plan.md` 的職責、必要欄位與更新方式。

## Rule 1 — 工作計畫是唯一接續依據

- `.skillevol/<target-skill>/loop/working-plan.md` 是本流程的工作計畫。
- 工作計畫必須記錄目標 skill、目前階段、目前是否在等使用者、測試狀態、正在處理的失敗、選定修改範圍、下一個驗證點與每輪修改紀錄。
- 未知值寫 `待判定`，不得捏造；既有修改紀錄必須保留，只追加或更新當前紀錄。

### Good

情境: 目前停在「等使用者確認補測試方向」。

```
- current phase: Phase 1 — 找出現有驗證抓不到的問題
- active gate: confirm-failing-test-plan
- next verification: 等使用者確認補測試方向
```

結果: 重新開始後可直接知道目前不能跑測試，必須先處理使用者確認。

### Bad

情境: 工作計畫只寫一句摘要。

```
目前正在優化 demo-plan-skill，下一步繼續。
```

結果: 無法判定目前階段、是否已取得使用者同意、或是否可以開始修改。

預期改法:

- 依 `templates/working-plan.template.md` 補齊正式欄位，至少寫出 current phase、active gate 與 next verification。

## Rule 2 — 待辦必須反映目前階段與目前步驟

- loop 開始後必須用執行環境的 TODO 或等效任務工具建立待辦。
- 第一層待辦對應 `SKILL.md` 的各個 Phase。
- 只有目前正在執行的 Phase 需要展開成步驟待辦；尚未進入的 Phase 不預先展開。
- 每完成一步，就更新待辦與工作計畫。

### Good

情境: 目前剛進 Phase 2。

```
Phase:
- Phase 0 completed
- Phase 1 completed
- Phase 2 in_progress
- Phase 3 pending
- Phase 4 pending

Phase 2 steps:
- read 使用者確認
- think 判斷是否確認
- delegate 建立或更新測試
```

結果: 對話壓縮後仍可還原現在卡在 Phase 2 的哪個步驟。

### Bad

情境: 只在聊天中口頭說「我會依序做完」。

```
沒有工具化待辦，也沒有工作計畫更新。
```

結果: 壓縮或重新開始後無法核對漏步，也無法知道確認點是否已通過。

預期改法:

- 立即建立工具化待辦；只展開目前 Phase 的第一層步驟，並在每步完成後更新。
