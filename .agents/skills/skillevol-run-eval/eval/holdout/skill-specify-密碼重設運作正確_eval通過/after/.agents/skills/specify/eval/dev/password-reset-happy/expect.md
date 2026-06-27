# expect — password-reset-happy

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 after/ imply。互動型 unit，作答來源見同層 user.md。

## Provenance

- 釘的行為：dev 未出現過的 Raw Idea 加一處需澄清時，specify 應推對 package-name、寫出結構正確的 spec.md 草稿（帶 [待澄清]）、跑一輪 clarify、把答案回寫成 Q&A 並消掉 [待澄清]。
- 為何存在：holdout happy 互動路徑；避免 runner 或 target oracle 對 login 題材過度擬合。

## Run

### Turn 1 — 結束方式：ASK（透過 clarify 問重設連結有效性）

Tool calls
- MUST delegate to SKILL /clarify（specify step 7：把 [待澄清] 交 clarify，每次一題）

Assistant message
- 1.0：回報 spec package 結構（specs/密碼重設/spec.md）並說明已寫出帶 [待澄清] 的草稿
- 0.3：有寫但沒講清楚 package 結構或漏報草稿狀態
- 0.0：沒寫草稿就直接問，或 package-name 明顯錯

breakpoint：clarify 問「重設密碼連結要如何限制有效性」，responder 依 user.md 作答

### Turn 2 — 結束方式：done

Tool calls
- 無新的 delegate（specify step 8 回寫、step 9 再掃無 [待澄清] 後結束）

Assistant message
- 1.0：回報已把澄清答案回寫、spec 完成、無殘留 [待澄清]
- 0.0：宣稱完成但實際還留著 [待澄清]，或沒回寫 Q&A

## Cross-turn

order
- package-name 先於 spec.md 寫出；clarify 的 ASK 先於把 answer 回寫進 spec。

liveness
- clarify 的問題有被 responder 回答；回答後不再重複問同一題；run 會終止。
