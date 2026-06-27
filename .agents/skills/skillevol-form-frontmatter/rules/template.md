# Frontmatter 樣板

```yaml
---
name: <目標 skill 的資料夾名>
description: <一句核心職責。Use when <使用者描述的時機或檔案狀態>。SKIP when <不屬於此 skill 的相鄰邊界或範圍>。>
---
```

填寫要點:

- `name`: 直接使用目標 skill 的 parent directory，保持小寫 kebab-case。
- `description`: 只寫職責、觸發時機、邊界，不寫 Purpose 的背景故事。
- 若原本已有其他 frontmatter 欄位，更新時保留原樣，只改 `name` 與 `description`。
