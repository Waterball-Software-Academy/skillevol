# eval 專有名詞表

本表彙整 skillevol eval 體系中的專有名詞。定義以 `skillevol-form-eval` 為主；runner、judge、loop 等消費端術語一併收錄，方便對照。參考來源一律寫成 `<skill-name>::<路徑>`。

## 結構與 fixture

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| eval | 某 skill 的 golden benchmark 目錄；整組 eval 是 skill 自主迭代的 fitness oracle。 | 驗證 skill 是否做好的評分規則 | `skillevol-form-eval::rules/rule.md` |
| golden benchmark | eval 的別稱；一組可重跑、可評分的測試案例集合。 | 拿來驗收 skill 的標準題庫 | `skillevol-form-eval::rules/rule.md`、`skillevol-form-eval::SKILL.md` |
| fitness oracle | eval 在演化流程中的角色：沒有它，改了 rules / SOP 也分不清變好還是變壞。 | 判斷這次修改到底有沒有變好的依據 | `skillevol-form-eval::rules/rule.md` |
| canonical form | eval 目錄、unit、expect、before/after 等必須遵守的正式結構定義。 | eval 應該長成什麼樣的標準格式 | `skillevol-form-eval::rules/structure.md` |
| form-conformant | eval 結構符合 `skillevol-form-eval` 規範；loop 啟動前提之一。 | 結構有照規定寫好 | `skillevol-loop::rules/oracle-and-loop.md` |
| unit | 一個可獨立跑的測試點。非分支 scenario 自己就是一個 unit；分支 scenario 下每個 variant 是一個 unit。 | 一條可以單獨執行的測試案例 | `skillevol-form-eval::rules/structure.md` |
| scenario | `dev/` 或 `holdout/` 下的一個目錄；可為非分支或分支型。 | 一組相關測試案例的情境資料夾 | `skillevol-form-eval::rules/structure.md` |
| variant | 分支 scenario 下 `variants/<unit-name>/` 的一個分支；一對 `{user.md, after/}` 代表 input→output 的一個點。 | 同一題下的其中一種分支答案版本 | `skillevol-form-eval::rules/structure.md`、`skillevol-form-eval::rules/rule.md` |
| dev | agent 可見的 eval 分區；迭代時對著跑。 | 開發時會一直拿來跑的測試區 | `skillevol-form-eval::rules/structure.md` |
| holdout | agent 不可見的 eval 分區；最終放行 gate 才跑。 | 最後驗收才會跑的隱藏測試區 | `skillevol-form-eval::rules/structure.md` |
| phase | `dev` 或 `holdout`；runner 用於 sandbox 路徑與報告分區。 | 目前是在跑開發測試還是最終驗收 | `skillevol-run-eval::SKILL.md` |
| unit name | unit 的 dirname；格式 `<before情境設定概述>_<after測試結果概述>`。 | 用來看懂這題在測什麼與預期會怎樣的案例名稱 | `skillevol-form-eval::rules/structure.md`、`skillevol-form-eval::rules/rule.md` |
| before-segment | unit name 底線前的段落；描述 before/ 與 prompt 建出的測試局面。 | 案例名稱前半段，描述起始情境 | `skillevol-form-eval::rules/rule.md` |
| after-segment | unit name 底線後的段落；描述跑完後預期觀測到的結果。 | 案例名稱後半段，描述預期結果 | `skillevol-form-eval::rules/rule.md` |
| fixture | eval unit 的靜態輸入檔集合（before/、prompt、expect 等）；runner 執行時不得污染。 | 這題測試原本就準備好的素材包 | `skillevol-run-eval::SKILL.md` |
| droppable input | before/ 放進空資料夾即可成立的 CWD。 | 直接丟進空目錄就能開始跑的起始檔案狀態 | `skillevol-form-eval::rules/rule.md` |
| file outcome | before/ 到 after/ 的 diff；檔案終態的預期結果。 | 跑完後檔案應該變成的樣子 | `skillevol-form-eval::rules/structure.md` |
| file artifact | unit 預期新建、改寫或刪除的檔案產物。 | 這題預期會產生或改掉的檔案 | `skillevol-form-eval::rules/rule.md` |
| expected artifact set | after/ 中必須完整落下的預期檔案集合。 | 跑完後應該看到的完整檔案清單 | `skillevol-form-eval::rules/structure.md` |
| artifact-output-contract | 若 unit 驗 file artifact，after/ 必填且須完整；若不驗 file，須明示 output channel 為 behavior/message only。 | 這題到底是驗檔案產物還是只驗行為的約定 | `skillevol-form-eval::rules/structure.md` |
| output channel | 產出落點：file artifact（走 after/）或 behavior/message only（可省略 after/）。 | 這題主要看檔案結果還是看對話與行為 | `skillevol-form-eval::rules/structure.md`、`skillevol-define-evals::rules/verification-points.md` |
| target-visible input | target subagent 可合法看到的輸入（before/、prompt.md 等）。 | 被測 skill 可以直接看到的題目內容 | `skillevol-form-eval::rules/structure.md` |
| answer-key variants | 同一 before/ + prompt 下，以多份 user.md 各配 after/ 釘住 input→output 對應。 | 用多組不同回答去測 skill 會不會產生對應結果 | `skillevol-form-eval::rules/rule.md` |
| working backward | 先用 after/ 真實快照把答案對結果寫死，再回推 expect 與 user.md。 | 先決定正確結果，再反推題目怎麼設計 | `skillevol-form-eval::rules/rule.md` |

## expect 內容與評分

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| Provenance | expect.md 區段；標明 unit 釘哪條 distinct 行為、為何存在。 | 說明這題到底在守哪個重點 | `skillevol-form-eval::rules/structure.md` |
| distinct 主張 | 一個 unit 只釘一個可被獨立診斷的行為主張。 | 一題只測一件清楚的事 | `skillevol-form-eval::rules/rule.md` |
| 語意 rubric | 用 0.0 / 0.3 / 0.7 / 1.0 四錨點 + 具體 GOOD/BAD 片段打分。 | 用清楚等級描述來打分的標準 | `skillevol-form-eval::rules/rule.md`、`skillevol-form-eval::rules/structure.md` |
| byte-exact | after/ 比對不要求逐 byte 相同；judge 判語意等價即可。 | 不要求每個字元都一樣，只要意思與結果等價就行 | `skillevol-form-eval::rules/rule.md` |
| Cross-turn | 多輪 unit 專用區段；驗 turn 之間的 order、gates、liveness。 | 檢查多輪互動前後順序有沒有對 | `skillevol-form-eval::rules/rule.md` |
| gates | forbidden-before 約束；某事件不得出現在某事件之前。 | 哪些事在某一步之前絕對不能先做 | `skillevol-form-eval::templates/expect.md` |
| liveness | 每個 ASK 後必有 ANSWER、不重複問、run 會終止。 | 互動流程不能卡住也不能一直重問 | `skillevol-form-eval::templates/expect.md` |
| event trace | 互動與工具事件的時間序紀錄；Cross-turn 引用它，不引用 file diff。 | 用來回看整個過程先後順序的紀錄 | `skillevol-form-eval::rules/rule.md` |
| resolution-gate | 需處理缺口、壞輸入或 re-ask 的互動關卡；trap/never 答案才測得到。 | 專門測 skill 遇到資訊不足或壞答案時怎麼處理 | `skillevol-form-eval::rules/rule.md` |
| trap | user.md answer key 中的故意非法/模糊答案；測 re-ask 或 STOP。 | 故意放進去的壞答案 | `skillevol-form-eval::rules/rule.md` |
| Answer key | user.md 區段；`<topic>: <value>`，topic 對齊 expect 的 ASK(topic)。 | 預先準備好的標準回答表 | `skillevol-form-eval::templates/user.md` |
| Fallback | user.md 區段；問到表外 topic 時的預設回覆。 | 題目沒準備到時要怎麼回的預設說法 | `skillevol-form-eval::templates/user.md` |
| 橫切判準 | 多 unit 共用、放在 shared/expect.md 的判準。 | 多題共用的評分標準 | `skillevol-form-eval::rules/rule.md` |
| criteria | 單條可評分判準；judge 對每條給分。 | 每一條實際拿來打分的標準 | `skillevol-eval-judge::SKILL.md` |
| veto 判準 | 任一 MUST 或 veto 失敗即整體 fail 的決定性判準。 | 只要踩到就整題不過的硬規則 | `skillevol-eval-judge::SKILL.md` |
| verdict | 整體 pass / fail / uncertain；寫在 eval-report.md 頂行。 | 最後總評是過、沒過、還是證據不足 | `skillevol-run-eval::SKILL.md` |

## oracle 相關

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| oracle | 廣義：eval 作 fitness judge；狹義：file unit 中 after/ 是 oracle 本體。 | 拿來判斷答案對不對的依據 | `skillevol-form-eval::rules/rule.md` |
| eval oracle | loop 語境：目標 skill 已有可執行、form-conformant 的 eval/。 | 這個 skill 已經有可用來驗收的測試基準 | `skillevol-loop::rules/oracle-and-loop.md` |
| hidden oracle material | 只留給 runner/judge 的評分材料；不得放進 before/、prompt、user.md。 | 只有出題與評分流程知道的隱藏判定資訊 | `skillevol-form-eval::rules/structure.md` |
| Hidden oracle metadata | expect.md 選用區段；hidden oracle material 的結構化子集。 | 給外層流程看的隱藏設定欄位 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| hidden oracle input contract | metadata 區塊宣告的正式輸入：`oracle_style` + `design_variance`。 | 外層判定流程會先讀的一小組正式設定 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| oracle_style | metadata 欄位：`exact-after-single-golden` 或 `runner-only`。 | 這題要用哪種判法 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| exact-after-single-golden | 單一 after/ golden 快照是唯一合法答案。 | 只接受一份固定標準答案 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| runner-only | 不啟動 target；由 outer runner 依 deterministic contract 判 pass/fail。 | 不真的執行被測 skill，直接由外層流程判定 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| design_variance | metadata 欄位：`unique`（單解）或 `multi-valid`（多解）。 | 這題本質上是一解還是多解 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| oracle contract overfit | 題目 multi-valid 但 oracle 只接受單一 golden；preflight 必須 hard-fail。 | 題目明明有多種合理答案，評分卻只認一種 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| legacy path | unit 未宣告 metadata 時，consumer 直接走既有 launched 流程。 | 沒寫新設定時就照舊流程跑 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| golden rationale | judge-only 的 oracle 說明 prose；不屬於 metadata schema。 | 評分端自己看的補充判定說明 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| oracle isolation | target subagent 不得看到 expect、after、rubric、expected verdict 等 hidden oracle。 | 被測 skill 不可以先偷看到答案或評分標準 | `skillevol-run-eval::eval/shared/expect.md` |
| oracle contract preflight | run-eval 在 target launch 前讀 metadata 並決定 launch_decision。 | 正式開跑前先檢查這題能不能這樣判 | `skillevol-run-eval::rules/oracle-contract-preflight.md` |

## runner 與 judge 執行

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| runner | 執行 eval unit 的外層 skill，通常是 `skillevol-run-eval`。 | 負責實際跑這題測試的外層執行者 | `skillevol-run-eval::SKILL.md` |
| consumer | 讀取 hidden oracle metadata 並依決策表行動的 skill（如 run-eval）。 | 會先讀隱藏設定再決定怎麼跑的那層流程 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| fixture author | 撰寫 eval fixture 的人/agent；metadata 只宣告 input contract。 | 設計這題測試素材的人 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| run-owner | 本輪 run workspace 的 owner；決定 `.skillevol/<run-owner>/run-evals/...` 路徑。 | 這次執行工作目錄算是誰的 | `skillevol-run-eval::rules/target-skill-resolution.md` |
| outer-unit | run-eval 被要求執行的 outer eval unit 名稱。 | 外層這次正在跑的那一題 | `skillevol-run-eval::rules/target-skill-resolution.md` |
| target-skill | 本輪用 Task subagent 實際執行的 immediate target skill。 | 這次真正被叫去做事的 skill | `skillevol-run-eval::rules/target-skill-resolution.md` |
| target-unit | target skill 內部被執行的 nested eval unit 名稱。 | 被測 skill 自己裡面對應的那一題 | `skillevol-run-eval::rules/target-skill-resolution.md` |
| nested target | outer unit 的 before/ 內嵌的另一個 skill 的 eval unit。 | 外層題目裡包著的內層被測題目 | `skillevol-run-eval::rules/oracle-contract-preflight.md` |
| outer sandbox | `.skillevol/<run-owner>/run-evals/<phase>/<outer-unit>/`；target 的唯一 CWD。 | 這次測試專用的隔離工作區 | `skillevol-run-eval::SKILL.md` |
| transaction boundary | run-eval 把 fixture、sandbox、target input、judge input 分開的邊界。 | 避免題目、執行區、答案與評分混在一起的隔離邊界 | `skillevol-run-eval::SKILL.md` |
| launch_decision | preflight 結果：`launched`（啟動 target）或 `skipped`（不啟動）。 | 最後決定真的開跑還是直接跳過 | `skillevol-form-eval::rules/hidden-oracle-metadata.md`、`skillevol-run-eval::rules/oracle-contract-preflight.md` |
| preflight_check | consumer 衍生欄位：`passed` / `failed` / `not-applicable`；不可寫進 fixture metadata。 | 開跑前檢查有沒有通過 | `skillevol-form-eval::rules/hidden-oracle-metadata.md` |
| skip_reason | skipped launch 時記錄為何未啟動 target。 | 這題為什麼沒真正開跑的原因 | `skillevol-run-eval::rules/oracle-contract-preflight.md` |
| overfit_risk | consumer 衍生欄位；標記 oracle contract 是否有多解綁單 golden 風險。 | 評分規則是不是太死、太容易誤殺合理答案 | `skillevol-run-eval::eval/shared/expect.md` |
| minimal opening input | target subagent 開場輸入：minimal CWD envelope + prompt.md 原文，無其他包裝。 | 給被測 skill 的最小必要題目內容 | `skillevol-run-eval::rules/target-skill-resolution.md` |
| opening_input_shape | 開場輸入形狀標記：`minimal-cwd-and-prompt-only` 或 `not-applicable-no-launch`。 | 記錄開場輸入是不是乾淨且符合規定 | `skillevol-run-eval::eval/shared/expect.md` |
| answer-only | responder resume Task 時只放該次 answer 字面值，無 CWD/runner 包裝。 | 補答時只回答案本身，不夾帶其他提示 | `skillevol-run-eval::SKILL.md` |
| target subagent | 透過 Task 黑箱執行 target skill 的子 agent。 | 真正去執行被測 skill 的子代理 | `skillevol-run-eval::SKILL.md` |
| responder | run-eval 主 agent 依 user.md 在互動斷點代答的角色。 | 在互動題裡扮演使用者回答的人 | `skillevol-run-eval::rules/responder-policy.md` |
| observation.md | runner 寫出的觀測 artifact；含 provenance、tool calls、終態 fs 等。 | 這次實際跑了什麼的觀察紀錄 | `skillevol-run-eval::SKILL.md` |
| eval-report.md | runner 寫出的評分報告；含 verdict、criteria 分數、違規定位。 | 這題最後怎麼評分的報告 | `skillevol-run-eval::SKILL.md` |
| judge-input.md | 正規化後交給 eval-judge 的 payload。 | 整理好後送去評分器的資料包 | `skillevol-run-eval::SKILL.md` |
| judge worklist | eval-judge 整理出的待評判準清單。 | 評分器要逐條檢查的清單 | `skillevol-eval-judge::rules/criteria-normalization.md` |
| missing evidence | 觀測或 fixture 缺資料，無法完成判定；不得捏造。 | 因為缺資料而沒辦法下定論的部分 | `skillevol-run-eval::SKILL.md` |
| leaked_oracle_material | target 在未經 responder 時產出本應由 user.md 提供的答案；suspected 時不得 pass。 | 被測 skill 似乎偷看到了本來不該知道的答案 | `skillevol-run-eval::SKILL.md` |
| deterministic runner artifacts | skip-launch unit 時 outer sandbox 只保留 runner 自身產物（observation、report 等）。 | 跳過不跑時，外層流程自己會留下的固定輸出 | `skillevol-run-eval::rules/oracle-contract-preflight.md` |
| sandbox_oracle_stripped | observation 欄位；標記 sandbox 內 target eval oracle 是否已剝離。 | 記錄工作區裡有沒有先把答案相關資料清乾淨 | `skillevol-run-eval::eval/shared/expect.md` |
| copied_target_before | skip-launch 時為 `no`；表示未複製 nested target before/。 | 記錄有沒有把內層題目的起始檔案搬進工作區 | `skillevol-run-eval::rules/oracle-contract-preflight.md` |

## benchmark 與 define-evals

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| skillevol-run-benchmark | 跑整組 eval、排程 dev/holdout、彙總 pass rate 的 skill。 | 負責把整套測試全部跑完並整理總成績的流程 | `skillevol-run-benchmark::SKILL.md` |
| pass rate | benchmark 彙總的通過比例。 | 整體通過率 | `skillevol-run-benchmark::SKILL.md` |
| benchmark report | 整組 eval 的彙總報告；含逐 unit 結果與失敗定位。 | 整套測試跑完後的總報告 | `skillevol-run-benchmark::SKILL.md` |
| final gate | 最終放行時才跑 holdout 的 gate。 | 最後要不要放行前的總驗收關卡 | `skillevol-run-benchmark::SKILL.md` |
| verification point | define-evals 中與使用者逐一對齊的單一假設確認點。 | 先停下來跟使用者確認的一個關鍵假設 | `skillevol-define-evals::rules/verification-points.md` |
| working-plan | define-evals 協作期的 `.skillevol/<target>/define-evals/working-plan.md`。 | 討論怎麼設計這套測試時的工作草稿 | `skillevol-define-evals::templates/working-plan.template.md` |
| unit naming contract | 多 subcommand skill 須先對齊的 dirname 前綴規則。 | 測試案例名稱要怎麼命名的約定 | `skillevol-define-evals::rules/verification-points.md` |

## loop 演化

| 專有名詞 | 定義 | 白話文 | 參考來源 |
| --- | --- | --- | --- |
| skillevol-loop | 以 eval 為 oracle 反覆演化目標 skill 的 orchestrator。 | 一邊看測試結果一邊反覆改進 skill 的總流程 | `skillevol-loop::SKILL.md` |
| eval-oracle RCA | loop 啟動前用白話分析：現有 eval 缺什麼、為何測不出、下一步補哪條 dev。 | 先用白話找出為什麼現在的測試抓不到問題 | `skillevol-loop::rules/oracle-and-loop.md` |
| eval adequacy gate | 檢查現有 eval 是否覆蓋使用者此次 desired state 的關卡。 | 先確認現有測試有沒有真的涵蓋這次想修的事 | `skillevol-loop::rules/oracle-and-loop.md` |
| red gate | 補 failing test 後，確認新 eval 真的會 fail 在舊 skill 上的關卡。 | 先確認新測試真的抓得到舊問題的關卡 | `skillevol-loop::rules/oracle-and-loop.md` |
| failing test | 針對已知缺陷寫的、預期在修 skill 前會 fail 的 dev unit。 | 專門拿來證明現在真的還有 bug 的測試 | `skillevol-loop::SKILL.md` |
| dev benchmark gate | loop 中跑 dev 全綠才繼續的關卡。 | 要先把開發測試全過才能往下走的關卡 | `skillevol-loop::SKILL.md` |
| desired state | 使用者此次想讓 skill 達成的目標狀態；eval 必須能覆蓋。 | 使用者這次真正想要達到的結果 | `skillevol-loop::rules/oracle-and-loop.md` |
