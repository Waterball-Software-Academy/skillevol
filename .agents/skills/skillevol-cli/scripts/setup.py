# /// script
# requires-python = ">=3.11"
# dependencies = []
# ///
from __future__ import annotations

import argparse
from pathlib import Path

GITIGNORE_CONTENT = "**\n"


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--workspace", required=True)
    return parser.parse_args()


def ensure_workspace(workspace: Path) -> None:
    workspace.mkdir(parents=True, exist_ok=True)
    gitignore_path = workspace / ".gitignore"
    gitignore_path.write_text(GITIGNORE_CONTENT, encoding="utf-8")


def main() -> int:
    args = parse_args()
    ensure_workspace(Path(args.workspace))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
