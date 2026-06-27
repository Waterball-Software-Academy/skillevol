# shared expect — skillevol-derive-subsop 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準（local override）。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：skillevol-derive-subsop（把指定 SOP 步的有序子程序抽成 Sub-SOP 的 mutation skill）。
- inputs：`before/` 即 CWD 的 fs snapshot ＋ `prompt.md` 一句 context-free 單輪 user-prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`git diff (before/ ↔ 跑完的 fs)`）。

## Tool calls（橫切）

決定性 invariant：
- 唯一允許 delegate 的 form skill 是 skillevol-form-subsop；MUST NOT delegate skillevol-form-rule-file 或 skillevol-form-template-file。
- 唯一允許向 user 提問的工具是 askUserQuestion；inputs 已含子程序內容時 MUST NOT 提問。

## File diff（橫切）

決定性 invariant：
- 只能動 prompt 指定的那一個 SOP 步、以及該步對應的 Sub-SOP（目錄與 SOP.md）；目標 skill 的其餘 SOP 步與其他檔必須 byte-identical。
- 不得寫到 CWD 外的任何路徑。
- 若有建立 Sub-SOP，它必須滿足 Sub-SOP form 的結構不變式（S1–S7：步驟有序、法定 verb、entry/exit、自洽子樹、單一職責、parent 以 invoke reference 串接、無 program-like 與 markdown 裝飾）。
- parent 指定步若被改寫，句末 reference 形式固定為「執行 `<dir>`。read `<dir>/SOP.md`。」。

語意 rubric（橫切）：
- 1.0：搬入 Sub-SOP 的子步忠實對映 inputs、保持原順序與依賴、零新增步驟。
- 0.7：忠實但有抽象話或把某步輕微放大縮小。
- 0.3：漏搬一步，或塞了 inputs 沒授權的步。
- 0.0：自行發明整段新程序，或把無序規定硬抽成 Sub-SOP。

## Assistant message（橫切）

語意 rubric：
- 1.0：講清楚抽了哪一步、開了哪個 Sub-SOP、parent 怎麼被改成 invoke reference；不宣稱做了沒做的事。
- 0.7：講對但漏掉其中一項（例如沒交代 parent 改法）。
- 0.3：含糊帶過，看不出實際 mutation 範圍。
- 0.0：宣稱完成了實際 fs 沒發生的變更。
