# Rule 1 - 回寫原 step 時必須把 script 的執行時機、路徑與預設執行方式寫進句子

- Level: `MUST`
- 開新 script 後，回寫原 step 時應在原句中明確寫出什麼情況下需要執行該 script、script 路徑，以及預設執行方式 `uv run <script.py>`。
- 路徑應直接寫成 `scripts/xxx.py`，並嵌在 step 的動作句內，讓執行者知道何時執行、執行哪支 script、執行後拿結果做什麼。
- 不可只新增一句籠統提醒，例如「必要時執行某支 script」或「參考 scripts/ 底下的腳本」，而不交代觸發時機與執行方式。

## Good Example

- 這個例子是好的，因為它把觸發條件、路徑與命令都直接嵌回原本的 step。

```md
原本：
2. DELEGATE 若需要先把訪談摘要整理成固定 JSON 結構，再把整理後的結果交回後續分析步驟。

新增 `scripts/normalize_interview_notes.py` 後：
2. DELEGATE 若需要先把訪談摘要整理成固定 JSON 結構，執行 `uv run scripts/normalize_interview_notes.py --input tmp/interviews.json` 產出標準化結果，再把整理後的結果交回後續分析步驟。
```

## Bad Example

- 這個例子是壞的，因為它提到 script，卻沒有交代何時執行、執行哪一支與怎麼執行。

```md
2. DELEGATE 需要時參考 `scripts/normalize_interview_notes.py`。
```

# Rule 2 - 回寫後的 step 不可只剩命令，仍須保留當下動作與流程目的

- Level: `MUST`
- 回寫後的 step 仍必須描述當下要完成的動作與目的，不能退化成單純貼一條命令。
- script 命令只是完成該 step 的手段之一，不是整個 step 的全部語意。
- 若 step 在移除命令後完全失去行動指向，表示回寫方式把主流程壓扁成命令清單，應立即修正。

## Good Example

- 這個例子是好的，因為命令被包在完整動作句裡，step 仍清楚描述要先整理輸入，再接回後續流程。

```md
1. DELEGATE 若需要先清洗批次輸入資料，執行 `uv run scripts/clean_batch_input.py --input data/raw.json --output tmp/clean.json` 產出可分析版本，再用清洗後結果繼續後續步驟。
```

## Bad Example

- 這個例子是壞的，因為 step 只剩下一條命令，讀者看不出這一步的流程目的。

```md
1. DELEGATE `uv run scripts/clean_batch_input.py --input data/raw.json --output tmp/clean.json`
```

# Rule 3 - 若 step 需要提及依賴處理，必須指向 script 內宣告的 metadata 與標準 fallback

- Level: `SHOULD`
- 若回寫後的 step 或相鄰說明需要提及 script 的相依處理，應說明依賴已宣告在 script 檔頭，預設用 `uv run` 執行。
- 只有在 `uv` 不可用時，才補充 `pip 26+` 的 `--requirements-from-script` 作為 fallback。
- 不應把套件名稱直接再抄一次到 step 中，避免讓 SOP 與 script 檔頭的依賴清單分叉。

## Good Example

- 這個例子是好的，因為它把依賴來源維持在 script 本身，只補充必要的執行提示。

```md
若執行者需要確認相依處理方式，說明此 script 的依賴已宣告在檔頭 metadata，預設執行 `uv run scripts/fetch_notes.py`；僅在 `uv` 不可用時，才改用 `python -m pip install --requirements-from-script scripts/fetch_notes.py`。
```

## Bad Example

- 這個例子是壞的，因為它把依賴資訊複製到 SOP，之後容易和 script 檔頭失去同步。

```md
請先手動安裝 `httpx`、`rich`、`pydantic`，再執行 script。
```
