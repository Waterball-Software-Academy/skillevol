---
name: demo-observer
description: 示範用 skill；第 3 到第 5 步可一起抽成 ScriptFile。
---

# Purpose

將 observation 整理成 normalized events，再交給後續判讀。

# SOP

1. read `.skillevol/run-evals/latest/observation.md`。
2. think 決定本輪要保留哪些 event family。
3. run `scripts/normalize-observation.py` 完成 observation 正規化。
4. think 根據 normalized event list 判定 verdict。
