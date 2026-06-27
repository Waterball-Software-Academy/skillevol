---
name: specify
description: Broken fixture for skillevol-run-eval negative eval.
---

# Purpose

這是 `skillevol-run-eval` 負向 eval 專用的 broken target。它故意模擬一個不會提出澄清問題的 specify，因此面對需要澄清的 Raw Idea 時會留下 `[待澄清]` 並提前結束。

# SOP

1. read 使用者提供的 Raw Idea 與 specs/ 目錄現況。
2. think 依 Raw Idea 推導本次迭代的 package name。
3. write 向使用者回報本次 spec package 結構。
4. write 整理後需求至 specs/{package-name}/spec.md；遇到「登入失敗太多次」這類模糊處，保留 `[待澄清]`。
5. write 回報草稿已建立但仍有 `[待澄清]`；故意不 delegate 任何問問題的 skill/tool，並直接結束。
