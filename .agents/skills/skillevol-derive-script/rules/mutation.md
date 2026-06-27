# Mutation 規則

## Rule 1 — 只動指定步驟群與其對應的單一 ScriptFile

- 本 skill 的 mutation surface 只有兩塊：目標 `SKILL.md` 的指定步驟群，以及承接它們的單一 ScriptFile。
- 未經使用者授權，不順手改 Purpose、其他 SOP 步驟、其他 rules/templates/scripts，或整份 skill 的 wording。

### Good

情境: 使用者指定 `demo-observer/SKILL.md` 第 3 到第 5 步。

只更新第 3 到第 5 步，並建立 `scripts/normalize-observation.py`。

結果: mutation 邊界清楚，維護者知道這次 derive 只影響哪段 SOP 與哪支 script。

### Bad

情境: 為了讓風格一致，順手把第 2、6 步一起改寫，或順手新建第二支 `scripts/helpers.py`。

結果: 變更面擴散，已超出本次 derive 的最小邊界。

預期改法:

- 回到「指定步驟群 + 單一 ScriptFile」的最小 mutation。

## Rule 2 — ScriptFile 要開在該 SOP 所屬層級的 `scripts/`

- 若指定步驟群已 reference 某個 ScriptFile，直接開那個既有檔案，不另起新檔。
- 若尚未 reference，則在該 SOP 所屬層級建立或開啟 `scripts/<kebab-case>.py`。
- derive-script 只決定開哪個檔與放在哪裡；ScriptFile 的 form 由 `skillevol-form-script` 負責。

### Good

情境: 目標 skill 為 `.agents/skills/demo-observer/`，指定步驟群尚未有 reference。

建立或開啟 `.agents/skills/demo-observer/scripts/normalize-observation.py`。

結果: ScriptFile 與 owning SOP 同層，parent 可用相對路徑穩定呼叫。

### Bad

情境: 在 repo 根建立 `scripts/normalize-observation.py`，或明明已有 `scripts/prepare-observation.py` 卻另開 `scripts/prepare-observation-v2.py`。

結果: 路徑漂移，reference 與實際責任分裂。

預期改法:

- 若已有 reference 就沿用原檔；若無則在 owning level 的 `scripts/` 下建立語意清楚的新檔。

## Rule 3 — 只有同職責、mechanical、可一起自動化的步驟群能被抽成 script

- 被抽的 1..* 步驟必須服務同一個共同職責，且核心責任是 mechanical 自動化，而不是語意判讀或使用者互動。
- 若內容是無序原子規定，改走 RuleFile；若是固定輸出骨架，改走 TemplateFile；若是有序但不應自動化的子程序，改走 Sub-SOP 或保留 inline。

### Good

情境: 三個連續步驟分別負責讀 observation、清理噪音、寫 normalized events。

結果: 三步共享「observation 正規化」這個共同職責，可一起抽成一支 script。

### Bad

情境: 把「判斷 design conflict」與「決定是否 escalate」也一起塞進 script。

結果: 這些步驟核心是語意判讀，不是 mechanical 自動化。

預期改法:

- script 只吃 mechanical 子工作；語意判讀留在 SOP，或改走 `skillevol-derive-subsop`。

## Rule 4 — ScriptFile form 必須 delegate，不得 inline 套用

- 建立或改寫 ScriptFile 前，必須 Task delegate `skillevol-form-script`。
- 不得自行 read `skillevol-form-script` 的 rules 後直接寫 ScriptFile；那是跳過 form skill 的 process violation。

### Good

情境: 準備新建 `scripts/normalize-observation.py`。

先 Task delegate `skillevol-form-script`，再依其 form 寫 ScriptFile。

結果: form 與 derive 的分工被保留，eval 可驗證 delegate 紀律。

### Bad

情境: 直接 read `skillevol-form-script/rules/*.md`，略過 delegate 就寫 ScriptFile。

結果: 檔案可能 form 正確，但違反 derive family 的 delegation contract。

預期改法:

- 補 Task delegate `skillevol-form-script`，再進入 write 步驟。

## Rule 5 — Script 內容只搬既有自動化邏輯，不發明新 workflow

- ScriptFile 內只搬指定步驟群已明示的工作與必要 glue code，不臆測新的業務流程、隱性輸入、輸出檔名或第三方依賴。
- 若缺少腳本契約所需的關鍵資訊，先停；不要用猜的把 workflow 補完。

### Good

情境: 指定步驟已明說讀哪個 input、如何正規化、寫到哪個 output。

結果: script 只把這些既有工作 materialize 成可執行程式。

### Bad

情境: 步驟只說「整理 observation」，卻自行補出另外三份 summary 與未授權的統計檔。

結果: derive 變成創作新 workflow，而不是抽離既有責任。

預期改法:

- 只搬已知工作；缺 contract 時先停或回頭補清楚。

## Rule 6 — 擴充既有 ScriptFile 時，必須 materialize SOP 明示 I/O

- 沿用既有 script 時，保留原檔路徑與檔名；只在該檔內擴充被抽離步驟的工作。
- 被抽離的 SOP 步驟若明示輸入、輸出路徑或產物語意（例如 `noise summary`），ScriptFile 必須如實 materialize，不得改成 raw lines、不同 summary 格式或未授權的參數名。
- parent run 步驟的共同職責句，應忠實概括被合併步驟的工作，不泛化成不同語意。

### Good

情境: 既有 `scripts/prepare-observation.py` 只產 normalized events；SOP 第 4 步要求 `write noise summary 至 .../noise-summary.md`。

擴充同一支 script，新增 `--noise-output`，並 materialize Counter 型式的 noise summary。

結果: reuse path 正確，I/O 契約與 SOP 一致。

### Bad

情境: 為了省事，把 noise summary 改成寫入原始被略過行，或把參數命名成 `--noise-summary-output`。

結果: 路徑 reuse 看起來對，但 script 契約已偏離 SOP 明示語意。

預期改法:

- 回到 SOP 明示的產物語意與路徑，在既有 script 內擴充，不另開新檔。

## Rule 7 — parent SOP 的 mutation 形式固定為單一步驟加 script reference

- 抽離完成後，指定步驟群要折成一個單一步驟，不保留已移入 ScriptFile 的細步。
- 句型固定為 `run \`scripts/<檔名>.py\` 完成 <共同職責>。`

### Good

情境: 第 3 到第 5 步已抽到 `scripts/normalize-observation.py`。

```markdown
3. run `scripts/normalize-observation.py` 完成 observation 正規化。
```

結果: parent SOP 一眼可掃讀，script reference 句型也穩定。

### Bad

情境: 抽離後仍保留原細步，或把 reference 另起一行。

```markdown
3. run `scripts/normalize-observation.py` 完成 observation 正規化。
4. read observation 內容，略過空行與註解。
5. write normalized events 至 `.skillevol/run-evals/latest/normalized-events.md`。
```

結果: SOP 與 ScriptFile 內容重複，mutation 不完整。

預期改法:

- 刪掉已抽離的細步，只保留單一步驟與固定 reference 句型。

## Rule 8 — 若內容不是 script-type，就停止 derive

- derive-script 只處理可被穩定自動化的步驟群。
- 若步驟群本質上是語意判讀、使用者協商、設計決策，或共同職責不成立，就必須停止，不得硬抽成 script。

### Good

情境: 使用者指定的三步其實是在判斷是否為 design conflict、決定 escalate、撰寫建議。

停止 derive-script，回報它們不屬於 script-type，並指出應保留 inline 或改走 `skillevol-derive-subsop`。

結果: 錯型別的內容不會被誤自動化。

### Bad

情境: 明知是語意判讀，仍建立 `scripts/judge-conflict.py` 把決策硬寫進程式。

結果: 把應保留給 agent 的判讀責任錯塞到 script。

預期改法:

- 停止本 skill，不建立 ScriptFile，也不改動目標 `SKILL.md`。
