# 互動斷點作答規則

跑被測 skill 遇到 askUserQuestion（或其他向使用者問答的 tool）時，由跑測的 agent 自己依該 unit 的 user.md 作答、不轉交真人。本檔規範該怎麼答。

## Rule 1 — 只在被問到的 topic 才透露對應值

- responder 只在 skill 真的問到某個 topic 時，才給 user.md answer key 上對應那一筆；不主動把整張表倒出去。主動倒等於替 skill 跳過它本該自己問的步驟，eval 就測不出它會不會問。

### Good

情境: skill 只問了 stack 一題，user.md 有 stack／service 名／layout 三筆。

responder 只回 stack 的值 python_e2e，其餘兩筆不給。

結果: skill 有沒有問其他 topic 被如實測到。

### Bad

情境: skill 只問了 stack。

responder 把 user.md 三筆一次全倒給 skill。

預期改法:

- 只回被問到的 stack 一項，其餘等被問到再給。

## Rule 2 — 給選項就挑對應選項，free-text 才給字面值

- skill 用 options 問時，從選項裡挑對應 user.md 那筆 fact 的那一個；skill 開放 free-text 時，才給字面值。option 題塞選項外的字串、或 free-text 題回成選項代號，都會讓答案落在 skill 消費不了的形狀。

### Good

情境: skill 給 options「python_e2e／java_e2e／nextjs」，user.md 的 stack 是 python_e2e。

responder 選 python_e2e 這個選項。

結果: 答案落在 skill 能直接消費的形狀。

### Bad

情境: 同一題。

responder 回一句「就用 python 那個」。

預期改法:

- 直接選 options 裡的 python_e2e，不要用自由文描述。

## Rule 3 — 問到表外的 topic 一律回 fallback，不自編值

- skill 問到 user.md 沒列的 topic 時，回該 unit 的 Fallback（預設「你決定」），不要臨時編一個值。臨時編值會把未授權的決定注入 input，使終態對不回 answer key，也誤導 skill。

### Good

情境: user.md 沒有「要不要加 CI」這筆，skill 卻問了。

responder 回「這個你決定就好」。

結果: 沒有替 skill 注入未授權的決定。

### Bad

情境: 同上。

responder 自己回「加 GitHub Actions」。

預期改法:

- 回該 unit 的 Fallback，不自編 answer key 沒有的值。

## Rule 4 — 標 reveal never 的 fact 永不透露

- user.md 標 reveal: never 的 fact，即使被問到也不給；這是用來測 skill 拿不到該資訊時怎麼處理（該停、該降級、還是會亂編）。一旦給了，這個缺口測試就毀了。

### Good

情境: user.md 的 db 密碼標 reveal: never，skill 問了密碼。

responder 回「這項我不能提供」。

結果: 測到 skill 在缺資訊下的行為。

### Bad

情境: 同上。

responder 還是把密碼給了出去。

預期改法:

- reveal: never 的 fact 不給，看 skill 如何處理拿不到的缺口。

## Rule 5 — 標 trap 的值原樣給，不替 skill 修

- user.md 標 trap 的值（非法、模糊、矛盾）要原樣給，用來測 skill 會不會 re-ask 或 STOP。responder 若先幫它修成合法值，就測不到 skill 自己的把關。

### Good

情境: user.md 的 service 名標 trap「My Service」（含空白與大寫，非法 kebab），skill 問 service 名。

responder 原樣回「My Service」。

結果: 測到 skill 會不會擋下非法值。

### Bad

情境: 同上。

responder 自動回「my-service」，先幫 skill 修好了。

預期改法:

- 原樣給「My Service」，把關責任留給被測 skill。

## Rule 6 — 作答簡短、不引導、不補脈絡

- responder 只給被問到的值，不暗示「正確答案」、不補 skill 沒問到的脈絡、不評論它的提問。多給的引導會讓 skill 顯得比實際更會問，污染對它提問能力的量測。

### Good

情境: skill 問 layout。

responder 回「repo root」。

結果: skill 的提問能力被如實量到。

### Bad

情境: 同上。

responder 回「repo root，順帶一提你還沒問我 stack 喔」。

預期改法:

- 只回 repo root，不提示 skill 漏問了什麼。

## Rule 7 — 未達互動斷點不得 proactive resume

- target subagent 尚未在互動斷點（例如 clarify 的 AskQuestion、或回傳待答問題）提出可被 `user.md` 對應的 topic 時，responder MUST NOT 以 user.md 答案主動 resume Task。
- target 只是停止、或僅宣告「下一步要 delegate /clarify」但未實際 delegate 或發問，視為未達 breakpoint；此時不得先答，應記錄 missing breakpoint 並等待 target 真的發問，或判定 run 無法繼續。

### Good

情境: specify 寫完 `[待澄清]` 草稿後停止，未 delegate `/clarify`、也未 AskQuestion。

responder 不 resume；observation 記錄 target 未達 clarify breakpoint。

結果: 不替 target 跳過它本該自己問的步驟。

### Bad

情境: 同上。

responder 依 user.md 主動 resume「失敗 5 次鎖定 30 分鐘…」。

預期改法:

- 等 target 真的在 clarify 斷點發問後，才 answer-only resume。
