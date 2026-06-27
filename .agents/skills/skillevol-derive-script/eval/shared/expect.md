# shared expect — skillevol-derive-script 橫切判準

所有 unit 繼承本檔；個別 unit 只補差異，衝突以個別為準。expect 只管行為與時序；檔案終態由各 unit 的 after/ imply。

被測：skillevol-derive-script（把指定 SOP 步驟群抽成 ScriptFile 的 mutation skill）。
inputs：before/（即 CWD）+ prompt.md；互動型 unit 另有 user.md。
outputs：每個 turn 的 Tool calls 與 Assistant message；檔案終態看 after/；多輪看 event trace。

## Tool calls（橫切）

- 唯一允許 delegate 的 sub-skill 是 skillevol-form-script（ScriptFile 的 form 交它）。
- 唯一允許向 user 提問的 tool 是 askUserQuestion。
- 哪些 unit MUST / MUST NOT，由各 unit 的 expect 指定。

## File diff（橫切）

- mutation surface 只動「指定步驟群所在的 `SKILL.md` + 承接它們的單一 ScriptFile」；其他檔與其他步驟保持不變。
- ScriptFile 必須位於 owning level 的 `scripts/`，且符合 skillevol-form-script 的 form：檔首有 PEP 723 metadata、單一 `main()`、`if __name__ == "__main__": raise SystemExit(main())`、輸入輸出顯性、無 markdown/prompt 文案。
- 若指定步驟群被抽走，parent 必須折成單一步驟，句型為 `run \`scripts/<檔名>.py\` 完成 <共同職責>。`
- 不得寫到 CWD 外的任何路徑。
- 不得臆測 inputs 沒授權的新 workflow、第三方依賴或額外 artifact。

## Assistant message（橫切）

- 1.0：精準回報抽了哪個步驟群、開了或沿用了哪支 ScriptFile、parent 如何被改寫；若停止，也講清楚為何不是 script-type。
- 0.7：方向正確，但漏交代其中一項關鍵資訊。
- 0.3：含糊帶過，看不出實際 mutation 邊界或 stop 理由。
- 0.0：宣稱做了沒做的事，或把語意判讀硬講成可自動化腳本。
