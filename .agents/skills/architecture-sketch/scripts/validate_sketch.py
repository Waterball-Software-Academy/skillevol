#!/usr/bin/env python3
"""
validate_sketch.py — mechanical gate for a Sketch ImplPlan (architecture-sketch skill).

Checks the structural integrity of the strict-YAML Sketch grammar and the mechanically-decidable
invariants G2, G3, G5, G7 (plus partial G1/G6 as warnings). Semantic invariants (G4, and the
judgment half of G1/G6) are left to the model — see references/invariants.md.

Usage:
    python3 validate_sketch.py <plan.yaml> [<plan2.yaml> ...]

Exit code 0 = all hard checks pass; 1 = at least one FAIL (do not render); 2 = bad input.
"""

import sys

try:
    import yaml
except ImportError:
    sys.stderr.write("ERROR: PyYAML required. Install with: pip install pyyaml\n")
    sys.exit(2)

LEVELS       = {"C1", "C2", "C3", "C4"}
NODE_KINDS   = {"component", "container", "external", "view", "datastore", "actor"}
IMPL_KINDS   = {"package", "module", "class", "file", "service"}
EDGE_KINDS   = {"call", "dataflow", "injection", "render"}
NOTE_TYPES   = {"behavioral-contract", "design-constraint", "scope-bound",
                "reference-exemplar", "impl-directive"}
SALIENCE     = {"high", "normal"}
SENT_ENDS    = "。．.!?！？；;"


def load_sketches(path):
    """Return (plan_title, [sketch, ...]). Accepts {plan,sketches}, bare sketch, or a list."""
    with open(path, "r", encoding="utf-8") as f:
        doc = yaml.safe_load(f)
    if doc is None:
        return None, []
    if isinstance(doc, dict) and "sketches" in doc:
        return doc.get("plan"), doc.get("sketches") or []
    if isinstance(doc, list):
        return None, doc
    if isinstance(doc, dict):
        return None, [doc]
    raise ValueError("top-level YAML must be a mapping or a list")


def norm_edge(s):
    return str(s).replace("→", "->").replace(" ", "")


class Report:
    def __init__(self):
        self.fails = []
        self.warns = []

    def fail(self, sketch_id, code, msg):
        self.fails.append((sketch_id, code, msg))

    def warn(self, sketch_id, code, msg):
        self.warns.append((sketch_id, code, msg))


def validate_sketch(sk, all_sketch_ids, rep):
    sid = sk.get("id", "<no-id>") if isinstance(sk, dict) else "<not-a-mapping>"
    if not isinstance(sk, dict):
        rep.fail(sid, "STRUCT", "sketch is not a mapping")
        return

    # --- required scalar fields ---
    if not sk.get("id"):
        rep.fail(sid, "STRUCT", "missing required field: id")
    if sk.get("level") not in LEVELS:
        rep.fail(sid, "STRUCT", f"level must be one of {sorted(LEVELS)}, got {sk.get('level')!r}")
    focus = sk.get("focus")
    if not focus or not str(focus).strip():
        rep.fail(sid, "STRUCT", "missing required field: focus (one sentence)")

    nodes = sk.get("nodes") or []
    edges = sk.get("edges") or []
    notes = sk.get("notes") or []
    lanes = sk.get("lanes") or []

    if not nodes:
        rep.fail(sid, "STRUCT", "a sketch needs >=1 node")

    # --- collect ids ---
    lane_ids = {l.get("id") for l in lanes if isinstance(l, dict)}
    node_ids = []
    for n in nodes:
        if not isinstance(n, dict):
            rep.fail(sid, "STRUCT", f"node is not a mapping: {n!r}")
            continue
        node_ids.append(n.get("id"))
    dup = {i for i in node_ids if node_ids.count(i) > 1}
    if dup:
        rep.fail(sid, "STRUCT", f"duplicate node ids: {sorted(dup)}")
    node_id_set = set(node_ids)

    edge_ids, edge_pairs = set(), set()
    for e in edges:
        if not isinstance(e, dict):
            rep.fail(sid, "STRUCT", f"edge is not a mapping: {e!r}")
            continue
        if e.get("id"):
            edge_ids.add(str(e["id"]))
        frm, to = e.get("from"), e.get("to")
        if frm not in node_id_set:
            rep.fail(sid, "STRUCT", f"edge.from {frm!r} is not a node id")
        if to not in node_id_set:
            rep.fail(sid, "STRUCT", f"edge.to {to!r} is not a node id")
        if e.get("kind") not in EDGE_KINDS:
            rep.fail(sid, "STRUCT", f"edge {frm}->{to} kind must be one of {sorted(EDGE_KINDS)}")
        if not e.get("verb"):
            rep.fail(sid, "STRUCT", f"edge {frm}->{to} missing verb")
        edge_pairs.add(norm_edge(f"{frm}->{to}"))

    # --- per-node checks (kind, lane, impl, multiplicity, zoom, G1) ---
    for n in nodes:
        if not isinstance(n, dict):
            continue
        nid = n.get("id")
        if not nid:
            rep.fail(sid, "STRUCT", "node missing id")
        if n.get("kind") not in NODE_KINDS:
            rep.fail(sid, "STRUCT", f"node {nid!r} kind must be one of {sorted(NODE_KINDS)}")
        if "lane" in n and n["lane"] not in lane_ids:
            rep.fail(sid, "STRUCT", f"node {nid!r} lane {n['lane']!r} not in declared lanes")
        if "multiplicity" in n and str(n["multiplicity"]).upper() not in {"1", "N"}:
            rep.fail(sid, "STRUCT", f"node {nid!r} multiplicity must be 1 or N")
        impl = n.get("impl")
        if impl is not None:
            if not isinstance(impl, dict) or "unit" not in impl:
                rep.fail(sid, "STRUCT", f"node {nid!r} impl needs a 'unit'")
            elif impl.get("kind") not in IMPL_KINDS:
                rep.fail(sid, "STRUCT", f"node {nid!r} impl.kind must be one of {sorted(IMPL_KINDS)}")
        if "zoom" in n and all_sketch_ids is not None and n["zoom"] not in all_sketch_ids:
            rep.warn(sid, "STRUCT", f"node {nid!r} zoom {n['zoom']!r} not in this file "
                                    "(ok if the drill-down sketch lives in a sibling plan file)")

    # --- note checks + anchor resolution (G3) + per-anchor count (G2) ---
    valid_anchors = node_id_set | lane_ids | edge_ids | {"canvas"}
    per_anchor = {}
    used_types = set()
    for nt in notes:
        if not isinstance(nt, dict):
            rep.fail(sid, "STRUCT", f"note is not a mapping: {nt!r}")
            continue
        anchor = nt.get("anchor")
        if anchor is None:
            rep.fail(sid, "G3", "note has no anchor")
            continue
        a = str(anchor)
        resolved = a in valid_anchors or norm_edge(a) in edge_pairs
        if not resolved:
            rep.fail(sid, "G3", f"note anchor {anchor!r} resolves to nothing")
        per_anchor[a] = per_anchor.get(a, 0) + 1
        if nt.get("type") not in NOTE_TYPES:
            rep.fail(sid, "G7", f"note type {nt.get('type')!r} not a known type {sorted(NOTE_TYPES)}")
        else:
            used_types.add(nt["type"])
        if "salience" in nt and nt["salience"] not in SALIENCE:
            rep.fail(sid, "STRUCT", f"note salience must be high|normal, got {nt['salience']!r}")
        if not nt.get("text"):
            rep.fail(sid, "G3", f"note on {anchor!r} has no text")

    # G2: local load ceiling
    for a, c in per_anchor.items():
        if c > 3:
            rep.fail(sid, "G2", f"anchor {a!r} carries {c} notes (>3); drill it down into its own sketch")

    # G5: both layers present
    if not (nodes or edges):
        rep.fail(sid, "G5", "no physical layer (nodes/edges)")
    if not notes:
        rep.fail(sid, "G5", "no guidance layer (notes) — a pure-physical sketch is incomplete")

    # G7: declared legend must cover every used type
    legend = sk.get("legend")
    if isinstance(legend, dict):
        missing = used_types - set(legend.keys())
        if missing:
            rep.fail(sid, "G7", f"declared legend missing colours for: {sorted(missing)}")

    # G1 (partial, WARN): every node should carry a note or an impl
    noted_anchors = set(per_anchor.keys())
    for n in nodes:
        if not isinstance(n, dict):
            continue
        nid = n.get("id")
        if nid not in noted_anchors and not n.get("impl"):
            rep.warn(sid, "G1", f"node {nid!r} carries no note and no impl — justify the box or omit it (G4)")

    # G6 (partial, WARN): focus should read as one sentence
    if focus:
        ends = sum(str(focus).count(c) for c in SENT_ENDS)
        if ends > 1:
            rep.warn(sid, "G6", f"focus looks multi-sentence ({ends} terminators) — split the sketch if it serves >1 concern")


def main(argv):
    if len(argv) < 2:
        sys.stderr.write(__doc__)
        return 2
    overall_fail = False
    for path in argv[1:]:
        rep = Report()
        try:
            plan, sketches = load_sketches(path)
        except Exception as e:  # noqa: BLE001
            print(f"✗ {path}: cannot parse — {e}")
            overall_fail = True
            continue
        ids = {s.get("id") for s in sketches if isinstance(s, dict)}
        if not sketches:
            print(f"✗ {path}: no sketches found")
            overall_fail = True
            continue
        for sk in sketches:
            validate_sketch(sk, ids, rep)

        title = f"{path}" + (f"  (plan: {plan})" if plan else "")
        print(f"\n=== {title} — {len(sketches)} sketch(es) ===")
        for sid, code, msg in rep.fails:
            print(f"  ✗ FAIL [{code}] {sid}: {msg}")
        for sid, code, msg in rep.warns:
            print(f"  ⚠ WARN [{code}] {sid}: {msg}")
        if not rep.fails and not rep.warns:
            print("  ✓ all mechanical checks pass")
        elif not rep.fails:
            print("  ✓ no hard failures (warnings above need your judgment)")
        overall_fail = overall_fail or bool(rep.fails)

    print("\n— LLM still owns: G4 (signal/noise), and the judgment half of G1 (is the "
          "decomposition warranted?) and G6 (is the focus truly singular?). See invariants.md.")
    return 1 if overall_fail else 0


if __name__ == "__main__":
    sys.exit(main(sys.argv))
