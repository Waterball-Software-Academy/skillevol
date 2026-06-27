# Granularity Law + Invariants (the quality gate)

A Sketch must pass these BEFORE rendering. Some are checked mechanically by
`scripts/validate_sketch.py`; the rest require judgment and are checked by you (the model).

## The granularity law (the whole point of this skill)

A reader scanning one element should see **one box + 1–2 constraints/hints on it — no more, no
less.** This produces minimal *local* cognitive load while still recording architecture
constraints and impl hints at the right grain.

- Decompose an element only as deep as **"just enough to hang a *different* constraint/hint"**,
  then stop. (`Mutator` does NOT expand into methods — nothing different to hang; `UI` DOES split
  into two panels — each panel carries a distinct responsibility.)
- Physical detail that carries **no** constraint and maps to **no** impl unit → **omit it**.
- Every note is **anchored** to an element/edge/region. No floating prose.

## Invariants G1–G7

| | Invariant | Checked by |
|---|---|---|
| **G1** | **Constraint-bearing decomposition.** Each `Node` is decomposed exactly deep enough to hang ≥1 `Note` or one `impl` binding; never subdivided for visual reasons alone. | **script (partial)** flags any node with no note AND no impl; **you** judge whether depth is *justified* |
| **G2** | **Local load ceiling.** ≤ 3 `Note`s on any single anchor; if more, that element must drill down into its own Sketch. | **script** |
| **G3** | **Anchoring.** Every `Note` has an `anchor` that resolves to a real node/edge/lane/`canvas`. No canvas-level prose pile. | **script** |
| **G4** | **Signal/noise.** Physical detail bearing no constraint and no `impl` is omitted. | **you** (semantic) |
| **G5** | **Both layers present.** A Sketch has ≥1 physical element (nodes/edges) AND ≥1 note. Pure-physical or pure-notes fails. | **script** |
| **G6** | **Single focus.** A Sketch optimizes exactly one `focus`; multi-focus → split into multiple Sketches. | **script (partial)** warns if `focus` is multi-sentence; **you** judge true single-focus |
| **G7** | **Legend consistency.** salience/type colours are uniform across the figure and match `legend`. | **script** |

## How to act on the verdict

1. Run `python3 scripts/validate_sketch.py <plan.yaml>`.
2. **Any hard FAIL → fix the YAML and re-run.** Do not render a failing Sketch.
   - G2 fail → split the overloaded anchor into a drill-down Sketch (`zoom:`), moving notes down.
   - G5 fail → a pure-physical sketch is missing its guidance layer; add the constraints that
     motivated each box. A pure-notes sketch has no anchors to hang on; add the physical layer.
   - G3/G7 fail → fix the anchor / unknown type / legend gap.
3. **G1 WARN (bare node)** → either hang the note/impl that justifies the box, or merge/omit it
   (G4). A box with nothing to say is noise.
4. **G6 WARN (multi-sentence focus)** → if the sketch really serves two concerns, split it
   (one ImplPlan, two Sketches, linked by `zoom:`).
5. **Semantic checks (G4, and the judgment half of G1/G6)** are yours — the script cannot see
   whether a decomposition is *warranted* or a focus is *genuinely* singular. Verify them before
   rendering.
