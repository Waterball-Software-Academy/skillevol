# eval 結構（canonical form）

## Rule 1 — eval 的目錄、unit、expect、user.md、after 必須符合 canonical 定義

- eval 的結構組成必須遵守底下定義。一個「unit」是一個可獨立跑的測試點：非分支 scenario 自己就是一個 unit，分支 scenario 下每個 variant 是一個 unit。

    ```
    eval/ ::=
      shared/expect.md             1       # 橫切判準，被各 unit 繼承
      dev/    <scenario>+          1       # agent 可見，對著迭代
      holdout/ <scenario>*         0..1    # agent 不可見，最終放行 gate

    <unit-name> ::=
      <before情境設定概述>_<after測試結果概述>
      # 若目標 skill 有多個可選 subcommand，<before情境設定概述> 應以 <subcommand動作>- 開頭

    <scenario> ::=
      非分支:
        dirname = <unit-name>
        before/                    1       # 真實 fs snapshot：drop 即成 CWD，同時是 diff base
        prompt.md                  1       # 使用者開場那一句（單輪起點）
        user.md                    0..1    # 互動型才有：模擬使用者 answer key
        expect.md                  1       # 行為 spec
        after/                     0..1    # 預期終態 fs，imply file outcome；若 unit 預期新增/修改/刪除任何 file artifact，則此欄必填
      分支(answer-key variants):
        before/   prompt.md        1       # 分支共用 input
        variants/<unit-name>/      1..N    # 每個 unit-name 是一個 unit
          user.md                  1       # 此分支答案(input)
          after/                   1       # 此分支預期終態 fs(output)，imply file outcome
          expect.md                1       # 此分支行為 spec

    expect.md ::=
      標題(H1) + 繼承宣告           1       # 繼承 shared/expect.md，衝突以本檔為準
      ## Hidden oracle metadata      0..1    # 只允許出現在 unit-local expect.md；正式 schema 見 rules/hidden-oracle-metadata.md
        <key>: <value>               1..N    # 目前正式欄位只有 oracle_style / design_variance
      ## Provenance                1       # 釘哪條行為 + 為何存在
      ## Run                       1       # 過程 = 1..N 個 one-turn 的有序序列
        ### Turn k                 1..N
          Tool calls               # 該 turn：MUST/MUST NOT 關鍵 tool + 選用語意 rubric
          Assistant message        # 該 turn：語意 rubric(0.0/0.3/0.7/1.0 + 具體片段)
          breakpoint               # 0..1：ASK(...) 交還 user，responder 依 user.md 作答；末 turn 為 done/STOP
      ## Cross-turn                0..1    # 僅多輪：order / gates / liveness，引用 event trace
      規範                         expect.md 不含 file diff／Outcome 區段；file 終態由 after/ imply

    user.md ::=
      標題(H1) + Persona/作答紀律   1       # 只在被問到時答、不主動講、給選項就挑對應那個
      ## Answer key                1       # <topic>: <value>；topic 名對齊 expect 的 ASK(topic)
        每筆 [reveal: when-asked|never] [kind: option|free] [trap: <一句>]
      ## Fallback                  1       # 問到表外的東西怎麼回（預設「你決定」）
      ## Notes                     0..1    # trap/never/turn cap 等特例

    after/ ::=
      預期終態的真實 fs 快照，結構與 before/ 對齊。
      before/ 到 after/ 的 diff 即此 unit 的 file outcome；比對交 judge，語意等價即可，非 byte-exact。

    artifact-output-contract ::=
      若 unit 預期產出 file artifact（新增/修改/刪除任何檔案），after/ 必須存在，且必須完整落下 expected artifact set。
      若 unit 不驗 file outcome，Provenance 或上游 verification point 必須明示 output channel 為 behavior/message only。
      hidden oracle material 只能留在 expect / shared expect / judge 端，不得放進 before/、prompt.md 或 user.md 假裝成 target 可見 input。
      若 unit 需要 runner / judge 在 target launch 前做契約判讀，可在 unit-local expect.md 額外宣告 `## Hidden oracle metadata`；此區塊屬 hidden oracle material 的結構化子集，正式 schema 見 rules/hidden-oracle-metadata.md。

    不變式 ::=
      E1  每 unit 必有 before/(分支則繼承 scenario)、prompt.md、expect.md，且 drop 完即可跑
      E2  before/ 同時是 droppable CWD 與 diff base
      E3  file 終態不寫進 expect.md；由 after/ imply。無 after/ 即不驗 file 終態，只驗行為
      E4  ### Turn 只含 Tool calls 與 Assistant message，不含 file diff
      E5  互動型 unit 必有 user.md；非互動省略
      E6  分支 scenario 每個 variant 是 {user.md, after/} 一對，即 input 對 output 函數的一個點
      E7  ## Cross-turn 僅多輪需要；其 order/gates/liveness 引用 event trace 的事件，不引用 file diff
      E8  語意 rubric 一律用 0.0/0.3/0.7/1.0 四錨點 + 具體片段；橫切判準放 shared/expect.md
      E9  每 unit 在 ## Provenance 標清楚它釘的 distinct 行為主張與存在理由
      E10 holdout 與 dev 結構相同；差別只在對 agent 隱藏
      E11 每個 unit dirname 必須是 `<before情境設定概述>_<after測試結果概述>`，用一個底線分出測試局面與預期觀測；若目標 skill 有多個可選 subcommand，before-segment 必須以 `<subcommand動作>-` 起頭
      E12 若 unit 預期新增/修改/刪除任何 file artifact，after/ 必填，且 after/ 必須含完整 expected artifact set
      E13 無 after 只允許 output channel 是 behavior/message only，且 Provenance 或 working-plan 已明示不驗 file outcome
      E14 `## Hidden oracle metadata` 若存在，只能被 runner / judge / outer evaluator 消費；不得被當成 target-visible input，且不得放進 shared/expect.md
    ```

### Good

情境: 一個互動型、有分支的 scenario

```
eval/
  shared/expect.md
  dev/互動訪談產生骨架/
    before/.aibdd/arguments.yml
    prompt.md
    variants/使用者選python根目錄_產出python根目錄骨架/
      user.md       # {python_e2e, zh-hant, course-api, repo root}
      after/        # 預期終態 fs：boundary id=course-api、python tail…
      expect.md     # Provenance / Run(Turn × tool+message) / Cross-turn
```

結果: drop before/ 餵 prompt 即可重跑；unit dirname 先講 before 局面、底線後講 after 觀測；行為看 expect.md，終態看 after/，答案↔結果由 user.md↔after/ 成對釘死，E1–E11 全中。

### Bad

情境: unit dirname 只叫 `happy`，並把預期檔案結果用 invariant 散文寫進 expect.md 的一個 `## Outcome`，且 turn 內也列了 file diff。

結果: 違反 E3（file 終態應由 after/ imply，不寫進 expect）、E4（turn 不含 file diff）與 E11（unit dirname 沒有分出測試局面與預期觀測）。

預期改法:

- 將 unit dirname 改成 `<before情境設定概述>_<after測試結果概述>`，刪掉 expect.md 的 Outcome/file diff，把預期終態落成真實的 after/ 快照；turn 只留 Tool calls 與 Assistant message。

### Bad

情境: unit 預期 AI 產出 `docs/architecture/order-checkout.class.mmd`，但作者認為「expect 已經寫 MUST 寫圖」所以省略 after/。

結果: 違反 E12。檔案產物沒有被真實 fs 快照釘住，judge 只能依語意猜測，會漏掉「圖沒寫到磁碟」或「寫錯路徑」。

預期改法:

- 在 after/ 寫出 `docs/architecture/order-checkout.class.mmd` 等完整 expected artifact；expect 只保留 Tool calls 與 Assistant message 的關鍵斷言。
