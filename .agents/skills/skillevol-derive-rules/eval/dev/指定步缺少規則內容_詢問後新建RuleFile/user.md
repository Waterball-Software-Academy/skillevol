# user.md — provides-rules

Persona / 作答紀律：
- 只在被問到時才答；不主動講 answer key 上沒被問到的事。
- skill 問「要納入哪些 API 規則」時，就把下面四條給它（free-text，逐條照給）。
- 答得簡短、不引導、不替 skill 做決定。

## Answer key

which-rules: 四條 API 文件格式規定（free）
  1. 每個端點須含 HTTP 方法與路徑。
  2. 請求與回應須各附一個 JSON 範例。
  3. 錯誤碼至少標出 400（參數錯）與 409（狀態衝突）的語意。
  4. path 用名詞複數，operationId 用 camelCase。

## Fallback

問到上面沒有的 → 「這個你決定就好」

## Notes

- 這個 variant 測 R4：skill 必須先問才動手；問到 which-rules 才會拿到內容。
- 若 skill 沒問就自行腦補規則，視為失敗（見 expect 的 Tool calls 與 Cross-turn）。
