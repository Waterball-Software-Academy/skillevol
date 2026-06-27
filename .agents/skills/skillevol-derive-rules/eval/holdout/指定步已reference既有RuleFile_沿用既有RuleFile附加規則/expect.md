# expect — 指定步已reference既有RuleFile_沿用既有RuleFile附加規則

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 `after/` imply。holdout：agent 迭代時看不到，僅最終 gate 跑，用來偵測它有沒有過擬合成「一律建新檔」。

## Provenance

- 釘的行為：mutation.md Rule 2（後半）——指定步已 reference 既有 RuleFile 時，必須開那個既有檔附加，不得另起新檔。
- 為何存在：happy-path 教「建新檔」；若 agent 背成「一律建新檔」，這條會抓到——正解是 append 到既有 `rules/api-doc-style.md`。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-rule-file（新增的 Rule 仍要合 form）
- MUST NOT 呼叫 askUserQuestion（分頁規定已內嵌第 5 步、齊全）

Assistant message
- 1.0：明說「沿用既有 `rules/api-doc-style.md`、附加一條分頁規定、移除第 5 步殘留 bullet」，並點出沒有新建檔
- 0.7：講對但沒強調是沿用既有檔
- 0.3：邊界含糊，看不出是 append 還是新建
- 0.0：宣稱建了新檔，或宣稱改了其他 Rule
