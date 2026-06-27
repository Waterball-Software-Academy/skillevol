# expect — 指定步內嵌API規定_新建RuleFile並回寫reference

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 `after/` imply。

## Provenance

- 釘的行為：derive 主幹 + mutation.md Rule 1 / 2(建新) / 3——指定步內容確屬 rule-type 時，正確抽成 RuleFile 並回寫 SOP。
- 為何存在：skill 的 happy path，最該釘死；同時釘 R1（只動指定步）、R2（在目標 skill `rules/` 下建新檔）、R3（SOP 改成「主指令 + reference」）。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-rule-file（RuleFile 的 form 交它）
- MUST NOT 呼叫 askUserQuestion（四條規定已內嵌第 5 步、齊全，無須問）

Assistant message
- 1.0：明說「只動了 specify 第 5 步 + 新建 `rules/api-doc-style.md`，移入四條 API 文件規定」，邊界清楚
- 0.7：講對但夾帶對 specify 其他步的順手建議
- 0.3：只說「已展開規則」，看不出動了哪一步、哪個檔
- 0.0：宣稱也整理了其他步，或把四條講成自己新訂的規範
