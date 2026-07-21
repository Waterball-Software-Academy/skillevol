# Rule 1 - skill 改動與 contract SSOT 一致

- Description:
  1. `aibdd-core/references/ssot/` 下每個檔是一個面向的 contract SSOT；任何 skill 改動的產出必須與 ssot/ 下每一條一致。
  2. 一旦改動與某條 SSOT 不一致，不得放著共存——必須二擇一決議：改 skill 回到符合 SSOT，或改 SSOT 並連帶更新所有依賴該條的 skill。
- Rationale:
  1. SSOT 之所以成立靠「沒有第二份真相」這個 invariant；skill 與 SSOT 不一致就是兩份真相，下游無法判斷該信哪個。
  2. 不一致只有兩個根因——skill 寫錯，或 SSOT 過期；二擇一的決議逼出哪個才是真相，容忍漂移會讓 SSOT 緩慢腐化成謊言。

## Good

1. 某 skill 改了 specs 目錄結構的假設，PR 同時更新 `ssot/spec-package-paths.md`，兩者一致。
2. reviewer 發現某 skill 寫死的路徑與 `ssot/arguments.yml` 的 key 值不符，PR 明確決議「改 skill 對齊 SSOT」並落實。

## Bad

1. skill 改動引入一個與 `ssot/spec-package-paths.md` 矛盾的目錄路徑，PR 既沒動 SSOT 也沒對齊 skill（兩份真相並存；必須二擇一決議）
2. PR 改了 `ssot/arguments.yml` 某個 path key，卻仍有別的 skill 照舊用舊路徑（改了 SSOT 沒連帶更新依賴它的 skill；SSOT 與部分 skill 漂移）
3. skill 直接假設「規格一定是 DBML」，與 `ssot/boundary-specific-packaging.md`「DBML 是某 boundary 的配置事實、非普世真相」矛盾，PR 兩邊都沒對齊（與 SSOT 不一致）
