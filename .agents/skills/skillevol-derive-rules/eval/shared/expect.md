# shared expect — skillevol-derive-rules 橫切判準

所有 unit 繼承本檔；個別 unit 只補差異，衝突以個別為準。expect 只管行為與時序；檔案終態由各 unit 的 after/ imply。

被測：skillevol-derive-rules（mutation / planning skill）。
inputs：before/（即 CWD）+ prompt.md；互動型 unit 另有 user.md。
outputs：每個 turn 的 Tool calls 與 Assistant message；檔案終態看 after/；多輪看 event trace。

## Tool calls（橫切）

- 唯一允許 delegate 的 sub-skill 是 skillevol-form-rule-file（RuleFile 的 form 交給它）。
- 唯一允許向 user 提問的 tool 是 askUserQuestion。
- 哪些 unit MUST / MUST NOT，由各 unit 的 expect 指定。

## Assistant message（橫切）

- 1.0：精準回報這次 mutation 的邊界——動了哪一步、開了哪個 RuleFile、移了哪些規定；無腦補、無多餘鋪陳。
- 0.7：講對但夾帶與本次 mutation 無關的建議。
- 0.3：邊界含糊，看不出實際只動了哪裡。
- 0.0：宣稱做了沒做的事，或把未授權的新規則講得像既有需求。

## after/ 比對（橫切）

- 跑完的終態 fs 與該 unit 的 after/ 判語意等價即可，非 byte-exact。
- 凡 after/ 內含一個 RuleFile，它必須合 skillevol-form-rule-file 的 form：恰一個 H1、每條是 `## Rule N — …`、descriptions 至少一句、`### Good` 與 `### Bad` 各一、Bad 附「預期改法:」、全檔零粗體/emoji/箭頭。
- mutation surface 只動「指定步所在的 SKILL.md + 該步對應的單一 RuleFile」；after/ 內其他步、其他檔與 before/ 相同（不 broadcast）。
- 不臆測新規則：after/ 內每條 rule 都對得回 inputs（指定步內嵌 bullet、prompt、或 user.md 提供的內容）。
