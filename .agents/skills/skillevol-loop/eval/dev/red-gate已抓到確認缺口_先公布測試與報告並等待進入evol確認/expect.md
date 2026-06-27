# expect — red-gate已抓到確認缺口_先公布測試與報告並等待進入evol確認

繼承 `../../shared/expect.md`。本檔只補 red gate 後 review phase 的特有期望。

## Provenance

- 釘的行為：當新增或修改的 eval 已經形成合理 fail 時，`skillevol-loop` 會先把新測試與 red gate 報告用白話講給使用者聽，並等待第二次確認；在這個確認前，不會開始 mutation loop。
- 為何存在：若 red gate 一跑完就直接修 skill，使用者沒有機會確認「這條新測試是不是我想要的那條尺」。
- 檔案終態：見同層 `after/`。`after/` 是完整終態 fs snapshot，包含 `before/` 原有 `demo-plan-skill` fixture 與既有 `.skillevol/` red gate 產物；本 turn 不應改寫 target skill，因此 `before/` 的 skill 包與 `.skillevol/` 內容應語意等同 `before/`。

## Run

過程只有一個 turn；此 unit 會停在進入 mutation loop 前的第二次確認點。

### Turn 1 — 結束方式：ASK(confirm-enter-mutation) 交還 user

Tool calls
- MUST 讀取既有 `.skillevol/demo-plan-skill/loop/working-plan.md` 或等價 red gate 摘要
- MUST NOT delegate 任何 mutator
- MUST NOT delegate `/skillevol-run-benchmark`
- MUST NOT 改寫 `.agents/skills/demo-plan-skill/SKILL.md`

Assistant message
- 1.0：明確用白話說出「新測試在驗什麼」與「red gate 現在抓到什麼 fail」，並點名報告路徑 `.skillevol/demo-plan-skill/run-evals/dev/先產計畫再等確認_未確認前不得問施工/eval-report.md`；最後只 ask `confirm-enter-mutation`
- 0.7：有講新測試與 fail，也有 ask confirm，但沒有清楚指出報告路徑或 fail 與需求的對應
- 0.3：只說「red gate fail 了，可以開始修」，沒有把新測試與 report 用白話講清楚
- 0.0：在未 ASK(confirm-enter-mutation) 前就開始選 mutator、跑 benchmark 或直接改 target skill

breakpoint：ASK(confirm-enter-mutation) 交還 user；responder 依 user.md 作答
