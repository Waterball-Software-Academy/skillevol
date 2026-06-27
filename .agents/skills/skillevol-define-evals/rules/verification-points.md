# 驗證點排序規則

排出要跟使用者逐一對齊的驗證點清單時，依本檔。一個驗證點是一個「先確認、才往下」的假設。

## Rule 1 — 依 working-backward 排序，最根本最可能整套錯的先驗

- 驗證點要依「錯了會讓後面全白做」的程度排序：最根本、最可能整套理解錯的放最前面先驗。一旦根本的形狀對了，後面的細節才有意義；反過來先做細節，根本一錯就全部重來。

### Good

情境: 為一個 meta skill（跑別的 skill）定義 eval。

第一個驗證點先對齊「一次 inputs 到 outputs、before/ 該裝什麼、output channel 是對話還是 file artifact」的嵌套形狀，之後才排「第一個 happy unit 的設計」。

結果: 形狀一旦確認，後面每個 unit 都站得住；形狀若錯也只浪費一次對話。

### Bad

情境: 同上。

第一個驗證點就直接給「happy unit 的完整 expect 與 after」，把嵌套形狀的假設藏在裡面沒先單獨對齊。

預期改法:

- 把最根本的形狀假設抽成第一個驗證點先驗，細節 unit 排在它之後。

## Rule 2 — 用典型骨幹起手，但它是骨幹不是教條

- 多數 skill 的點序可以套同一個骨幹，依序是：形狀、artifact output contract、可跑骨架（target 載入、互動誰答、judge 是誰）、第一個最小 happy unit 的設計、寫出 happy unit、負向 unit、holdout、shared、completion matrix。先用這個骨幹起手，省去從零想點序。
- 但它只是預設骨幹：對齊過程中使用者在某點說「不」，就可能要新增、拆分或重排後續的點，依實況調整。

### Good

情境: 一個非互動的純轉換 skill。

沿用骨幹，但「可跑骨架」裡跳過「互動誰答」（無互動斷點），其餘點序照走。

結果: 骨幹給了起點，又依這個 skill 沒有互動而合理裁掉一項。

### Bad

情境: 同上。

不管這個 skill 有沒有互動，硬把骨幹七點一字不差全列，包括用不到的 user.md 與 responder。

預期改法:

- 以骨幹為起點，依目標 skill 的實況增刪重排，不硬套不適用的點。

## Rule 3 — 一個驗證點只對齊一個假設，且具體到可一句確認

- 一個驗證點只承載一個 distinct 的假設，並且要攤成具體、可被一句話 confirm 或 reject 的東西（一張圖、一份草案、一個 yes/no 選擇），不是抽象描述「我會做 X」。
- 一個點塞多個假設，使用者任一不同意都得整點打回，又分不清是哪個假設錯。

### Good

情境: 要對齊產出落點。

驗證點寫成一個明確選擇：「報告寫成 markdown 檔用 after/ 驗，還是只在對話訊息用 rubric 驗？」

結果: 使用者一句就能選，選了就往下。

### Bad

情境: 同上。

一個驗證點同時問「產出落點、judge 用誰、第一個 target 是什麼、報告欄位有哪些」四件事。

預期改法:

- 拆成各自獨立、可單獨確認的驗證點，一點一個假設。

## Rule 4 — 在可跑骨架前，先單獨對齊 artifact output contract

- 只要目標 skill 預期產出 file artifact，就必須在「可跑骨架」之前，先單獨確認 output channel、expected artifact path、allowed diff、forbidden diff，以及哪些資訊是 target-visible input、哪些只能留在 hidden oracle。這一點若沒先對齊，後面的 before/after、prompt、expect 很容易一起歪掉。

### Good

情境: 一個會先產架構圖檔再問是否施工的 skill。

先開一個驗證點問清楚：「這條 eval 要驗 repo 內 `docs/architecture/*.class.mmd` 檔，還是只驗 assistant message？若驗檔案，唯一合法 diff 是新增圖檔，`src/` 不得變。」

結果: 後面寫 happy unit 時，after/ 是否 mandatory、該有哪些 artifact path、expect 該斷言哪個 MUST WRITE，都已站在已確認的契約上。

### Bad

情境: 同上。

先確認 happy path 流程與 assistant message 長相，等寫到 after/ 才臨時決定「好像要補一個 .mmd」。

預期改法:

- 把 artifact output contract 抽成獨立 verification point，排在可跑骨架之前。

## Rule 5 — 多 subcommand 目標要先單獨對齊 unit naming contract

- 若目標 skill 有多個可選 subcommand，且不同 subcommand 會導出不同操作路徑、artifact family 或驗證局面，必須在第一個 happy unit 設計前，先單獨確認 unit naming contract。
- 命名形式不是抽象地重複 `<before情境設定概述>_<after測試結果概述>` 就好，而是要把 chosen subcommand 放在 before-segment 最前面，採 `<subcommand動作>-<情境設定概述>_<預期結果概述>`。
- 這個 naming contract 若沒先對齊，後面即使 happy unit 的行為與 after/ 都看起來合理，unit dirname 仍可能失去辨識力，讓 benchmark report、run workspace 與失敗摘要無法一眼分出是在驗哪個 subcommand。

### Good

情境: 目標 skill 同時支援 `setup` 與 `reset`。

在第一個 happy unit 設計前，先單獨問清楚：「這條 unit 是不是要命名成 `setup-未有workspace_建立gitignore並初始化workspace`，用 `setup-` 把它和 `reset-...` 分開？」

結果: 一旦 naming contract 被確認，後面寫 prompt / expect / after / report 時，unit identity 都站在已對齊的名字上。

### Bad

情境: 同上。

直接把第一個 happy unit 寫成 `未有workspace_建立gitignore並初始化workspace`，把它其實在驗 `setup` 這件事藏在內文裡，等 benchmark report 才發現名稱辨識力不夠。

預期改法:

- 在 happy unit 設計前先抽出 naming contract 這個獨立 verification point，確認 chosen subcommand 與前綴格式，再往下寫 unit。
