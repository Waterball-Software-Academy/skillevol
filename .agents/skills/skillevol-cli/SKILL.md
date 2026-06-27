---
name: skillevol-cli
description: 管理 `.skillevol` workspace 的 setup 與 reset。Use when 要初始化 `.skillevol/`，或清空既有 workspace 但保留 `.gitignore`。
---

# Purpose

讓 skillevol 生態裡重複出現的 `.skillevol` workspace 管理有一個共用入口，而不是每支 skill 各自重寫 bootstrap 與 cleanup。
本 skill 只負責兩件事：建立 `.skillevol/` 與 `.gitignore` 的 `setup`，以及保留 `.gitignore`、清空其餘內容的 `reset`。
若缺少這個共用層，`skillevol-define-evals`、`skillevol-loop` 等 skills 會繼續各自維護一份 workspace 初始化邏輯，重複且容易漂移。

# SOP

1. read 目前 cwd 內的 `.skillevol/` 狀態與使用者請求的 command。
2. think 判定本次要執行的 command。
   1. 若使用者要建立、補建或初始化 `.skillevol/`，選 `setup`。
   2. 若使用者要清空 `.skillevol/` 內既有 workspace，但保留 `.gitignore`，選 `reset`。
   3. 若使用者沒有清楚指定 `setup` 或 `reset`，停止並要求他明講 command。
3. run `scripts/setup.py` 完成 `.skillevol/` workspace 初始化。（僅 command = `setup`）
4. run `scripts/reset.py` 完成 `.skillevol/` workspace 重置。（僅 command = `reset`）
5. think 驗證 `.skillevol/.gitignore` 存在且內容為 `**`，且變更邊界只限 `.skillevol/`。
6. write 回報本次執行的是哪個 command、`.gitignore` 狀態，以及新增、保留或刪除了哪些 workspace 路徑。
