# expect — 指定步是有序子程序_STOP且不改檔

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 `after/` imply。

## Provenance

- 釘的行為：mutation.md Rule 5——指定步內容其實是「先掃描、再分類、最後產出」的有序子程序，不是無序原子規定，skill 必須停止 derive 並改導向 Sub-SOP，不得硬抽成 RuleFile。
- 為何存在：derive-rules 最關鍵的負向把關。抽錯型別會把程序流變成無序規章，是最該防的 false-positive。

## Run

### Turn 1 — 結束方式：STOP

Tool calls
- MUST NOT delegate skillevol-form-rule-file（根本不該進到寫 RuleFile）
- MUST NOT 呼叫 askUserQuestion（型別判斷不需問 user，內容已足以判定是有序程序）

Assistant message
- 1.0：明確指出第 5 步是有序子程序（點出「先／再／最後」的順序語意），停止 derive，建議改走 Sub-SOP（或 TemplateFile）；不抽、不改檔
- 0.7：判斷對、有停，但沒說明為何不是 rule-type，或沒指出正確去向
- 0.3：含糊說「這個不太適合」，語氣像在徵詢、沒明確停
- 0.0：仍把三條當無序規定、宣稱可抽，或直接動手抽了
