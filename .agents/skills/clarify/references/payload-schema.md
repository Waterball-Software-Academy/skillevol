# Payload Schema（Core）

Planner → clarify 的批次疑問契約：本檔只定義所有 route 共用的欄位與全域 invariant。

完整契約必須再加上選定 route 的專章：

1. SpecFormula-Fork：`../sub-skill/SpecFormula-Fork/references/payload-schema.md`
2. AskUserQuestion：`../sub-skill/AskUserQuestion/references/payload-schema.md`

## Core Schema

```yaml
questions:
  - id: Q1
    context: <單句上下文>
    question: <單句提問>
    options:                        # 2 ≤ len ≤ 4
      - id: <option-id>
        label: <選項名>
        impact: <單句影響>
    recommendation: <option id>
    recommendation_rationale: <單句>
    priority_score: <int>
    dependencies: [<Qid>, ...]      # 可選
```

路由專屬欄位（例如錨點、`OTHER` 選項）一律只定義在對應 sub-skill 的 `references/payload-schema.md`。

## Core Required Fields

每題至少必填：`id`、`context`、`question`、`options`、`recommendation`、`recommendation_rationale`、`priority_score`。

全域限制：

1. `2 <= len(options) <= 4`
2. `recommendation` 必須指向既有 option id
3. 若有 `dependencies`，不得形成循環

缺項或違反上述全域規則時，clarify 回傳：

```yaml
status: incomplete
missing:
  - "<field or invariant>"
```

## Dependency Ordering

若 `Q_i.dependencies = [Q_j]`，caller 應保證 `Q_j` 不晚於 `Q_i`；clarify 依 `references/question-priority.md` 排序，若發現循環依賴則回傳：

```yaml
status: invalid_dependencies
cycle_or_missing:
  - "<cycle path>"
```
