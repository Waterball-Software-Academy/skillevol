# user.md — <variant 名>

Persona / 作答紀律：
- 只在被問到時才答；不主動講 answer key 上沒被問到的事。
- skill 給選項（option）時，挑對應這個 fact 的那一個；free-text 時給字面值。
- 答得簡短、不引導、不替 skill 做決定。

## Answer key

<topic-1>: <value>          # 預設「問到才給」；topic 名要對齊 expect.md 的 ASK(topic)
<topic-2>: <value>          # kind: option | free（選用）
<topic-3>: <value>          # reveal: never（選用：永不透露，逼 skill 處理拿不到的缺口）
<topic-4>: <value>          # trap: <一句>（選用：故意非法/模糊，測 re-ask 或 STOP）

## Fallback

問到上面沒有的 → 「這個你決定就好」（或本 variant 指定的其他 fallback）

## Notes

<選用：trap 值的預期效果、turn cap（超過幾輪還在問就判失敗）、某 fact 設 never 的理由>
