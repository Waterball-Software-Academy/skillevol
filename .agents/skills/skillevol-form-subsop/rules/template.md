# Sub-SOP 內容樣板

一個 Sub-SOP 是目標 skill 根下一個目錄加一份 SOP.md。骨架如下。

## 目錄

```
<kebab-case 名；需與兄弟 Sub-SOP 排序時加 NN- 前綴，例 01-ask-config>/
  SOP.md
  rules/        <選用: 本 Sub-SOP 專用 RuleFile>
  templates/    <選用: 本 Sub-SOP 專用 TemplateFile>
  steps/        <選用: 本 Sub-SOP 專用 ReasoningFile 等>
```

## SOP.md

```
# <Sub-SOP 名稱: 一句它的職責>

參數: <entry，綁定本子程序要的輸入路徑與來源；parent 傳入的值也列在此>

# SOP

1. <verb> <主指令>。<選用 reference: 依 rules/<檔> 或 templates/<檔>>
2. <verb> <主指令>。
3. <verb> <主指令>。
N. <verb> 把產物或結論交回 parent。
```

說明:

1. verb 限 read、think、write、delegate、run，用自然語言，不寫 program-like。
2. 步驟有序，後步可依賴前步產物。
3. 開頭參數段是 entry，最後一步是 exit（交產物給 parent）。
4. parent SOP 以「執行 <dir>」或「read <dir>/SOP.md」串接本 Sub-SOP。
