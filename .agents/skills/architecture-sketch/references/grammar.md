# Sketch Grammar — strict YAML schema (SSOT)

The DEFINE artifact is **strict YAML**. It is the single source of truth; every SVG is a
projection of it. Edit the YAML and re-render — never hand-edit an SVG.

A plan file contains an **ImplPlan**: one or more `Sketch`es, each optimizing a different local
focus (§ multi-diagram). The validator (`scripts/validate_sketch.py`) parses this exact shape.

## Top-level

```yaml
plan: "<plan title>"          # optional
sketches:                     # 1..* — each is one diagram
  - <Sketch>
```

A file MAY instead be a single bare `<Sketch>` (no `plan:`/`sketches:` wrapper) — the validator
accepts both, but **prefer the `sketches:` list** so drill-down (`zoom:`) can resolve in one file.

## Sketch

```yaml
- id: c2-modules              # REQUIRED, unique; also the SVG file stem (c2-modules.svg)
  level: C2                   # REQUIRED ∈ {C1,C2,C3,C4} — declared layer, independent of content
  focus: "<one sentence>"     # REQUIRED — the single local concern THIS diagram optimizes (G6)
  lanes:                      # optional — architectural-layer swimlanes
    - {id: L1, label: "程式底層"}
  nodes:    [ <Node>, ... ]   # REQUIRED (≥1)
  edges:    [ <Edge>, ... ]   # optional
  notes:    [ <Note>, ... ]   # REQUIRED (≥1) — the guidance layer (G5)
  legend:   <Legend>          # optional override; omit to use the standard SKIN legend
```

## Node — `⟨id, label, kind, lane?, stereotype?, icon?, contains?, panels?, multiplicity?, impl?, zoom?⟩`

```yaml
- id: SDK                     # REQUIRED, unique within the sketch
  label: "Claude Code SDK"    # REQUIRED
  kind: external              # REQUIRED ∈ {component, container, external, view, datastore, actor}
  lane: L1                    # optional — must match a lanes[].id
  stereotype: "3rd"           # optional — rendered as «3rd» (source/nature tag)
  icon: "⌨"                   # optional — one semantic glyph
  multiplicity: 1             # optional ∈ {1, N}; N → drawn as a stack (a plural/extensible set)
  contains: ["A", "B"]        # optional — HOMOGENEOUS children (names, or nested Node objects)
  panels:                     # optional — HETEROGENEOUS internal facets (distinct responsibilities)
    - {label: "💬 與 AI 對話", icon: "💬"}
  impl:                       # optional — bind this box to one implementation unit (see below)
    unit: "adapters/claude_code_sdk"
    kind: package             # ∈ {package, module, class, file, service}
    plan: "<impl-requirement summary>"   # optional prose
  zoom: c1-orchestration      # optional — id of another Sketch this box drills into (see Multi-diagram)
```

`contains` vs `panels`: a set of the **same** kind that may grow → `contains` + `multiplicity: N`
(stack). A box split into **different** responsibilities → `panels`. Decompose only as deep as
needed to hang a different note/impl (granularity law, see invariants).

## Edge — `⟨from, to, verb, payload?, kind⟩`

```yaml
- id: e1                      # optional — lets a note anchor to this edge by id
  from: M1                    # REQUIRED — a node id
  to: SDK                     # REQUIRED — a node id
  verb: "呼叫 claude -p"       # REQUIRED — the action ("呼叫","渲染","apply"…)
  payload: "system+user prompt"   # optional — data flowing on the edge
  kind: call                  # REQUIRED ∈ {call, dataflow, injection, render}
```

## Note — `⟨anchor, type, salience, text⟩` (the guidance layer — non-physical)

```yaml
- anchor: SDK                 # REQUIRED — NodeId | LaneId | "canvas" | EdgeId | "FROM->TO"
  type: design-constraint     # REQUIRED ∈ {behavioral-contract, design-constraint,
                              #             scope-bound, reference-exemplar, impl-directive}
  salience: high              # optional ∈ {high, normal} (default normal) → colour/marker weight
  text: "<prose>"             # REQUIRED
```

Edge anchors: use the edge's `id`, or the literal `"FROM->TO"` (ASCII `->`, e.g. `"M2->M3"`).
The validator resolves both. Every note MUST resolve to an existing anchor (G3).

Note `type` → meaning:
| type | what it records |
|---|---|
| `behavioral-contract` | how a thing must behave / be called (contract, protocol) |
| `design-constraint` | a hard architectural constraint (e.g. OCP, vendor-swap) |
| `impl-directive` | a packaging/cohesion implementation instruction |
| `scope-bound` | a version/scope boundary (e.g. "MVP-out", deferred) |
| `reference-exemplar` | an external example to imitate (e.g. "like pencil.dev …") |

## Impl binding — `⟨unit, kind, plan?⟩` (physical element ↔ implementation plan)

Drawing a box **is** scheduling an implementation unit. `kind` aligns with `level`:
C2 ↔ `package`/`module`; C1 ↔ `class`/`file`.

## Multi-diagram (one ImplPlan = many Sketches)

- A node with `impl.kind ∈ {module, package}` MAY declare `zoom: <sketch-id>` to **drill down**
  into its own lower-level Sketch (e.g. a C2 module box → its own C1 class diagram).
- **Continuity:** the upper box's `id` is the root scope of the lower Sketch; align across
  diagrams by `Node.id`.
- Don't draw everything in one diagram — **each Sketch makes exactly one local thing clear** (G6).

## Legend (optional override)

```yaml
legend:
  design-constraint:    "#c0392b"
  impl-directive:       "#d35400"
  behavioral-contract:  "#2471a3"
  scope-bound:          "#7d3c98"
  reference-exemplar:   "#117a65"
```

Omit `legend` to inherit the standard palette in `references/skin.md`. If declared, it MUST cover
every note `type` used in the sketch (G7).
