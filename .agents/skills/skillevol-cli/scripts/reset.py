# /// script
# requires-python = ">=3.11"
# dependencies = []
# ///
from __future__ import annotations

import argparse
import shutil
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


def clear_workspace(workspace: Path) -> None:
    for child in workspace.iterdir():
        if child.name == ".gitignore":
            continue
        if child.is_dir() and not child.is_symlink():
            shutil.rmtree(child)
        else:
            child.unlink()


def main() -> int:
    args = parse_args()
    workspace = Path(args.workspace)
    ensure_workspace(workspace)
    clear_workspace(workspace)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
