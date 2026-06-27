# TemplateFile 內容樣板

TemplateFile 有兩種變體，依「誰來消費」選一種來寫。

## raw 變體（無 guideline）

用於: 由 script 或 generator 逐字消費的成品骨架。整檔就是範本本體，除 placeholder 外逐字即成品；不放任何指導、不放 outPath。

骨架:

```
<成品第一行，要填處寫成 ${NAME} 或 {{NAME}}>
<成品其餘每一行；非 placeholder 的字元都會原樣輸出>
```

## guideline 變體

用於: 由 LLM 渲染、需要指導才填得對的範本。整檔分兩個 H1 段: Guideline 講怎麼填，Template 放骨架。

骨架:

```
# Guideline

<怎麼渲染這個範本: 每個 placeholder 填什麼、來源、禁忌、回答或批次格式>

# Template

<成品骨架；placeholder 用 ${NAME} 或 {{NAME}}；除 placeholder 外逐字即成品>
```
