# shared expect — synthetic-runner-only 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這個 synthetic target 只用來提供 hidden oracle contract，不代表真實 target 行為。

## 被測 skill 與通道

- 被測：`synthetic-runner-only`（只供 outer runner 做 preflight 合約驗證）。
- inputs：nested unit 的 `before/` 與 `prompt.md`。
- outputs：behavior / message only；不產 file artifact，也不應真的啟動 target。
