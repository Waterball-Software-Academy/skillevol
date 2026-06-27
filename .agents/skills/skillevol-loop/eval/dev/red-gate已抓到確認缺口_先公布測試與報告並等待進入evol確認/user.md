# user.md — confirm-enter-mutation

Persona / 作答紀律：
- 只在被問到時才答；不主動要求具體 mutation。
- 先看新測試與 red gate 結果，再決定要不要放行進 mutation loop。
- 答得簡短，不替 loop 選 mutator。

## Answer key

confirm-enter-mutation: 可以，這條測試與 fail 都是我要的，開始修吧。（free）

## Fallback

問到上面沒有的 → 「先把測試與 red gate 講清楚」

## Notes

- 本 unit 以 ASK(confirm-enter-mutation) 結束，不要求真的開始 mutation。
