# TemplateFile 內容樣板

每份樣板都是骨架檔與範例檔的雙檔配對: 兩檔同 basename、同副檔名，範例檔多一個 .example，皆置於目標 skill 的 templates/ 目錄。

## 骨架檔 templates/<name>.<ext>

用於: 供執行者複製後填值的成品骨架。只放固定結構、固定語法與 {{UPPER_SNAKE_CASE}} placeholder；placeholder 只覆蓋可變內容，固定格式符號保留在骨架中。

骨架:

```
<成品的固定結構與語法，逐行保留>
<可變處寫成 {{PLACEHOLDER_NAME}}；同一語意用同一名稱>
```

## 範例檔 templates/<name>.example.<ext>

用於: 供執行者對照填完後樣子的具體成品。與骨架檔同結構，把每個 placeholder 換成真實值。

骨架:

```
<與骨架檔相同的固定結構與語法>
<每個 {{PLACEHOLDER_NAME}} 換成對應的具體值>
```
