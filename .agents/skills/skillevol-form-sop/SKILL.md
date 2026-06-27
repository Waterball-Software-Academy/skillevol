---
name: skill-form-sop
description: |
  當要撰寫 Skill 的 SOP 部位時，必須遵守此 Skill 之規範撰寫。
---

# Purpose

SOP 不只是 read/think/write/delegate 的句型組合；它必須先反映任務的控制流程型態，再套用局部步驟規範。
本 skill 在要補完或重寫某 skill 的 `# SOP` 時啟用，負責判定 control-flow type、梳理 gate/branch/loop，並產出可執行、可追蹤的 SOP。
若缺少 control-flow 判定，agent 容易把 loop orchestrator 寫成線性 checklist，或用 delegate 串接取代 gate 與 back edge。

# SOP

1. read 目標 skill 的 `SKILL.md`、使用者 prompt，以及 `rules/rule.md`、`rules/control-flow.md`、`rules/template.md`。
2. think 判定 SOP control-flow type。請嚴格遵守 `rules/control-flow.md` 來執行此步驟。
   1. 判定任務是 sequential、gated，還是 loop orchestrator。
   2. 若 prompt 或 Purpose 描述 intake gate、red gate、regression gate、per-item loop、final exit，必須按 loop orchestrator 處理。
   3. 若只是固定順序產出，才允許 sequential flow。
3. think 梳理控制流程狀態轉移。請嚴格遵守 `rules/control-flow.md` 來執行此步驟。
   1. 列出每個 gate 的 gate outcome 與 branch target。
   2. 對每個 loop 寫出 loop invariant 與 back edge。
   3. 寫出 exit condition；未滿足 exit condition 時不得收尾。
   4. 確認 delegate 只作為 action，不作為控制流程骨架。
4. write 補完或重寫目標 skill 的 `# SOP` 區塊。請嚴格遵守 `rules/rule.md` 與 `rules/template.md` 來執行此步驟。
   1. 保留既有 frontmatter 與 Purpose；只修改 `# SOP`。
   2. 讓頂層步驟直接呈現 gate、branch、loop back edge 與 exit condition。
   3. 不要把 prompt 裡的控制流程壓成一句「流程要清楚」或線性 checklist。
