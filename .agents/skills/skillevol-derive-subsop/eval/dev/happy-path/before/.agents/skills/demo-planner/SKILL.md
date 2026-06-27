---
name: demo-planner
description: 示範用 planner skill；第 3 步內嵌了一段有序子程序，待抽成 Sub-SOP。
---

# Purpose

示範 SOP，其中第 3 步把一整段有前後依賴的子程序內嵌在步內。

# SOP

1. read 目標需求檔。
2. think 確認需求齊全。
3. write 收集配置：依序執行以下子步——
   3.1 $args = RESOLVE 路徑變數
   3.2 READ 現有的 config.yml
   3.3 ASSERT 必要鍵齊全；BRANCH 缺鍵 ? STOP : 續
   3.4 think 比對缺哪些配置值
   3.5 WRITE 回寫補齊的 config.yml，交出 $config 給後續步
4. write 依 $config 產生骨架。
5. write 回報結果。
