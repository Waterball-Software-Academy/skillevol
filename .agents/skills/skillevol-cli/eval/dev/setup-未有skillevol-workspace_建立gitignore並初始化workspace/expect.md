# expect — setup-未有skillevol-workspace_建立gitignore並初始化workspace

繼承 `../../shared/expect.md`。本檔只補 setup 這個 happy path 的特有期望。

## Provenance

- 釘的行為：當 cwd 尚無 `.skillevol/` 時，`skillevol-cli` 的 `setup` 會建立 `.skillevol/` 並 materialize `.skillevol/.gitignore`。
- 為何存在：`skillevol-define-evals` 與 `skillevol-loop` 都依賴這個 bootstrap；若連最小初始化都不穩，後面所有 workspace-based orchestration 都會漂移。

## Run

過程只有一個 turn；本條 eval 不含互動斷點。

### Turn 1 — 結束方式：done

Tool calls
- MUST run `scripts/setup.py` against `.skillevol`
- MUST NOT run `scripts/reset.py`
- MUST NOT ask user questions or delegate

Assistant message
- 1.0：明確回報已建立 `.skillevol/`，並寫入 `.skillevol/.gitignore` = `**`，且說明其他檔案未變。
- 0.7：回報 setup 完成與 `.gitignore` 已存在，但沒有點出變更邊界。
- 0.3：只說初始化完成，沒有交代 `.gitignore` 或 workspace path。
- 0.0：把 setup 說成 reset，或沒提 `.gitignore` 已建立。

breakpoint：末 turn 為 done，無 breakpoint
