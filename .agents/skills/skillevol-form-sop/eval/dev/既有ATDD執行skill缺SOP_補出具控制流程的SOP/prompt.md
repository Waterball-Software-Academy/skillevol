請補完目前專案中已存在的 Cursor/Claude skill：`.agents/skills/atdd-execution/SKILL.md`。

這個檔案已經有 frontmatter、Purpose 與 `# SOP` 標題，但 SOP 目前只有 `<TODO>`。請只補完 `# SOP` 區塊；不要重寫 frontmatter 或 Purpose，也不要為了看起來完整而新增不必要的 `rules/` 或 `templates/`。

這個 skill 的用途是執行 Acceptance Test-Driven Development（ATDD，驗收測試驅動開發）。它不是單純幫使用者「寫測試、寫程式、跑測試」的 checklist，而是一個複雜的 loop engineering skill：它要用驗收測試當控制器，持續驅動 feature 從需求進入可驗收、可實作、可回歸驗證、可交付的狀態。

請把 ATDD 的外圈與內圈都寫清楚。

外圈是整個 feature 的交付控制流程：

1. 先讀 feature request、acceptance criteria、既有 spec、既有測試與目前實作狀態。
2. 先判斷 acceptance criteria 是否可測。若 AC 只是抽象願望、沒有使用者可觀察行為、沒有例子、沒有明確輸入輸出、沒有驗收邊界，就不能直接實作；必須先停下來澄清或補成可測 AC。
3. 把每個可測 AC 轉成 acceptance test plan，並標出哪些能自動化、哪些只能用人工或 LLM judge 驗收。
4. 先建立或更新 acceptance tests，再確認這些 tests 是對 user outcome，不是對 implementation detail。
5. 執行 red gate：新增或更新的 acceptance test 必須先 fail。若它一開始就 pass，要判斷是功能已存在、測試太弱、測錯層級，或 fixture 錯；不能跳過 red gate 直接實作。
6. 對每個 AC 進入內圈，逐一用最小實作讓測試通過。
7. 所有 AC 的 acceptance tests 通過後，跑 regression、unit、lint、typecheck 或其他專案現有驗證。
8. regression fail 時要回到 impact analysis，判斷是新功能破壞既有行為、測試需要更新，還是實作方向錯。
9. 綠燈後才允許 refactor；refactor 後必須重跑 acceptance tests 與 regression。
10. 只有所有 AC 都有 test evidence、所有 relevant tests pass、沒有未解釋 skip、沒有 unresolved question，才可以收尾回報。

內圈是每個 acceptance criterion 的 red-green-refactor 小循環：

1. 選定下一個 AC。
2. 寫或更新能代表該 AC 的最小 acceptance test。
3. 跑該 test，確認 red。
4. 若沒有 red，回 test design，不進 implementation。
5. 寫足以推進該 AC 的最小實作，不順手做無關 refactor。
6. 跑該 acceptance test。
7. 若 fail，讀 failure provenance，判斷回到 test design、fixture 修正、需求澄清，或 implementation 修正。
8. 若 pass，更新 evidence，才進下一個 AC。
9. 若所有 AC pass，才出內圈進外圈 regression gate。

請特別注意 SOP 的形狀。這個 skill 的 SOP 必須看得出 control-flow，不可以只是線性列出「寫 acceptance tests、寫實作、跑測試、修問題、回報」。你應該明確寫出 gate、branch、loop back edge、exit condition，例如「若 AC 不可測，停止並要求澄清」、「若 acceptance test 沒有 red，回到 test design」、「若 implementation 後仍 fail，回到 failure analysis」、「若 regression fail，回到 impact analysis」、「若 refactor 後測試 fail，回到對應 loop」、「全部 pass 才收尾」。

請把完成後的 SOP 寫回 `.agents/skills/atdd-execution/SKILL.md`。重點是 `SKILL.md` 的 SOP 必須能忠實表達這是一個 ATDD loop orchestrator。
