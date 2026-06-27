---
name: synthetic-runner-only
description: Synthetic target skill used only for skillevol-run-eval anti-overfit positive-control fixtures.
---

# Purpose

這個 synthetic skill 不承擔真實 target 行為；它只提供一個 nested eval unit，讓 `skillevol-run-eval` 可以驗證「multi-valid 但 runner-only contract 合法」的 preflight 判斷，不需要啟動 target subagent。

# SOP

1. assistant-message 說明此 synthetic skill 不應被直接啟動。
2. stop。
