# Grading rubric (per-invariant pass/fail)

Grade ONE executor artifact (Sketch YAML + SVG list) against a case. The grader sees this file +
the case's 埋藏陷阱 / 預期 / 不應出現 only — never the executor's reasoning.

## Step 0 — mechanical gate
Run `python3 ../scripts/validate_sketch.py <output>.yaml`. Record FAIL/WARN lines. A mechanical
FAIL forces the matching invariant below to **fail**.

## Per-invariant criteria

| Inv | Pass iff | Mainly judged by |
|---|---|---|
| **G1** | every box carries ≥1 note or an impl binding, **and** no box is decomposed deeper than needed to hang a *different* note/impl (e.g. a repository/class is NOT exploded into CRUD methods) | grader (script flags bare boxes) |
| **G2** | no anchor carries >3 notes; overloaded concerns were pushed into a `zoom:` drill-down sketch | script |
| **G3** | every note has a resolving anchor; no canvas-level prose dump | script |
| **G4** | physical detail that carries no constraint and maps to no impl unit was omitted (no decorative boxes) | grader |
| **G5** | the sketch has BOTH a physical layer (nodes/edges) and ≥1 guidance note | script |
| **G6** | each sketch optimizes exactly one `focus`; a second concern was split into another sketch, not crammed in | grader (script warns on multi-sentence focus) |
| **G7** | note type→colour and salience markers are uniform and match the legend across all of the plan's SVGs | script + grader (SVG visual) |

## Value check (the granularity law itself)
- [ ] Scanning any single box surfaces **1–2** constraints/hints — not zero (noise), not a wall.
- [ ] Both layers are genuinely present and useful (not notes bolted on as an afterthought).
- [ ] Every box is bound to a real impl unit (`impl:`), so the diagram doubles as an impl schedule.
- [ ] Multi-diagram drill-down used where a single view would overload.

## Buried-trap check
For each trap listed in the case's **埋藏陷阱**, mark **avoided / tripped**. Any tripped trap =
case fail (and note which invariant it maps to).

## Scoring sheet (copy per case)
```
case: NN-<slug>
mechanical gate: PASS | FAIL (codes: ...)
G1 _  G2 _  G3 _  G4 _  G5 _  G6 _  G7 _      (✓/✗)
value check: _/4
traps: [avoided|tripped] x ...
CASE: PASS | FAIL
notes: ...
```

## Roll-up
Across all cases, report **pass-rate per invariant** (e.g. `G1 4/5`) and overall case pass-rate.
The weakest invariant column is the next thing to fix in the skill.
