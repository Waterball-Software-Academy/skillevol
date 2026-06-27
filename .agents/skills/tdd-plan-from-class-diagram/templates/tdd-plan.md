# Guideline

渲染此範本產出 `docs/tdd/<feature-scope>.tdd-plan.md`。

- 保留 `# Template` 區塊的章節順序；可依實際情況增減 slices，但不要刪掉每個 slice 的欄位。
- `${SOURCE_DIAGRAM}`：填入 Mermaid 來源路徑；若使用者直接貼 inline Mermaid，填 `inline-mermaid`。
- `${PLAN_GOAL}`：用一句話描述此輪 TDD 要保護的可觀察行為，不重抄整份需求。
- `${PRIMARY_SEAMS}`：列出最先下手的 public seam、協調者與外部邊界。
- `${SLICE_BLOCKS}`：依「最小可觀察行為 -> 協調流程 -> 外部邊界 contract -> 失敗/回歸」排序，渲染為 3-6 個 `### Slice N — ...` 區塊。
- 若類別圖含 interface 與具名 impl，先把 interface/fake/spy 或 contract test 排進 slices，再決定何時碰 concrete impl。
- `${REGRESSION_GATES}`：只列此輪施工前應保留的必要回歸與守門，不預寫無關 implementation detail。

# Template

# TDD Plan — ${FEATURE_SCOPE}

## Scope

- source diagram: ${SOURCE_DIAGRAM}
- goal: ${PLAN_GOAL}
- out of scope for this cycle: ${OUT_OF_SCOPE}

## Test Strategy

- primary seams:
${PRIMARY_SEAMS}
- doubles strategy: ${DOUBLES_STRATEGY}

## Slice Order

${SLICE_BLOCKS}

## Regression Gate

${REGRESSION_GATES}

## Open Questions

${OPEN_QUESTIONS}
