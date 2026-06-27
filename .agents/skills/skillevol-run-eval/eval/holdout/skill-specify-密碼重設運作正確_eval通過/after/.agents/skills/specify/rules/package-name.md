# Rule 1 -- 術語：package 指 specs/ 下的一次迭代目錄

## Good

Raw Idea 為「使用者登入後可以收藏文章」，推導 package name 為 `文章收藏`，對應路徑 `specs/文章收藏/spec.md`。

## Bad

將 `specs/` 本身稱為 package，或把 `spec.md` 檔名當成 package name。

# Rule 2 -- 語言對齊：package name 須貼近 Raw Idea 的語言

## Good

Raw Idea 以繁體中文撰寫 → package name 用繁體中文，如 `訂單退款`。
Raw Idea 以英文撰寫 → package name 用英文，如 `order-refund`。

## Bad

Raw Idea 全文繁中，卻強譯為 `order-refund`。
Raw Idea 全文英文，卻改寫為 `訂單退款`。

# Rule 3 -- 格式：依語言採對應命名格式

## Good

英文：`user-login`、`order-refund`（kebab-case 小寫）。
中文：`使用者登入`、`訂單退款`（繁體中文短語，不含空格）。

## Bad

英文：`UserLogin`（PascalCase）、`user_login`（snake_case）、`user login`（含空格）。
中文：`用戶 登入`（含空格）、`user-login`（Raw Idea 為中文時硬套英文 kebab-case）。

# Rule 4 -- 語意：2 到 4 個詞，概括 Raw Idea 主題

## Good

Raw Idea 談「訂單成立後 7 天內可申請退款」→ `訂單退款` 或 `order-refund-window`（語言與 Raw Idea 一致時）。

## Bad

`feature`（過於空泛）、`訂單退款通知信與後台儀表板`（超過 4 詞、難掃讀）。

# Rule 5 -- 唯一性：不得與 specs/ 下既有 package 同名

## Good

`specs/` 已有 `使用者登入`，本次迭代改為 `使用者登入-oauth` 或 `使用者登入-v2`。

## Bad

`specs/` 已有 `使用者登入`，仍新建同名目錄 `specs/使用者登入/`。

# Rule 6 -- 禁用：特殊符號與占位詞

## Good

`payment-webhook`、`購物車結帳`

## Bad

`user.login`、`new-feature`、`temp`、`spec`、`update`、含 `/ \ : * ? " < > |` 等檔名非法字元。
