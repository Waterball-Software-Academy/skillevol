# architecture-sketch — blind rubric eval

Tests whether the skill reliably produces the **right element granularity** (one box + 1–2
constraints/hints), a **dual layer** (physical + guidance), correct **impl binding**, and proper
**drill-down** — on held-out impl-planning prompts the skill has never seen (NOT the 004-mvp /
abathur domain).

## Design: blind, rubric-scored

- **Executor is blind to the rubric.** It receives *only* the case's **需求原文** (an
  implementation-planning prompt) and is told to use the `architecture-sketch` skill. It must NOT
  see the case's 埋藏陷阱 / 預期 / 不應出現 sections.
- **Grader is blind to the executor's reasoning.** It receives the produced artifact (the Sketch
  YAML + SVG list) + `rubric.md` + the case's expected/trap sections, and scores **per invariant
  G1–G7 pass/fail** plus the case's buried traps. Nothing else.

## How to run

For each `cases/NN-*.md`:

1. **Run the mechanical gate first** on the executor's YAML:
   `python3 ../scripts/validate_sketch.py <output>.yaml` — any FAIL is an automatic G-fail for the
   covered invariants (G2/G3/G5/G7 + structure).
2. **Executor** (blind): give only the **需求原文** to a fresh agent + "use the architecture-sketch
   skill; output the Sketch YAML and the SVG(s)."
3. **Grader** (blind): give the artifact + `rubric.md` + the case's 埋藏陷阱 / 預期 / 不應出現.
   Score each invariant and each buried trap. Fill the scoring sheet in `rubric.md`.

Can be driven manually, or with the Agent tool: one executor agent + one grader agent per case
(the grader must be a *separate* agent so it never sees the executor's chain of thought).

## Pass criteria

A case **passes** iff: all hard invariants (G2, G3, G5, G7, structural) pass **and** no buried trap
is tripped **and** the model-judged invariants (G1, G4, G6) pass per the grader. Report the
**pass-rate per invariant** across all cases — that localizes which granularity behavior is weak.

## Cases (held-out domains, none overlapping 004-mvp)

| # | File | Primary traps |
|---|---|---|
| 01 | `cases/01-url-shortener.md` | over-decompose repo into CRUD (G1/G4); rate-limit as note not box; analytics scope-out |
| 02 | `cases/02-realtime-chat.md` | dual-layer (G5); at-least-once contract; no-history scope-bound; pluggable transport (OCP) |
| 03 | `cases/03-etl-ingestion.md` | N source connectors as `multiplicity:N` + OCP; idempotent load; transform box overload → drill-down (G2) |
| 04 | `cases/04-checkout-payments.md` | «3rd» PSP + vendor-swap design-constraint; idempotency-key contract; refunds scope-out; reference-exemplar |
| 05 | `cases/05-browser-ext-sync.md` | two lanes; content-script vs worker as `panels`; pure-physical temptation (G5); single-focus split (G6) |
