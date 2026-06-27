# Render pipeline + SKIN / Legend conventions

## The 5-stage pipeline (structure → content → SVG → hand-drawn skin)

```
1. DEFINE   : produce the Sketch YAML (grammar.md)        ← the reviewable, version-controlled product
2. VALIDATE : python3 scripts/validate_sketch.py <yaml>   ← pass G1–G7 (invariants.md)
3. CONTENT  : resolve into concrete elements + anchored note list
4. LAYOUT   : place geometry (lanes, containers, stacks, edge routing) → clean SVG
5. SKIN     : apply the hand-drawn style below (palette, jittered tone, ★/💡 markers)
```

**The YAML is the SSOT; the SVG is a projection.** To change a diagram, change the YAML and
re-render — never hand-edit the SVG.

## Shared SKIN (every sketch in a plan uses the SAME skin — G7)

| element | convention |
|---|---|
| canvas | warm paper `#fbfaf7` |
| font | monospaced stack — `'JetBrains Mono','SF Mono','Menlo','Consolas','PingFang TC','Microsoft JhengHei',monospace` (NOT a script/handwriting face) |
| Node box | rounded rect, dark-ink stroke `#2b2b2b`; `«stereotype»` at top (italic `#9b8bd0`); icon at left |
| container / lane | dashed frame + column-header label |
| homogeneous stack | `multiplicity: N` → three offset card edges (= "many of one type") |
| impl binding | a small chip `▸ <unit>` (`#f0ede6` fill) inside the box |
| edge | solid arrow + `verb`; `payload` labelled on the line |
| edge kinds | `call`/`dataflow` = solid ink; `injection` = **red dashed** `#c0392b`; `render` = thin grey `#7a756c` |
| note box | filled by type colour (below), thin matching stroke, dashed leader line to its anchor |
| markers | `★` = high salience; `💡` = impl hint |

Note `type` → colour (fill / stroke / text):
| type | fill | stroke | text |
|---|---|---|---|
| design-constraint | `#f9e7e5` | `#c0392b` | `#922b21` |
| impl-directive 💡 | `#fbeee2` | `#d35400` | `#a04000` |
| behavioral-contract | `#eaf1f8` | `#2471a3` | `#1b4f72` |
| scope-bound | `#f1e8f6` | `#7d3c98` | `#5b2c6f` |
| reference-exemplar | `#e6f3ee` | `#117a65` | `#0b5345` |

## SVG scaffold

Start from `assets/skin-defs.svg` — it carries the reusable `<defs>` (arrow / triangle / diamond /
inverse markers) and the palette as comments. Copy its `<svg>` opening + `<defs>` into each diagram.

Every diagram ends with a **Legend** strip (physical-layer glossary + the type→colour swatches +
`★ = high salience`) and a footer line: `SSOT = <plan>.yaml；本 SVG 為投影。改圖請改 YAML 後重渲染。`

## C1 class diagrams use UML relation lines

When `level: C1` and nodes are classes, render UML relations (these are expressed in node
`contains`/edges or in prose within the sketch, not new grammar):

- solid line + **hollow triangle** = inheritance / realize (`..|>`)
- **filled diamond** = composition; **hollow diamond** = aggregation; line endpoints carry multiplicity
- dashed arrow = dependency (`..>`)
- a class box has up to three compartments: name (+ `«interface»`/`«abstract»`) / attributes / methods

The markers for all of the above are predefined in `assets/skin-defs.svg`.

## Worked reference

`references/exemplar.md` holds the canonical worked example (the 004-mvp C2 sketch in strict YAML)
and points to the rendered `.svg` files to imitate for layout and skin.
