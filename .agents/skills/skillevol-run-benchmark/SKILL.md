---
name: skillevol-run-benchmark
description: 跑某 skill 的整組 eval 並彙總：排出 dev（迭代用、可見）與 holdout（最終 gate）的 unit 清單，逐 unit 委派 /skillevol-run-eval，再彙總成 pass rate 與失敗清單。Use when 要對某 skill 跑完整 benchmark 或最終放行 gate。SKIP when 只跑單一 unit（用 /skillevol-run-eval）。
---

# Purpose

跑某 skill 的整組 eval 並彙總結果。從目標 skill 的 eval/ 排出要跑的 unit 清單（dev/ 全部用於對著迭代；holdout/ 僅在最終放行 gate 才跑），逐一委派 `/skillevol-run-eval` 取得每個 unit 的評分，再彙總成整體 pass rate 與失敗清單。

本 skill 只負責「選 unit、排程、彙總」；單一 unit 怎麼跑與評分由 `/skillevol-run-eval` 負責。dev 與 holdout 的差別只在可見性與時機，由本 skill 的排程決定，不影響單一 unit 的跑法。

# SOP

1. read 目標 skill 的 eval/，含 shared/expect.md、dev/ 與 holdout/ 下各 unit。
2. think 排出本次要跑的 unit 清單：對著迭代時只取 dev/ 全部；最終放行 gate 時才納入 holdout/。
3. delegate to SKILL /skillevol-run-eval
   - input: 清單中的每一個 unit
4. think 彙總各 unit 的評分為整體 pass rate 與失敗清單，標出每個失敗對應的 provenance。
5. write benchmark 報告：整體 pass rate、逐 unit 結果、失敗定位，以及 dev 與 holdout 是否分別通過。
