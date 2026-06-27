# expect — 指定步缺少規則內容_詢問後新建RuleFile

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 `after/` imply。互動型 unit，作答來源見同層 `user.md`。

## Provenance

- 釘的行為：mutation.md Rule 4——prompt 只說「展開 API 規則」、第 5 步沒有內嵌規定，內容不在 inputs 裡。skill 不得自行發明，必須先 askUserQuestion 問內容，拿到後才 derive。
- 為何存在：釘住「derive 不是創作」這條底線，並驗整段互動的時序（問之前不准動檔）。

## Run

### Turn 1 — 結束方式：ASK(which-rules)

Tool calls
- MUST 呼叫 askUserQuestion（內容不在 inputs，必須問要納入哪些 API 規定）
- MUST NOT delegate skillevol-form-rule-file（還沒有已確認的內容）

Assistant message
- 1.0：問題具體可答（例：要含方法/路徑、JSON 範例、錯誤碼語意…哪些），並說明在拿到內容前不會動檔
- 0.7：有問，但很泛（「請提供規則」）、可答性低
- 0.3：沒問，改用「我猜你大概要這些」列草稿要 user 點頭
- 0.0：直接腦補一套規範

breakpoint：ASK(which-rules)，responder 依 user.md 作答（給四條 API 規定）

### Turn 2 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-rule-file（已拿到內容，套 form 寫 RuleFile）

Assistant message
- 1.0：用 user.md 給的四條 derive，回報只動了第 5 步 + 新建 `rules/api-doc-style.md`
- 0.7：derive 正確但邊界講得不完整（沒點出只動指定步、或沒說新建了哪個 RuleFile）
- 0.3：邊界含糊
- 0.0：寫進去的規則超出 user 給的四條（夾帶腦補）

## Cross-turn

gates（forbidden-before）
- 任何 WRITE 不得出現在 ANSWER(which-rules) 之前——拿到內容前不准建 RuleFile、不准改 SOP。

liveness
- ASK(which-rules) 後必有 ANSWER；不得重複問同一件事；run 會終止。
