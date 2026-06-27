# RuleFile 形式（規則檔的寫法判準）

本檔本身就是一個 RuleFile，用 RuleFile 的形式描述「一個 RuleFile 該怎麼寫」。

## Rule 1 — H1 後說明段只能交代整份 RuleFile 範圍

- H1 後可以有一段短說明段，只用來交代整份規則檔的範圍與用途。這段不是任何一條 Rule 的 descriptions，不得放入 格式、表格、範本或可驗收判準。

### Good

情境: RuleFile 開頭只交代整份檔案的領域。

```
# 輸出路徑規則

本檔規範產物路徑、檔名與覆寫行為。

## Rule 1 — 輸出檔必須寫在指定目錄
```

結果: H1 後說明段只提供範圍，真正的可驗收判準從 H2 Rule 開始。

### Bad

情境: H1 後說明段塞入可驗收格式。

```
# 輸出路徑規則

格式:
output path:
- directory: ...
- filename: ...

## Rule 1 — 輸出檔必須寫在指定目錄
```

結果: `格式:` 成為沒有 Good/Bad 的隱形 rule，違反 RuleFile 結構。

預期改法:

- 將 `格式:` 搬進一條正式 H2 Rule 的 descriptions，並替該 Rule 補上 Good、Bad 與預期改法。

## Rule 2 — Rule 必須從 H2 開始並包含三個部位

- 一條 Rule 必須從 H2 `## Rule N — ...` 開始，且只由三個部位組成: descriptions、Good、Bad。descriptions 是一句以上的判準本體；Good 是帶具體資料的正例 storyboard；Bad 是帶具體資料的反例 storyboard，且必附「預期改法」。

### Good

情境: 一條完整 Rule。

```
## Rule 1 — service 名必須 kebab-case

- 全小寫、連字號分詞。此值會寫進 boundary id，大小寫或底線會讓下游 ID 對不上。

### Good

course-api

### Bad

CourseAPI

預期改法:

- 轉全小寫、以連字號分詞。
```

結果: Rule 內順序是 descriptions、Good、Bad，讀者能完整驗收。

### Bad

情境: 把 Rule 組成寫在 H1 後前言，而不是做成 H2 Rule。

```
一條 Rule 的組成:
1. descriptions
2. Good
3. Bad
```

結果: 這段沒有自己的 Good/Bad，變成無法驗收的前言規定。

預期改法:

- 將該規定改成正式 H2 Rule，並補上 descriptions、Good、Bad 與預期改法。

## Rule 3 — 格式表格範本必須放在 descriptions 內

- `格式:`、表格與範本都屬於 Rule 的 descriptions block，只能放在某條 H2 Rule 標題之後、`### Good` 之前。若放在 H1 後說明段、Good/Bad 之外或檔案尾端，它們就失去所屬判準，後續 derive 或審查時會不知道該引用哪條 Rule。

### Good

情境: 一條 Rule 需要定義固定輸出格式。

```
## Rule 1 — 欄位清單必須使用固定格式

- 欄位清單必須使用固定格式。
格式:
field list:
- name: ...
- type: ...

### Good
...
```

結果: `格式:` 明確屬於 Rule 1 的 descriptions。

### Bad

情境: 固定格式放在 H1 後說明段。

```
# 欄位清單規則

格式:
field list:
- name: ...

## Rule 1 — 欄位名稱必須使用 snake_case
```

結果: `格式:` 沒有所屬 H2 Rule，也沒有自己的 Good/Bad。

預期改法:

- 將 `格式:` 搬到對應 H2 Rule 的 descriptions 內，放在該 Rule 的 `### Good` 之前。

## Rule 4 — 每條 rule 至少一句 descriptions，不留空殼

- 一條 rule 的核心是它的判準本身。descriptions 至少要有一句，講清楚「要看什麼、為什麼這樣對、踩到會怎樣」，這是 rule 的因果，讀者光看這段就該懂。
- 只有 good/bad 而沒有 descriptions 的 rule 是空殼: 讀者得自己從兩個例子逆推你的意圖，一旦推錯，整條 rule 就被誤用。

### Good

情境: 一條講「命名要 kebab-case」的 rule

```
## Rule 1 — service 名必須 kebab-case
- 全小寫、連字號分詞。此值會寫進 boundary id 與 Maven artifactId，大小寫或底線會讓下游 ID 對不上。
### Good
course-api
### Bad
CourseAPI
```

結果: 讀者一句話就知道判準與因果，不必從例子猜。

### Bad

```
## Rule 1 — service 名必須 kebab-case
### Good
course-api
### Bad
CourseAPI
```

預期改法:

- 在標題與 Good 之間補一句 descriptions，寫出「為什麼要 kebab-case、踩到的後果」，再保留 good/bad。

## Rule 5 — 不用 markdown 裝飾代替結構

- 全檔不得出現粗體、斜體、底線、emoji、裝飾性 blockquote。重點靠精準用詞、標題層級、列表表達，不靠視覺強調。Text Mode 下直視檔案就要一眼可辨層級。
- 需要在 descriptions 內帶出一張對照表或一段範本時，用一行「標籤: 」起頭，下一行接 markdown 表格或範本字串，不用粗體當標籤。
- Good 與 Bad 的小節用 `### Good` 與 `### Bad`，不加勾叉 emoji；預期改法用「預期改法:」一行起頭接列表，不用粗體。

### Good

情境: 一條規則同時有散文判準與一張對照表

```
- path 用名詞複數、method 用 CRUD 對應，operationId 用 camelCase。
- method 對照表:
  | 動作 | method |
  | --- | --- |
  | 建立 | POST |
```

結果: 散文與表格各自成行、無裝飾，純文字下就讀得順。

### Bad

```
- **path 用名詞複數、method 用 CRUD 對應。**
### ✅ Good
### ❌ Bad
**預期改法**
```

預期改法:

- 拿掉所有 `**` 與 emoji: 散文直接寫，小節用 `### Good` / `### Bad`，修正段用「預期改法:」起頭。

## Rule 6 — Good 與 Bad 都要有具體 example data，不是抽象話

- rule 要可操作，靠的是看得到的具體資料。Good 與 Bad 都要走 storyboard: 一句「情境:」起頭，接一段真實片段（程式碼、路徑、輸出、或 markdown 表格），必要時用「結果:」收一句後果。
- 禁止用「要寫好」「不要寫壞」這種抽象話充當 example: 抽象的對錯讀者無法照著做，也無法照著檢查。

### Good

情境: 示範 Mermaid annotation 的字元集規則

```
### Good
情境: type = web-service
boundary.yml                 type: web-service
component-diagram.class.mmd   class X { <<web_service>> }
### Bad
component-diagram.class.mmd   <<web-service>>
```

結果: 兩端各用各的拼法，讀者一眼對照得出差異。

### Bad

```
### Good
annotation 要寫對
### Bad
annotation 寫錯了
```

預期改法:

- 兩邊都換成帶情境的具體片段: Good 給正確的 `<<web_service>>`，Bad 給會炸的 `<<web-service>>`，讓對錯看得到。

## Rule 7 — Bad 必附預期改法，且是針對該壞例的最小修正

- 每個 Bad 都要回答「那到底要怎麼改」。預期改法是針對這個壞例的最小、對症的修正動作: 從這個壞狀態出發，最短怎麼走出來。
- 它不是把整段 Good 複製貼上。複製 Good 只說「正確長這樣」，沒說「你現在錯在這、下一步動哪裡」。最小修正才有教學力。

### Good

情境: 壞例是 java package 帶了 hyphen

```
### Bad
com.example.course-api   （'-' 非法，編譯失敗）
預期改法:
- 對 tlb_id 做 replace('-', '')：course-api 變 com.example.courseapi
```

結果: 直接點出「移除 hyphen」這一個動作。

### Bad

```
### Bad
com.example.course-api
預期改法:
- （整段把 Good 的 com.example.courseapi 連同前後說明再貼一次）
```

預期改法:

- 把預期改法改寫成「針對此壞例的最小動作」（移除 hyphen），不要複製整段 Good。

## Rule 8 — 一條 rule 是一個原子判準

- 一條 rule 只承載一個主題的對錯軸。把兩個彼此獨立的判準塞進同一條，會讓 good/bad 失焦（一組例子要同時示範兩件事，兩件都講不清），也讓別人無法單獨引用其中一條。
- 發現一條 rule 在講兩件不相干的事，就拆成兩條，各自配自己的 descriptions 與 storyboard。

### Good

```
## Rule 3 — service 名必須 kebab-case
## Rule 4 — 規格語言非 zh-hant 時只改 PROJECT_SPEC_LANGUAGE 一處
```

結果: 兩個獨立判準各成一條，各有聚焦的 good/bad。

### Bad

```
## Rule 3 — service 名要 kebab-case 而且語系只改一處
### Good
course-api，且只改 PROJECT_SPEC_LANGUAGE
```

預期改法:

- 拆成兩條 rule: 命名與語系各一條，各配自己的 descriptions 與 good/bad。

## Rule 9 — table 與範本用字串保真，且只在橫向比較時才用 table

- descriptions 裡的 table 就是一段 markdown 表格字串、範本就是一段範本字串，原樣保留最直接、最不失真。不要為了結構化把表格拆成 rows / cols 欄位或把範本拆成 schema，那只是多一層無人消費的中介結構，反而讓形狀走樣。
- table 只在「每格短小原子、欄位固定、讀者要橫向比較、Text Mode 不易折行」時才用。任一格出現段落、列表、例外或推理說明，就改用 H3 加列表，不要硬塞表格。

### Good

```
- 狀態碼分工:
  | code | 用途 |
  | --- | --- |
  | 400 | 參數錯 |
  | 409 | 狀態衝突 |
```

結果: markdown 表格字串，每格短、欄位固定，純文字下不跑版。

### Bad

```
- 狀態碼分工:
    columns: [code, 用途]
    rows:
      - [400, 參數錯]
      - [409, 狀態衝突]
```

預期改法:

- 把結構化的 table 還原成一段 markdown 表格字串，配一行「狀態碼分工:」標籤。
