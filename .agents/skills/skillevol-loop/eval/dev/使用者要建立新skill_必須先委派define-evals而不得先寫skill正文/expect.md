# expect — 使用者要建立新skill_必須先委派define-evals而不得先寫skill正文

繼承 `../../shared/expect.md`。本檔只補「建立新 skill 時必須先走 define-evals，而不是先寫 skill 正文」這個 unit 的特有期望。

## Provenance

- 釘的行為：當使用者要建立全新的 `specify-fr` skill 時，`skillevol-loop` 會先把它視為「尚未有 eval oracle 的新目標」，優先委派 `/skillevol-define-evals` 啟動 eval-first 流程，而不是先草擬 `Purpose`、`SOP` 或其他 skill 正文。
- 為何存在：若 loop 遇到新 skill 意圖時仍先寫正文，就等於繞過 eval-first，回到沒有尺先生成內容的舊路。
- 檔案終態：見同層 `after/`。`after/` 只允許 `.skillevol/**` 變化，代表 loop 與 define-evals 的 workspace 已啟動；`.agents/skills/specify-fr/**` 在這一輪仍必須不存在。

## Run

過程只有一個 turn；本條 eval 不含互動斷點。

### Turn 1 — 結束方式：done

Tool calls
- MUST 寫出 `.skillevol/.gitignore`
- MUST 寫出 `.skillevol/specify-fr/loop/working-plan.md`
- MUST 寫出 `.skillevol/specify-fr/define-evals/working-plan.md`
- MUST NOT 寫出 `.agents/skills/specify-fr/SKILL.md`
- MUST NOT 寫出 `.agents/skills/specify-fr/eval/**`

Assistant message
- 1.0：明確說出 `specify-fr` 是新 skill，現在還沒有 eval 尺，所以這一輪先進入 eval-first 的 define-evals 階段；並用白話提到這個新 skill 的核心輸出形狀是把自然語言需求拆成 1..n 個 FR 區塊，每個 FR 需含「功能 / 規則 / 驗收標準 / 標準情境（Gherkin）」，資訊不足處標 `[待澄清:<問題點>]`；同時明講這一輪不先寫 `Purpose` 或 `SOP`，且點名 `.skillevol/specify-fr/define-evals/working-plan.md` 已作為後續協作入口
- 0.7：有先說要進 define-evals / eval-first，也有說不先寫 SKILL 的正文，但沒有把 `specify-fr` 的 FR 輸出形狀講清楚，或沒指出 define-evals working-plan 路徑
- 0.3：只有泛泛說「先補 eval」或「先走正規流程」，沒有明確禁止先寫 `Purpose` / `SOP`，也沒有指出已經落下 define-evals 的工作計畫
- 0.0：直接開始解釋 `specify-fr` 的設計、先草擬 skill 正文，或只口頭說之後再補 eval

breakpoint：末 turn 為 done，無 breakpoint
