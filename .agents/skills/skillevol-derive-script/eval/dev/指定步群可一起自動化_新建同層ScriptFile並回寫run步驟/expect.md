# expect — 指定步群可一起自動化_新建同層ScriptFile並回寫run步驟

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。

## Provenance

- 釘的行為：derive 主幹 + mutation.md Rule 1 / 2 / 3 / 5——指定步驟群同職責且可一起自動化時，正確抽成同層 ScriptFile，並把 parent 改成單一 run 步驟。
- 為何存在：這是 derive-script 的正向核心路徑；若這條做不對，整個 mutator 就無法成立。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-script（ScriptFile 的 form 交它）
- MUST NOT 呼叫 askUserQuestion（步驟內容、範圍與檔名都已在 inputs）

Assistant message
- 1.0：明說只動了 `demo-observer/SKILL.md` 第 3 到第 5 步，並新建 `scripts/normalize-observation.py`，parent 已折成單一步驟
- 0.7：講對但漏掉檔名或步驟群範圍
- 0.3：只說「已抽成 script」，看不出動了哪裡
- 0.0：宣稱還順手改了別步，或把新的 workflow 說成原本就有
