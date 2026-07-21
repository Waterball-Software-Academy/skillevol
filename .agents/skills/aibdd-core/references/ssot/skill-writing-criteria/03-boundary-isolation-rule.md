# Rule 1 - boundary 開放封閉原則（端點相關之特定知識只准住在 boundary folder 內）

- Description:
  1. 任何「某技術端點才成立」的假設（spec = API + Data、state specifier 用 DBML、operation specifier 用 OpenAPI，以及任何 backend／frontend／mobile 專屬的 specifier 形狀）只能出現在 `aibdd-core/assets/boundaries/<boundary>/` 該 boundary 的 implementation folder 內。
  2. boundary folder 以外的任何 skill 原始碼（planner／form／execute／principle／共用 harness scripts）必須對端點保持不可知：它讀 boundary `profile.yml` 的綁定（`state_specifier`／`operation_contract_specifier`／`component_contract_specifier`）來決定用哪個 specifier，絕不寫死某端點的 specifier 種類。
  3. 支援一個新端點（frontend／mobile／firmware／用戶自定義）的合法產出形狀，是新增一個遵照 `_template` 的 boundary implementation folder；不得靠修改 boundary folder 以外的 skill 來達成。
  4. boundary 內的 Python harness（如 `part_to_dsl.py`）不得把只對某端點成立的假設外洩進共用 code path；端點專屬的 fan-out 要留在該 boundary 自己的 harness。
- Rationale:
  1. 這套 family 的擴充模型是 OCP：換任何技術端點都只「加一個 boundary folder」，folder 外的 skill 一律端點不可知；一旦把端點假設漏進共用 skill，等於把系統對其他端點封死。
  2. backend 是最常見的心智預設（spec 就是 API + DBML），所以寫死「state specifier 是 DBML」讀起來像對的——但它只對 backend 成立，frontend／mobile 的 specifier 根本不是這些。reviewer 要識破這種「現在剛好為真」的假設，它對非 backend 端點是無聲的破壞。
  3. boundary 帶 Python harness，最容易在共用 code 裡夾帶端點分支；folder 外每一處都要查是否把某端點專屬決策寫了進去。

## Good

1. form／planner 產出寫「依 active boundary 的 `profile.yml` 取 `state_specifier`，DELEGATE 對應 specifier skill」，本身不假設它是 DBML 還是別的。
2. 新增 frontend 端點支援的 PR 只動 `aibdd-core/assets/boundaries/web-frontend/`（新增 folder、照 `_template` 實作），folder 外的 skill 一行未改。
3. `web-service` boundary 的 `profile.yml` 綁 `state_specifier: { skill: /aibdd-form-entity-spec, format: dbml }`，而 `web-frontend` 綁 `state_specifier: { skill: none, format: none, semantics: ephemeral-mock }`；「用 DBML」只是 backend 這個 boundary 的 binding 事實，不是普世真相。

## Bad

1. 某 planner SOP 寫「data spec 一律用 DBML、operation 一律用 OpenAPI」（把 backend 專屬的 specifier 形狀寫死進 folder 外的共用 skill；frontend／mobile 被無聲封死——OCP 破壞）
2. PR 宣稱「加上 frontend 支援」，卻去改 `aibdd-form-entity-spec` 共用 skill 加 `IF boundary == frontend` 分支（用修改 folder 外 skill 來支援新端點；合法形狀是新增一個 frontend boundary folder）
3. 某共用 form／execute skill 的 SOP 寫死一步：
```
READ ${ENTITY_DSL}（.dbml），FOR EACH table DERIVE 一個 persistence 驗證 step
```
（直接假設這個 boundary 一定有 DBML data contract；但 `web-frontend` 的 `state_specifier` 是 `none`／`ephemeral-mock`、根本沒有 `.dbml`，於是這步在 frontend 端點抓不到檔、`FOR EACH` 跑出空集合。正確形狀是讀 active boundary `profile.yml` 的 `state_specifier`，由它決定「有沒有 data contract、用哪一種」，而不是把 `.dbml` 與 table 迴圈寫死在 folder 外的共用 SOP——OCP 破壞）
4. principle 檔舉例宣稱「Contract 分析就是 API + Data」（把 backend 的 contract 形狀當普世真相，寫進跨端點的 principle）
