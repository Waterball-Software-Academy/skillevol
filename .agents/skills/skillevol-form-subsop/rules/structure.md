# Sub-SOP 結構（canonical form）

## Rule 1 — Sub-SOP 的結構組成必須符合 canonical 定義

- Sub-SOP 的結構組成必須遵守底下定義

    ```
    SubSOP ::=
      目錄                            1      # 目標 skill 根下；kebab-case；需與兄弟 Sub-SOP 排序時加 NN- 前綴
      └─ SOP.md                       1
           參數段                     0..1   # entry: 綁定輸入路徑與來源
           "# SOP"                    1
             Step                     1..N   # 有序，編號自 1 遞增
           leaf 子樹                  0..N   # 選用: <dir>/rules、<dir>/templates、<dir>/steps...

    Step ::=
      序號 + verb + 主指令            1      # verb in {read, think, write, delegate, run}；自然語言
      reference                       0..1   # 依 <dir> 子樹下的 rules/templates

    exit ::= 最後一步把產物或結論交回 parent

    invocation ::= parent SOP 步以「執行 <dir>」或「read <dir>/SOP.md」串接

    不變式 ::=
      S1  步驟有序、編號自 1 遞增、順序有語意（後步吃前步產物）
      S2  每步一個動作、法定 verb、自然語言（無 program-like）
      S3  有明確 entry（綁輸入）與 exit（交產物給 parent）
      S4  只動職責內 artifact；引用的 leaf 放自己子樹
      S5  一個 Sub-SOP 一個職責
      S6  parent 以 invoke reference 串接，不在 parent 展開子步
      S7  全檔無 program-like（$$ / BRANCH / GOTO / pseudo-verb）與 markdown 裝飾
    ```

### Good

情境: 一個符合定義的 Sub-SOP

```
01-ask-config/
  SOP.md
  rules/answer-resolution-gate.md

# SOP.md 內容
參數: 需求錨點 PLAN_SPEC、feature 根目錄 FEATURE_SPECS_DIR

# SOP
1. read 現有的 KICKOFF_PLAN.md。
2. think 比對缺哪些配置。請嚴格遵守 rules/answer-resolution-gate.md 來執行此步驟。
3. write 回寫答案，並把 decisions 交回 parent。
```

結果: 目錄加 SOP.md、有參數 entry、步驟有序且法定 verb、leaf 在自己子樹、結尾交付。S1 至 S7 全中。

### Bad

情境: 一個違反定義的 Sub-SOP（步驟無序、program-like、無 exit）

```
# SOP
1. $cfg = PARSE(plan); BRANCH ok ? GOTO 3 : STOP
2. 命名要 kebab-case
3. 同義規則去重
```

結果: 步驟其實無序（違反 S1）、用了 program-like 記號（違反 S2/S7）、跑完沒交產物（違反 S3）。

預期改法:

- 無序規定改抽成 RuleFile；保留的有序步改成自然語言加法定 verb，並補上 entry 綁輸入與 exit 交產物。
