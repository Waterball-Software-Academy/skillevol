# RuleFile 結構（canonical form）

## Rule 1 — RuleFile 的結構組成必須符合 canonical 定義

- RuleFile 的結構組成必須遵守底下定義

    ```
    RuleFile ::=
      標題(H1)                        1       # 一句話領域描述
      說明段                          0..1
      Rule                            1..N

    Rule ::=
      判準標題(H2 "## Rule N — …")    1       # 主詞 + 必須/應/不得 + 單一條件；N 自 1 遞增
      descriptions                    1..N Block
      Good(H3 "### Good")             1       Exhibit
      Bad(H3 "### Bad")               1       Exhibit + 預期改法

    Block ::= paragraph | table | format | code | path | output
      paragraph         : 一行散文判準
      table             : "標籤: " 一行 + markdown 表格字串（僅原子、欄位固定、橫向比較才用）
      format            : "標籤: " 一行 + 範本字串
      code | path | output : fenced 片段
      規範              : table/format 用字串保真，不拆成結構化欄位；不用粗體當標籤

    Exhibit ::=
      情境: <一句>                    0..1
      example-data-block              1..N    # 具體片段（code/path/output/table/帶真實資料的描述），非抽象話
      結果: <一句後果>                0..1    # 取代箭頭符號
      預期改法:                       1（僅 Bad）
        - <針對該壞例的最小修正>      1..N    # 對症最小修正，不複製 Good

    不變式 ::=
      I1  恰一個 H1 標題在檔首
      I2  至少一條 Rule，每條是一個 H2
      I3  每條 Rule 內順序固定: 判準標題、descriptions、Good、Bad
      I4  每條 Rule 的 descriptions 至少一個 Block
      I5  每條 Rule 恰一個 ### Good 與一個 ### Bad
      I6  每個 Bad 必附預期改法，且至少一個修正項
      I7  全檔不用粗體、斜體、底線、emoji、裝飾性 blockquote、箭頭符號
    ```

### Good

情境: 一個符合上述定義的最小 RuleFile

```
# 命名規則

## Rule 1 — service 名必須 kebab-case

- 全小寫、連字號分詞。此值寫進 boundary id 與 Maven artifactId，大小寫或底線會讓下游 ID 對不上。

### Good

course-api

### Bad

CourseAPI
預期改法:
- 轉全小寫、以連字號分詞: CourseAPI 變 course-api
```

結果: 一個 H1、一條 H2 Rule、descriptions 至少一句、Good 與 Bad 各一、Bad 附預期改法、全檔零裝飾，七條不變式全中。

### Bad

情境: 一個違反定義的 RuleFile（Rule 缺 Bad、descriptions 留空）

```
# 命名規則

## Rule 1 — service 名必須 kebab-case

### Good

course-api
```

結果: 違反 I4（descriptions 空）與 I5/I6（缺 ### Bad 與預期改法）。

預期改法:
- 在判準標題下補一句 descriptions，並補上 `### Bad` 與其「預期改法:」，使順序回到 判準標題、descriptions、Good、Bad。
