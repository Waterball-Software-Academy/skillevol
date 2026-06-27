# expect — happy-path

繼承 `../../shared/expect.md`。本檔只補本 scenario 特有期望；衝突時以本檔為準。

## Provenance

- 釘的行為：mutation.md Rule 1 / 3 / 4 — 把有序子程序抽成 Sub-SOP、parent 改成 invoke reference、搬移時保持順序與依賴並把 program-like 改成法定-verb 步。
- 為何存在：這是 derive-subsop 的正向核心路徑；若抽不出來、或抽出來打散了順序／殘留 program-like，這支 mutator 就沒做到本職。

## Inputs（提醒，真值在 before/ 與 prompt.md）

- before/：demo-planner 完整五步 SOP，第 3 步內嵌 3.1–3.5 的有序子程序（含 $args / RESOLVE / READ / ASSERT / BRANCH / WRITE 等 program-like 記號）。
- prompt：把第 3 步的有序子程序抽成 Sub-SOP。

## Tool calls

決定性：
- MUST delegate skillevol-form-subsop（Sub-SOP 的 form 交給它）。
- MUST NOT 呼叫 askUserQuestion（子程序內容已在 inputs）。

## Assistant message

語意 rubric（疊加 shared）：
- 1.0：講明抽出第 3 步、建了哪個 Sub-SOP 目錄、parent 第 3 步已改成 invoke reference。
- 0.7：講對但漏交代 parent 改法或目錄名。
- 0.3：含糊帶過。
- 0.0：宣稱完成了實際沒發生的變更。

## File diff

決定性 invariant：
- 新建一個 Sub-SOP 目錄與其 `SOP.md`（kebab-case 目錄名，需排序時帶 NN- 前綴），位於 `.agents/skills/demo-planner/` 之下。
- 該 `SOP.md` 滿足 Sub-SOP form S1–S7：步驟有序、用 read/think/write 等法定 verb、有 entry（綁輸入）與 exit（把 config 交回 parent）、無 $args/RESOLVE/ASSERT/BRANCH 等 program-like 記號。
- demo-planner 第 3 步改寫為「主指令 ＋ 執行 `<dir>`。read `<dir>/SOP.md`。」，且不再殘留 3.1–3.5 子步。
- demo-planner 第 1、2、4、5 步 byte-identical；不得寫到 CWD 外。

語意 rubric：
- 1.0：3.1–3.5 一一對映成 Sub-SOP 內的有序步、順序與依賴不變（3.5 仍交出 config 給 parent）、program-like 記號全改成自然語言法定-verb、零新增步。
- 0.7：忠實但有抽象話，或 entry/exit 補得不夠明確。
- 0.3：打散了順序，或漏搬某一子步。
- 0.0：自行發明新步，或殘留 BRANCH/$args 等 program-like。
