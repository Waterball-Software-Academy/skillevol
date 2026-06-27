# expect — 已有miniweb-framework-tdd-plan_完成framework核心實作及測試

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；檔案終態由同層 `after/` imply，不在這寫 file diff。

## Provenance

- 釘的行為：當 `before/` 已含 `docs/tdd/miniweb-framework.tdd-plan.md` 且 prompt 已授權施工時，`implement-tdd-plan` 必須把 Router、MiddlewareChain、RequestDispatcher、JdkHttpHandlerAdapter 與對應 tests 一次落成完整 framework 終態，而不是只做某個局部 slice。
- 為何存在：這是 framework 型 holdout；防止 skill 只會做業務 happy path CRUD，卻無法從 framework TDD plan 正確展開 pure logic、orchestration、boundary contract 與 failure mapping。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST 依 `docs/tdd/miniweb-framework.tdd-plan.md` 寫出完整 framework 實作與 tests。
- MUST NOT 修改 `docs/tdd/miniweb-framework.tdd-plan.md`、`docs/architecture/miniweb-framework.class.mmd` 或 `docs/miniweb-requirements.md`。
- MUST NOT 再 ask `implement?`、confirm-plan，或退回「先整理更細的 framework implementation plan」。

Assistant message
- 1.0：明確回報已完成 MiniWeb framework 的核心實作，點出 routing、middleware、dispatcher、adapter 與 tests 已落地，並說明 404 / exception mapping 也已被測試保護。
- 0.7：主要實作與 tests 已完成，但摘要漏掉 boundary contract 或 failure mapping 其中一項。
- 0.3：只回報完成部分 framework slices，或只提 code 沒提 tests。
- 0.0：退回 re-planning、只做局部實作，或重問是否開始施工。

breakpoint：done
