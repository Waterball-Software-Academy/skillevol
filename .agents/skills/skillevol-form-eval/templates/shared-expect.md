# shared expect — <target-skill> 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準（local override）。

本檔不得宣告 `## Hidden oracle metadata`。hidden oracle metadata 只允許出現在 unit-local `expect.md`，正式 schema 以 `rules/hidden-oracle-metadata.md` 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：<target-skill>（<一句它的角色，例如 mutation / planning skill>）。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 user-prompt（context-free 單輪）。
- outputs 三通道：Tool calls、Assistant message、File diff（`git diff (before/ ↔ 跑完的 fs)`）。

## Tool calls（橫切）

決定性 invariant：
- <跨 scenario 共通的工具紀律，例如：唯一允許向 user 提問的是 askUserQuestion；唯一允許 delegate 的是 <某 skill>>

## File diff（橫切）

決定性 invariant：
- <跨 scenario 共通的變更邊界，例如：只能動指定 target；不得寫到 CWD 外；該動以外的檔 byte-identical>
- <若產出含某種 artifact，補它必須滿足的 form 不變式>

語意 rubric（橫切）：
- <跨 scenario 共通的內容紀律，例如「不臆測 inputs 沒授權的內容」，配 0.0/0.3/0.7/1.0 錨點>

## Assistant message（橫切）

語意 rubric：
- 1.0：<跨 scenario 共通的好對話標準>
- 0.7：<…>
- 0.3：<…>
- 0.0：<…>
