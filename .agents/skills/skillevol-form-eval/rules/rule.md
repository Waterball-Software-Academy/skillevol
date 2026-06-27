# eval 形式（golden benchmark 的寫法判準）

本檔本身是一個 RuleFile，用 RuleFile 的形式描述「一個 skill 的 eval 該怎麼寫」。eval 是 skill 自主迭代的 fitness oracle：沒有它，改了 rules / scripts / SOP 也分不清變好還是變壞。

排版守則: 全檔不用粗體、斜體、底線、emoji、裝飾性 blockquote 與箭頭符號；結構只靠 Header、列表與「主題: 內容」。

## Rule 1 — 每個 unit 必須是可重現的實驗

- 一條 eval 要把 inputs / outputs 講到「照著就能跑出來」。可重現契約固定為五步: 開一個空資料夾，把該 unit 的 before/ 放進去成為 CWD，餵入 prompt.md，讓 AI 跑(呼叫 tools、輸出對話、改檔)，再用 expect.md 與 after/ 評分。做不到「放進去就能跑」，這條 eval 就不算寫清楚。

### Good

情境: 一個可跑的 unit

```
dev/specify內容完整_產出規格並通過評分/
  before/.agents/skills/specify/SKILL.md   # 內嵌待處理的狀態，放進去即成 CWD
  prompt.md
  expect.md
```

結果: 任何人放下 before/、餵 prompt 就能重跑。

### Bad

情境: unit 只寫一段散文「假設有個 specify skill，內含某些狀態」。

結果: 沒有真實檔案系統可放，AI 看到的 CWD 不成立，無法重現也無法算 diff。

預期改法:

- 把假設的檔案狀態落成真實的 before/ 目錄，prompt 與 expect 各自成檔。

## Rule 2 — before/ 同時是 droppable input 與 diff base

- before/ 一檔兩用: 它是放進空資料夾就成立的 CWD，也是算「整體變更」的基準。把跑之前的完整、可成立狀態落進去，不是只放要被改的那一小段;要測「不誤改其他檔」就必須有完整基準可比。

### Good

情境: 要測「只動指定步、不波及其他」

```
before/.agents/skills/specify/SKILL.md   # 完整七步的目標 skill
```

結果: 跑完拿 fs 跟 before/ 比，就能判斷有沒有誤改其他步。

### Bad

情境: before/ 只放「待修改的那一步」一小段文字。

結果: 沒有完整基準，誤改其他檔測不到。

預期改法:

- before/ 放下完整、可成立的目標專案狀態。

## Rule 3 — turn 只驗行為，file 終態交給 after/，不寫進 expect

- 一個 turn 是 agent loop 跑到把控制權交還 user(askUserQuestion)或結束的一段。turn 內只驗它的 tool calls 與 assistant message，不放 file diff。檔案的最終結果由 after/ 這份真實快照 imply: before/ 到 after/ 的 diff 就是預期 file outcome，不必在 expect.md 手寫「某檔某欄必為某值」這種散文。
- 若這個 unit 預期 AI 產出 file artifact（新建、改寫或刪除任何檔案），after/ 不是可有可無的佐證，而是必須存在的 oracle 本體。只有純行為、純訊息、無 file outcome 的 unit 才能省略 after/。

### Good

情境: 一個 turn 的 expect

```
### Turn 1 結束: ASK(stack,lang,name,layout)
  Tool calls: MUST askUserQuestion
  Assistant message: 一次批次問完四題，不逐題往返
```

`after/` 另存預期終態 fs。

結果: 行為與終態各歸各位，expect 不重抄檔案內容。

### Bad

情境: expect.md 開一個 Outcome 段，用 invariant 散文列「boundary id 必為 course-api…」，turn 內也列 file diff。

結果: 違反「turn 不含 file diff」與「終態由 after/ imply」，expect 與 after 重複表達同一件事。

預期改法:

- 刪掉 expect 的 file diff/Outcome，把預期終態落成真實 after/ 快照;turn 只留 tool calls 與 message。

### Bad

情境: 規劃型 skill 應新增 `docs/architecture/foo.class.mmd`，但作者只在 expect.md 寫「MUST 產圖」，after/ 留空。

結果: tool calls 雖能斷言「有沒有想要寫圖」，卻不能釘死「是否真的在正確路徑寫出 artifact」；judge 只能靠語意猜，artifact outcome 會漏驗。

預期改法:

- 若期望新增 `docs/architecture/foo.class.mmd`，就在 after/ 真實放下這個檔案；expect.md 只保留 `MUST 寫出 docs/architecture/foo.class.mmd` 這種關鍵工具與訊息斷言。

## Rule 4 — 用 answer-key variants 把 input 對 output 的函數釘成例子

- 互動型 skill 的驗證力來自分支: 同一個 before/ 與 prompt，branch 出多份 user.md，每份配一份 after/。一對 {user.md, after/} 就是函數上一個點「給這組答案，就該得這個終態」;整組 variants 把曲線釘出來。這正是 working backward: 先用真實快照把答案對結果整張表寫死。

### Good

情境: kickoff 的兩個分支

```
variants/使用者選python根目錄_產出python根目錄骨架/   user.md(python_e2e…)   after/(boundary id=course-api、python tail)
variants/使用者選java子目錄_產出java子目錄骨架/      user.md(java_e2e…)     after/(java tail、base_package=com.example.ordersvc)
```

結果: 換答案就換該得的結果，驗得出 skill 有沒有真的消費答案。

### Bad

情境: 只放一份 user.md 與一份 after/。

結果: 只釘一個 happy 點，skill 把某個答案寫死照樣全過，input 對 output 的對應沒被驗到。

預期改法:

- 對「會岔開終態」的答案各開一個 variant;岔不開結果的答案留在同一份 key 裡。

## Rule 5 — user.md 預設「問了才說」，並用 trap 答案測 robustness

- user.md 是模擬使用者的 answer key。每筆 fact 預設只在被問到時才透露、不主動講，逼 skill 去問、不准假設。要測缺口就把某 fact 設 reveal: never;要測壞輸入就放 trap(非法、模糊、不知道)答案，看 skill 會不會 re-ask 或 STOP。乾淨答案永遠測不到 resolution-gate 那類行為。

### Good

情境: 測非法 service 名

```
## Answer key
service name: "My Service"   # trap: 含空白與大寫，非法 kebab，應觸發 re-ask 或標 unresolved
```

結果: 驗到 skill 對壞輸入的處理，不只 happy path。

### Bad

情境: answer key 每筆都給乾淨合法值，且 skill 沒問也主動全給。

結果: 只測得到一切順利的路徑，缺口、壞輸入、該不該停全沒驗到。

預期改法:

- 至少一個 variant 放 trap 或 never 答案;保持「問了才說」，不主動吐。

## Rule 6 — Cross-turn 驗 turn 之間的時序，抓兩端皆盲的違規

- 有互動斷點時才需要 Cross-turn。它驗 turn 跟 turn 的關係: order(誰先誰後)、gates(誰不准在誰前)、liveness(每個 ASK 後必有 ANSWER、不重複問、會終止)。它專抓「逐 turn 看都對、終態看也對，但順序錯了」這種 per-turn 與 after/ 兩端都看不到的違規，最典型是「沒批准就先寫檔」: 先寫後確認與先確認後寫的終態檔案一樣，只有時序斷言抓得到。order/gates/liveness 引用 event trace 的事件，不引用 file diff。

### Good

情境: 寫入要先確認

```
## Cross-turn
gates: 任何 WRITE 不得出現在 CONFIRM 之前
liveness: 每個 ASK 後必有 ANSWER
```

結果: 抓得到「沒批准就動 user 的 repo」這種終態不留痕的安全違規。

### Bad

情境: 只靠 after/ 比終態，不寫任何 Cross-turn。

結果: 先寫後確認因終態相同而漏抓;依賴前序的提問(layout 依 stack)亂序也漏抓。

預期改法:

- 把承載正確性的時序寫成 gates/order;只列真正必要的邊，避免每條 spurious 邊變脆弱。

## Rule 7 — Tool calls 只斷言關鍵、刻意的工具，不逐筆稽核

- tools 通道只看少數承載意圖的關鍵呼叫，例如該訪談就 MUST 呼叫 askUserQuestion、不該打擾使用者就 MUST NOT。逐筆 READ / WRITE 不在此斷言，那些檔案動作的結果已被 after/ 的終態吸收。

### Good

```
## Run
### Turn 1
  Tool calls:
  - MUST 呼叫 askUserQuestion(內容不在 inputs，必須問)
  - MUST NOT 在拿到答案前就 RUN 產生骨架的 script
```

結果: 只釘住「該問就問、別搶跑」的意圖。

### Bad

情境: tools 通道列「必須先 READ SKILL.md、再 READ rules/、再 WRITE…」逐筆對帳。

結果: 把實作細節釘死，skill 換個合理讀寫順序就被判錯，且和 after/ 重複。

預期改法:

- 只保留關鍵工具的 MUST / MUST NOT，刪掉逐筆讀寫對帳。

## Rule 8 — 語意 rubric 用 spec-by-example，給四錨點與具體片段

- 語意維度一律用 0.0 / 0.3 / 0.7 / 1.0 四個錨點，每個錨點掛一段具體 GOOD/BAD 片段，讓 judge 與人工對同一把尺打分。after/ 的比對同理由 judge 判語意等價、非 byte-exact。禁止用「品質要好」「大致正確」這種抽象話當錨點，那無法校準也無法回歸。

### Good

```
## Run
### Turn 1
  Assistant message:
  - 1.0: 一次批次問完四題、問題具體可答、不腦補需求
  - 0.3: 逐題往返
  - 0.0: 沒問就自行假設答案
```

結果: judge 與人對著同一組片段打分，可比、可回歸。

### Bad

```
Assistant message:
- 高分: 對話好。
- 低分: 對話差。
```

預期改法:

- 換成四錨點，每錨點掛一段具體會出現的 GOOD/BAD 片段。

## Rule 9 — 橫切判準放 shared，每 unit 標 provenance、只釘一個主張

- 多個 unit 共用的判準放 shared/expect.md，個別 unit 只補本情境差異，衝突以個別為準。每個 unit 在 Provenance 標清楚「釘哪條行為、為何存在」，且只釘一個 distinct 主張。沒有 provenance，eval 集合會長出重複、沒人知道為何在那的 test;一個 unit 想測多件事，任一失敗都分不清是哪個行為壞了。

### Good

```
## Provenance
- 釘的行為: 互動訪談解出四個決策後，正確 scaffold python_e2e/repo_root。
- 為何存在: happy 互動路徑，且釘住答案對終態的 fidelity。
```

結果: 一個 unit 對一個主張，誰看都知道它守什麼。

### Bad

情境: 一個 unit 同時想測「抽得對 + 不波及 + 缺內容要問 + 時序」，且沒寫 provenance。

結果: 任一條失敗都分不清是哪個行為壞了，重複時也無法去重。

預期改法:

- 一個 unit 拆成各自獨立的主張，共用判準上移 shared，各自補 Provenance。

## Rule 10 — unit name 必須用情境設定與測試結果命名

- unit name 是 golden benchmark 的人類可讀案例身份，必須採用 `<before情境設定概述>_<after測試結果概述>`。底線前描述 before/ 與 prompt.md 建出的測試局面；底線後描述跑完後預期觀測到的結果。
- 若目標 skill 涵蓋多個可選 subcommand，`<before情境設定概述>` 不得只寫 generic 情境，必須把 chosen subcommand 放在最前面，採 `<subcommand動作>-<情境設定概述>`。這樣 benchmark report、run workspace 與失敗摘要才看得出是在驗哪個 action。
- unit name 不要求固定包含 skill 名稱，也不要求固定包含 eval verdict。`skill-specify-內容運作正確_eval通過` 合法，是因為它的 before 情境自然提到 skill-specify，after 結果自然是 eval 通過，不是因為 `skill-` 或 `eval通過` 是固定語法。
- 名稱要能在 benchmark report、run workspace 與失敗摘要中單獨成立。只看 dirname 就該知道「餵了什麼局面」與「預期看到什麼結果」。

### Good

情境: 三個 unit dirname 都同時表達 before 局面與 after 觀測。

```
skill-specify-內容運作正確_eval通過
skill-specify-內容故意留下瑕疵_eval不通過
setup-未有skillevol-workspace_建立gitignore並初始化workspace
非法service名稱_拒絕寫入並要求重填
```

結果: 不打開 expect.md 也能看出每個 unit 的測試局面與預期結果。

### Bad

情境: unit dirname 只寫局部代號、流水號、單邊結果，或在多 subcommand skill 上漏掉 subcommand 前綴。

```
happy
test-1
login-happy
eval-pass
未有skillevol-workspace_建立gitignore並初始化workspace
```

結果: 看不出 before 局面與 after 觀測，benchmark report 只能列出不具診斷力的代號。

預期改法:

- 改成 `<before情境設定概述>_<after測試結果概述>`；若目標 skill 有多個可選 subcommand，before-segment 要再前綴 chosen action，例如 `setup-未有skillevol-workspace_建立gitignore並初始化workspace`、`登入資料完整_直接產出登入規格` 或 `缺少必要資訊_要求澄清後停止`。

## Rule 11 — Hidden oracle metadata 只宣告 input contract，不混入 consumer 衍生決策

- `## Hidden oracle metadata` 只用來宣告 runner / judge / outer evaluator 在 target launch 前需要讀的 hidden oracle input contract。正式 schema、合法值、優先序與決策表，一律以 `rules/hidden-oracle-metadata.md` 為準。
- 若區塊存在，目前正式欄位只有 `oracle_style` 與 `design_variance`，且兩者都必填。不要在這裡混入 consumer 事後推導的欄位，例如 `launch_decision`、`preflight_check`、`skip_reason`、`overfit_risk`。
- `preflight_policy`、`target_launch_policy`、`target_artifact_family` 不屬於正式 preflight schema，不得再寫進 metadata。`exact-target-after` 也不得作為 `oracle_style` 的值；正式值只保留 `exact-after-single-golden`。
- `## Hidden oracle metadata` 只允許寫在 unit-local `expect.md`，不得寫進 `shared/expect.md`。沒有需要時，整節直接刪掉，不要為了「完整」硬留空殼。

### Good

情境: 多解 target 的 preflight contract

```md
## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid
```

結果: fixture 只宣告 input contract；consumer 會依 schema 決定 launched 或 skipped，不需要作者手寫處置。

### Good

情境: 合法的 runner-only positive control

```md
## Hidden oracle metadata

- oracle_style: runner-only
- design_variance: multi-valid
```

結果: fixture 明確說的是「題目多解，且由 runner-only 判定」；沒有把 pass/fail 或 skip 策略重複寫進 metadata。

### Bad

情境: metadata 混入 consumer 衍生欄位與舊別名

```md
## Hidden oracle metadata

- oracle_style: exact-target-after
- design_variance: multi-valid
- preflight_policy: hard-fail-before-target-launch
- target_launch_policy: skip
```

結果: input contract、consumer 決策與舊 alias 混在一起；一旦 runner 決策表更新，fixture 會和 consumer 漂移。

預期改法:

- 改成只保留 `oracle_style: exact-after-single-golden` 與 `design_variance: multi-valid`；其餘由 consumer 依 schema 推導。
