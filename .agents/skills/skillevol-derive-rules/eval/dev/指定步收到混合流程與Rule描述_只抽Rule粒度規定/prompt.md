請修改 `.agents/skills/rule-author/SKILL.md` 第 4 步。

我要把下面這包「看起來都像規則描述」的內容加到第 4 步，但你要先判斷哪些才是真的 rule-type 規定，哪些其實只是流程或不該變成 RuleFile 的描述。

混合描述如下：

1. 若內容描述的是先後順序、分支、停止條件或回到哪一步，應留在 SOP 或 Sub-SOP，不得抽成 RuleFile。
2. 先讀使用者需求；若目標 skill 不明確，停止並詢問；若目標 skill 明確，再讀目標 skill 與既有工作計畫。
3. 跨多個 concern 的共用背景不得抽成單一大 RuleFile。
4. 每個流程判斷都可以拆成自己的 `rules/*.md`，這樣目錄看起來比較完整。
5. 不能只因為看到多個判斷，就機械拆成多個 RuleFile；若用 SOP 流程表達更清楚，就留在 SOP。
6. active gate、mutation authorization、eval adequacy 這類 protocol label 都應寫進 glossary，然後再包成規則，方便後續引用。
7. 術語或 protocol label 不應包成規則；若白話流程更清楚，就改成流程句或刪除該詞。

請把真正 rule-type 的 Rule 粒度規定抽成 RuleFile，並回寫第 4 步 reference。
