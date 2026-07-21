# 圖書借閱權限多層簽核需求_產出測試情境導向activity — expect

繼承 shared/expect.md；衝突以本檔為準。

## Provenance

- 釘的行為: flows-specify 依需求中的系統情境，把多層簽核旅程展開成測試情境導向的 `.activity`：簽核 Actor 具體展開為需求載明的角色（櫃檯主任／分館館長／區群總館長／副總館長／總館長），分支條件帶需求中的具體額度門檻（20／50／100／200 點）逐級展開而非抽象成「是否已達最後層級」迴圈回跳，且旅程含查詢／觀察類確認步驟（查審核工作台清單、查審核明細、查權限詳情確認生效或看到駁回原因）並讓查詢類 feature 被 action 節點實際綁定。
- 為何存在: 當前 skill 會把多層簽核壓成泛用「審核人」加迴圈回跳、不含查詢確認步，導致 activity 只剩純流程邏輯、無法當 UAT 測試情境用；此 unit 是 test-case-as-activity 演化的 red-gate 依據。file outcome 由 after/ imply（2 張 .activity、7 張 rule-less .feature、impact-matrix.yml 更新；spec.md 僅允許合法澄清拍板後於澄清紀錄區 append-only 追寫，其餘區段不得變動，after/ 比對忽略澄清區——2026-07-21 使用者拍板放寬）。
- Runner setup contract（2026-07-21 使用者拍板）: before/ 不內嵌任何 skill；runner 建沙盒時除複製 before/ 外，須自 repo-level vendored 副本把執行期依賴 skill（aibdd-core、aibdd-form-activity、clarify、analyze-and-clarify）複製進沙盒 `.agents/skills/` 並建 `.claude/skills -> ../.agents/skills` symlink；所有被搬入沙盒的 skill 一律剝除其 `eval/` 子目錄（oracle isolation，準用 run-eval Rule 5）；target skill 本體仍不進沙盒、由 repo-level live 版經 session 解析執行。after/ 比對忽略注入的依賴目錄。

## Run

### Turn 1 結束: done

Tool calls:
- MUST EXECUTE `.claude/skills/aibdd-core/scripts/cli/resolve_args.py`（以 resolver 綁定 SOP 變數，不得手工猜路徑）
- MUST DELEGATE /aibdd-form-activity 落檔兩張 `.activity`（不得繞過 form skill 徒手寫 DSL）
- MUST NOT 就「目標額度 0 是否送審」向使用者提問（規格已明定必須大於 0，不存在澄清缺口）

Assistant message:
- 1.0: 結案報告逐一列出本批產出的兩張 `.activity`、七張 `.feature` 與 resolved 的 impact，並指向下一步 /aibdd-rules-specify
- 0.7: 有結案報告與產物清單，但漏列部分產物或未指向下一步
- 0.3: 只聲稱完成、無具體產物清單
- 0.0: 未跑完 SOP 即終止，或聲稱完成但檔案未落地
