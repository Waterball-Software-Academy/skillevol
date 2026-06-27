# skillevol-derive-script

1. 新增 `.agents/skills/skillevol-form-script` 與 `.agents/skills/skillevol-derive-script`。
2. `form-script` 只負責 ScriptFile form；必須獨立成 skill，但不配 `eval/`。
3. `derive-script` 由 `/skillevol-loop` 打造；所有 benchmark 與 oracle 都集中在 derive 層。
4. `derive-script` 的責任是：從目標 skill 的特定 1..* 個 SOP 步驟抽出同職責、可一起自動化的步驟群，落成同層 `scripts/*.py`。
5. ScriptFile 形式固定為 Python 單檔 + PEP 723 metadata。
6. ScriptFile 必須放在該 SOP 所屬層級的 `scripts/`，抽法比照 `rules/` 的 owning-level 規則。
7. 若內容其實是無序規定、固定骨架、或語意判讀子程序，`derive-script` 必須停止並改走對應 mutator。