# TemplateFile 結構（canonical form）

## Rule 1 — TemplateFile 的結構組成必須符合 canonical 定義

- TemplateFile 的結構組成必須遵守底下定義

    ```
    TemplateFile ::= raw | guideline       # 依消費者擇一變體

    raw ::=                                 # 給 script / generator 逐字消費
      template-body                  1      # 整檔就是範本本體；byte-exact
                                            # 無 Guideline、無指導散文、無 outPath、無 displayName 框

    guideline ::=                           # 給 LLM 渲染
      Guideline(H1 "# Guideline")    1      # 渲染/填寫/禁忌/回答格式的指導散文
      Template(H1 "# Template")      1      # 範本本體（fenced，配 lang）

    template-body ::=
      除 placeholder 外逐字即成品；不正規化、不加說明、不改縮排

    placeholder ::= ${NAME} | {{NAME}}      # 固定記號、語意自明、同檔風格一致
    lang        ::= 範本語言標註            # guideline 變體 Template fence 用

    不變式 ::=
      T1  raw 變體只有範本本體，無 Guideline、無指導、無 outPath
      T2  guideline 變體恰一個 # Guideline 與一個 # Template
      T3  範本本體除 placeholder 外逐字即成品，不正規化、不加說明
      T4  placeholder 用固定記號、語意自明、同檔風格一致
      T5  指導只在 Guideline 段，不滲進 Template 段
      T6  一個 TemplateFile 只承載一種產物形狀
      T7  outPath 不寫進範本本體
    ```

### Good

情境: 一個符合定義的 guideline 變體 TemplateFile

```
# Guideline

Q3 service 名填 kebab-case，全小寫連字號；此值寫進 boundary id。

# Template

- id: q3-backend-service-name
  answer: {{TLB_ID}}
```

結果: 恰一個 Guideline 與一個 Template；指導全在 Guideline；Template 只有骨架與固定記號 placeholder；無 outPath。T1 至 T7 全中。

### Bad

情境: 一個違反定義的檔（raw 卻包了指導、placeholder 記號混用）

```
# Guideline
這個 tail 會被 append
# Template
PY_X: ${X}
db: <<那個值>>
```

結果: 給 script 逐字消費者卻用 guideline 框（違反 T1）；placeholder 記號混用且語意不明（違反 T4）。

預期改法:

- 改回 raw 變體: 整檔只留範本本體、拿掉 Guideline/Template 兩段；placeholder 統一成 `${...}` 並改用語意自明的名字。
