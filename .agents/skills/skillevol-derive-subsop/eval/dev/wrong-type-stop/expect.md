# expect — wrong-type-stop

繼承 `../../shared/expect.md`。本檔只補本 scenario 特有期望；衝突時以本檔為準。

## Provenance

- 釘的行為：mutation.md Rule 6 — 若內容不是 subsop-type（有序子程序），就停止 derive，並指向正確的 mutator。
- 為何存在：型別誤判是 derive-subsop 最該防的 false-positive。把無序原子規定硬抽成 Sub-SOP，會把規章變成假程序流；這條負向 gate 守住「該停就停」。

## Inputs（提醒，真值在 before/ 與 prompt.md）

- before/：demo-namer，第 2 步內嵌的三條是彼此無序、可逐條獨立驗收的命名規定（不是有前後依賴的子程序）。
- prompt：要求把第 2 步抽成 Sub-SOP。

## Tool calls

決定性：
- MUST NOT delegate skillevol-form-subsop（內容不是 subsop-type，不該進 form）。
- MUST NOT 呼叫 askUserQuestion（型別判斷不需問 user）。

## Assistant message

語意 rubric（疊加 shared）：
- 1.0：明確指出第 2 步內容是無序原子規定、不是有序子程序，所以停止 derive-subsop，並建議改走 skillevol-derive-rules。
- 0.7：判對是無序、停了，但沒指名正確的 mutator。
- 0.3：勉強停下但理由模糊。
- 0.0：沒辨識出型別，照樣抽成 Sub-SOP。

## File diff

決定性 invariant：
- diff 必須為空：不得新建任何 Sub-SOP 目錄或 SOP.md，也不得改動 demo-namer/SKILL.md 任何 byte。

語意 rubric：
- 本 scenario 不靠語意 rubric 放行；正確與否由「停止 ＋ diff 為空 ＋ 對話指向 derive-rules」決定。
