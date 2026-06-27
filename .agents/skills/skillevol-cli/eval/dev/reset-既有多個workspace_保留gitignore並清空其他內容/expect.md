# expect — reset-既有多個workspace_保留gitignore並清空其他內容

繼承 `../../shared/expect.md`。本檔只補 reset 這個 cleanup path 的特有期望。

## Provenance

- 釘的行為：當 `.skillevol/` 內已存在多個 skill workspace 時，`skillevol-cli` 的 `reset` 會保留 `.gitignore`，刪除其餘內容。
- 為何存在：這條 command 是所有 skillevol scratch state 的全域 cleanup；若保留項或刪除邊界錯誤，後續 benchmark 與 working-plan 都可能被舊狀態污染。

## Run

過程只有一個 turn；本條 eval 不含互動斷點。

### Turn 1 — 結束方式：done

Tool calls
- MUST run `scripts/reset.py` against `.skillevol`
- MUST NOT run `scripts/setup.py`
- MUST NOT delete `.skillevol/.gitignore`
- MUST NOT ask user questions or delegate

Assistant message
- 1.0：明確回報執行的是 reset，`.skillevol/.gitignore` 被保留，且其餘 workspace 內容已清空。
- 0.7：回報 reset 完成，但沒有清楚列出保留項與刪除範圍。
- 0.3：只說清掉舊資料，沒有交代 `.gitignore` 是否留下。
- 0.0：刪掉 `.gitignore`、保留多餘 workspace，或把 reset 說成 setup。

breakpoint：末 turn 為 done，無 breakpoint
