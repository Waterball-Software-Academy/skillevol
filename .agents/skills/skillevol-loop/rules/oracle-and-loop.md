# Eval oracle 與 loop 邊界規則

本檔規範 `skillevol-loop` 如何把 eval 當作唯一法官，並決定何時先做白話 RCA、何時要停在使用者確認 gate、何時可進入 mutation。

## Rule 1 — 缺少可信 eval oracle 時必須停止

- `skillevol-loop` 只能在目標 skill 已有可執行 eval 時啟動。若目標 skill 沒有 `eval/`，或 eval 結構不符合 `skillevol-form-eval` 的基本形狀，不能靠主觀理解開始改 skill。
- 停止回報必須指出缺口，並把下一步導向 `/skillevol-form-eval` 或 `/skillevol-define-evals`。

### Good

情境: 使用者要求對 `.agents/skills/specify` 跑 skillevol-loop，但該 skill 沒有 `eval/`。

```
停止: specify 缺少 eval oracle，不能開始演化。
下一步: 先使用 /skillevol-define-evals 建立 golden benchmark，或用 /skillevol-form-eval 修正 eval form。
```

結果: loop 沒有在無法驗證的狀態下創造 skill 內容。

### Bad

情境: 同一個目標 skill 沒有 `eval/`。

```
我先根據 spec 寫一版完整 SKILL.md，之後再補 eval。
```

結果: 改造沒有 oracle，會回到一次生成與主觀判斷。

預期改法:

- 停止本輪 loop，要求先補 eval oracle；不要先改 skill。

## Rule 1b — 執行 loop 時不得讀本 skill 的 self-eval fixture

- `skillevol-loop` 在執行時，`eval oracle` 一律指「目標 skill 的 eval/」，不是 `skillevol-loop` 自己 repo 內的 self-test fixture。
- 除非使用者明確要求修改 `skillevol-loop/eval/**`，否則本 skill 不得讀取自己的 `eval/`、unit `expect.md`、`after/`、golden report 或其他 self-eval material。
- 若 `skillevol-loop` 自己正被外層 `/skillevol-run-eval` 評估，這些 self-eval fixture 仍屬 hidden oracle material，不是 runtime input；不得為了猜現在在測什麼而回頭讀它們。

### Good

情境: `skillevol-loop` 正在優化 `demo-plan-skill`，外層 runner 剛把 `demo-plan-skill` fixture 複製到 sandbox。

```
read: sandbox 內的 demo-plan-skill、demo-plan-skill/eval、使用者需求、sandbox 內既有 working-plan
not read: repo/.agents/skills/skillevol-loop/eval/dev/.../expect.md
```

結果: loop 只靠 target skill 與使用者需求做判斷，不會偷看自己這題的答案。

### Bad

情境: 同上。

```
我先讀 skillevol-loop/eval/dev/.../expect.md 和 after/，這樣比較知道這題要我做什麼。
```

結果: 被測 skill 從 repo 讀到 self-eval golden，破壞 oracle isolation；即使行為面看起來正確，也不能算 pass。

預期改法:

- 把可讀輸入限制在 target skill、target eval、使用者需求與 sandbox 內 working-plan；不要讀本 skill 的 self-eval fixture。

## Rule 1c — Phase 0 產物必須先於 RCA 與確認 gate 落盤

- `skillevol-loop` 在完成 Phase 0 後，必須先寫出 `.skillevol/.gitignore` 與 `.skillevol/<target-skill>/loop/working-plan.md`，才能開始任何 RCA 說明或確認 gate。
- 這兩個檔案不是收尾 artifact，而是後續 RCA、red gate 與 mutation loop 的 state carrier。若它們缺失，就算白話 RCA 本身正確，也不能算通過本輪 unit。

### Good

情境: loop 剛判定目標 skill、desired state 與目前 gate。

```
1. write .skillevol/.gitignore
2. write .skillevol/<target>/loop/working-plan.md
3. 再開始白話 RCA 與 ask confirm
```

結果: 後續 phase 有可追溯 state，且 pre-mutation gate 的 file diff 與訊息一致。

### Bad

情境: loop 直接先講 RCA，最後才想起要不要補 working-plan。

```
先說明缺口、先 ask confirm；.skillevol/** 完全沒寫
```

結果: RCA 看似合理，但沒有 state carrier，unit 的 MUST file artifacts 失敗。

預期改法:

- 把 `.skillevol/.gitignore` 與 working-plan 視為 Phase 0 的 blocking output，而不是可有可無的附帶產物。

## Rule 2 — 進 red gate 前先做白話 RCA

- 既有 skill 要優化時，先檢查現有 eval 是否覆蓋使用者此次 desired state。若新需求、新限制、新風格或新邊界沒有出現在 eval 中，必須先做一段 eval-oracle RCA，再決定如何補 failing test。
- 這段 RCA 要用白話文講清楚三件事：現有測試還缺什麼、為什麼目前測不出這次問題、以及下一步是擴充既有 dev scenario 還是新增 dev scenario。
- 預設把使用者當成不熟悉 skillevol 行話的人；除非是目標 skill 本身的詞，否則避免直接丟 `oracle`、`adequacy gate`、`provenance` 這類內部術語。

### Good

情境: 使用者說「讓某個規劃 skill 在開始施工前一定要先等我確認」，但現有 eval 只測 happy path 產物。

```
現有測試只會看它有沒有寫出計畫，沒有看它會不會停下來等你確認。
所以現在就算 skill 偷偷直接往下做，這組測試也抓不到。
這次我會新增一條 dev scenario，不沿用原本 happy path，因為「先等確認」是新的互動關卡，不是同一條 happy path 的小分支。
```

結果: 使用者能用常識理解缺口，並看懂補測試的方向。

### Bad

情境: 同上。

```
目前 eval adequacy 不足，需先補 oracle，再用 red gate 檢查 provenance。
```

結果: 詞彙是對的，但使用者不知道到底缺哪條測試、為什麼缺、要怎麼補。

預期改法:

- 把缺口翻成白話：現在測了什麼、沒測什麼、因此漏抓了什麼行為、下一步是改既有 dev 還是加新的 dev。

## Rule 3 — RCA 與 failing-test 計畫必須先經使用者確認，才能進 red gate

- `skillevol-loop` 不得在自己提出「怎麼補 failing test」後立刻往下跑 red gate；要先把 RCA 與測試計畫給使用者確認。
- 這個確認點的核心是：讓使用者先拍板「你抓到的缺口是不是我在意的那個問題」，避免 agent 幫錯方向的 test 上鎖。

### Good

情境: loop 剛完成 RCA，判定應新增一條 dev scenario。

```
我會新增一條 dev scenario，專門驗「寫出計畫後必須先等確認」。這條不沿用既有 happy path，因為它要釘的是新的互動關卡。
你先確認這個補測試方向對不對；確認後我才進 red gate。
```

結果: failing test 的方向先被拍板，再進入 red gate。

### Bad

情境: 同上。

```
我已經幫你補完新 eval，現在直接跑 red gate。
```

結果: 使用者還沒確認補測試方向，agent 已經把一條可能校歪的尺寫死了。

預期改法:

- 停在 RCA 結尾，把「缺口 + 補法」壓成一個可 confirm/reject 的提案，等確認後再跑 red gate。

## Rule 4 — 新增或修改 eval 後必須先通過 red gate

- 新增或修改的 eval unit 必須先對現有 skill 執行，並證明至少一個目標 failure 成立。若該 eval 一開始就 pass，應判斷是現有 skill 已滿足需求，還是 eval 太弱。
- 若 pass 不合理，回到 `/skillevol-define-evals` 加嚴 expect 或 rubric；不能用 pass 的弱 eval 當作改造依據。

### Good

情境: 新增 `dev/wait-for-confirm-before-implement` 後，現有 skill 仍會跳過確認直接往下做。

```
red gate: /skillevol-run-eval 顯示 unit fail
failure: 互動 gate 順序錯，skill 沒有停在 confirm
下一步: 先把這條新 eval 和 red gate 報告給使用者看
```

結果: 法官先證明會抓問題，後續改造才有壓力來源。

### Bad

情境: 新增 eval 後未執行 red gate。

```
eval 看起來已經寫好了，我直接開始重寫 SKILL.md。
```

結果: 不知道 eval 是否真的能抓到缺陷，loop 可能對錯誤目標優化。

預期改法:

- 先跑新增或修改的 eval unit；若沒有形成 failure，先修 eval。

## Rule 5 — red gate 後必須公開 eval 與測試報告，再等第二次確認

- red gate 成立後，`skillevol-loop` 不得立刻進 mutation loop。必須先把新寫出的 eval 怎麼寫、red gate 抓到了什麼、報告路徑在哪裡，用白話文公布給使用者看。
- 第二次確認的核心是：讓使用者確認「這條新測試真的代表我要的要求」，以及「現在抓到的失敗也真的是我要修的那個失敗」。
- 只有在這一步被確認後，才可把後續視為「通過前不要停止」的授權，進入持續 mutation。

### Good

情境: red gate 剛抓到新加的確認 gate 缺口。

```
我新增了一條 dev scenario，專門驗它會不會先停下來等你確認。
剛剛 red gate 的結果是 fail，因為它現在還是會直接往下走。
這是報告路徑：.skillevol/<target>/run-evals/dev/<unit>/eval-report.md
如果這條測試跟這個失敗都符合你的想法，我再開始進 mutation loop。
```

結果: 使用者先看見測試與失敗，再決定是否授權進入持續改造。

### Bad

情境: red gate 剛跑完。

```
fail 了，我現在直接開始修。
```

結果: 使用者沒機會確認新測試與失敗是否真的代表自己在意的問題。

預期改法:

- 先公開 eval 與 red gate 報告，再 ASK 第二次確認；確認後才可開始 mutation。

## Rule 6 — 迭代驗證必須先單點回歸，再跑 dev benchmark，最後才跑 holdout

- 每次 mutation 後，先跑與該 failure 對應的單一 eval unit。單點通過後，再跑 dev benchmark 檢查可見回歸。dev 全綠後，才跑納入 holdout 的 final gate。
- holdout 是最終放行，不是每輪調參工具；除非使用者明確要求，不得在每輪 mutation 後反覆跑 holdout。

### Good

情境: 本輪修改是為了修 `dev/derive-existing-rulefile`。

```
1. 跑 /skillevol-run-eval: dev/derive-existing-rulefile
2. 若 pass，跑 /skillevol-run-benchmark: dev only
3. dev 全綠後，跑 /skillevol-run-benchmark: final gate with holdout
```

結果: 驗證成本低，且 holdout 保留最終 gate 的意義。

### Bad

情境: 每改一行就跑全部 dev 與 holdout。

```
每輪 mutation 後都跑 final gate，並根據 holdout failure 直接猜修。
```

結果: 迭代慢，且容易對 holdout 過度擬合。

預期改法:

- 先跑相關單元，再跑 dev；holdout 只在 final gate 使用。

## Rule 7 — provenance 不足或 desired state 衝突時必須停止

- 若 eval report 無法指出 failure provenance，不能猜測要改哪個部位。若使用者 desired state 與目標 skill 的公開責任邊界衝突，也不能自行擴權。
- 停止回報要列出卡住原因、需要補的證據，以及建議回到哪個上游 skill。

### Good

情境: judge 只說「結果不佳」，沒有指出檔案、步驟、規則或 artifact 差異。

```
停止: failure provenance 不足，無法選擇 mutation。
下一步: 修正 expect 或 judge rubric，使 report 指出違規定位。
```

結果: loop 沒有在證據不足時亂改 skill。

### Bad

情境: provenance 不足。

```
我猜可能是 SOP 不夠詳細，所以先把 SOP 補長。
```

結果: 以猜測取代 failure analysis，容易增加 instruction bloat。

預期改法:

- 停止並要求補足 eval report 的違規定位。
