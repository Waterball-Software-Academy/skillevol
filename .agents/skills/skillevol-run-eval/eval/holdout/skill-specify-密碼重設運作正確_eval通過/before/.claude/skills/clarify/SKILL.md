---
name: clarify
description: 向使用者澄清問題時的統一規範（白話選擇題、AskQuestion 逐題提問、Q&A 留存）。Use when 任何 skill 或 workflow 需要向 PM 澄清待決事項、使用者下 /clarify、或呼叫方指向本 skill 的 rules/clarify-rules.md。SKIP when 問題已全數確定、無需向使用者提問。
---

# Purpose

工作流中多處需要向 PM 澄清待決事項；若每個 skill 各自撰寫澄清規範，用詞、選項格式與 Q&A 留存方式容易不一致，維護成本也高。
本 skill 在任一 skill 或流程需要向使用者澄清問題時啟用，提供統一的澄清基本原則：白話提問、AskQuestion 逐題互動、選項格式與 Q&A 留存。
它是澄清行為的共用規範層；呼叫方 skill 負責決定澄清對象與答案回寫位置，本 skill 只規範「怎麼問、怎麼留紀錄」。
若跳過本 skill 各自發明澄清方式，常見後果是技術語過多、選項缺「其他」、或 Q&A 遺失無法追溯。

# SOP

1. read 呼叫方 skill 或使用者提供的待澄清項目與上下文。
2. read `rules/clarify-rules.md`。
3. write 依 rules 向使用者逐題澄清，並回報完整 Q&A 結果予呼叫方；若無待澄清項目則結束。
