# ScriptFile 內容樣板

一個 ScriptFile 是所屬層級 `scripts/` 下的一個單檔 Python 腳本。骨架如下。

## 路徑

```
scripts/<kebab-case>.py
```

## 內容

```python
# /// script
# requires-python = ">=3.11"
# dependencies = []
# ///
from __future__ import annotations

import argparse
import sys
from pathlib import Path


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", required=True)
    parser.add_argument("--output", required=True)
    return parser.parse_args()


def run(input_path: Path, output_path: Path) -> None:
    data = input_path.read_text(encoding="utf-8")
    output_path.write_text(data, encoding="utf-8")


def main() -> int:
    args = parse_args()
    input_path = Path(args.input)
    if not input_path.exists():
        print(f"Input not found: {input_path}", file=sys.stderr)
        return 2
    run(input_path, Path(args.output))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
```

說明:

1. 檔首固定是 PEP 723 metadata block。
2. `main()` 是唯一 entrypoint；`run()` 與其他 helper 只服務同一責任。
3. 輸入輸出走顯性參數；不要硬寫 repo 根或隱性工作目錄。
4. 若需要第三方依賴，改寫 `dependencies`，但仍維持單檔、單一職責。
5. 失敗時把可理解的錯誤訊息寫到 stderr，並以非零 exit code 結束；成功才回傳 0。
6. 預設執行方式寫成 `uv run scripts/<name>.py --input ... --output ...`；不要預設 `python`、虛擬環境或手動安裝。
7. 缺少 `uv` 時先提示安裝再重跑；若當下只能用 `pip 26+`，先 `python -m pip install --requirements-from-script scripts/<name>.py` 再 `python scripts/<name>.py`，不要靠 `ModuleNotFoundError` 逐一猜裝。
8. 不依賴特定 OS 的 shell 行為（`chmod +x`、bash-only、`.sh` 包裝、shebang 當唯一入口）；路徑與參數走 Python 可攜方式。
