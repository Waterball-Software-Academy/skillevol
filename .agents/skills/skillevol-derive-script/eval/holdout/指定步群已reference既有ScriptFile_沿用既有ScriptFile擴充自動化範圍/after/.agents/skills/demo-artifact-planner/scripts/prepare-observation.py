# /// script
# requires-python = ">=3.11"
# dependencies = []
# ///
from __future__ import annotations

import argparse
from collections import Counter
from pathlib import Path


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("--input", required=True)
    parser.add_argument("--normalized-output", required=True)
    parser.add_argument("--noise-output", required=True)
    return parser.parse_args()


def classify_lines(text: str) -> tuple[list[str], Counter[str]]:
    normalized: list[str] = []
    noise = Counter()
    for raw_line in text.splitlines():
        stripped = raw_line.strip()
        if not stripped:
            noise["blank"] += 1
            continue
        if stripped.startswith("#"):
            noise["comment"] += 1
            continue
        normalized.append(stripped)
    return normalized, noise


def render_noise_summary(noise: Counter[str]) -> str:
    lines = [f"{kind}: {count}" for kind, count in sorted(noise.items())]
    if not lines:
        return ""
    return "\n".join(lines) + "\n"


def main() -> int:
    args = parse_args()
    input_text = Path(args.input).read_text(encoding="utf-8")
    normalized, noise = classify_lines(input_text)

    normalized_content = "\n".join(normalized)
    if normalized_content:
        normalized_content += "\n"

    Path(args.normalized_output).write_text(normalized_content, encoding="utf-8")
    Path(args.noise_output).write_text(render_noise_summary(noise), encoding="utf-8")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
