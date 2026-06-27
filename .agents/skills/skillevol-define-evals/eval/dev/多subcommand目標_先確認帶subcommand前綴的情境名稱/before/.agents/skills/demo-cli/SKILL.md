---
name: demo-cli
description: 管理 `.demo-workspace` 的 setup 與 reset。Use when 要初始化 workspace，或清空既有 workspace 但保留 `.keep`。
---

# Purpose

讓使用者透過 `setup` 與 `reset` 兩個 subcommand 管理 `.demo-workspace`。

# SOP

1. read 目前 workspace 狀態與使用者指定的 subcommand。
2. think 判定本次要跑 `setup` 或 `reset`。
3. run `scripts/setup.py` 建立 `.demo-workspace`。（僅 `setup`）
4. run `scripts/reset.py` 清空 `.demo-workspace` 但保留 `.keep`。（僅 `reset`）
