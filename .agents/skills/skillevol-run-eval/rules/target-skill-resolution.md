# run-eval identity 與 sandbox 判定規則

本檔規範 `skillevol-run-eval` 在執行單一 eval unit 前，如何分開判定 run workspace owner、outer unit、target skill 與 target unit，避免把 benchmark subject、run-eval sandbox、target skill 與 target 內部 delegation 混成同一層身份。

## Rule 1 — run-owner 決定 outer sandbox，target-skill 只決定要執行誰

- `<run-owner>` 指的是本輪 run workspace 的 owner，決定 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。
- `<target-skill>` 指的是本輪要用 Task subagent 實際執行的 immediate target skill。
- 當本輪是在評估 `skillevol-run-eval` 自身時，`<run-owner>` 必須是 `skillevol-run-eval`；即使 target skill 是 `/specify`，sandbox 仍屬於 `skillevol-run-eval`。
- `<target-skill>` 不得覆寫 `<run-owner>`；target skill 的輸出只能落在 outer sandbox 內。

### Good

情境: benchmark 正在測 `/skillevol-run-eval`，其中一個 unit prompt 寫著「對 before/ 下的 specify skill，跑它 eval 的 `login-happy` unit 並評分」。

```
run-owner: skillevol-run-eval
outer-unit: skill-specify-內容運作正確_eval通過
target-skill: specify
target-unit: login-happy
run workspace: .skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/
```

結果: 可直接觀測 `skillevol-run-eval` 是否正確建立自己的 outer sandbox，再於 sandbox 內驅動 `/specify`。

### Bad

情境: 同一個 benchmark unit 內，因為 target skill 是 `/specify`，就把 run workspace 放到 target-owned namespace。

```
run workspace: .skillevol/specify/run-evals/dev/login-happy/
```

結果: `skillevol-run-eval` 自身的 transaction boundary 消失，target run 看似成功但無法驗證 outer sandbox、oracle isolation 與 runner provenance。

預期改法:

- 保持 `run-owner = skillevol-run-eval`，並把 `target-skill = specify` 記在 observation 與 report provenance。

## Rule 2 — outer-unit 與 target-unit 必須分開記錄

- `<outer-unit>` 是本輪 `skillevol-run-eval` 被要求執行的 eval unit 名稱，用於 outer sandbox 路徑。
- `<target-unit>` 是 target skill 內部被執行的 nested eval unit 名稱，用於 target provenance 與 judge payload。
- 在簡單情境中兩者可以相同；在評估 meta skill 時兩者通常不同。

### Good

情境: 對 `/skillevol-run-eval` 跑 dev benchmark，兩個 outer units 都要求它去跑 `/specify` 的不同 target units。

```
.skillevol/skillevol-run-eval/benchmark-report.md
.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容運作正確_eval通過/eval-report.md
.skillevol/skillevol-run-eval/run-evals/dev/skill-specify-內容故意留下瑕疵_eval不通過/eval-report.md
```

結果: outer sandbox 可追溯到被評估的 runner unit；target unit 名稱另寫入 observation 與 report。

### Bad

情境: 只記 target unit，丟失 outer unit。

```
.skillevol/skillevol-run-eval/run-evals/dev/login-happy/eval-report.md
```

結果: report 路徑看起來像 `skillevol-run-eval` 自己有 `login-happy` unit，outer eval provenance 變得含混。

預期改法:

- outer sandbox 用 `<outer-unit>`，target unit 只放進 observation、judge payload 與 report provenance。

## Rule 3 — target subagent 必須從 outer sandbox 以 minimal opening input 啟動

- target subagent 的 CWD 必須是 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。
- target subagent 的 opening input 只能由 minimal CWD envelope 與 target unit 的 `prompt.md` 原文組成。CWD 用絕對路徑；Prompt 區塊逐字放入 `prompt.md` 內容。
- target subagent opening input 不得包含 target SOP 摘要、expected tool calls、互動腳本、constraints、return schema、觀測需求或 parent runner 指令。這些資料只能留在 parent observation、judge payload 或 eval report。
- target unit 的 `expect.md`、`shared/expect.md`、`after/`、rubric、expected verdict、golden output 或測試動機不得提供給 target subagent。
- responder 所需的 `user.md` 由 run-eval 主 agent 持有；target subagent 問到對應 topic 時，只回該 topic 的 answer，不整份外洩。

格式:

```
CWD (ONLY work here, never read files out of here):
<absolute outer sandbox path>

Prompt:
<exact target unit prompt.md content>
```

### Good

情境: target unit `既有CartOrder模組_產出結帳類別圖並問施工` 要跑 `/plan-with-class-diagram`。

```
CWD (ONLY work here, never read files out of here):
/Users/johnnypan/Projects/skill-演進式-研究/.skillevol/skillevol-run-eval/run-evals/dev/既有CartOrder模組_產出結帳類別圖並問施工

Prompt:
/plan-with-class-diagram 請實作訂單結帳功能：使用者從 Cart 結帳成 Order，並透過 PaymentGateway 完成付款。先看 `src/main/java/com/example/` 既有程式再規劃。
```

結果: target skill 像真實被測 agent 一樣只看到任務與 sandbox，不會被提示要產哪個 artifact、問哪個確認或回傳哪些觀測資料。

### Bad

情境: 為了讓 subagent 更清楚，把 runner 對 target 行為的期望一起貼給它。

```
CWD (ONLY work here):
/Users/johnnypan/Projects/skill-演進式-研究/.skillevol/skillevol-run-eval/run-evals/dev/既有CartOrder模組_產出結帳類別圖並問施工

Prompt:
/plan-with-class-diagram 請實作訂單結帳功能...

Instructions:
1. Read and follow `.agents/skills/plan-with-class-diagram/SKILL.md` exactly.
2. Produce Mermaid class diagram to `docs/architecture/<feature-scope>.class.mmd`.
3. Ask user to confirm the diagram. Do not proceed until user responds.

Return to parent when done:
- All assistant messages
- All tool calls made
- Full content of the written `.class.mmd` file
```

結果: 即使沒有貼出 expect.md，target 也已被 parent runner 指導成要通過該 unit，eval 失去黑箱驗證力。

預期改法:

- 刪除 `Instructions`、`Constraints`、`Return to parent` 與所有 target 行為提示，只保留 minimal CWD envelope 與 `Prompt:` 區塊。

## Rule 3b — responder resume input 必須是 answer-only

- target subagent 進入互動斷點後，parent responder 每次 `resume` 給 Task 的輸入只能是該次 answer 本身。
- free-text 題就只送字面值；option 題就只送被選 option 的值或 label。不得重貼 CWD、Prompt、runner 解釋、`User response:` 包裝句、`Continue` 指令或任何測試脈絡。
- `resume` 輸入的最小合法形狀就是一段 answer-only payload；因為 session 已存在，parent 不需要再次提供 task framing。

### Good

情境: target subagent 問 `confirm-diagram`，fixture `user.md` 的 answer 是「可以，先這樣。」。

```
可以，先這樣。
```

結果: target 只收到使用者回答本身，不會被注入新的 runner 脈絡。

### Bad

情境: 同一題，parent 在 resume 時又補了測試框架說明。

```
User response to ASK(confirm-diagram):

可以，先這樣。

Continue from Step 7 and ask implement?.
```

結果: 雖然 answer 本身正確，但 `resume` payload 已混入 runner 指令與流程暗示，破壞 oracle isolation。

預期改法:

- `resume` 時只送「可以，先這樣。」這個 answer 本身；其他任何 framing 或指令都留在 parent observation / report，不得送進 target session。

## Rule 4 — target 內部 delegation 不得改寫 target-skill

- `<target-skill>` 一旦依本輪 run-eval immediate target 判定後，不會因 target skill 內部再 delegate 其他 skill 而改變。
- downstream delegation 只屬於 observation、tool trace 與 provenance，不是 run workspace 的根 namespace。

### Good

情境: 本輪 run-eval 要跑 `/specify` 的 `login-happy` unit，而 `/specify` 執行中會 delegate `/clarify` 詢問登入鎖定規則。

`<target-skill>` 保持為 `specify`，`/clarify` 記錄在 observation 與 provenance。

結果: target-skill 維持 `specify`，downstream `/clarify` 只寫進 observation；outer sandbox 仍維持 `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`。

### Bad

情境: 看到 `/specify` 內部 delegate `/clarify` 後，把 `<target-skill>` 改成 `clarify`。

`<target-skill>` = `clarify`

結果: 原本要評分的 `/specify` unit identity 被下游 delegation 取代。

預期改法:

- 保持 `<target-skill>` 為 `specify`，只把 `/clarify` 作為 target run 的 tool-call evidence 與 provenance。

## Rule 5 — 複製 before/ 後必須剝離 sandbox 內的 target eval oracle

- 複製 target unit 的 `before/` 後，sandbox 可能仍含 nested eval 的 `user.md`、`expect.md`、`after/` 或 `eval/shared/expect.md`；這些都不得留在 target subagent 可讀位置。
- MUST 自 sandbox 刪除 `<target-skill>` package 下的整個 `eval/` 樹（`.agents/skills/<target-skill>/eval/` 與 `.claude/skills/<target-skill>/eval/` 若存在皆刪）。
- responder 用的 `user.md` MUST 只從 fixture 的 target unit 路徑讀取，例如 `.agents/skills/<target-skill>/eval/<phase>/<target-unit>/user.md`；不得從 sandbox 讀取。
- observation MUST 記錄 `sandbox_oracle_stripped: yes` 與被刪除的路徑摘要。

### Good

情境: outer sandbox 已從 login-happy 的 before/ 複製完成。

```
deleted from sandbox:
- .agents/skills/specify/eval/
- .claude/skills/specify/eval/
responder reads user.md from:
- .agents/skills/specify/eval/dev/login-happy/user.md
target subagent cwd still has specify SKILL.md, rules/, and empty specs/
```

結果: target 無法從 filesystem 讀到 answer key；clarify 斷點只能經 parent responder 取得答案。

### Bad

情境: 只複製 before/，未刪 sandbox 內 nested eval。

```
sandbox still contains:
- .claude/skills/specify/eval/dev/login-happy/user.md
target subagent reads answer key directly
observation shows no parent responder turn
```

結果: oracle isolation fail；即使 prompt-only，target 仍可從 CWD 讀到答案。

預期改法:

- 複製後立刻刪除 sandbox 內 `<target-skill>/eval/`；`user.md` 只留給 parent responder。
