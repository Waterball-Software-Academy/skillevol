# Skill 撰寫 Antipattern

本檔整理 AI 寫 skill 時最容易產生的壞結構。
每一條都不是單純文風問題，而是會讓 agent 執行時不知道當下該做什麼、該讀哪個規則、該停在哪個 gate，或讓使用者讀不懂 skill 的真實流程。

## 1. 把流程寫成規則

問題:
AI 很容易把「若 A 就進 B，否則停下來」這種控制流程抽成 RuleFile。RuleFile 看起來很正式，但 SOP 反而看不到真正的分支。

為什麼會爛:
流程應該留在 SOP，因為 SOP 是執行主線。若把流程藏進 RuleFile，agent 必須先讀外部檔才知道下一步去哪，人的掃讀也會斷掉。

### Bad

```markdown
# SOP

1. read 使用者想達成的結果與目標 skill。
2. think 判斷本輪入口。請嚴格遵守 `rules/entry-mode.md` 來執行此步驟。
3. delegate to SKILL /skillevol-define-evals
   - skip: 若規則判定不需要建立驗證集合

# rules/entry-mode.md

## Rule 1 — 沒有驗證集合時必須先建立驗證集合

- 若目標 skill 不存在，進入建立驗證集合流程。
- 若目標 skill 存在但沒有驗證集合，進入建立驗證集合流程。
- 若目標 skill 有驗證集合，進入缺口分析流程。
```

結果:
SOP 第 2 步沒有直接說出分支，讀者必須跳到 RuleFile 才知道會去哪。這是流程外包，不是規則抽取。

### Good

```markdown
# SOP

1. read 使用者想達成的結果與目標 skill。
2. think 判斷本輪入口。
   1. 若目標 skill 不存在，進入 Phase 0B。
   2. 若目標 skill 存在但沒有可信的驗證集合，進入 Phase 0B。
   3. 若目標 skill 有可信的驗證集合，且使用者要建立初始能力，進入 Phase 1。
   4. 若目標 skill 有可信的驗證集合，且使用者要改進既有缺陷，進入 Phase 1。
3. delegate to SKILL /skillevol-define-evals
   - input: 目標 skill 與使用者想達成的結果
   - skip: 若第 2 步沒有進入 Phase 0B
```

結果:
流程分支留在 SOP。RuleFile 不需要存在，因為沒有額外無序判準。

## 2. 把共用背景當成規則

問題:
AI 常建立一個大 `rules/runtime-state.md`、`rules/oracle.md` 或 `rules/common.md`，讓很多 step 都引用它。

為什麼會爛:
這違反步驟規則內聚性。每個 step 會被迫載入與當下職責無關的背景規則，最後 agent 讀了很多但不知道哪條規則正在約束這一步。

### Bad

```markdown
# SOP

1. read 使用者需求。請嚴格遵守 `rules/runtime-state.md`。
2. think 判斷目標 skill。請嚴格遵守 `rules/runtime-state.md`。
3. write `.skillevol/.gitignore`。請嚴格遵守 `rules/runtime-state.md`。
4. write 工作計畫。請嚴格遵守 `rules/runtime-state.md`。
5. think 判斷是否接續舊進度。請嚴格遵守 `rules/runtime-state.md`。

# rules/runtime-state.md

## Rule 1 — 目標不明確時必須詢問
## Rule 2 — 不得讀 self-test 答案
## Rule 3 — `.skillevol/.gitignore` 必須寫成兩個星號
## Rule 4 — 工作計畫必須保留 iteration history
## Rule 5 — 接續執行時先看 active gate
```

結果:
同一份 RuleFile 同時服務 read、think、write、resume routing。每個 step 都載入太多非局部規則。

### Good

```markdown
# SOP

1. read 使用者需求。
2. think 判斷目標 skill 是否明確。
   1. 若目標不明確，停止並詢問使用者要改哪個 skill。
   2. 若目標明確，進入第 3 步。
3. read 目標 skill 狀態、驗證集合狀態與既有工作計畫。
   - 不得讀取本 skill 測試答案。
4. write `.skillevol/.gitignore`。請嚴格遵守 `rules/workspace-gitignore.md`。
5. write 工作計畫。請依 `templates/working-plan.template.md` 渲染，並遵守 `rules/working-plan-state.md`。
6. think 判斷是否要接續舊進度。
   1. 若正在等待使用者確認補測試方向，進入 Phase 2。
   2. 若正在等待使用者確認是否開始修改，進入 Phase 3。
   3. 否則進入第 7 步。
```

結果:
流程直接留在 SOP。只有真正需要固定內容或產物格式的 step 才引用窄 RuleFile 或 TemplateFile。

## 3. 太早暴露內部術語

問題:
AI 很容易一開頭就寫 `eval oracle`、`red gate`、`provenance`、`mutation`、`active gate`。讀者還不知道要做什麼，就被迫先理解內部 protocol。

為什麼會爛:
術語不是不能用，但必須按需要出現。上層應先說白話流程，再把術語放在括號、工作計畫欄位或 glossary 中。

### Bad

```markdown
# Purpose

本 skill 以 eval oracle 驅動 mutation loop，先做 RCA，再跑 red gate，根據 provenance 選 mutator，最後跑 benchmark 與 holdout。

# SOP

1. think 執行 eval adequacy gate。
2. think 執行 red gate。
3. think 根據 failure provenance 選擇 mutation level。
4. delegate to SKILL /<chosen-mutator>
```

結果:
句子短，但讀者不懂 eval oracle、red gate、provenance、mutator 時，完全看不懂流程。

### Good

```markdown
# Purpose

本 skill 在使用者要新建或改進 skill 時啟用，先確認是否已有可重跑的驗證集合，這組驗證集合在本流程中稱為 `eval`。
若還沒有 `eval`，本 skill 只啟動建立驗證集合的流程，不先寫 skill 正文。
若已經有 `eval`，本 skill 會先找出現有驗證抓不到的問題，等使用者確認測試方向，再依失敗報告選擇最小修改，直到單點驗證、開發驗證與最終驗證通過。

# SOP

1. think 檢查現有驗證是否涵蓋使用者想修的問題。
2. write 對使用者的確認問題。
   - 公布現有驗證缺口。
   - 詢問使用者：這條補測試方向是否真的在測你在意的問題。
3. delegate to SKILL /skillevol-run-eval
   - input: 只跑第 2 步確認後的那一條測試
```

結果:
讀者先理解「驗證集合、補測試、確認失敗、再修改」。術語只在需要穩定稱呼時出現。

## 4. 用 Principle 或 Rules 區取代指令

問題:
AI 喜歡在 Purpose 後面加一堆 `## Principle` 或 `# Rules`，像是先背誦憲法，再進 SOP。

為什麼會爛:
這會形成第二套法源。真正執行時，agent 不知道是 SOP 指令優先，還是上面的 Principle 優先。若原則是必要行為，就應該出現在對應步驟。

### Bad

```markdown
# Purpose

本 skill 用於改進 skill。

## Principle — State Before Reasoning

任何 RCA、red gate、mutation 或 benchmark 前，必須先建立可恢復 state。

## Principle — Eval Before Skill Body

目標 skill 不存在或沒有可信 eval oracle 時，本 skill 只允許進入 eval bootstrap。

## Principle — Confirmation Before Irreversible Pressure

補 failing test 前必須先取得確認。

# SOP

1. read 使用者需求。
2. think 判斷目標 skill。
3. delegate to SKILL /skillevol-define-evals
```

結果:
真正的 gate 不在 SOP 裡。SOP 看起來可以直接委派，但上面又說有很多前置原則。

### Good

```markdown
# Purpose

本 skill 用於新建或改進 skill，先確認是否已有可重跑的驗證集合，再依驗證結果修改。

# SOP

1. read 使用者需求與目標 skill。
2. write `.skillevol/.gitignore`。
3. write `.skillevol/<target-skill>/loop/working-plan.md`。
4. think 判斷本輪入口。
   1. 若目標 skill 不存在，進入 Phase 0B。
   2. 若目標 skill 存在但沒有可信驗證集合，進入 Phase 0B。
   3. 若目標 skill 有可信驗證集合，進入 Phase 1。
5. write 對使用者的確認問題。
   - 若要補測試，先詢問補測試方向是否正確。
```

結果:
必要行為直接成為步驟與分支，不需要共用 Principle。

## 5. SOP 抽象層級混雜

問題:
同一層 SOP 同時放步驟、理由、例外、格式、話術、評分準則與模板骨架。

為什麼會爛:
SOP 應該是主控流程。當 SOP 同時承載所有細節，人和 agent 都很難掃出主線。

### Bad

```markdown
# SOP

1. think 分析現有驗證缺口。這一步很重要，因為如果不先分析，AI 可能寫錯測試。請務必避免使用 oracle、provenance 等術語，除非你有先解釋。輸出格式如下：
   - existing tests:
   - missing coverage:
   - proposed scenario:
   同時要遵守以下評分標準：
   - 1.0: 使用者能完全理解
   - 0.7: 大致能理解
   - 0.3: 有術語
   - 0.0: 完全黑箱
   若使用者不同意，請停止。若同意，就委派 define-evals。
```

結果:
一個 step 裡混了目的、規則、模板、rubric 與分支。主流程被淹沒。

### Good

```markdown
# SOP

1. think 用白話分析現有驗證缺口。請嚴格遵守 `rules/coverage-gap.md`。
   - 說明現有測試已經測了什麼。
   - 說明現有測試沒測到什麼。
   - 說明因此漏抓哪個錯誤行為。
   - 判斷要擴充既有測試，還是新增一條測試。
2. write 工作計畫，記錄缺口分析與補測試提案。
3. write 對使用者的確認問題。
   - 公布現有驗證缺口。
   - 公布補測試方向。
   - 詢問使用者：這條補測試方向是否真的在測你在意的問題。
```

結果:
SOP 只承載流程與當步產物。品質判準移到當步 RuleFile，輸出形狀交給工作計畫模板。

## 6. 用術語讓句子變短，但不是變清楚

問題:
AI 會把「問使用者這個失敗是否真的代表要修的行為」壓成「取得 `confirm-enter-mutation`」。

為什麼會爛:
短不是好。若句子短到只剩 protocol label，讀者反而要先查 glossary。

### Bad

```markdown
# SOP

1. think 執行 red gate。
2. write 取得 `confirm-enter-mutation`。
3. 若取得，進入 mutation loop。
```

結果:
讀者不知道要問什麼，也不知道為什麼要確認。

### Good

```markdown
# SOP

1. read 第一次失敗的測試報告。
2. write 對使用者的確認問題。
   - 公布這條測試在驗什麼。
   - 公布它抓到什麼失敗。
   - 點名測試報告路徑。
   - 詢問使用者：這個失敗是否真的代表你要修的行為，若是才開始自動修改。
3. think 判斷使用者是否同意開始修改。
   1. 若使用者未確認，停止並等待使用者修正測試或失敗判斷。
   2. 若使用者已確認，開始修改。
```

結果:
句子稍長，但每一步都能直接執行。

## 7. 過度拆檔

問題:
AI 會把每個小判斷都拆成 RuleFile，讓目錄看起來模組化。

為什麼會爛:
不是所有內容都該拆檔。若內容是有順序、有分支的流程，就該留在 SOP 或 Sub-SOP；若只是單一步驟的兩三個子判斷，也可以直接寫在 step 裡。

### Bad

```text
rules/
  target-identity.md
  intake-inputs.md
  resume-routing.md
  entry-mode.md
  bootstrap-close.md
  test-plan-confirmation.md
  change-authorization.md
  verification-order.md
```

```markdown
# SOP

1. think 判斷目標 skill 是否明確。請嚴格遵守 `rules/target-identity.md`。
2. read 目標 skill 狀態。請嚴格遵守 `rules/intake-inputs.md`。
3. think 判斷是否要接續舊進度。請嚴格遵守 `rules/resume-routing.md`。
4. think 判斷本輪入口。請嚴格遵守 `rules/entry-mode.md`。
```

結果:
每一步都要跳檔案，但那些檔案其實只是在寫 IF/ELSE。這不是 progressive disclosure，是跳讀負擔。

### Good

```markdown
# SOP

1. read `GLOSSARY.md`、使用者想達成的結果、目標 skill 路徑或使用者指令中的目標名稱。
2. think 判斷目標 skill 是否明確。
   1. 若目標不明確，停止並詢問使用者要改哪個 skill。
   2. 若目標明確，進入第 3 步。
3. read 目標 skill 狀態、目標 skill 的驗證集合狀態、既有工作計畫。
   - 若目標 skill 存在，讀取目標 skill；否則記錄目標 skill 不存在。
   - 若目標 skill 的驗證集合存在，讀取該驗證集合；否則記錄缺少驗證集合。
   - 若既有工作計畫存在，讀取工作計畫；否則記錄目前從入口開始。
4. think 判斷本輪入口。
   1. 若目標 skill 不存在，進入 Phase 0B。
   2. 若目標 skill 存在但沒有可信的驗證集合，進入 Phase 0B。
   3. 若目標 skill 有可信的驗證集合，進入 Phase 1。
```

結果:
流程留在 SOP，讀者能直接看到控制流。只把真正無序、可重用、會被當步消費的判準拆成 RuleFile。

## 8. 不知道何時該刪詞

問題:
AI 會覺得每個內部概念都應該命名，例如 `active gate`、`eval adequacy`、`mutation authorization`。命名後又要寫 glossary，最後詞越來越多。

為什麼會爛:
如果一個詞只在單一步驟出現，或用流程句更清楚，就不該成為術語。

### Bad

```markdown
# GLOSSARY

active gate:
目前正在等待的關卡。

mutation authorization:
使用者允許進入修改迴圈。

eval adequacy:
現有 eval 是否涵蓋 desired state。

# SOP

1. think 執行 eval adequacy gate。
2. write active gate = confirm-enter-mutation。
3. read mutation authorization。
```

結果:
術語本身沒有降低歧義，只是把簡單流程變成 protocol。

### Good

```markdown
# SOP

1. think 檢查現有驗證是否涵蓋使用者想修的問題。
   1. 若現有驗證已足夠覆蓋本輪需求，進入 Phase 2 第 4 步。
   2. 若現有驗證不足，說明缺口並提出補測試方向。
2. write 對使用者的確認問題。
   - 詢問使用者：這條補測試方向是否真的在測你在意的問題。
3. read 使用者對是否開始修改的回答。
4. think 判斷使用者是否同意開始修改。
   1. 若使用者未確認，停止並等待使用者修正測試或失敗判斷。
   2. 若使用者已確認，開始修改。
```

結果:
不需要 `active gate`、`mutation authorization`、`eval adequacy` 這些詞，流程句本身就夠清楚。

## 9. 把要點誤寫成編號步驟

問題:
AI 很容易把 step 內的補充要點也寫成 `1. 2. 3.`。但編號代表有順序、有執行焦點、有完成前後依賴；如果只是同一步裡要涵蓋的內容，應該用 bullet。

為什麼會爛:
編號會讓讀者與 agent 以為每一點都是必須依序執行的小步驟。若那些內容其實只是同一步的檢查面向或輸出要點，使用編號會製造假的流程結構。

### Bad

```markdown
# SOP

1. think 用白話分析現有驗證缺口。
   1. 說明現有測試已經測了什麼。
   2. 說明現有測試沒測到什麼。
   3. 說明因此漏抓哪個錯誤行為。
   4. 判斷要擴充既有測試，還是新增一條測試。
2. write 對使用者的確認問題。
   1. 公布現有驗證缺口。
   2. 公布補測試方向。
   3. 詢問使用者：這條補測試方向是否真的在測你在意的問題。
```

結果:
這些內層項目不是「完成第 1 點後才能進第 2 點」的嚴格流程，而是同一個 think/write 步驟中應涵蓋的要點。編號讓它們看起來像子流程。

### Good

```markdown
# SOP

1. think 用白話分析現有驗證缺口。
   - 說明現有測試已經測了什麼。
   - 說明現有測試沒測到什麼。
   - 說明因此漏抓哪個錯誤行為。
   - 判斷要擴充既有測試，還是新增一條測試。
2. write 對使用者的確認問題。
   - 公布現有驗證缺口。
   - 公布補測試方向。
   - 詢問使用者：這條補測試方向是否真的在測你在意的問題。
```

結果:
頂層步驟保留編號，表示執行順序；步驟內要點改用 bullet，表示同一步必須涵蓋的內容。

### Numbered point 的合法情境

```markdown
1. think 判斷使用者是否同意開始修改。
   1. 若使用者未確認，停止並等待使用者修正測試或失敗判斷。
   2. 若使用者已確認，進入第 2 步。
2. delegate to SKILL /skillevol-run-benchmark
```

結果:
這裡的內層編號是合法的，因為每一點是互斥分支或順序步驟，會決定下一步控制流。

## 總結

爛 skill 不是不夠完整，而是把流程、規則、詞彙、產物格式、狀態混在一起。
好的 skill 先讓 SOP 成為可直接執行的主線，再把真正必要的局部判準、固定產物骨架或有序子程序放到對的位置。
