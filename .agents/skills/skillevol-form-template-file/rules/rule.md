# TemplateFile 形式（樣板的寫法判準）

本檔是一個 RuleFile，描述「一份 TemplateFile 樣板該怎麼寫」。一份樣板由骨架檔與範例檔雙檔配對組成: 骨架檔留固定結構、固定語法與 placeholder，供執行者複製後填值；範例檔提供同結構的具體成品，供執行者對照填完後的樣子。兩檔皆置於目標 skill 的 templates/ 目錄。

排版守則: 全檔不用粗體、斜體、底線、emoji、裝飾性 blockquote、箭頭符號；結構只靠 Header、列表、與「主題: 內容」。

## Rule 1 — 每份樣板都必須同時提供骨架檔與範例檔

- 每份樣板必須以同 basename、同副檔名的兩個檔案成對存在: 骨架檔 `<name>.<ext>`，範例檔 `<name>.example.<ext>`。
- 兩個檔案都必須放在目標 skill 的 templates/ 目錄下。
- 不得只建立骨架檔而缺少範例檔，也不得只保留範例檔而沒有骨架檔。

### Good

情境: 一組同主題、同副檔名的完整雙檔配對

```
templates/class-diagram.mmd
templates/class-diagram.example.mmd
```

結果: 骨架與成品成對，執行者複製骨架後可對照範例填值。

### Bad

```
templates/class-diagram.mmd
```

預期改法:

- 補上 templates/class-diagram.example.mmd，提供同結構的具體成品，讓執行者同時看到骨架與成品。

## Rule 2 — 骨架檔與範例檔的責任必須清楚分開

- 骨架檔只放固定結構、固定語法與待改寫的 placeholder，讓執行者可直接複製後再填值。
- 範例檔必須提供同結構的具體成品，讓執行者知道填完後應長成什麼樣子。
- 不得讓骨架檔塞滿一次性的具體內容，也不得讓範例檔只是再貼一份尚未替換的 placeholder。

### Good

情境: 骨架檔保留結構與 placeholder，範例檔提供對照用的完整內容

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

結果: 兩檔角色分明，骨架指導複製、範例指導填值。

### Bad

```
骨架檔 templates/class-diagram.mmd
classDiagram
class Order {
  +id: UUID
}

範例檔 templates/class-diagram.example.mmd
classDiagram
class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}
```

預期改法:

- 兩檔角色對調: 骨架檔改回結構與 placeholder，範例檔改成同結構的具體成品。

## Rule 3 — 樣板名稱應直指產物主題與格式

- 樣板名稱應讓人一眼看出要生成的產物主題，而非只用抽象或暫時性名稱。
- 副檔名應與實際輸出格式一致，避免骨架檔與最終產物格式脫節。
- 同一組雙檔應只對應單一產物主題，避免把多種無關格式混進同一個樣板名稱下。

### Good

情境: 檔名與副檔名都對齊要生成的 Mermaid 類別圖

```
templates/class-diagram.mmd
templates/class-diagram.example.mmd
```

結果: 一眼看出這組樣板要生成什麼、輸出成什麼格式。

### Bad

```
templates/template.txt
templates/template.example.txt
```

預期改法:

- 依產物主題與實際輸出格式改名，例如 templates/class-diagram.mmd 與 templates/class-diagram.example.mmd。

## Rule 4 — placeholder 必須使用 {{UPPER_SNAKE_CASE}} 格式

- 骨架檔中的 placeholder 必須用雙大括號包住大寫蛇形命名，例如 {{CLASS_NAME}}、{{FIELD_TYPE}}。
- 名稱必須表達語意，不得使用 {{VALUE1}}、{{TEXT}} 這類過度抽象的命名。
- 同一段若需多個 placeholder，應讓每個名稱都對應到明確的待填欄位。

### Good

情境: placeholder 包法一致、名稱直接對應待填內容

```
class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}
```

結果: 人與 script 都能機械定位並替換。

### Bad

```
class [name] {
  +{{value1}}: <<type>>
}
```

預期改法:

- 統一成 {{...}} 雙大括號包法，並改用語意自明的大寫蛇形名稱: {{CLASS_NAME}}、{{FIELD_NAME}}、{{FIELD_TYPE}}。

## Rule 5 — 同一語意的 placeholder 應保持一致命名

- 同一份骨架檔中，多處代表同一語意槽位時，應重複使用同一個 placeholder 名稱。
- 不宜把同一語意拆成多個不同名稱，否則執行者難以判斷哪些位置要同步改寫。
- 只有語意真的不同時，才拆成不同的 placeholder。

### Good

情境: 同一個類別名稱在多處都用 {{CLASS_NAME}}

```
class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}
{{CLASS_NAME}} --> {{RELATED_CLASS_NAME}}
```

結果: 改寫時不會混淆哪些位置代表同一個值。

### Bad

```
class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}
{{ENTITY_NAME}} --> {{RELATED_CLASS_NAME}}
```

預期改法:

- 同一語意統一成 {{CLASS_NAME}}，避免讓人誤以為 {{ENTITY_NAME}} 要填不同值。

## Rule 6 — placeholder 只覆蓋可變內容，不覆蓋固定格式符號

- placeholder 只應標示真正會變動的內容區段；固定語法、分隔符號、標題符號與結構關鍵字應保留在骨架中。
- 不得把 Markdown 標記、程式語法或圖表語法整段包進 placeholder，否則骨架會失去格式引導作用。
- 若某段文字同時含固定格式與可變內容，應只讓 placeholder 覆蓋可變部分。

### Good

情境: Markdown 標題與 Mermaid 語法都保留，只把會替換的值抽成 placeholder

```
## {{SECTION_TITLE}}

class {{CLASS_NAME}} {
  +{{FIELD_NAME}}: {{FIELD_TYPE}}
}
```

結果: 骨架持續提供穩定結構，只留可變值待填。

### Bad

```
{{## SECTION_TITLE}}

{{class CLASS_NAME {
  +FIELD_NAME: FIELD_TYPE
}}
```

預期改法:

- 固定格式（## 標題、class 語法）留在骨架，placeholder 只包實際會替換的值: {{SECTION_TITLE}}、{{CLASS_NAME}} 等。

## Rule 7 — 樣板文字以繁體中文為主

- 樣板中的章節標題、欄位名稱、註解、說明文字與範例內容必須以繁體中文為主。
- 程式語法、固定識別 token、檔名與專有名詞除外，保留原樣。

### Good

情境: 一份說明性文字為繁中的骨架檔

```
## {{區段標題}}

# 待填: 依產物主題填入實際內容
class {{CLASS_NAME}}
```

結果: 說明性文字皆繁中，程式語法與固定 token 保留原樣。

### Bad

```
## {{SECTION_TITLE}}

# TODO: fill in the actual content by product theme
class {{CLASS_NAME}}
```

預期改法:

- 把章節標題與註解等說明性文字改為繁中，僅保留 class 等程式語法與固定 token。
