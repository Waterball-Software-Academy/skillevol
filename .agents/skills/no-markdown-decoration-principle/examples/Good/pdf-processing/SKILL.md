---
name: pdf-processing
description: 處理 PDF 的讀取、合併、拆分、旋轉、OCR 與表單填寫。Use when 使用者提到 PDF 擷取、合併文件、頁面拆分、掃描檔 OCR 或 PDF 表單處理。
---

# Purpose

讓使用者在需要處理 PDF 文件時，能快速得到清楚、可執行的操作指引，而不是先被裝飾性的 Markdown 排版干擾。
此 skill 適用於 PDF 文字擷取、合併、拆分、旋轉、OCR 與表單填寫等任務。
本 skill 假設使用者已經明確指出要操作 PDF，接下來由 skill 把任務範圍、限制與輸出方式交代清楚。

# Rules

## Supported Tasks

1. Extract text from PDF.
2. Merge multiple PDFs into one file.
3. Split selected pages into separate files.
4. Rotate pages.
5. Run OCR on scanned PDF.
6. Fill PDF forms when fields are available.

## Required Inputs

1. PDF 路徑或檔名。
2. 目標操作: 擷取、合併、拆分、旋轉、OCR 或填表。
3. 若涉及頁面操作，需提供頁碼或頁碼範圍。

## Constraints

1. 如果使用者沒有提供檔案路徑，不要假設檔案存在。
2. 如果 PDF 是掃描影像而非可選文字，先說明需要 OCR。
3. 如果需求涉及破壞性覆寫，先提醒風險並確認輸出檔名。
4. 不要捏造 OCR 成功率、頁數或欄位名稱。

## Output Style

1. 用標題與列表說明步驟，不靠粗體、斜體或底線製造層次。
2. 若要列出操作步驟，使用 1. 2. 3. 的有序列表。
3. 若要列出限制或注意事項，使用短 bullet list。
4. 除非真有橫向比較需求，否則不要把長說明塞進表格。
