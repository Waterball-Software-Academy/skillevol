# ScriptFile 形式（內容判準）

本檔是一個 RuleFile，描述「一個 ScriptFile 該怎麼寫」。ScriptFile 是被 skill 的某個 SOP 步驟呼叫的單檔 Python 腳本，用來承接同職責、可一起自動化的 mechanical 工作。

排版守則: 全檔不用 markdown 標題、列表、粗體、斜體、emoji、裝飾性註解；只保留程式需要的註解與字串。

## Rule 1 — 只承載可穩定自動化的 mechanical 工作

- ScriptFile 只處理 deterministic 或可明確規格化的工作，例如路徑整理、檔案正規化、結構轉換、結果 materialize。
- 若步驟核心是語意判讀、設計決策、與使用者協商，這不是 ScriptFile 該吃的責任。

### Good

情境: 將 observation 檔去空行、去註解、整理成 normalized event list。

結果: 輸入輸出可明確規格化，適合寫成 script。

### Bad

情境: 在 script 內判斷某個 failure 是不是 design conflict，並決定要不要 escalate。

結果: 核心責任是語意判讀，不是 mechanical 自動化。

預期改法:

- 把 semantic judgment 留在 SOP 或改走 Sub-SOP；script 只保留 mechanical 子工作。

## Rule 2 — 檔首固定使用 PEP 723 metadata block

- ScriptFile 的第一個結構必須是 PEP 723 metadata block。
- 若無第三方依賴，也要明寫 `dependencies = []`，不要省略 metadata block。

### Good

```python
# /// script
# requires-python = ">=3.11"
# dependencies = []
# ///
```

結果: 執行契約清楚，後續可被標準工具直接解析。

### Bad

情境: 直接從 `import` 開始，或用任意註解假裝 metadata。

預期改法:

- 把 PEP 723 metadata block 補回檔首，並用合法欄位宣告 Python 與依賴。

## Rule 3 — 輸入輸出必須顯性，不偷吃隱性狀態

- ScriptFile 透過 CLI 參數、函式參數或明確常數接收輸入；不要默默假定當前目錄、repo 根、暫存檔名一定長某樣。
- 產物寫到哪裡、讀哪些來源，都要在 code 中看得出來。

### Good

情境: `--input` 與 `--output` 由 `argparse` 明確接收，再用 `Path` 讀寫。

結果: 呼叫端與 script 的契約清楚，易於重用與測試。

### Bad

情境: 直接寫死 `Path(".skillevol/run-evals/latest/observation.md")`，沒有任何輸入參數。

結果: script 與單一工作目錄強耦合，離開當前情境就失效。

預期改法:

- 把路徑改成顯性輸入，讓 parent SOP 決定餵什麼參數。

## Rule 4 — `main()` 是唯一 entrypoint，helper 只服務主流程

- ScriptFile 必須有單一 `main()` entrypoint，並由 `if __name__ == "__main__": raise SystemExit(main())` 收尾。
- helper function 可以存在，但只能支援同一個責任；不要把第二個流程偷偷塞進 helper。

### Good

情境: `parse_args()`、`normalize_lines()`、`main()` 各自單純，最後由 `main()` 統合。

結果: 主流程清楚，可直接執行。

### Bad

情境: 沒有 `main()`，或同一檔裡同時做 observation 正規化與最終 verdict 判讀。

結果: entrypoint 含糊，責任也混成兩件事。

預期改法:

- 補 `main()` 與 `__main__` guard；不相干責任拆檔。

## Rule 5 — 依賴最小化，標準庫優先

- 先用標準庫完成；只有標準庫做不到，才引入第三方套件，且必須寫進 PEP 723 `dependencies`。
- 不得在 script 內隱性依賴 repo 其他 Python 模組，除非那是明確的同層 import 契約。

### Good

情境: 用 `argparse`、`pathlib`、`json` 完成檔案轉換，`dependencies = []`。

結果: script 可移植、可直接執行，依賴面最小。

### Bad

情境: 為了 trim 空白引入第三方套件，卻沒寫進 metadata。

結果: 執行環境不透明，後續容易爆在安裝與重跑。

預期改法:

- 能用標準庫就回標準庫；若真的要第三方，顯性寫進 PEP 723 metadata。
