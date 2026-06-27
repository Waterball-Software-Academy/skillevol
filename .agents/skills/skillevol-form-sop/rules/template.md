# SOP 樣板

```markdown
# SOP

1. read <要讀的檔案、上下文或輸入。>
2. think <單一推理任務。>
3. write <單一產出物或單一區塊。>
```

補充樣板:

- 有序子流程:

```markdown
2. think <單一推理任務。>
   1. <第一個固定順序子步。>
   2. <第二個固定順序子步。>
```

- 三項以內的無序規定:

```markdown
3. write <單一產出物。>
   - <第一條無序規定。>
   - <第二條無序規定。>
   - <第三條無序規定。>
```

- 超過三項時改用 rules reference:

```markdown
3. write <單一產出物>。請嚴格遵守 `rules/<rule-file>.md` 來執行此步驟。
```

- delegate:

```markdown
4. delegate to SKILL /<skill-name>
   - input: <輸入一>
   - skip: <跳過條件>
```

- gated flow:

```markdown
2. think 執行 <gate-name>。
   1. <gate 檢查一>
   2. 若 <outcome A>，進入第 <N> 步。
   3. 若 <outcome B>，回到第 <M> 步。
   4. 若 <outcome C>，停止並回報 <原因>。
```

- loop orchestrator:

```markdown
6. think 進入 <loop-name>。
   1. 選定下一個 <loop item>。
   2. 若 <exit condition>，進入第 <N> 步。
   3. 否則進入第 <M> 步。
8. think 驗證目前 <loop item>。
   2. 若 pass，記錄 evidence，回到第 6 步。
   5. 若 fail 且 failure 指向 implementation，回到第 7 步。
```

填寫要點:

- 先依 `rules/control-flow.md` 判定 sequential、gated 或 loop orchestrator，再套用局部句型。
- 每個頂層步驟只用 `read`、`write`、`think`、`delegate` 其中之一開頭。
- 每步只做一件事；不同產出要拆成不同頂層步驟。
- gate 與 loop 必須寫出 gate outcome、branch target、loop invariant、back edge、exit condition。
- 同一指令下不要混用 Subflow 數字編號與無序 bullet。
