---
name: demo-artifact-planner
description: 示範用 skill；已經有一支 script，但步驟群還沒完全吸進去。
---

# Purpose

先整理 observation，再根據中間產物決定後續 gate。

# SOP

1. read `.skillevol/run-evals/latest/observation.md`。
2. run `scripts/prepare-observation.py` 完成 observation 前處理。
3. think 依 normalized events 與 noise summary 決定後續 gate。
