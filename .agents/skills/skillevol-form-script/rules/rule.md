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

## Rule 6 — 失敗時輸出可理解錯誤並以非零 exit code 結束

- ScriptFile 可能失敗時，應把可理解的錯誤訊息寫到 stderr，並以非零 exit code 結束，讓呼叫端能辨識成敗。
- 成功路徑回傳 0；輸入缺失、格式錯誤等可預期失敗回傳明確的非零碼，不要吞掉錯誤只印 "done"。

### Good

情境: 找不到輸入檔時，印錯誤到 stderr 並回傳非零碼。

```python
if not path.exists():
    print(f"Input not found: {path}", file=sys.stderr)
    return 2
```

結果: 呼叫端可用 exit code 判斷失敗，錯誤訊息也可讀。

### Bad

情境: 直接 `open(source).read()` 後只印 `print("done")`，失敗時沒有可依賴的訊號。

結果: 呼叫端無法從 exit code 或訊息判斷成敗。

預期改法:

- 對可預期失敗做檢查，錯誤寫 stderr 並回傳非零 exit code；成功才回傳 0。

## Rule 7 — 預設執行方式寫成 `uv run <script.py>`

- SOP 步驟提到執行 `scripts/` 下的 Python script 時，預設執行方式必須寫成 `uv run scripts/<name>.py`。
- 這個預設路徑對有無第三方套件都適用，避免呼叫端在 `python`、虛擬環境與手動安裝之間自行猜測。
- 不要把 `python <script.py>`、`./script.py` 或平台特定 launcher 當成主要執行方式。

### Good

```md
1. DELEGATE 執行 `uv run scripts/normalize-observation.py --input tmp/obs.md --output tmp/obs.json`，再用輸出回填後續步驟。
```

結果: 步驟直接指定統一且跨平台的執行命令。

### Bad

```md
1. DELEGATE 執行 `python scripts/normalize-observation.py`。
```

預期改法:

- 改成 `uv run scripts/normalize-observation.py ...`，並以顯性參數傳入輸入輸出。

## Rule 8 — 缺少 `uv` 時提示標準 fallback，不猜測式安裝

- 若執行環境缺少 `uv`，應優先提示安裝 `uv`，再重跑原本的 `uv run scripts/<name>.py`。
- 若當下不能安裝 `uv`，且 `pip` 支援 `--requirements-from-script`，可先 `python -m pip install --requirements-from-script scripts/<name>.py`，再 `python scripts/<name>.py`。
- 不要根據 `ModuleNotFoundError` 臨時猜套件名逐一安裝，也不要要求呼叫端自行讀 import 反推依賴。

### Good

```md
若 `uv` 不可用，先提示安裝 `uv` 後執行 `uv run scripts/fetch-report.py`。
若當下只能用 `pip 26+`，先執行 `python -m pip install --requirements-from-script scripts/fetch-report.py`，再執行 `python scripts/fetch-report.py`。
```

結果: 依賴來源固定在 PEP 723 metadata，備援路徑一致可預測。

### Bad

```md
若腳本失敗，就看缺哪個模組再自己 `pip install`。
```

預期改法:

- 提供固定 fallback: 先裝 `uv`，或用 `pip --requirements-from-script`，不要逐一猜裝。

## Rule 9 — 不依賴特定作業系統的 shell 行為

- ScriptFile 與 SOP 步驟不可把 `chmod +x`、bash-only 語法、`.sh` 包裝器、POSIX 路徑假設等 OS 專屬 shell 行為當成必要前提。
- 傳遞路徑、參數或輸出位置時，用 Python 可攜的方式與顯性參數，不要倚賴某個 shell 展開規則。
- shebang 可作為額外便利，但不可成為主要或唯一的執行方式。

### Good

```md
1. DELEGATE 執行 `uv run scripts/export-notes.py --source data/input.md --output data/output.json`。
```

結果: 主要路徑是跨平台的 `uv run`，不預設 POSIX 專屬設定。

### Bad

```md
1. DELEGATE 先 `chmod +x scripts/export-notes.py`，再用 `./scripts/export-notes.py` 跑。
```

預期改法:

- 主要執行改回 `uv run scripts/export-notes.py ...`；不要求 `chmod +x` 或 POSIX 專屬前提。
