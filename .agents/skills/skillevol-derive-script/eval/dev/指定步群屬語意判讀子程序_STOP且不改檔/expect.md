# expect — 指定步群屬語意判讀子程序_STOP且不改檔

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。

## Provenance

- 釘的行為：mutation.md Rule 3 / 6——若指定步驟群本質上是語意判讀與決策，而不是 mechanical 自動化，就必須停止 derive。
- 為何存在：型別誤判是 derive-script 最危險的 false-positive；一旦把 agent judgment 錯塞進 script，整個 skill 邊界就歪掉。

## Run

### Turn 1 — 結束方式：STOP(not-script-type)

Tool calls
- MUST NOT delegate skillevol-form-script（內容不是 script-type，不該進 form）
- MUST NOT 呼叫 askUserQuestion（型別判斷不需問 user）

Assistant message
- 1.0：明確指出第 2 到第 4 步核心是語意判讀與決策，不屬於 script-type，所以停止 derive-script，並建議保留 inline 或改走 `skillevol-derive-subsop`
- 0.7：停下來了，但沒指向正確去向
- 0.3：勉強停下但理由模糊
- 0.0：沒辨識出型別，照樣抽成 script
