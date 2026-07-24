# TemplateFile 結構（canonical form）

## Rule 1 — TemplateFile 的結構組成必須符合 canonical 定義

- 一份 TemplateFile 樣板的結構組成必須遵守底下定義

    ```
    TemplateFile ::= skeleton-file + example-file    # 雙檔配對，缺一不可

    skeleton-file ::= <name>.<ext>                   # 骨架檔；固定結構 + 固定語法 + placeholder
    example-file  ::= <name>.example.<ext>           # 範例檔；同結構的具體成品

    <name> ::= 直指產物主題與格式的名稱
    <ext>  ::= 與實際輸出格式一致的副檔名；骨架檔與範例檔相同

    placeholder ::= {{UPPER_SNAKE_CASE}}             # 雙大括號包大寫蛇形；語意自明；
                                                     # 同檔一致；只覆蓋可變內容

    location ::= 骨架檔與範例檔皆置於目標 skill 的 templates/ 目錄

    不變式 ::=
      T1  每份樣板同時有骨架檔與範例檔，同 basename、同副檔名，範例檔多一個 .example
      T2  骨架檔與範例檔皆置於 templates/ 目錄
      T3  骨架檔只放固定結構、固定語法與 placeholder；範例檔放同結構的具體成品
      T4  placeholder 用 {{UPPER_SNAKE_CASE}}、語意自明、同檔一致
      T5  placeholder 只覆蓋可變內容，不覆蓋固定格式符號
      T6  樣板名稱直指產物主題與格式；副檔名與輸出格式一致
      T7  樣板文字以繁體中文為主（程式語法、固定 token、檔名、專有名詞除外）
    ```

### Good

情境: 一組符合定義的雙檔配對

```
骨架檔 templates/class-diagram.mmd
classDiagram
class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}

範例檔 templates/class-diagram.example.mmd
classDiagram
class Order {
  +id: UUID
}
```

結果: 骨架檔與範例檔同 basename、同副檔名，範例檔多一個 .example；骨架檔只留結構與 {{UPPER_SNAKE_CASE}} placeholder，範例檔給同結構的具體成品。T1 至 T7 全中。

### Bad

情境: 一份違反定義的樣板（缺範例檔、placeholder 記號與命名皆不合）

```
骨架檔 templates/template.mmd
classDiagram
class [name] {
  +{{value1}}: <<type>>
}
```

結果: 缺對應範例檔（違反 T1）；名稱過度抽象、看不出產物主題（違反 T6）；placeholder 記號混用且語意不明（違反 T4）。

預期改法:

- 補上 templates/<主題>.example.mmd 提供同結構的具體成品；骨架檔改名直指產物主題（例如 class-diagram.mmd）；placeholder 統一成 {{UPPER_SNAKE_CASE}} 並改用語意自明的名字。
