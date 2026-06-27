---
name: demo-observer
description: 示範用 skill；第 3 到第 5 步可一起抽成 ScriptFile。
---

# Purpose

將 observation 整理成 normalized events，再交給後續判讀。

# SOP

1. read `.skillevol/run-evals/latest/observation.md`。
2. think 決定本輪要保留哪些 event family。
3. read observation 內容，略過空行與 `#` 開頭註解。
4. think 將保留的行做 trim，整理成 normalized event list。
5. write normalized events 至 `.skillevol/run-evals/latest/normalized-events.md`。
6. think 根據 normalized event list 判定 verdict。
