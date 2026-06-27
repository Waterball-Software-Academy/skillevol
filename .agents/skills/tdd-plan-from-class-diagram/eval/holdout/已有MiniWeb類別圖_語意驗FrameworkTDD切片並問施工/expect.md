# expect — 已有MiniWeb類別圖_語意驗FrameworkTDD切片並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本 unit 不提供 `after/`：file outcome 以 semantic rubric 判定，不用單一 golden TDD plan 釘死唯一切法。唯一合法新增檔案是 `docs/tdd/miniweb-framework.tdd-plan.md`。

## Provenance

- 釘的行為：在 MiniWeb framework 類別圖與 FR/NFR 輸入上規劃 TDD 計畫時，skill 應從責任分工推導切片順序，而不是背出某一張固定 golden 計畫。
- 為何存在：這是 semantic holdout；驗 `tdd-plan-from-class-diagram` 是否能在不靠 exact-after 的情況下，仍排出 cover routing、boundary adapter、request/response abstraction、middleware chain、dispatch 與 handler boundary 的 framework TDD slices。

## Run

### Turn 1 — 結束方式：ASK(confirm-tdd-plan)

Tool calls
- MUST 寫出 `docs/tdd/miniweb-framework.tdd-plan.md`。
- MUST NOT 修改 `docs/architecture/miniweb-framework.class.mmd` 或 `docs/miniweb-requirements.md`。
- MUST NOT 在 Turn 1 新增 framework source 或 test 檔。

Assistant message
- 1.0：回報已寫出 `docs/tdd/miniweb-framework.tdd-plan.md`；計畫明確表達下列責任切片：route matching / registry、request/response abstraction、middleware chain、dispatch coordinator、handler boundary、JDK transport adapter、404/exception mapping；並說明確認前不動 source/test，以 ASK(confirm-tdd-plan) 結束。命名可不同，只要責任對齊即可。
- 0.7：主要 framework 責任切片齊全，但漏一個次要責任（如 response abstraction 或 dispatch coordinator），或切片順序略大。
- 0.3：有 TDD 計畫，但只排 Router + Handler 兩三個大塊，或把 adapter / middleware / dispatch 其中兩項以上漏掉。
- 0.0：無 TDD 計畫檔、Turn 1 已開始寫測試/實作，或把輸入收斂成單一 traced golden（例如只會背某張既有 checklist，卻無法說明為何那些 slices 由類別圖與 FR/NFR 推出）。

breakpoint：ASK(confirm-tdd-plan)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `docs/architecture/miniweb-framework.class.mmd` 或 `docs/miniweb-requirements.md`（eval 不驗實作階段）。
- MUST NOT 修改 `docs/tdd/miniweb-framework.tdd-plan.md` 以外的檔案。

Assistant message
- 1.0：收到確認後，明確問是否按此 framework TDD 計畫施工；不自行開始寫 source 或 tests。
- 0.7：有問施工意願，但未呼應使用者已確認 TDD 計畫。
- 0.3：未問施工意願。
- 0.0：跳過確認或 Turn 1/2 已改 code。

breakpoint：ASK(implement?)，eval 在此終止。

## Cross-turn

order（happens-before）
- ASK(confirm-tdd-plan) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/`、`test/`、`docs/architecture/miniweb-framework.class.mmd` 或 `docs/miniweb-requirements.md` 的 WRITE 不得出現在 ANSWER(confirm-tdd-plan) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-tdd-plan) 之前。

liveness
- ASK(confirm-tdd-plan) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。
