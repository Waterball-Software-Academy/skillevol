# Rule 1 - 不留 transitional 敘事

- Description:
  1. SKILL.md／references／templates／SSOT 的產出只寫 as-is 規則，不得帶過渡或決策脈絡敘事——「之前 X 現在 Y」「interim」「待 X 後替換」「後續 PR 再實作」「Future Expansion」「previously deferred」等。
  2. 豁免：commit message、PR 描述、`CHANGELOG.md`、`specs/`、`.specify/`、`docs/adr/`（這些本就是 tracking artifact）。
- Rationale:
  1. transitional 敘事描述的是「改動當下」的狀態，下一次改動後就過期，卻留在檔裡誤導讀者以為現在還在某個過渡中。

## Good

1. profile.yml 直接寫「`state_specifier` 為 `none` 時跳過 persistence 步」。
2. SKILL.md 直接寫當前唯一流程，沒有「先這樣、之後再改」的時態。

## Bad

1. profile.yml 留「Future Expansion (not active in v1)：未來換 Zod schema」（描述未來才要做的事；as-is 產出不寫未來，要做時再加）
2. SKILL.md 步驟寫「先走舊流程，待 reconcile skill 上線後改走新流程」（過渡敘事；上線後沒人回來改，敘事永久誤導）
3. reference 寫「此分類沿用舊版，下一版會重整」（決策脈絡；讀者要的是現在的分類，不是它的歷史）

# Rule 2 - 註解不帶外部追蹤代號

- Description:
  1. 任何被 build／import／進 bundle 的 source 或 skill artifact，其註解不得帶 tracker／PR／commit／cross-repo 路徑／外部 spec section 代號（`SPE-557`、`PR #295`、commit hash、跨 repo 路徑、`§2.1`）。
  2. 豁免同 Rule 1（commit message、PR 描述、`CHANGELOG.md`、`specs/`、`.specify/`、`docs/adr/`）。
  3. 完整 why 與反例見 `/principle-comment-as-is`。
- Rationale:
  1. 源碼壽命遠長於票號／PR／外部路徑的可解析期——三年後 tracker 搬家、PR 被 squash、外部 repo 重組，這些代號全變成無法解碼的死字串，反而妨礙理解。

## Good

1. 註解寫現在式因果：「以最長前綴比對，避免短前綴吃掉特例」。
2. 註解直接重述規則本身，不指向外部出處。

## Bad

1. `# 見 SPE-557`（tracker 代號；tracker 搬家後解不開，應直接重述規則）
2. `# 對應 specformula-agent/graph_driver 的某 .py`（cross-repo 路徑；外部 repo 重組後無法解碼，應重述邏輯）
3. `# 詳見 spec §2.1`（外部 spec section ref；section 編號會變動，應直接重述該段邏輯）
