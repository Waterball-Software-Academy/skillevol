# expect — 指定步收到混合流程與Rule描述_只抽Rule粒度規定

繼承 `../../shared/expect.md`。本檔只管行為與時序；檔案終態由同層 `after/` imply。

## Provenance

- 釘的行為：derive 主幹 + mutation.md Rule 1 / 2(建新) / 3 / 4 / 5。prompt 提供一包混合描述，其中有些是真正 rule-type 規定，有些只是流程句或反向建議。skill 必須智能判斷，只抽出真正的 Rule 粒度規定，建立單一聚焦 RuleFile，並回寫指定 SOP step 的 reference。
- 為何存在：現有 eval 只測「指定步已經有明確規定」時能不能抽。這條要測的是：使用者要求「加上許多看似流程與規則混在一起的規則描述」時，derive-rules 不能照單全收，也不能把流程句或壞建議包成規則。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-rule-file（RuleFile 的 form 交它）
- MUST NOT 呼叫 askUserQuestion（prompt 已提供足夠內容，且可自行判斷哪些是 rule-type）

Assistant message
- 1.0：明說「只動了 rule-author 第 4 步 + 新建 `rules/rule-granularity.md`」，並說明只抽了四條 Rule 粒度規定，排除了流程句與反向壞建議
- 0.7：derive 正確但回報沒有清楚說明哪些描述被排除
- 0.3：只說「已抽成規則」，看不出是否有判斷混合描述
- 0.0：把所有七條都抽進 RuleFile、停止不抽、抽成多個 RuleFile、或新增 prompt 沒提供的規則

## File outcome

Final RuleFile MUST contain exactly these four rule topics:
- 流程型內容不得抽成 RuleFile
- 跨多個 concern 的共用背景不得抽成單一大 RuleFile
- 不得因多個判斷而機械拆成多個 RuleFile
- 術語或 protocol label 不應包成規則

Final RuleFile MUST NOT contain these prompt items as rules:
- 「先讀使用者需求；若目標 skill 不明確...」這是流程句，應留在 SOP。
- 「每個流程判斷都可以拆成自己的 rules/*.md...」這是反向壞建議。
- 「active gate、mutation authorization、eval adequacy 都應寫進 glossary...」這是反向壞建議。
