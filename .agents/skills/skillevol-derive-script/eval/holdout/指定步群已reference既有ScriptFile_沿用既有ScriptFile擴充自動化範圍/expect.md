# expect — 指定步群已reference既有ScriptFile_沿用既有ScriptFile擴充自動化範圍

繼承 `../../shared/expect.md`。本檔只補本 unit 特有期望；衝突時以本檔為準。

## Provenance

- 釘的行為：mutation.md Rule 2 / 4 / 5——若指定步驟群已 reference 既有 ScriptFile，必須沿用原檔擴充自動化範圍，而不是另起新檔或殘留重複步驟。
- 為何存在：reuse gate 是 derive family 很容易滑掉的地方；這條 holdout 用來防止 path 漂移與 v2 script 膨脹。

## Run

### Turn 1 — 結束方式：done

Tool calls
- MUST delegate skillevol-form-script（ScriptFile 的 form 交它）
- MUST NOT 呼叫 askUserQuestion（既有 script 路徑與步驟群邊界已在 inputs）

Assistant message
- 1.0：明說沿用了既有 `scripts/prepare-observation.py`，把第 2 到第 4 步折成單一步驟，未另開新檔
- 0.7：講對但漏交代「沿用既有檔」或「未另開新檔」
- 0.3：只說「已更新 script」，看不出 SOP 邊界
- 0.0：另開新檔，或宣稱沿用既有 script 但實際沒有
