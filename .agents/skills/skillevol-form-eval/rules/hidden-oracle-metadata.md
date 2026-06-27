# Hidden oracle metadata 規則

本檔定義 `expect.md` 中 `## Hidden oracle metadata` 的正式 schema、合法值、放置邊界與 consumer 決策表。`skillevol-form-eval` 是這組 metadata 的 SSOT；其他 skill 只能消費這裡定義的契約，不得自帶第二套 schema。

## Rule 1 — `## Hidden oracle metadata` 只描述 hidden oracle input contract

- 這個區塊只用來宣告 fixture author 要交給 runner / judge / outer evaluator 的 hidden oracle input contract。
- 它不是 target-visible input，不能出現在 `before/`、`prompt.md`、`user.md`，也不能被塞進 target subagent opening input。
- 它不是 consumer 執行結果輸出區。`launch_decision`、`preflight_check`、`overfit_risk`、`skip_reason` 這類欄位屬於 runner 衍生 observation，不得由 fixture author 寫進 metadata。

## Rule 2 — 放置位置與優先序

- `## Hidden oracle metadata` 只允許出現在 unit-local `expect.md`。
- `shared/expect.md` 不得宣告 hidden oracle metadata，避免把條件式契約誤擴散到整組 units。
- 若 outer unit 與 nested target unit 都宣告 metadata，consumer 必須以 nested target unit 的 metadata 為準；outer unit 只能補自己的 runner-side expectation，不得覆寫 nested target contract。

## Rule 3 — 區塊存在時的最小正式 schema

- 目前正式輸入欄位只有兩個：
  - `oracle_style`
  - `design_variance`
- 若 `## Hidden oracle metadata` 區塊存在，這兩個欄位都必填；缺一不可。
- 未宣告 metadata 代表走 legacy path：consumer 可直接進既有 launched 流程。

正式 skeleton:

```md
## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid
```

## Rule 4 — 合法值

### `oracle_style`

- `exact-after-single-golden`
  - 表示 oracle 仍用單一 `after/` golden 快照充當唯一合法答案。
- `runner-only`
  - 表示不啟動 target；由 outer runner 直接依 deterministic runner contract 決定 pass/fail。

### `design_variance`

- `unique`
  - 表示題目預期單一正解或單一路徑的合法輸出。
- `multi-valid`
  - 表示題目本質上多解，允許多種語意等價的合法輸出。

## Rule 5 — consumer 決策表

consumer 必須依下表解讀 metadata，不得偏離：

| design_variance | oracle_style | launch_decision | preflight_check | verdict |
| --- | --- | --- | --- | --- |
| `multi-valid` | `exact-after-single-golden` | `skipped` | `failed` | `fail` |
| `multi-valid` | `runner-only` | `skipped` | `passed` | `pass` |
| `unique` | `*` | `launched` | `passed` | delegate to target judge |
| 未宣告 metadata | `*` | `launched` | `not-applicable` | delegate to target judge |

補充解釋:

- `multi-valid + exact-after-single-golden` 代表題目多解，但 oracle 只接受一個 golden；這是 oracle contract 自己的 overfit，consumer 必須 preflight hard-fail，且不得啟動 target。
- `multi-valid + runner-only` 是合法 contract；consumer 必須直接走 skip-launch 路徑，不得因為看到 `multi-valid` 就一律 fail。

## Rule 6 — 禁止欄位與別名

下列欄位或值不屬於正式 hidden oracle input schema，不得再寫入：

- `preflight_policy`
  - 這是 consumer 根據決策表推導的處置，不是獨立輸入。
- `target_launch_policy`
  - 這與 `launch_decision` 重複；應由 consumer 推導，不是 fixture author 輸入。
- `target_artifact_family`
  - 這不是 preflight hidden oracle contract 的正式欄位；若未來真的需要，應另立非 preflight metadata 規則。
- `exact-target-after`
  - 這是舊別名；正式值只保留 `exact-after-single-golden`。

## Rule 7 — 與其他 hidden oracle material 的關係

- `## Hidden oracle metadata` 只是 hidden oracle material 的結構化子集。
- rubric、golden rationale、judge-only commentary、Provenance 推理等仍可存在於 `expect.md`、`shared/expect.md` 或 judge payload，但不屬於本 schema。
- 換句話說，本規則只管「consumer 啟動前要先讀哪些結構化鍵」，不管所有 hidden oracle prose。

### Good

情境: 多解 target 被單一 golden oracle 綁死，outer runner 應在 target launch 前 hard-fail

```md
## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid
```

結果: contract 誠實宣告「題目多解但評分尺只吃單一 golden」，consumer 可依決策表直接 `skipped + failed + fail`。

### Good

情境: 多解 target 交給 runner-only 合法契約，outer runner 應直接 pass

```md
## Hidden oracle metadata

- oracle_style: runner-only
- design_variance: multi-valid
```

結果: contract 誠實宣告 runner-only pass；consumer 可依決策表直接 `skipped + passed + pass`。

### Bad

情境: 在 metadata 區塊混入 consumer 衍生欄位

```md
## Hidden oracle metadata

- oracle_style: runner-only
- design_variance: multi-valid
- preflight_policy: hard-fail-before-target-launch
- target_launch_policy: skip
```

結果: 輸入 contract 與 runner 衍生決策混層；同一語意被重複宣告，之後一旦 consumer 決策表改動就會漂移。

預期改法:

- 只保留正式輸入欄位 `oracle_style` 與 `design_variance`；其餘交由 consumer 依決策表推導。

### Bad

情境: 在多解題上沿用舊別名或自創值

```md
## Hidden oracle metadata

- oracle_style: exact-target-after
- design_variance: multi-valid
```

結果: consumer 與 fixture 會出現 alias drift；不同 skill 可能把它誤解成另一個新值。

預期改法:

- 統一改成 `oracle_style: exact-after-single-golden`。
