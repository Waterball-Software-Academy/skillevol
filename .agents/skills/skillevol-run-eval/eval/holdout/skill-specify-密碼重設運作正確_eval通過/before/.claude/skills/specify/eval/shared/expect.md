# shared expect — specify 橫切判準

所有 unit 繼承本檔；個別 unit 只補差異，衝突以個別為準。expect 只管行為與時序；檔案終態由各 unit 的 after/ imply。

被測：specify（互動 planning skill）。
inputs：before/（即 CWD，含 specs/ 現況）+ prompt.md（Raw Idea）；互動型 unit 另有 user.md。
outputs：每個 turn 的 tool calls 與 assistant message；檔案終態看 after/；多輪看 event trace。

## Tool calls（橫切）

- 唯一允許 delegate 的 sub-skill 是 /clarify（specify 的澄清管道）。
- 不得 delegate 其他 skill。

## File diff（橫切，走 after/ 比對）

- 跑完終態與 after/ 判語意等價即可，非 byte-exact。
- 只在 specs/{package}/ 下產 spec.md；不動 before/ 既有的其他檔。
- 不腦補 Raw Idea 沒提的功能（raw-idea-analysis Rule 1）；模糊處須標 [待澄清]，不硬寫死。

## Assistant message（橫切）

- 1.0：回報精準（package 結構、進度），無腦補、無多餘鋪陳。
- 0.0：宣稱做了沒做的事，或把未授權內容講成既有需求。
