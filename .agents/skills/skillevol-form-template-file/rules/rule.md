# TemplateFile 形式（內容樣板的寫法判準）

本檔是一個 RuleFile，描述「一個 TemplateFile 該怎麼寫」。TemplateFile 是某種產物的內容樣板: generator 或 LLM 拿它去填出成品。它有兩種變體: raw（無 guideline，給 script 逐字消費）與 guideline（範本加指導，給 LLM 渲染）。

排版守則: 全檔不用粗體、斜體、底線、emoji、裝飾性 blockquote、箭頭符號；結構只靠 Header、列表、與「主題: 內容」。

## Rule 1 — 範本本體逐字即成品，除 placeholder 外不加料

- TemplateFile 的範本本體會被原樣吐成產物。除了 placeholder 以外，每個 byte 都照搬。
- 不得為了好看在範本裡加說明註解、加散文、改縮排或正規化空白: 那些都會跟著被吐進產物、污染成品。指導要寫的話一律放 guideline 段。

### Good

情境: 一段給 generator 逐字輸出的 yaml 範本

```
PROJECT_SPEC_LANGUAGE: ${PROJECT_SPEC_LANGUAGE}
STARTER_VARIANT: ${STARTER_VARIANT}
```

結果: generator 逐字吐出，產物即合法 yaml，沒有多餘文字。

### Bad

```
# 請把下面的語言改成你的（這行會被一起吐出去）
PROJECT_SPEC_LANGUAGE: ${PROJECT_SPEC_LANGUAGE}
```

預期改法:

- 把指導句移到 guideline 段或直接刪除，範本只留成品骨架與 placeholder。

## Rule 2 — placeholder 用固定記號、語意自明、同檔一致

- 要填的位置用固定記號標出（`${NAME}` 或 `{{NAME}}`），名字看得出要填什麼，且整檔風格一致，讓人與 script 都能機械定位並替換。
- 混用記號、或用模糊名（例如 `${X}`、`<<那個值>>`）會讓替換對不準、漏填或填錯。

### Good

```
boundary id: ${KICKOFF_TLB_ID}
type: ${KICKOFF_BOUNDARY_TYPE}
```

結果: 兩個 placeholder 同記號、語意自明，grep 與手填都不會搞錯。

### Bad

```
boundary id: ${X}
type: <<那個型別>>
```

預期改法:

- 統一成一種記號、改用語意自明的名字: `${KICKOFF_TLB_ID}`、`${KICKOFF_BOUNDARY_TYPE}`。

## Rule 3 — raw 與 guideline 兩變體要選對消費者

- raw（無 guideline）整檔就是範本本體，byte-exact，給 script 或 generator 逐字消費。
- guideline 是範本加一段指導，給需要說明才填得對的 LLM 渲染。
- script 逐字消費的卻包成 guideline、或 LLM 要填的卻沒給 guideline，都是選錯變體。

### Good

情境: 一個給 script append 的 tail 檔

```
（整檔只有範本本體，無任何 Guideline 段）
PY_TESTCONTAINERS: ${PY_TESTCONTAINERS}
```

結果: script 逐字 append，不會吐出多餘標題。

### Bad

```
# Guideline
這個 tail 會被 append 到 arguments.yml
# Template
PY_TESTCONTAINERS: ${PY_TESTCONTAINERS}
```

預期改法:

- script 逐字消費者一律用 raw 變體，整檔只留範本本體，拿掉 Guideline/Template 兩段框。

## Rule 4 — 指導只待在 Guideline 段，不滲進 Template 段

- guideline 變體裡，「每格填什麼、來源、禁忌、回答或批次格式」一律寫在 Guideline 段。
- Template 段只放骨架。把指導混進 Template，渲染時會被一起吐成產物。

### Good

情境: 一張問卷 question record 範本

```
# Guideline
Q3 service 名填 kebab-case，全小寫連字號。
# Template
- id: q3-backend-service-name
  answer: {{TLB_ID}}
```

結果: 指導在 Guideline，Template 只有骨架，渲染後產物乾淨。

### Bad

```
# Template
- id: q3-backend-service-name
  answer: {{TLB_ID}}   (這裡填 kebab-case，全小寫)
```

預期改法:

- 把「填 kebab-case」移到 Guideline 段；Template 只留 `{{TLB_ID}}`。

## Rule 5 — 一個 TemplateFile 只承載一種產物形狀

- 一支 TemplateFile 只放一種產物的骨架。把多種不相干產物塞同一檔，generator 無法單獨取用，placeholder 也會互相干擾。
- 多種產物就拆多支 TemplateFile。

### Good

```
（boundary.yml 一支 TemplateFile）
id: ${KICKOFF_TLB_ID}
type: ${KICKOFF_BOUNDARY_TYPE}
```

結果: 一檔一種產物，generator 點名即取。

### Bad

```
id: ${KICKOFF_TLB_ID}
---
classDiagram
  class ${KICKOFF_TLB_ID}
```

預期改法:

- 拆成兩支 TemplateFile: boundary.yml 一支、component-diagram.class.mmd 一支。

## Rule 6 — outPath 不寫進範本本體

- 檔最後要吐到哪個路徑，是 runtime 或 generator 的事，不是範本內容。
- 把 outPath 寫進範本本體會被一起吐出、污染產物；落點交給呼叫端決定。

### Good

```
id: ${KICKOFF_TLB_ID}
type: ${KICKOFF_BOUNDARY_TYPE}
```

結果: 範本只有成品內容，落點由 generator 參數決定。

### Bad

```
# outPath: specs/architecture/boundary.yml
id: ${KICKOFF_TLB_ID}
```

預期改法:

- 刪掉 outPath 註解；落點交呼叫端，範本本體只留成品。
