# ScriptFile 結構（canonical form）

## Rule 1 — ScriptFile 必須是所屬層級 `scripts/` 下的單一 Python 檔

- ScriptFile 的結構組成必須遵守底下定義。

    ```
    ScriptFile ::=
      所屬層級/                     1      # 目標 skill 根，或某個 Sub-SOP 根
      └─ scripts/                  1
           <kebab-case>.py         1

    <kebab-case>.py ::=
      PEP 723 metadata block       1      # 檔首；`# /// script` ... `# ///`
      imports                      0..N   # 先 stdlib，後第三方
      helper function              0..N   # 可抽 helper，但仍單一職責
      main()                       1      # 單一 entrypoint；回傳 exit code
      __main__ guard               1      # `raise SystemExit(main())`

    不變式 ::=
      S1  路徑固定在所屬層級的 `scripts/`，不用 repo 根共用 scripts
      S2  檔首必有合法 PEP 723 metadata block
      S3  一檔一職責；只承載可被穩定自動化的 mechanical 工作
      S4  輸入與輸出走顯性參數，不偷吃未宣告的隱性狀態
      S5  必有 `main()` 與 `__main__` guard；可直接執行
      S6  內容是程式碼，不混入 markdown 標題、流程說明或 prompt 文案
      S7  失敗以非零 exit code 結束，錯誤訊息寫到 stderr；成功回傳 0
    ```

### Good

情境: 目標步驟屬於某個 skill 根下的 observation 正規化工作。

```
.agents/skills/demo-observer/
  scripts/
    normalize-observation.py
```

結果: script 與它服務的 SOP 同層，路徑穩定、可被 parent 以相對路徑呼叫。

### Bad

情境: 把 script 寫到 repo 根 `scripts/normalize.py`，或把兩個不相干流程塞進同一支檔案。

結果: 路徑脫離 owning level（違反 S1），責任也失焦（違反 S3）。

預期改法:

- 把 script 放回所屬層級的 `scripts/`，並拆回一檔一職責。
