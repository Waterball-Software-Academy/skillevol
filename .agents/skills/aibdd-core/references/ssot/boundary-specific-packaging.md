# Boundary-Specific Packaging

AIBDD 的 packaging assumption SSOT：技術端點專屬知識為什麼被打包成 boundary package。package 內部「裝什麼、怎麼讀、怎麼新增、背後的 assumption」一律由 template 定義，不在此重述。

## 為什麼有這個機制

1. AIBDD 的 skill 主體技術端點不可知；但每個端點對「規格長什麼樣、怎麼驗」各有技術知識——backend 是 API + 持久化 Data，frontend 是 UI operation + ephemeral mock state，mobile／firmware／客戶自訂端點又各不相同。
2. 這些端點專屬知識全收斂進一個 boundary package，目的有二：
   1. 同一套 skill 不必為每個端點分叉——端點差異被隔離在 package 內。
   2. 客戶能複製一個 package、填好、在 `boundary.yml` 指定 type，就擴出支援自家技術配置的新端點。

## package 的內容與讀法

1. 一個 boundary package 該裝什麼、每個檔給誰消費、怎麼 authoring、怎麼新增一個 boundary，以及背後的 packaging assumption，全部由 `_template/` 定義。
2. 請看 `_template/` 底下的 README，來瞭解如何閱讀這個 template。本檔不重述其內容。
