# SOP control-flow 規則

## Rule 1 — 先判定 control-flow type，再寫步驟

- 寫 SOP 前必須先判定任務是 sequential flow、gated flow，還是 loop orchestrator。
- 若 Purpose 或 prompt 提到 red gate、regression gate、per-item loop、retry loop、final exit、back edge，預設是 loop orchestrator，不是 sequential checklist。
- 不得把 control-flow 需求壓成一句「流程要清楚」或「避免 delegate 太多」。

### Good

情境: prompt 要求 ATDD 外圈 gate 與內圈 per-AC loop。

```
判定: loop orchestrator
下一步: 梳理 intake gate、red gate、per-AC back edge、regression gate、refactor gate、final exit
```

結果: SOP 會顯式寫出狀態轉移，而不是線性寫測試、寫實作、跑測試。

### Bad

情境: 同一 prompt。

```
判定: sequential
SOP: read → write tests → write impl → run tests → report
```

結果: 控制流程被壓平，red gate 與 back edge 消失。

預期改法:

- 改判 loop orchestrator，並為每個 gate 寫 gate outcome 與 branch target。

## Rule 2 — gated flow 必須寫出 gate outcome 與 branch target

- 每個 gate 必須用 `think 執行 <gate-name>` 或等價頂層步驟承載。
- gate 子流程必須寫出至少兩種 outcome：通過時去哪一步、失敗或阻塞時去哪一步或停止。
- 「停止並回報」是合法 branch target；不得把 gate 寫成無分支的敘述。

### Good

```markdown
5. think 執行 red gate。
   1. 跑新增或更新的 acceptance tests。
   2. 若 test fail 且 failure 對應尚未實作的 behavior，進入第 6 步。
   3. 若 test pass，判斷測試太弱、測錯層級或 fixture 錯；若成立，回到第 3 步。
   4. 若無法判斷為什麼沒有 red，停止並回報 red gate 不成立。
```

結果: gate outcome 與 branch target 可驗收。

### Bad

```markdown
5. think 執行 red gate，確認測試先 fail 再實作。
```

結果: 有 gate 名字，但沒有可驗收的分支。

預期改法:

- 把每個 gate outcome 拆成編號 subflow，並寫清楚 branch target。

## Rule 3 — loop orchestrator 必須寫出 loop invariant、back edge 與 exit condition

- 每個 loop 必須說明循環不變量（loop invariant），例如「仍有一個未通過的 AC」或「regression 尚未全綠」。
- 必須寫出 back edge：失敗或未完成時回到哪一步。
- 必須寫出 exit condition：只有滿足什麼條件才離開 loop 或進入 final report。
- per-item loop 與 outer loop 都要各自有 invariant、back edge、exit condition。

### Good

```markdown
6. think 進入每個 acceptance criterion 的內圈。
   1. 選定下一個未通過的 acceptance criterion。
   2. 若所有 acceptance criteria 都有 passing evidence，進入第 10 步。
   3. 否則進入第 7 步。
8. think 驗證目前 criterion。
   2. 若 pass，記錄 evidence，回到第 6 步選下一個 criterion。
   5. 若 fail 且 failure 指向 implementation，回到第 7 步。
```

結果: 內圈 loop invariant、back edge、exit condition 都清楚。

### Bad

```markdown
6. 對每個 AC 寫測試、寫實作、跑測試直到通過。
7. 全部 AC 通過後跑 regression。
```

結果: 有循環語意，但沒有 back edge 與 exit condition。

預期改法:

- 用 `think` 步承載 loop，並為 pass/fail/全部完成分別寫 branch target。

## Rule 4 — delegate 是 action，不是控制流程骨架

- `delegate` 只能出現在某個控制狀態內的 action，不得用一串 delegate 取代 gate、branch、loop。
- 若任務需要 orchestration，主 SOP 必須保留 think 步來判定何時 delegate、何時回到上游 gate。
- `skip` 只能修飾某個 action 是否執行，不能代替 branch target。

### Good

```markdown
2. think 執行 intake gate。
   3. 若 criteria 不可測，停止並要求澄清。
   4. 若 criteria 可測，進入第 3 步。
7. delegate to SKILL /some-helper
   - input: 目前 AC 的 test fixture
   - skip: 若 helper 不適用
```

結果: 控制流程由 think gate 承載，delegate 只是局部 action。

### Bad

```markdown
1. delegate to SKILL /planner
2. delegate to SKILL /test-writer
3. delegate to SKILL /implementer
4. delegate to SKILL /reporter
```

結果: 控制流程被 delegate 線性串取代。

預期改法:

- 改由 gate/loop think 步 orchestrate，只在必要 action 點 delegate。

## Rule 5 — control-flow 與局部 SOP form 必須共存

- 寫出 gate/loop 後，仍須遵守 `rules/rule.md` 的頂層動詞、單步職責、subflow 與 rules 分離、delegate 句型。
- 同一指令下不要混用 subflow 編號與無序 bullet。
- 超過三項無序規定時，抽到 `rules/*.md`，SOP 只保留 reference。

### Good

```markdown
10. think 執行 regression gate。
    1. 跑相關 regression、unit、lint、typecheck。
    2. 若 regression fail，進入 impact analysis。
    3. 若是實作破壞既有行為，回到第 7 步修正。
    4. 若 regression 全綠，進入第 11 步。
```

結果: 控制流程清楚，局部句型仍合規。

### Bad

```markdown
10. think 執行 regression gate。
    - 跑 regression
    - 若 fail 就修
    - 不得跳過測試
    - 修完要 rerun
    - 全綠才繼續
```

結果: gate 下混用 subflow 與 bullet，也違反局部 form。

預期改法:

- 有序 gate 子步用編號 subflow；無序禁止項移到 RuleFile。
