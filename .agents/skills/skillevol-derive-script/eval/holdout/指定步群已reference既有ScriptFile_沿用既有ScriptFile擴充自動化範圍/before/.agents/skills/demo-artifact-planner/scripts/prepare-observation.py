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
    parser.add_argument("--normalized-output", required=True)
    return parser.parse_args()


def normalize_lines(text: str) -> list[str]:
    return [line.strip() for line in text.splitlines() if line.strip() and not line.strip().startswith("#")]


def main() -> int:
    args = parse_args()
    normalized = normalize_lines(Path(args.input).read_text(encoding="utf-8"))
    content = "\n".join(normalized)
    if content:
        content += "\n"
    Path(args.normalized_output).write_text(content, encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
