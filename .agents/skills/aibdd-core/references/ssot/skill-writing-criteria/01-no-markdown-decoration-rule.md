# Rule 1 - 只用 primitive markdown（numbered-only，diff-scoped）

- Description:
  1. 新增／改動的 markdown hunk 只用 numbered list（含 nested numbered 表現層次）。
  2. 禁止：bullet（`-`／`*`，即使只有一項）、粗體（`**`／`__`）、斜體、底線（`<u>`／HTML）、emoji 裝飾、blockquote 裝飾（`>`）、堆疊水平線。
  3. 表格（`|...|`）有條件許可，邊界是閱讀體驗：每一格都是「屬性填值」——短的識別字／字面值／enum／型別名／路徑／固定 token、無句子——時，表格是可掃描的值矩陣，許可；一旦任一格塞進詮釋性描述（完整句子、why、條件說明、多子句解釋），該表即禁止，把那段描述抽出改用 nested numbered list。
  4. 層次一律用 nested numbered list，不要用「`Label:` 換行後接一串平面 list」這種飄離結構。
  5. 允許 inline backtick 與 fenced code block（code fence 內可示範反例）。
  6. 範圍：只套用於 skill 自身的撰寫面 markdown — `SKILL.md`、`SOP.md`，以及該 skill 的 assets（references、examples、rules deliverable、boundary preset 下被當作 skill 知識閱讀的 handler／variant markdown）；跳過 frontmatter YAML。
  7. 不套用於 template 造物：`templates/` 下會被 materialize／render 進產出專案的 scaffold markdown 屬「產出」而非 skill 撰寫面，不受本規則約束。
  8. diff-scoped + grandfather：只約束新增／改動的 hunk；既有未觸碰的裝飾不在範圍，同 hunk 順手 tidy 是建議、非 blocking。
- Rationale:
  1. 使用者經常直接讀 raw markdown。粗體／斜體／底線在 raw 形式不會被 render，只剩 `**`、`<u>` 這些符號夾在文字裡，是純干擾。
  2. 表格難讀與否取決於格內容：純屬性值的格窄而對齊，眼睛能照欄掃；格內一旦是句子，raw 形式會被 `|` 切斷、跨欄換行錯位，讀者得把被管線符號打散的句子重組——這才是表格難讀的根源，故以「格裝的是值還是詮釋」劃界，而非一律禁表。
  3. numbered list 每項有固定編號，PR comment／commit／對話可用「Rule 1 第 4 條」直接回指到某一行；bullet 沒編號，回指時只能整段引或貼原文。
  4. template 造物最終 render 進產出專案、受產出端格式規則管轄；本規則針對的是 reviewer 直接讀 raw 的 skill 撰寫面，故 template 不在範圍。
  5. 本規則 diff-scoped + grandfather：未被這個 PR 觸碰的既有裝飾不算違規，reviewer 只擋新增／改動 hunk 引入的裝飾，不去掃整個 repo。

## Good

1. nested numbered 表現層次：
```
1. 前置條件
   1. 必須先 READ arguments.yml
   2. boundary.yml 必須存在
```
2. 用措辭承載強調，不靠粗體：
```
1. 關鍵約束：未給路徑時不得假設檔案存在。
```
3. inline 識別字用 backtick，範例用 fenced code block：設定 `output_dir_key` 為 `arguments.yml` 的 key。
4. 純屬性值表格（每格皆短值、無句子）許可，是可掃描的值矩陣：
```
| dialect | parser | 副檔名 |
| --- | --- | --- |
| MySQL | MySQLSpecParser | `.mysql.sql` |
| PostgreSQL | PgSpecParser | `.pg.sql` |
```

## Bad

1. 子清單寫成 bullet：
```
- 必須先 READ arguments.yml
```
（用了 `-` bullet；即使只有一項也不行，bullet 沒編號無法被精準回指）
2. 粗體強調：
```
**Critical:** never assume the file exists
```
（`**` 在 raw markdown 是死符號；強調該靠措辭，如「關鍵約束：…」）
3. 表格的格內塞詮釋性描述：
```
| 步驟 | 說明 |
| --- | --- |
| READ | 先讀 arguments.yml；若不存在則中止回報，不得假設預設路徑 |
| WRITE | 僅在 boundary.yml 已存在時才寫入，否則先建立再寫 |
```
（`說明` 欄是整句描述，raw 形式被 `|` 切成碎句、跨欄錯位；該欄抽出改 nested numbered list。純值表格不在此限，見 Good 第 4 例）
4. Label 換行後接平面 bullet：
```
前置條件:
- READ arguments.yml
- boundary.yml 存在
```
（raw 形式看不出 `前置條件` 與兩個子項的歸屬；應改成 nested numbered）
5. 一張表多數欄是值、但某欄（如 `說明`／`理由`）整欄是句子，作者主張「其他欄都是值，所以整張保留表格」（只要有一欄是詮釋性描述就落入禁止；把該欄抽成 nested numbered，剩下的值矩陣才可留表——這是看似合規實際違例）
