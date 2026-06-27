# Sub-SOP 形式（子程序的寫法判準）

本檔是一個 RuleFile，描述「一個 Sub-SOP 該怎麼寫」。Sub-SOP 是一段被 parent SOP 呼叫的有序子程序: 自成一個目錄與 SOP.md，內含依序執行的步驟，跑完把產物交回 parent。

排版守則: 全檔不用粗體、斜體、底線、emoji、裝飾性 blockquote、箭頭符號；結構只靠 Header、列表、與「主題: 內容」。

## Rule 1 — Sub-SOP 承載有序子程序，步驟順序有意義

- Sub-SOP 的步驟前後有依賴、順序有語意: 後一步吃前一步的產物。
- 若步驟其實無序、可任意調換、可逐條獨立驗收，那是 RuleFile，不該編號成 Sub-SOP。

### Good

情境: 一段收集配置的子程序

```
1. resolve 路徑變數
2. 讀現有檔
3. 分析缺哪些配置
4. 回寫結果
```

結果: 步驟有明確先後依賴，順序不能調換。

### Bad

情境: 把無序規定編號成 SOP 步

```
1. 命名要 kebab-case
2. command 與 query 分檔
3. 同義規則去重
```

預期改法:

- 這些是無序原子規定，改抽成 RuleFile（skillevol-form-rule-file），不是 Sub-SOP。

## Rule 2 — 每步一個動作、法定 verb、自然語言

- 每步只做一個動作，動詞用法定 verb: read、think、write、delegate、run。
- 用白話陳述，不寫 program-like 記號（$$ 暫存器、BRANCH / GOTO、PARSE / ASSERT 之類 pseudo-verb）。

### Good

```
3. read 目標 feature 檔，解析它的 Rule 標題與 body。
```

結果: 一步一動作、法定 verb、純文字看得懂。

### Bad

```
3. $rules = PARSE(feature); ASSERT len>0; BRANCH ok ? GOTO 4 : STOP
```

預期改法:

- 改寫成自然語言加法定 verb 的一步一動作，把判斷與停止條件用白話寫。

## Rule 3 — 有明確 entry 與 exit

- Sub-SOP 開頭綁定它要的輸入（路徑、來源、parent 傳入的值），結尾把產物或結論交回 parent。
- 它是一段封閉、可被獨立呼叫的程序: 不靠沒綁定的隱性狀態，也不能跑完不交代產出什麼。

### Good

情境: 一個 SOP.md 開頭有參數段、結尾交付

```
參數: 需求錨點 PLAN_SPEC、feature 根目錄 FEATURE_SPECS_DIR
...
6. 把整理好的 decisions 交回 parent。
```

結果: 輸入綁在開頭、產物交在結尾，parent 知道呼叫它會拿到什麼。

### Bad

情境: 中途憑空用一個沒綁定的路徑，跑完也沒說產出什麼。

預期改法:

- 開頭補 entry 綁定所有輸入，結尾補 exit 明確交出產物給 parent。

## Rule 4 — 自洽: 只動職責內 artifact，引用的 leaf 放自己子樹

- Sub-SOP 只產生或修改它職責內的 artifact，不順手改 parent 的別步或別的 Sub-SOP。
- 它引用的 rules、templates 等 leaf 放在自己子樹下（例如 `<dir>/rules/`、`<dir>/steps/`），不散到 parent 或 repo 根。

### Good

情境: 目錄 `01-ask-config/` 下自帶 leaf

```
01-ask-config/
  SOP.md
  rules/answer-resolution-gate.md
  templates/plan.template.md
```

結果: Sub-SOP 與它的 leaf 同子樹，可整包搬移、整包理解。

### Bad

情境: Sub-SOP 引用 repo 根的 `rules/x.md`，或順手改了 parent 的別步。

預期改法:

- 把引用的 leaf 移進 Sub-SOP 自己子樹；變更只限職責內 artifact。

## Rule 5 — 一個 Sub-SOP 一個職責

- 一個 SOP.md 只承載一個內聚職責的子程序。
- 把兩個不相干子程序塞同一個 SOP.md，parent 無法分別呼叫，步驟也互相干擾。多職責就拆多個 Sub-SOP。

### Good

```
01-ask-config/SOP.md      收集配置
02-execute-layout/SOP.md  產生骨架
```

結果: 一個 Sub-SOP 一個職責，parent 點名即取。

### Bad

情境: 一個 SOP.md 同時收配置、產骨架、又跑驗證。

預期改法:

- 拆成多個 Sub-SOP，各承載一個職責。

## Rule 6 — parent 用 invoke reference 串接，不在 parent 展開子步

- parent SOP 步以「執行 `<dir>`」或「read `<dir>/SOP.md`」掛 Sub-SOP。
- parent 只看得到「呼叫這個 Sub-SOP」，子步細節留在 Sub-SOP 的 SOP.md，不在 parent 逐條展開。

### Good

```
2. 執行 01-ask-config 收集配置。read 01-ask-config/SOP.md。
```

結果: parent 一眼可掃讀，子程序細節封裝在 Sub-SOP 內。

### Bad

情境: parent 第 2 步底下又把 01-ask-config 的全部 10 個子步逐條列出。

預期改法:

- parent 只留 invoke reference，子步留在 Sub-SOP 的 SOP.md。
