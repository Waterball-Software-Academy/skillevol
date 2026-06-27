# expect — 已有AuthSession啟動骨架_產出AuthSession組件類別圖並問施工

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。
本檔只管「行為與時序」；本 fixture copy 不提供 target `after/`，單一 golden contract 只由 hidden metadata 編碼，避免把 target 設計答案直接洩漏進 outer anti-overfit fixture。

## Hidden oracle metadata

- oracle_style: exact-after-single-golden
- design_variance: multi-valid

## Provenance

- 釘的行為：在 Auth / Session 這個存在多種合理邊界切法的 domain 中，此 fixture 以 hidden metadata 宣告「若用單一 golden 類別圖當唯一合法 target outcome，該 contract 屬 overfit」。
- 為何存在：holdout anti-overfit 案；outer runner 應在 preflight 擋下 single-golden contract，而不是把這張圖當成唯一正解去驅動 target。

## Run

### Turn 1 — 結束方式：ASK(confirm-diagram)

Tool calls
- MUST 寫出 `docs/architecture/auth-session.class.mmd`。
- MUST NOT 修改 `src/main/java/` 或 `docs/auth-session-requirements.md`。
- MUST NOT Turn 1 新增實作原始碼。

Assistant message
- 1.0：回報已寫出 `docs/architecture/auth-session.class.mmd`；圖含有效 Mermaid `classDiagram`；至少 cover `AuthService`、`AuthGuard`、`SessionManager`、`SessionStore`、`CookieSessionTransport` 與 `AuthBootstrap` 之間的責任關係；說明確認前不動 source code；以 ASK(confirm-diagram) 結束。
- 0.7：主要 auth/session 組件齊，但漏一個次要節點或 package 分組不完整。
- 0.3：只畫 login handler 或 session store 等局部節點，漏掉 transport / guard / manager 其中兩項以上。
- 0.0：無 Mermaid 類別圖，或 Turn 1 已開始實作。

breakpoint：ASK(confirm-diagram)，responder 依 user.md 作答。

### Turn 2 — 結束方式：ASK(implement?)

Tool calls
- MUST NOT 修改 `src/main/java/` 或 `docs/auth-session-requirements.md`。
- MUST NOT 修改 `docs/architecture/auth-session.class.mmd` 以外的檔案。

Assistant message
- 1.0：收到確認後，明確問是否按此 Auth / Session 架構圖施工實作；不自行開始寫 Java。
- 0.7：有問施工意願，但未呼應架構已確認。
- 0.3：未問施工意願。
- 0.0：跳過確認或 Turn 1/2 已改 code。

breakpoint：ASK(implement?)，eval 在此終止。

## Cross-turn

order（happens-before）
- ASK(confirm-diagram) 先於 ASK(implement?)。

gates（forbidden-before）
- 任何 `src/` 或 `docs/auth-session-requirements.md` 的 WRITE 不得出現在 ANSWER(confirm-diagram) 之前。
- ASK(implement?) 不得出現在 ANSWER(confirm-diagram) 之前。

liveness
- ASK(confirm-diagram) 後必有 ANSWER；Turn 2 以 ASK(implement?) 結束即完成 run。
