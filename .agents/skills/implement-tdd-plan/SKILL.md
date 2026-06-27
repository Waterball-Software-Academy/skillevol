---
name: implement-tdd-plan
description: 依已確認的 `docs/tdd/*.tdd-plan.md` 把 TDD 計畫落成 source code 與 tests。Use when 使用者已確認 TDD plan 並授權施工、prompt 指向 `docs/tdd/*.tdd-plan.md`、或明確下 `/implement-tdd-plan`。SKIP when 還沒有 TDD plan、plan 尚未確認、使用者未授權實作、或要重新規劃 slices。
---

# Purpose

`tdd-plan-from-class-diagram` 解決的是把設計切成可執行的 TDD slices 並取得確認，但計畫本身不會自動變成 repo 內的 tests 與 production code。
本 skill 在使用者已確認 `docs/tdd/*.tdd-plan.md` 且已授權施工時啟用。
它會把 plan 中的每個 slice 依序落成可觀察的 red-green 變更，直到整份 plan 的 production code 與對應 tests 都 materialize 完成。
若缺少這個 implementation gate，agent 容易重問是否施工、只完成第一個 slice、只寫 production 不補 tests，或回頭修改上游 plan/diagram/requirements。

# SOP

1. read 使用者指定的 `docs/tdd/*.tdd-plan.md`、同主題的 `docs/architecture/*.class.mmd` 與需求文件（若 prompt 或 plan 有引用）、repo 內既有 `src/` / `test/`，以及 package 邊界。
   1. 以 TDD plan 為 primary source；diagram 與 requirements 只用來補行為脈絡，不得覆寫 plan 的 slice 順序。
   2. 若 prompt 已明示 plan 已確認且已授權施工，不得再 ask `implement?`、confirm-plan，或退回 re-planning。
2. think 執行 intake gate。
   1. 判定是否已拿到可解析的 TDD plan 檔，且 plan 內有 Slice Order。
   2. 若 plan 缺失、無法解析、或 slice 缺少 red test / implementation target，停止並要求先回到 `/tdd-plan-from-class-diagram` 補 plan。
   3. 若 prompt 未明示已確認且已授權施工，停止並要求使用者先確認 plan 並授權實作。
   4. 若可開始施工，進入第 3 步。
3. think 判定實作範圍與 forbidden diff。
   1. 從 plan 的 Slice Order 匯總所有 collaborators、implementation target、red test 與 test doubles 策略。
   2. 合法 diff 僅限 plan 導出的 `src/` / `test/` 檔案新增或修改。
   3. `docs/tdd/*.tdd-plan.md`、`docs/architecture/*.class.mmd`、需求文件與無關模組 MUST NOT 修改。
   4. 若 plan 的 Open Questions 尚未在 plan 內定案，選擇對上層最可觀察、且不破壞既有 slice contract 的預設，並在收尾摘要簡短說明；不得為此停止施工或回寫 plan。
4. think 決定 slice 施工順序與每 slice 的 done evidence。
   1. 嚴格依 plan 的 Slice Order 從第一個 slice 做到最後一個；不得跳過 slice，也不得在全部 slice 完成前宣告「先交付部分成果」。
   2. 每個 slice MUST 先落 red test（或補齊 plan 要求的 test doubles / contract test），再讓對應 production code 轉綠。
   3. 若 repo 已有部分 implementation，只在不破壞 plan contract 的前提下做極小修正，並優先補 plan 要求的 tests。
   4. 每完成一個 slice，依 plan 的 Regression Gate 保留先前已綠的 tests。
5. write 依 plan 完整施工：對每個 slice 新增或修改 `src/` 與 `test/` 檔案，直到 plan 中所有 slices 的 red test、implementation target、boundary contract 與 failure path 都有對應 code/tests。
   - MUST 同時 materialize production code 與 tests；不得只做 production 或只做 tests。
   - MUST 依 plan 的 doubles strategy 使用 fake、spy 或 contract test 隔離外部邊界；不得讓 domain/application tests 直接依賴 concrete provider 或 transport 細節。
   - MUST 實作 plan 明示的 failure / regression path（例如 payment failure、404、exception mapping），並以獨立 test 保護。
   - MUST NOT 新增 plan 未導出的模組、設定檔或文件。
6. assistant-message 回報實作結果。
   1. 明確指出已完成整份 TDD plan 的施工，並列出主要新增/修改的 source 與 test 檔。
   2. 簡短說明 orchestration seam、外部 boundary、test doubles 策略與 failure path 如何依 plan 落地。
   3. 不得重問是否施工，不得建議「下一步再補其他 slice」作為本次交付結尾。
