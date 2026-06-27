# before/ 與 after/ 的填法（這兩個是真實檔案快照，不是單檔 skeleton）

before/ 與 after/ 裝的是真實檔案，不是有 `<placeholder>` 的 skeleton；所以這份是填它們的規則，不是逐字複製的模板。

## before/

- before/ 就是 AI 跑這個 unit 時看到的 CWD。把「跑之前的整個專案狀態」原樣落成真實檔案放進來。
- 它同時是算 file outcome 的基準：跑完拿當下 fs 跟 before/ 做 diff，就是這個 unit 的整體變更。
- 放下「完整、可成立」的狀態，不是只放要被改的那一小段。要測「不波及其他檔」就必須有完整基準可比。
- 典型內容：被處理的目標 skill 整包，例如 `before/.agents/skills/<target>/SKILL.md`、`before/.agents/skills/<target>/rules/*.md`，或一個 pre-kickoff 的空專案 `before/.aibdd/arguments.yml`。
- 分支 scenario 的 before/ 放在 scenario 根，由所有 variant 共用。

## after/

- after/ 是這個 unit 的「預期終態 fs」。它 imply 整個 file outcome：before/ 到 after/ 的 diff 就是預期的檔案結果。
- 因此 file 終態不在 expect.md 寫 invariant 散文；after/ 用真實快照把它具體說清楚。
- 比對交 judge：判 run 跑完的 fs 與 after/ 是否語意等價即可，不是 byte-exact。語意型 skill 的合理輸出有多種長相，逐字比對 after/ 會誤殺。
- after/ 也順帶吸收兩個機械斷言：run 結果少了答案該有的東西（沒消費），或多了沒授權的東西（幻覺），都會表現為「不等於 after/」。
- 分支 scenario 每個 variant 各有自己的 after/：`variants/<v>/after/`。一對 `{ user.md, after/ }` 就是 input 對 output 函數的一個點。
- 結構與 before/ 對齊，例如 `after/.agents/skills/<target>/SKILL.md`（改寫後）、`after/.agents/skills/<target>/rules/<新檔>.md`（新建的 artifact）。

## Artifact Output Contract（寫 unit 前先判定）

- 在開始寫任何 unit 前，先判定這條 eval 的 output channel：
  - `behavior/message only`：只驗對話或工具行為，不驗 file outcome
  - `file artifact`：預期新增、修改或刪除檔案
  - `both`：同時驗行為與 file artifact
- 若 output channel 是 `file artifact` 或 `both`：
  - 先列出 expected artifact path(s)，例如 `docs/architecture/order-checkout.class.mmd`
  - 再列 allowed diff 與 forbidden diff
  - 最後把完整 expected file outcome 落進 after/
- 若 output channel 是 `behavior/message only`：
  - 可以省略 after/
  - 但 Provenance 或上游 verification point 必須明示「這條 unit 不驗 file outcome」

## Hidden Oracle 與 Target-visible Input 的分界

- target 可見 input 只能放 before/、prompt.md、user.md。
- judge 用的 hidden oracle（例如預期 component 清單、預期 artifact shape、評分 rubric）只能留在 expect.md、shared/expect.md 或 judge 端，不得偽裝成 before/ 需求檔。
- 若 before/ 或 prompt.md 已經直接給出預期設計答案，先回頭修 artifact contract；不要帶著外洩 fixture 繼續寫 unit。
