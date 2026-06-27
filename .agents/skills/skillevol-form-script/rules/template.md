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
    run(Path(args.input), Path(args.output))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
```

說明:

1. 檔首固定是 PEP 723 metadata block。
2. `main()` 是唯一 entrypoint；`run()` 與其他 helper 只服務同一責任。
3. 輸入輸出走顯性參數；不要硬寫 repo 根或隱性工作目錄。
4. 若需要第三方依賴，改寫 `dependencies`，但仍維持單檔、單一職責。
