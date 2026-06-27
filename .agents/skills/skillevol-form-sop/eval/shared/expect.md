# shared expect — skillevol-form-sop 橫切判準

所有 scenario 的 `expect.md` 都繼承本檔。這裡放跨情境共用的期望；個別 scenario 只補本情境特有的部分，衝突時以個別 scenario 為準。

評分一律用 spec-by-example：語意維度給 0.0 / 0.3 / 0.7 / 1.0 錨點掛具體片段，judge 與人工對同一把尺打分；決定性 invariant 交腳本驗。

## 被測 skill 與通道

- 被測：`skillevol-form-sop`（Skill SOP 部位的 form 規範 skill）。
- inputs：`before/` 即 CWD 的 fs snapshot，加上 `prompt.md` 一句 context-free user prompt。
- outputs 三通道：Tool calls、Assistant message、File diff（`before/` 到跑完 fs 的整體差異）。

## File diff（橫切）

決定性 invariant：
- 結果必須只修改 prompt 指定的 SOP 目標範圍；不得順手重寫 frontmatter、Purpose 或無關 skill package。
- 若 before/ 已有既定 frontmatter 與 Purpose，after/ 必須保留其語意，只讓 `# SOP` 從缺失或不足狀態變成符合任務的 SOP。
- SOP 必須直接呈現 control-flow form 的能力；不得用一串 delegate、skip 或線性 checklist 取代 gate、branch、loop 與 exit condition。
- control-flow 能力應與局部 SOP form 共存：頂層步驟仍要可執行，subflow 與 rules 不混雜，delegate 只作為 action，不作為控制流程骨架。

語意 rubric（橫切）：
- 1.0：SOP 先呈現任務的 control-flow type，並對 gated flow 或 loop orchestrator 寫出 gate outcome、branch target、loop invariant、back edge、exit condition；局部步驟句型仍清楚。
- 0.7：SOP 有 gate / loop 概念，但只涵蓋部分狀態轉移，例如有 gate outcome，沒有 back edge 或 exit condition。
- 0.3：SOP 只補一句「流程要清楚」或「避免 delegate 太多」，沒有可驗收的 control-flow 結構。
- 0.0：SOP 仍只是 read/think/write/delegate 線性清單，沒有處理 gated / loop flow。

## Assistant message（橫切）

語意 rubric：
- 1.0：清楚說明已依 prompt 補出具控制流程的 SOP，並點出 gate、branch、loop 或 exit condition 的關鍵改動。
- 0.7：說明已補 SOP 且提到 control-flow，但缺少具體 gate 或 back edge 描述。
- 0.3：只說已更新 SOP，沒有說明控制流程品質。
- 0.0：把任務當成一般 create-skill 或 checklist 撰寫，沒有回應 SOP control-flow 要求。
