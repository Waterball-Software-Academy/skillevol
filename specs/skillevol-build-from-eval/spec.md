# Raw idea

 這是最重要的 skill-creator like orchestrator，不過他的重點是擺在，從 eval 以終為始，透過「最小量逐步升級」來去驗證是否能達成 eval，為核心思想。避免skill產生過多多餘元素。
  使用者他要指定 一個有具備 eval （遵守 eval 規範）的 skill，若沒有就拒絕執行並且請他執行
  /skillevol-form-eval 來補上 eval


有eval 後，接著此 orchestrator 會先判斷一下此 skill 是否已經撰寫，分成兩個 cases:

1. 全新的 skill -> build-up mode
2. 舊有的 skill 要優化 -> evol mode

如果是 (2) evol mode：
那就是直接去跑 /skillevol-define-evals 去根據上下文先去初步判斷說，上下文是否足夠，足夠去判斷說現有 eval 哪裡不足。先依照此 skill 流程去 workplan 逐步確定新的 eval 符合使用者預期，並同時也根據此次需求提出可能還能一起優化的地方（一樣是定義在新版 eval 中），把這一次 workplan 長時間的優化任務變得更好。

如果新版 eval 定義好了，接著要確定他是 Failing Test，執行 /skillevol-run-eval for 特定 failing test 來逐步驗證，新版本 eval 是 not pass 的。一定要確認這點，否則就 loop 回去加強 eval 的嚴格程度。

直到 新版eval 定義好了，才開始 build-up，這部分的處理方針就和  (1) build-up mode 一樣。

接著要依照底下規則來去盤點，要如何使用 mutators 來做 build-up evolutions。

首先，SKILL 有三個主要的部位等級，連續升級每個部位等級的 mutator 不一樣：

1. LV1 SKILL 基礎部位（原始型態）：
    1. /skillevol-form-frontmatter
    2. /skillevol-form-purpose
    3. /skillevol-form-sop

2. LV2 SKILL 展開細節（LV1 不中用時，用底下 mutator 升級成 LV2）：
    1. 考慮 SOP 中的每一個步驟，是哪一個步驟需要更多 refined rules？如果是缺產出的樣板那就 /skillevol-derive-template, 如果是缺 規則導向的補充說明的話 則 /skillevol-derive-rules

3. LV3 SKILL 展開 subsop，當只有一層 SOP ，使得 progressive disclosure 運作不足時，應該要考慮把某一組步驟直接抽 subsop，讓 SKILL.md 一開始載入時佔用的 context 更簡潔。使用 skillevol-derive-subsop on 一組步驟。

這個 build-up procedure是 iterative procedure，不是一次生成完就收工，而是多次迭代。永遠都是先試著走 LV1 用基礎部位來演化 SKILL，如果成效不彰，需描述更多規則/生成物參考 -- 才升級成 LV2，最後若成效不彰需要更激進的 progressive disclosure，才會升級到 LV3。

判斷完部位等級後，編排一下，該用哪些 formulator or deriver，目標範疇在哪，原因為何，假設為何。

每次規劃完之後，就迭代去直接呼叫這些 mutators 來改 skill，改完就跑 /skillevol-run-eval 或是 /skillevol-run-benchmark 跑 skill evaluation。

直到完美通過 所有 eval units 後，才能停止本次優化。

優化 skill 時務必注意，不要疊床架屋，撰寫 skill 不要追求「最小改變」，而是追求「根本上」的改變，讓 skill 內容一切重組成「邏輯更簡單、更好懂」的流程和規則。根據第一性原則，積極追求重組、看起來沒用的文字就刪掉、反正有 eval 保護，不用擔心。

# Spec

## Skill 名稱

`.agents/skills/skillevol-loop`

## 一句話定義

`skillevol-loop` 是一個 eval-driven skill evolution orchestrator：它接收一個目標 skill，先確認該 skill 已有可信 eval，再以 eval 失敗作為唯一改造訊號，反覆選擇最小必要的 formulator 或 deriver mutation 來重組 skill，直到 dev eval 與最終 holdout gate 全部通過。

它不是一般 skill creator。它不以「一次生成完整 skill」為目標，而是以「讓 skill 在 eval 壓力下逐步長成剛好足夠的形狀」為目標。

它也不是單純修補器。若 eval 證明現有設計方向錯誤，`skillevol-loop` 應該重組 skill 的 Purpose、SOP、RuleFile、TemplateFile 或 Sub-SOP，而不是為了保留舊文字而層層加例外、補註、警告或相容 shim。

## 核心思想

### Eval 是唯一法官

`skillevol-loop` 必須把目標 skill 的 `eval/` 視為 fitness oracle。任何改造是否成立，不由 agent 的主觀感覺決定，也不由「看起來更完整」決定，而由 eval run 的 pass/fail、違規定位與 provenance 決定。

因此，沒有 eval 就不能 loop。若目標 skill 沒有 `eval/`，或 eval 不符合 `skillevol-form-eval` 的結構，`skillevol-loop` 必須拒絕進入改造流程，並要求使用者先補 eval。

可接受的下一步只有兩種：

1. 若使用者只是缺 eval form，請他先跑 `/skillevol-form-eval`。
2. 若使用者還沒定義 golden benchmark，請他先跑 `/skillevol-define-evals`。

`skillevol-loop` 不應在沒有 oracle 的狀態下憑感覺創造或改造 skill。

### Red 先於 Green

新增或補強 eval 後，必須先確認它會失敗。若某個「新需求」對應的 eval 一開始就是 pass，通常代表 eval 沒有真正捕捉到使用者想要的能力差距。

在這種情況下，`skillevol-loop` 不能假裝任務已完成，也不能直接開始改 skill。它必須回到 eval 定義流程，要求加嚴或修正 eval，直到該 eval 能對現有 skill 形成有效壓力。

Red gate 的意義是：先證明法官會抓到問題，再讓 skill 進化。

### 最小必要複雜度，不是最小文字改動

`skillevol-loop` 追求的不是最少 diff，也不是保留最多舊文字。它追求的是：在通過 eval 的前提下，使 skill 的 instruction surface 最小、邏輯最直、progressive disclosure 最清楚。

如果最小 diff 會導致 skill 疊床架屋，應該拒絕。

如果刪掉舊段落、重寫 SOP、重新分配 RuleFile 與 TemplateFile 能讓 skill 更簡單，應該採用。

只要 eval 覆蓋了相關行為，就可以積極重組，不必害怕移除舊文字。

### LV1 優先，LV2 謹慎，LV3 最後

`skillevol-loop` 的演化順序必須遵守由簡到繁：

1. 先嘗試用 LV1 的 `frontmatter`、`Purpose`、`SOP` 解決。
2. 若失敗來自某個 SOP 步驟缺少可驗收的細部規則或固定輸出骨架，才升級到 LV2 的 RuleFile 或 TemplateFile。
3. 若失敗來自主 SOP 過長、步驟群有明確順序依賴、或 progressive disclosure 已經失效，才升級到 LV3 的 Sub-SOP。

每次升級都要說明原因。不能因為想讓 skill 看起來完整，就提前建立 `rules/`、`templates/` 或 Sub-SOP。

## 目標使用情境

### Build-up mode：全新 skill 從 eval 長出來

使用者指定一個已有 eval 但尚未實作或只有空殼的 skill。

`skillevol-loop` 應該從最薄的 LV1 開始：

1. 建立或修正 frontmatter，使 skill 能被正確觸發與避開不該觸發的場景。
2. 建立 Purpose，使 skill 的存在理由、輸入、輸出、邊界清楚。
3. 建立 SOP，使 skill 有可執行的主流程。
4. 跑 dev eval。
5. 根據失敗 provenance 判斷是否繼續調整 LV1，或升級到 LV2 / LV3。

Build-up mode 的關鍵是「從 eval 反推能力」，而不是根據想像一次寫一份龐大的 SKILL.md。

### Evol mode：既有 skill 的能力升級

使用者指定一個已存在的 skill，並希望它變得更好、支援新需求、修正行為缺陷，或降低 instruction bloat。

`skillevol-loop` 不應立刻改 skill。它必須先進入 eval adequacy check：

1. 讀取目標 skill 現有 `SKILL.md`、`eval/`、相關 rules/templates/subsops。
2. 判斷目前 eval 是否已經覆蓋使用者這次要求的 desired state。
3. 若沒有覆蓋，委派 `/skillevol-define-evals` 逐步補 eval。
4. 補完 eval 後，針對新增或修改的 eval unit 跑 `/skillevol-run-eval`。
5. 確認它們在現有 skill 上是 failing tests。
6. 若沒有 fail，回到 `/skillevol-define-evals` 加嚴，直到 red gate 成立。
7. red gate 成立後，才進入與 Build-up mode 相同的 mutation loop。

Evol mode 的關鍵是：先讓「想變好」變成可失敗的 eval，再開始改 skill。

## 輸入

`skillevol-loop` 至少需要以下輸入：

1. 目標 skill 路徑，例如 `.agents/skills/<target-skill>`。
2. 使用者此次的 desired state，可以是「讓全部 eval pass」、一段新需求、一次失敗觀測、或一個想優化的方向。
3. 本次 loop 的範圍：只跑 dev、跑指定 eval unit、或最後跑完整 benchmark 含 holdout。

若使用者沒有明確給範圍，預設策略如下：

1. 迭代期間只跑 dev eval。
2. 每次針對剛修的 failure 優先跑相關 unit。
3. dev 全綠後才跑整組 dev benchmark。
4. dev benchmark 全綠後，最後才跑 holdout gate。

## 輸出

`skillevol-loop` 的主要輸出不是一份 spec，而是被改好的目標 skill 及其 eval run 報告。

它應該產出或更新：

1. 目標 skill 的 `SKILL.md`。
2. 必要時產生或更新 `rules/*.md`。
3. 必要時產生或更新 `templates/*.md`。
4. 必要時產生或更新 Sub-SOP 目錄與 `SOP.md`。
5. `.skillevol/<target-skill>/loop/working-plan.md`，記錄每輪失敗、假設、選擇的 mutation、結果與下一步。
6. 由 `/skillevol-run-eval` 或 `/skillevol-run-benchmark` 產出的 eval 報告。

`working-plan.md` 是 loop 的操作日誌，不是人類展示用企劃書。它必須足夠追溯每輪為什麼改、改了哪裡、根據哪個 failure 改、結果是否改善。

## 非目標

`skillevol-loop` 不負責撰寫 eval form 的底層規範。這是 `/skillevol-form-eval` 的責任。

`skillevol-loop` 不負責逐點與使用者協作定義 eval。這是 `/skillevol-define-evals` 的責任。

`skillevol-loop` 不負責單一 eval unit 的沙盒執行、互動回放、觀測收集與評分。這是 `/skillevol-run-eval` 的責任。

`skillevol-loop` 不負責整組 eval 的排程與 pass rate 彙總。這是 `/skillevol-run-benchmark` 的責任。

`skillevol-loop` 不直接取代 mutator。frontmatter、Purpose、SOP、RuleFile、TemplateFile、Sub-SOP 的 form 與 derive mutation 應該委派給既有 skillevol skills。

它的責任是 orchestration：判斷何時要補 eval、何時可以改 skill、要改哪個部位、要用哪個 mutator、跑哪個 eval、何時升級、何時停止。

## 狀態機

`skillevol-loop` 可以視為以下狀態機：

1. `Intake`
2. `Eval Presence Gate`
3. `Mode Detection`
4. `Eval Adequacy Gate`
5. `Red Gate`
6. `Failure Analysis`
7. `Mutation Planning`
8. `Mutation Execution`
9. `Verification`
10. `Escalation Decision`
11. `Final Benchmark Gate`
12. `Done`

### Intake

讀取使用者輸入，解析目標 skill、desired state、範圍與是否允許修改 eval。

若目標 skill 路徑不明確，必須先要求使用者指定。不能從上下文猜測並直接修改某個 skill。

若使用者指定的是一個不存在的 skill 路徑，仍可進入 Build-up mode，但前提是該 skill 的 eval 已存在且符合規範。若連 eval 也不存在，停止。

### Eval Presence Gate

檢查目標 skill 是否有 `eval/`，且至少包含可供執行的 dev 或 holdout unit。

若沒有 eval，停止並回報：

1. 不能執行 `skillevol-loop`。
2. 缺少 eval oracle。
3. 下一步應執行 `/skillevol-form-eval` 或 `/skillevol-define-evals`。

若 eval 結構疑似不符合規範，停止並要求先修 eval form。`skillevol-loop` 不應在 eval 結構不可信時繼續。

### Mode Detection

判斷目標 skill 是 Build-up mode 或 Evol mode。

Build-up mode 條件：

1. `SKILL.md` 不存在。
2. 或 `SKILL.md` 只有空殼。
3. 或 `SKILL.md` 明顯沒有可執行 Purpose / SOP。

Evol mode 條件：

1. `SKILL.md` 已存在。
2. 且已具備至少一段可執行 Purpose / SOP。
3. 本次任務是修正、補強、重組或通過新增 eval。

Mode detection 不只是檔案存在與否。若 `SKILL.md` 存在但沒有實質 instruction，仍應視為 Build-up mode。

### Eval Adequacy Gate

這一步主要用於 Evol mode。

`skillevol-loop` 必須讀現有 eval，判斷它是否能驗證使用者此次 desired state。

若 desired state 是「讓既有全部 eval pass」，且沒有新增需求，可以直接進入 Failure Analysis。

若 desired state 包含新能力、新約束、新風格或新邊界，但現有 eval 沒覆蓋，必須委派 `/skillevol-define-evals`。

委派時應給 `/skillevol-define-evals` 明確上下文：

1. 目標 skill。
2. 使用者此次 desired state。
3. 現有 eval 的覆蓋缺口。
4. 懷疑還可以一起優化的地方。
5. 要求逐點確認，而不是一次傾倒完整 eval 樹。

### Red Gate

新增或修改 eval 後，必須執行對應 unit，確認現有 skill 會 fail。

若 eval pass，判斷如下：

1. 若 pass 合理，代表該需求已被現有 skill 滿足，該項不需要修改 skill。
2. 若 pass 不合理，代表 eval 太弱，必須回到 `/skillevol-define-evals` 補強。
3. 若 judge 判斷模糊，先修 eval 的 expect / rubric，使 failure 可被定位。

只有當至少一個目標 failure 成立，才可進入 mutation loop。

### Failure Analysis

讀取 eval report，抽出每個 failure 的 provenance。

每個 failure 必須被分類到下列之一：

1. `Trigger Failure`：frontmatter 的 description / trigger / skip 邊界錯誤，導致 skill 不該觸發時觸發、該觸發時沒觸發，或描述太泛。
2. `Purpose Failure`：skill 的目的、責任、輸入、輸出或非目標不清楚，導致 agent 對任務角色理解錯。
3. `SOP Failure`：主流程順序、步驟粒度、法定 verb、決策點或停止條件錯誤。
4. `Rule Failure`：某步缺少無序、可逐條驗收的原子規則，導致執行時漏約束。
5. `Template Failure`：某步需要固定產物骨架，但 SOP 只用抽象描述，導致輸出形狀飄移。
6. `Progressive Disclosure Failure`：主 SOP 過長、步驟群有明確子流程、或 context load 過高，導致需要 Sub-SOP。
7. `Delegation Failure`：應委派既有 skill 卻自行重做，或委派時輸入不完整。
8. `Boundary Failure`：skill 做了非目標行為，例如越權改 eval、越權跑 holdout、越權生成未要求 artifact。
9. `Bloat Failure`：雖然可能 pass，但 instruction surface 過胖、重複、充滿禁令殘影或歷史包袱，違反最小必要複雜度。

分類後，`skillevol-loop` 必須把每個 failure 對應到一個候選 mutation，而不是立刻改檔。

### Mutation Planning

每輪 mutation planning 必須產生一個小型計畫，寫入 `.skillevol/<target-skill>/loop/working-plan.md`。

每輪計畫至少包含：

1. 本輪目標 failure。
2. failure provenance。
3. failure 類型。
4. 選擇的 mutation level：LV1 / LV2 / LV3。
5. 選擇的 mutator。
6. 修改範圍。
7. 為什麼這是目前最低足夠層級。
8. 成功後預期哪個 eval unit 會改善。
9. 若失敗，下一個升級條件是什麼。

計畫不需要詢問使用者逐輪確認，除非遇到設計分歧、eval desired state 不明、或需要改變使用者原本指定的目標範圍。

## Mutation Level 規則

### LV1：基礎 skill 部位

LV1 是預設起點。只要 failure 可以透過 SKILL.md 的基本部位解決，就不應升級到 LV2 或 LV3。

可用 mutator：

1. `/skillevol-form-frontmatter`
2. `/skillevol-form-purpose`
3. `/skillevol-form-sop`

適用情境：

1. skill 名稱、description、trigger、skip 邊界不準。
2. Purpose 沒有說清楚 skill 為什麼存在。
3. Purpose 沒說清楚輸入、輸出、責任邊界。
4. SOP 缺主要步驟。
5. SOP 順序錯。
6. SOP 的動詞不夠可執行。
7. SOP 沒有停止條件。
8. SOP 沒有明確委派到既有 skill。

禁止情境：

1. 不可把大量原子規則塞進 SOP，假裝 LV1 解決。
2. 不可把大段固定輸出範本塞進 SOP。
3. 不可把多層有序子流程塞進單一步驟。

若出現禁止情境，應考慮 LV2 或 LV3。

### LV2：RuleFile 與 TemplateFile

LV2 用於 SOP 某個步驟需要展開細節，但那些細節不應留在主 SOP。

可用 mutator：

1. `/skillevol-derive-rules`
2. `/skillevol-derive-template`

#### 使用 RuleFile 的條件

選擇 `/skillevol-derive-rules` 的條件：

1. failure 來自某步缺少規則型約束。
2. 規則彼此無明確順序依賴。
3. 每條規則可獨立驗收。
4. 規則是行為限制、判斷標準、命名規範、禁止事項、邊界條件或 consistency invariant。
5. 這些規則放在 SOP 會讓主流程變胖。

RuleFile 內容不應包含固定產物骨架。若內容有固定輸出形狀，應使用 TemplateFile。

#### 使用 TemplateFile 的條件

選擇 `/skillevol-derive-template` 的條件：

1. failure 來自輸出形狀不穩。
2. 某步需要固定章節、固定欄位、固定 placeholder 或固定 artifact skeleton。
3. 評分標準會檢查產物結構，而不只是語意。
4. SOP 用自然語言描述不足以穩定生成該產物。

TemplateFile 不應承載大量行為規則。若是可逐條驗收的無序規則，應使用 RuleFile。

### LV3：Sub-SOP

LV3 只在 progressive disclosure 真的失效時使用。

可用 mutator：

1. `/skillevol-derive-subsop`

適用情境：

1. 某個 SOP 步驟其實包含一段有序子程序。
2. 子步驟有前後依賴。
3. 主 SOP 因為塞太多細節變得難掃讀。
4. 多個 eval failure 都指向同一段複雜子流程。
5. 把子流程抽出去後，主 SOP 可以回到高階 orchestration。

禁止情境：

1. 不可為了分類漂亮而建立 Sub-SOP。
2. 不可把無序規則抽成 Sub-SOP。
3. 不可把固定輸出骨架抽成 Sub-SOP。
4. 不可讓 Sub-SOP 變成第二份 SKILL.md。

LV3 的判準是「是否改善 progressive disclosure」，不是「是否讓檔案看起來更完整」。

## Mutator 選擇矩陣

若 failure 是觸發與避開邊界錯誤，選 `/skillevol-form-frontmatter`。

若 failure 是角色、目的、責任或非目標不清楚，選 `/skillevol-form-purpose`。

若 failure 是主流程順序、缺步驟、決策點或停止條件錯，選 `/skillevol-form-sop`。

若 failure 是 SOP 某步缺少無序原子規則，選 `/skillevol-derive-rules`。

若 failure 是輸出 artifact 形狀不穩，選 `/skillevol-derive-template`。

若 failure 是某段有序子流程讓主 SOP 過胖，選 `/skillevol-derive-subsop`。

若 failure 同時命中多個類型，先選最上游原因：

1. Purpose 錯會污染 SOP，先修 Purpose。
2. SOP 步驟不存在，就不能先抽 RuleFile，先修 SOP。
3. SOP 步驟存在但細節混亂，才考慮 RuleFile / TemplateFile。
4. 多個 RuleFile / TemplateFile 都在補同一段有序流程，才考慮 Sub-SOP。

## Iteration SOP

`skillevol-loop` 的 SOP 應該長這樣：

1. read 目標 skill、目標 skill 的 eval/、使用者 desired state、既有 `.skillevol/<target-skill>/loop/working-plan.md`（若存在）。
2. think 檢查 eval oracle 是否存在且可執行；若不存在或不合規，停止並要求先補 eval。
3. think 判斷 Build-up mode 或 Evol mode。
4. 如果是 Evol mode，think 檢查現有 eval 是否覆蓋此次 desired state；若不足，delegate to SKILL `/skillevol-define-evals`。
5. 如果本次新增或修改 eval，delegate to SKILL `/skillevol-run-eval` 驗證 red gate；若未 fail，回到 eval 定義加嚴。
6. delegate to SKILL `/skillevol-run-benchmark`，迭代期間只跑 dev 或指定相關 unit。
7. think 讀取 eval report，依 failure provenance 做 failure classification。
8. think 選擇本輪最低足夠 mutation level 與 mutator，並寫入 working-plan。
9. delegate to 對應 formulator 或 deriver mutator，執行本輪 skill mutation。
10. delegate to SKILL `/skillevol-run-eval` 跑本輪相關 failing unit。
11. think 若該 unit 仍 fail，判斷是 mutation 不足、分類錯誤、eval 不清、或需要升級 mutation level。
12. think 若相關 unit pass，繼續跑 dev benchmark。
13. think 若 dev benchmark 還有 failure，回到第 7 步。
14. delegate to SKILL `/skillevol-run-benchmark` 執行 final gate，納入 holdout。
15. write 收尾報告：最終 pass/fail、改動摘要、每輪 mutation provenance、仍需人工決策的殘留問題。

## Working Plan 格式

`.skillevol/<target-skill>/loop/working-plan.md` 建議包含以下區塊：

```md
# Skillevol Loop Working Plan

## Target

- skill: <target-skill-path>
- mode: build-up | evol
- desired state: <user desired state>
- eval scope: dev | unit | final-gate

## Eval Oracle Status

- eval exists: yes | no
- form-conformant: yes | no | uncertain
- adequacy: sufficient | needs-new-evals | uncertain
- red gate: passed | not-yet | failed-to-fail

## Iterations

### Iteration <n>

- target failure:
- eval unit:
- provenance:
- failure type:
- chosen level:
- chosen mutator:
- mutation scope:
- rationale:
- expected improvement:
- result:
- next decision:

## Final Gate

- dev benchmark:
- holdout benchmark:
- final verdict:
```

這份 plan 要跟著每輪更新。它不是一次性產物，而是 loop 的持久記憶。

## Verification 策略

### 單點回歸

每次 mutation 後，優先跑與該 failure 對應的單一 eval unit。

目的：快速確認本輪假設是否有效。

### Dev benchmark

單點回歸通過後，跑 dev benchmark。

目的：確認修復沒有破壞其他可見 eval。

### Holdout final gate

dev benchmark 全綠後，才跑 holdout。

目的：避免 agent 針對 dev 過度擬合，並保留最終放行可信度。

Holdout 不應在每輪迭代中反覆跑，除非使用者明確要求。它是 final gate，不是日常調參工具。

## Escalation 規則

`skillevol-loop` 不能在第一次失敗後就升級結構層級。升級必須有具體證據。

### LV1 到 LV2

可升級條件：

1. LV1 已有正確 Purpose 與 SOP 主流程。
2. failure provenance 指向某個已存在 SOP 步驟的細部缺失。
3. 若繼續把細節塞進 SOP，會破壞主流程可掃讀性。
4. failure 類型明確是 Rule Failure 或 Template Failure。

### LV2 到 LV3

可升級條件：

1. 多個 LV2 細節其實共享同一段有序程序。
2. RuleFile 或 TemplateFile 開始承載流程順序，顯示位置錯了。
3. 主 SOP 需要引用一個可獨立執行的子流程。
4. eval failure 來自 agent 沒有穩定完成一串有順序依賴的操作。

### 不升級條件

不得因以下理由升級：

1. 想讓目錄看起來完整。
2. 想預留未來擴充。
3. 覺得所有 skill 都應該有 rules/。
4. 覺得長一點比較專業。
5. 為了避免刪舊文而把舊文搬到新檔。

## Simplification 規則

每輪 mutation 不只允許新增，也允許刪除與重組。

`skillevol-loop` 應主動尋找以下可刪內容：

1. 與現行合法 action set 無關的舊禁令。
2. 重複出現在 SKILL.md 與 RuleFile 的規則。
3. 已由 TemplateFile 承載的內嵌輸出骨架。
4. 已由 Sub-SOP 承載的內嵌子步驟。
5. 抽象但不可操作的形容詞。
6. 沒有被 eval 或使用情境支撐的預防性相容設計。
7. 只是在描述歷史脈絡、但不影響 agent 當前行為的文字。

刪除的標準不是「文字是否有點道理」，而是「留下它是否能改善 eval 對應的行為，或使 skill 更容易正確執行」。

## 與既有 skills 的分工

### `/skillevol-form-eval`

負責 eval form 的規範。

`skillevol-loop` 只檢查 eval 是否存在且可作為 oracle；若 form 不可信，停止並要求先修 form。

### `/skillevol-define-evals`

負責與使用者逐點確認 eval 的 desired state。

`skillevol-loop` 在 Evol mode 發現 eval 不足時委派它，而不是自己一次寫完 eval。

### `/skillevol-run-eval`

負責跑單一 eval unit、收集觀測、交給 judge、產出報告。

`skillevol-loop` 不應重作 sandbox、互動回放或 judge。

### `/skillevol-run-benchmark`

負責排 dev / holdout unit 並彙總 pass rate。

`skillevol-loop` 只決定何時跑 benchmark，以及當前是迭代階段還是 final gate。

### `/skillevol-form-frontmatter`

負責 frontmatter form。

`skillevol-loop` 在 Trigger Failure 時委派它。

### `/skillevol-form-purpose`

負責 Purpose form。

`skillevol-loop` 在 Purpose Failure 時委派它。

### `/skillevol-form-sop`

負責 SOP form。

`skillevol-loop` 在 SOP Failure 時委派它。

### `/skillevol-derive-rules`

負責從指定 SOP 步驟導出 RuleFile mutation。

`skillevol-loop` 必須先指定目標 skill、步驟、規則內容與原因，不能叫它自行發明規則。

### `/skillevol-derive-template`

負責從指定 SOP 步驟導出 TemplateFile mutation。

`skillevol-loop` 必須先確認 failure 是輸出骨架問題，而不是規則問題或子流程問題。

### `/skillevol-derive-subsop`

負責從指定 SOP 步驟導出 Sub-SOP mutation。

`skillevol-loop` 必須先確認該步內確實有有序子程序。

## 成功條件

`skillevol-loop` 完成時必須同時滿足：

1. 目標 skill 的 dev eval 全部通過。
2. final gate 的 holdout eval 全部通過，除非使用者明確指定本次不跑 holdout。
3. 每個新增或修改的 eval 都曾經經過 red gate。
4. 每個主要 mutation 都可追溯到 eval failure provenance。
5. `SKILL.md` 沒有不必要的 instruction bloat。
6. RuleFile、TemplateFile、Sub-SOP 只在必要時存在。
7. 主 SOP 維持可掃讀，不承載大量細節。
8. 最終報告能說明：原本 fail 什麼、怎麼修、修後哪個 eval 證明通過。

## 失敗與停止條件

`skillevol-loop` 遇到以下情況必須停止並回報，而不是繼續猜：

1. 目標 skill 不明確。
2. eval 不存在。
3. eval 結構不合規。
4. 使用者 desired state 與現有 eval 或 skill 目標衝突。
5. 新增 eval 無法形成 failing test，且需要使用者重新確認需求。
6. judge report 不足以定位 failure provenance。
7. 需要改變 skill 的公開責任邊界，但使用者尚未同意。
8. holdout fail 暗示 dev eval 缺 coverage，需要回到 eval 定義，而不是繼續對 holdout 猜修。

## 反模式

### 一次生成完整 skill

錯誤：讀 eval 後直接寫一份很完整的 SKILL.md、rules、templates、subsops。

正確：先寫 LV1，跑 eval，根據 failure 決定是否展開。

### 把 eval 當參考，不當法官

錯誤：看過 eval 後憑感覺說 skill 應該可以通過。

正確：必須跑 `/skillevol-run-eval` 或 `/skillevol-run-benchmark`。

### 沒有 red gate 就改 skill

錯誤：補了新 eval 後沒有確認它 fail，就開始改 skill。

正確：先確認 fail；若不 fail，先修 eval。

### 用 RuleFile 裝流程

錯誤：把一串有順序依賴的子步驟寫進 `rules/*.md`。

正確：若是有序子程序，使用 Sub-SOP。

### 用 TemplateFile 裝規則

錯誤：把一堆禁止事項、命名規則、判斷條件塞進 `templates/*.md`。

正確：若是無序原子規則，使用 RuleFile。

### 主 SOP 越寫越胖

錯誤：每次 fail 都在 SOP 補 bullet，最後 SKILL.md 變成百科。

正確：主 SOP 保留 orchestration，細節視情況抽 RuleFile、TemplateFile 或 Sub-SOP。

### 害怕刪除

錯誤：保留所有舊文字，只在後面補「但不要...」「另外注意...」。

正確：若舊文字造成錯誤或重複，直接刪除或重組。

## 最終 SKILL.md 應呈現的精神

`skillevol-loop` 的 `SKILL.md` 應該短而有力，但它背後的行為必須嚴格。

它的 Purpose 要講清楚：這是以 eval 為 oracle 的 skill 演化迴圈。

它的 SOP 要講清楚：

1. 先驗證 eval。
2. 再判斷 mode。
3. Evol mode 先補 eval 並 red gate。
4. 讀 failure provenance。
5. 選最低足夠 mutation。
6. 委派 mutator。
7. 跑 eval。
8. 反覆直到 dev 與 holdout 全綠。

它的 rules 可以承載：

1. oracle gate 規則。
2. red gate 規則。
3. mutation level 選擇規則。
4. simplification 規則。
5. escalation / stop 規則。

它的 templates 可以承載：

1. working-plan template。
2. final report template。

它的 Sub-SOP 只有在主 SOP 過長時才需要，例如：

1. `eval-adequacy-check/SOP.md`
2. `failure-classification/SOP.md`
3. `mutation-planning/SOP.md`

但初版不應預設全建。應先讓 eval 告訴我們需要哪些展開。