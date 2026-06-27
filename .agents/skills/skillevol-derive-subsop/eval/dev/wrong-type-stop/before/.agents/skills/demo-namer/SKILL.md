---
name: demo-namer
description: 示範用 namer skill；第 2 步內嵌的其實是無序原子規定，不是有序子程序。
---

# Purpose

示範 SOP，其中第 2 步內嵌了幾條彼此無序、可逐條獨立驗收的命名規定。

# SOP

1. read 目標 schema。
2. think 套用命名規定——
   - 名稱必須 kebab-case
   - command 與 query 必須分檔
   - 同義規則必須去重
3. write 輸出命名結果。
