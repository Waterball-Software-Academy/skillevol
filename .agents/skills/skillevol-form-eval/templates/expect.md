# expect — <before情境設定概述>_<after測試結果概述>

繼承 `<相對路徑>/shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply，不在這寫 file diff。
- 若這個 unit 預期產出 file artifact，可在 Provenance 與 Tool calls 點名 artifact path；artifact 的完整內容仍只落在 after/。

## Hidden oracle metadata

<選用：只允許寫在 unit-local `expect.md`，不得放進 `shared/expect.md`。正式 schema 以 `rules/hidden-oracle-metadata.md` 為準；若不用，整節刪掉。>

- oracle_style: <exact-after-single-golden | runner-only>
- design_variance: <unique | multi-valid>

## Provenance

- 釘的行為：<這個 unit 釘住哪一個 distinct 行為主張，對應哪條 rule 或哪個 defect；若有 file artifact，可點名 artifact path/family>
- 為何存在：<為什麼這條值得獨立成一個 golden；它防的是什麼失敗>

## Run

過程是 1..N 個 one-turn 的有序序列。每個 turn 只驗它的 tool calls 與 assistant message；turn 不含 file diff。

### Turn 1 — 結束方式：<ASK(<topics>) 交還 user ｜ done ｜ STOP(<reason>)>

Tool calls
- MUST <該 turn 必須出現的關鍵工具與條件，例如 MUST 呼叫 askUserQuestion>
- MUST <若此 unit 預期產出 file artifact，可在這裡點名 artifact path，例如 MUST 寫出 docs/architecture/foo.class.mmd>
- MUST NOT <該 turn 不准出現的關鍵工具，例如 MUST NOT 在拿到答案前 RUN 產生骨架的 script>

Assistant message
- 1.0：<這個 turn 最佳對話，帶具體用語>
- 0.7：<對但有瑕疵>
- 0.3：<勉強>
- 0.0：<明確的壞>

breakpoint：<ASK(...) 交還 user；responder 依 user.md 作答。末 turn 為 done/STOP，無 breakpoint>

### Turn 2 — <…>

<同上；非互動 skill 只有一個 Turn、結束 done，刪掉多餘 turn 與下方 Cross-turn>

## Cross-turn

<僅多輪需要；單輪刪掉整節。order/gates/liveness 引用 event trace 的事件，不引用 file diff>

order（happens-before）
- <ASK(scope) 先於 ASK(detail)；只列承載正確性的邊>

gates（forbidden-before）
- <no WRITE 任何 在 CONFIRM 之前>
- <no ASK(layout) 在 ANSWER(stack) 之前>

liveness
- 每個 ASK 後必有 ANSWER，無懸空；已答過的不再問；run 會終止。
