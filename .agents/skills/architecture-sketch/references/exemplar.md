# Worked example (few-shot)

This is the canonical reference: the 004-mvp MVP architecture, defined as an ImplPlan of Sketches.
Below is the **C2 module sketch in strict YAML** (the DEFINE artifact). The four C1 drill-downs
(`zoom:` targets) follow the same shape — see the rendered SVGs for their layout.

## Rendered output to imitate (layout + hand-drawn skin)

In this repo: `specs/004-mvp/` —
`c2-modules.svg` (style anchor — fix the SKIN/legend here first), then the C1 drill-downs
`c1-orchestration.svg` · `c1-mutator-engine.svg` · `c1-genome-gates.svg` · `c1-ui.svg`.
Source constraint figure (hand-drawn): `specs/004-mvp/architecture-c3-big-picture.png`.

Study `c2-modules.svg` for the lane/container/stack/note-leader layout and
`c1-mutator-engine.svg` for the UML class-diagram relation lines.

## C2 sketch (strict YAML)

```yaml
plan: "MVP Architecture"
sketches:
  - id: c2-modules
    level: C2
    focus: "runtime 系統如何把 LLM 呼叫 → mutator 施用 → genome 變更 → UI 視覺化 串成一條鏈"
    lanes:
      - {id: L1, label: "程式底層"}
      - {id: L2, label: "UI View"}
    nodes:
      - id: SDK
        label: "Claude Code SDK"
        kind: external
        lane: L1
        stereotype: "3rd"
        icon: "⌨"
        impl: {unit: "adapters/claude_code_sdk", kind: package}
      - id: M1
        label: "Orchestration"
        kind: container
        lane: L1
        icon: "🤖"
        contains: ["SystemPromptBuilder", "ToolDispatcher", "MessageCodec", "SdkPort"]
        impl: {unit: "app/agent", kind: module}
        zoom: c1-orchestration
      - id: M2
        label: "Mutator Engine"
        kind: container
        lane: L1
        icon: "🔧"
        multiplicity: N
        contains: ["MutatorRegistry", "«Mutator»×10", "Transaction"]
        impl: {unit: "app/mutators", kind: module}
        zoom: c1-mutator-engine
      - id: M3
        label: "Genome + Gates"
        kind: container
        lane: L1
        icon: "🧬"
        contains: ["Genome(hes/v2)", "Serializer", "WFValidator", "EvalGate"]
        impl: {unit: "app/genome", kind: module}
        zoom: c1-genome-gates
      - id: M4
        label: "UI View"
        kind: view
        lane: L2
        icon: "🖥"
        panels:
          - {label: "💬 與 AI 對話", icon: "💬"}
          - {label: "</> Skill-Json 結構視覺化", icon: "</>"}
        impl: {unit: "app/ui", kind: module}
        zoom: c1-ui
    edges:
      - {id: e1, from: M1,  to: SDK, verb: "呼叫 claude -p", payload: "system+user prompt", kind: call}
      - {id: e2, from: SDK, to: M1,  verb: "回傳",          payload: "結構化 message",      kind: dataflow}
      - {id: e3, from: M1,  to: SDK, verb: "注入工具",       payload: "tools' description",  kind: injection}
      - {id: e4, from: M1,  to: M2,  verb: "dispatch",      payload: "{mutator,args}",      kind: call}
      - {id: e5, from: M2,  to: M3,  verb: "apply",         payload: "G → G'",              kind: call}
      - {id: e6, from: M3,  to: M2,  verb: "gate",          payload: "WF/eval verdict",     kind: dataflow}
      - {id: e7, from: M1,  to: M4,  verb: "渲染",           payload: "結構化 message",      kind: render}
      - {id: e8, from: M3,  to: M4,  verb: "read",          payload: "Skill-Json (genome)", kind: dataflow}
    notes:
      - {anchor: SDK,   type: design-constraint,   salience: high, text: "Agent SDK 介接要有擴充性；Open for different vendor SDK ★"}
      - {anchor: M1,    type: behavioral-contract,                 text: "system prompt = 所有 tools 的 description + skill 演化「兩準則」"}
      - {anchor: M1,    type: scope-bound,                         text: "初版不存歷史對話 session"}
      - {anchor: M2,    type: impl-directive,       salience: high, text: "💡 每個 mutator = 一個 package，tool & tests & animation 同包分檔（內聚）"}
      - {anchor: M2,    type: scope-bound,                         text: "29 → 折 10（type-parameter 軸折三刀，reachability 不變）"}
      - {anchor: "M2->M3", type: behavioral-contract,              text: "一次施用 = 一筆 transaction（all-or-nothing，single locus）"}
      - {anchor: M3,    type: behavioral-contract,                 text: "identity = path；只有 RENAME 改 id，其餘九個只改 version"}
      - {anchor: M3,    type: scope-bound,                         text: "eval oracle E 未建 → P̃/B 暫以人類 double-confirm 代"}
      - {anchor: "M3->M4", type: reference-exemplar,               text: "Skill-Json 結構視覺化（gallery 式 before/after）"}
      - {anchor: M4,    type: reference-exemplar,   salience: high, text: "和 pencil.dev 一樣，每個 mutation tool 有自己的 working animation ★"}
      - {anchor: "SDK->M1", type: reference-exemplar,              text: "結構化 message 顆粒度參考 pencil.dev 的 tooling message"}
```

Note how this passes the gate: every box carries an `impl` binding + ≥1 note (G1); no anchor
exceeds 3 notes (G2); both layers present (G5); a single focus (G6); the four modules each
`zoom:` into their own C1 sketch (multi-diagram), so no single diagram is overloaded.
