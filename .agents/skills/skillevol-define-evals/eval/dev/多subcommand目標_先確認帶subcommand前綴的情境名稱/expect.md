# expect — 多subcommand目標_先確認帶subcommand前綴的情境名稱

繼承 `../../shared/expect.md`。本檔只補 multi-subcommand naming contract 這個 unit 的特有期望。

## Provenance

- 釘的行為：當 target skill 同時有多個可選 subcommand 時，`skillevol-define-evals` 會在第一個 happy unit 設計前，先把 naming contract 單獨攤出來，並用帶 subcommand 前綴的 sample unit name 跟使用者確認。
- 為何存在：若 naming contract 沒先被釘住，後面即使 prompt / expect / after 都合理，unit dirname 仍可能失去辨識力，讓 benchmark report 與 run workspace 分不出正在驗哪個 action。

## Run

過程只有一個 turn；此 unit 會在 naming contract 的確認點停下來。

### Turn 1 — 結束方式：ASK(unit-name-contract) 交還 user

Tool calls
- MUST 寫出 `.skillevol/.gitignore`
- MUST 寫出 `.skillevol/demo-cli/define-evals/working-plan.md`
- MUST NOT 先寫 `.agents/skills/demo-cli/eval/dev/**`

Assistant message
- 1.0：明確說出 `demo-cli` 有 `setup` / `reset` 兩個 subcommand，因此 sample unit name 應採 `setup-未有workspace_建立gitignore並初始化workspace` 這種前綴形式，並只請使用者確認這一點
- 0.7：有提出 `setup-...` sample，但沒有清楚點出它是在避免和 `reset-...` 混淆
- 0.3：只泛泛說「名稱要更清楚」，沒給具體 sample
- 0.0：把 sample 寫成 `未有workspace_建立gitignore並初始化workspace` 這種沒有 subcommand 前綴的 generic name，或一次丟出整棵 eval 樹

breakpoint：ASK(unit-name-contract) 交還 user；responder 依 user.md 作答
