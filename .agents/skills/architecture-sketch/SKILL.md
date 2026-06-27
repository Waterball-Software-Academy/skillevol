---
name: architecture-sketch
description: >
  Plan + sketch the architecture portion of an implementation plan as a structure-first artifact:
  a strict-YAML "Sketch" ImplPlan (the version-controlled SSOT) plus 1..* hand-drawn SVG diagrams,
  each optimizing one local view. Defines elements at a granularity where each box carries exactly
  1–2 architecture constraints / impl hints (minimal local cognitive load), records a dual layer
  (physical structure + non-physical guidance notes), binds every box to an implementation unit,
  and drills overloaded boxes down into their own diagrams.
  Use whenever planning an implementation that involves designing or communicating architecture —
  the architecture part of any impl/design plan, C4 level C1–C4, or a direct request to sketch /
  diagram a system's structure. Also invocable as /architecture-sketch.
metadata:
  user-invocable: true
  source: project-level
---

# Architecture Sketch

## What this is

When a task requires planning an implementation and that plan involves architecture, **the
architecture part is done with this skill.** It is *structure-first*: you write a structured
**Sketch** definition (strict YAML — reviewable, version-controlled), the structure decides the
content, the content decides the SVG, and only then is the hand-drawn skin applied.

**The granularity law (the whole point):** a reader scanning one element sees **one box + 1–2
constraints/hints on it — no more, no less.** Decompose only as deep as needed to hang a *different*
constraint or impl binding, then stop. This is C4-level-agnostic (works for C1–C4) and always
carries **two layers**: the physical structure *and* the non-physical guidance notes.

**Big picture of this skill's own operation:** `references/how-it-works.svg` (a self-example —
the skill applied to itself; its SSOT is `references/how-it-works.yaml`).

## Output contract

Two products, always:
1. **The Sketch plan** — one `<name>.yaml` ImplPlan (1..* sketches). This is the upstream SSOT.
2. **1..* SVG diagrams** — one per sketch, each optimizing a different local focus.

Never hand-edit an SVG. To change a diagram, change the YAML and re-render.

## Workflow

### 1 · DEFINE — write the Sketch YAML
Read `references/grammar.md` for the exact schema. Then:
- Split the architecture into **sketches by focus** — each sketch makes *one* local thing clear
  (one `focus` sentence). Don't draw everything in one diagram.
- For each box, set `kind`, and **bind it to an implementation unit** (`impl:` — drawing a box *is*
  scheduling an impl unit). Align `impl.kind` with `level` (C2↔module/package, C1↔class/file).
- Decompose with the granularity law: `contains`+`multiplicity:N` for a homogeneous extensible set;
  `panels` for distinct responsibilities; **stop when there's nothing different to hang.**
- Add the **guidance layer** — `notes` anchored to boxes/edges, typed (behavioral-contract /
  design-constraint / impl-directive / scope-bound / reference-exemplar), salience high/normal.
- When a box would carry **>3 notes**, give it `zoom:` to a lower-level sketch and move the detail
  down (multi-diagram drill-down).

Mirror the canonical example in `references/exemplar.md` (the 004-mvp plan; rendered SVGs live in
`specs/004-mvp/`).

### 2 · VALIDATE — pass the gate before rendering
```
python3 scripts/validate_sketch.py <plan>.yaml
```
- **Any FAIL → fix the YAML and re-run. Do not render a failing sketch.**
- Then do the **semantic checks the script can't**: G4 (omit detail carrying no constraint/impl),
  and the judgment half of G1 (is each decomposition *warranted*?) and G6 (is the focus *truly*
  singular?). See `references/invariants.md` for what to do per verdict.

### 3 · RENDER — project each sketch to an SVG
Read `references/skin.md`. Start each SVG from `assets/skin-defs.svg` (shared markers + palette).
- Render the **C2 / top sketch first** as the style anchor (fix SKIN + legend there), then the C1
  drill-downs, all using the *same* skin (G7).
- C1 class diagrams use the UML relation markers in `assets/skin-defs.svg`.
- End every diagram with a Legend strip + a footer: `SSOT = <plan>.yaml；SVG 為投影。`

## References
| File | Read it for |
|---|---|
| `references/grammar.md` | the strict-YAML Sketch schema (nodes/edges/notes/impl/zoom) |
| `references/invariants.md` | the granularity law + G1–G7, and how to act on each verdict |
| `references/skin.md` | the 5-stage pipeline + hand-drawn SKIN / legend / UML conventions |
| `references/exemplar.md` | the canonical worked example (004-mvp) in strict YAML + rendered SVGs |
| `references/how-it-works.yaml` / `.svg` | self-example: a big-picture sketch of how this skill operates |
| `scripts/validate_sketch.py` | mechanical gate (G2/G3/G5/G7 + structure; G1/G6 warnings) |
| `assets/skin-defs.svg` | reusable `<svg>`+`<defs>` (arrow/triangle/diamond markers, palette) |
