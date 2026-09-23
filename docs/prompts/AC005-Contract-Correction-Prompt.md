# EAIOC — AC-005 Surgical Source and Execution-Plan Correction

## Purpose

Apply the now-approved human decisions for the **AC-005 / CostLedgerEntry** contract reconciliation.

This is a **documentation-only correction**.

### Approved human decisions

#### D-1 — Schema Structure

`CostLedgerEntry` shall represent the Architecture `§27.1` Standard Ledger Fields using a **nested, category-preserving structure** corresponding to the nine `§27.1` groups:

- INPUT
- OUTPUT
- CACHE
- MODEL
- TOOLS
- WORKFLOW
- COST
- PERFORMANCE
- QUALITY

The representation must preserve the logical `category.field` structure from `architecture.md §27.1`.

#### D-2 — Existing-Field Equivalence

No existing INTF-047 field may be declared equivalent, aliased, renamed, replaced, or repurposed as a `§27.1` field unless the authoritative corpus explicitly establishes that correspondence.

Therefore:

- Do not infer equivalence from similar names.
- Do not infer equivalence from `§27.3` formula terminology.
- Do not infer equivalence from implementation convenience.
- Preserve existing INTF-047 fields unless a separate explicit contract correction authorizes otherwise.

#### D-3 — Availability Semantics

Preserve the authoritative **“where available”** semantics from the Problem Statement / Engineering Specification.

Do not fabricate unavailable values.

Do not silently convert unavailable values to zero.

Do not silently declare unavailable values invalid.

Where the corpus does not fully establish the interaction between availability and the `unverified` status, record that as an explicit source-gap/open decision rather than inventing behavior.

#### D-4 — Types

Use only:

- types explicitly defined by the authoritative corpus, or
- mechanically derivable primitive types supported by `conventions.md §3.6`.

Known conventions:

- token counts → `integer`
- monetary amounts → `float`
- timestamps → ISO8601 string
- schema field names → `lower_snake_case`

For `§27.1` fields whose exact type is not established by the corpus, do not invent enums, objects, or domain types.

Mark the unresolved type explicitly as `SOURCE-UNRESOLVED` and preserve it for subsequent controlled resolution.

---

# 1. Hard Read-Only Implementation Rule

This correction MUST NOT implement Java code.

Do NOT:

- modify `CostLedgerEntry`
- modify any constructor
- modify accounting tests
- modify `LedgerObservability`
- modify `StubPipelineStageCaller`
- modify `unverifiedFallback()`
- create a remediation implementation
- execute `REM-P0.1.B-01`
- execute any EXE-P0.1.B remediation
- execute `EXE-P0.2.B`
- approve Capability 1 Gate

This task may modify only the explicitly authorized documentation files listed below.

---

# 2. Authoritative Sources

Use the frozen corpus hierarchy exactly as defined by `conventions.md`:

1. Problem Statement
2. Engineering Specification
3. Architecture
4. Interfaces
5. `conventions.md`

Downstream planning documents must follow that hierarchy.

Do not silently alter the Problem Statement or Engineering Specification.

---

# 3. Required Source Contract

The correction must preserve these established facts:

1. `architecture.md §27.1` defines the Standard Ledger Fields.
2. `conventions.md §14.1` states that every request produces a `CostLedgerEntry` carrying all standard `§27.1` fields.
3. `interfaces.md §28.1` currently defines INTF-047 `CostLedgerEntry`.
4. INTF-047 is not currently a complete representation of `§27.1`.
5. The current existing INTF-047 fields must not be silently reinterpreted.
6. AC-005 requires distinction between cached, compressed, removed, generated, retrieved and reused tokens.
7. `REM-P0.1.B-01` does not yet exist as an authorized execution-plan unit and must not be created implicitly.

---

# 4. Files Authorized for Documentation Correction

Modify only these files:

```text
docs/interfaces.md
docs/conventions.md
docs/execution-plan.md
docs/execution-plan-p0-steps.md
CLAUDE.md
```

Do not modify:

```text
architecture.md
problemStatement
Engineering_Spec
requirements-traceability.md
implementation-plan.md
security.md
observability.md
quality-gates.md
eval.md
SCALING.md
ADRs
source code
tests
```

Exception:

If repository inspection proves that a direct upstream frozen-source correction is unavoidable for D-3 or D-4, STOP and report it. Do not modify that upstream file under this correction.

---

# 5. interfaces.md Correction

Update `interfaces.md §28.1 INTF-047 CostLedgerEntry`.

The corrected contract must:

- preserve all currently existing INTF-047 identity, attribution, and economic fields;
- introduce the canonical `§27.1` ledger representation using nested groups;
- preserve the exact nine `§27.1` category names;
- use `lower_snake_case` field names within the nested groups;
- avoid declaring unsupported equivalences between existing INTF fields and `§27.1` fields;
- explicitly document that the `§27.1` nested structure is the canonical ledger representation;
- explicitly document that existing INTF-047 fields are retained for interface compatibility unless independently mapped by authoritative evidence;
- preserve availability semantics;
- explicitly identify unresolved types rather than inventing them.

### Structural form

Use this only as structural guidance. Verify every field and type against the live authoritative corpus before writing the corrected schema.

```text
CostLedgerEntry {
  <existing identity / attribution / compatibility fields>

  input: {
    raw_input: integer
    sanitized: integer
    query_compressed: integer
    context: integer
    retrieved: integer
    pruned: integer
    deduplicated: integer
    context_compressed: integer
    cached_input: integer
    uncached_input: integer
  }

  output: {
    raw_output: integer
    optimized_output: integer
    truncated: integer
    expanded_retry: integer
  }

  cache: {
    exact_hits: <source-supported type>
    semantic_hits: <source-supported type>
    misses: <source-supported type>
    writes: <source-supported type>
    reads: <source-supported type>
    cacheable_tokens: integer
    reused_tokens: integer
  }

  model: {
    selected: <source-supported type>
    candidate: <source-supported type>
    routing_decision: <SOURCE-UNRESOLVED>
    escalation: <SOURCE-UNRESOLVED>
    reasoning_budget: <source-supported type>
    reasoning_tokens: integer | unavailable
  }

  tools: {
    calls_attempted: <source-supported type>
    calls_avoided: <source-supported type>
    output_tokens: integer
    filtered_tokens: integer
    cached_calls: <source-supported type>
  }

  workflow: {
    steps_planned: <source-supported type>
    steps_executed: <source-supported type>
    steps_skipped: <source-supported type>
    early_exits: <source-supported type>
    retries: <source-supported type>
  }

  cost: {
    input: float
    output: float
    cache: float
    compression: float
    tool: float | unavailable
    total_optimized: float
    baseline_estimated: float
    net_savings: float
    savings_pct: float
  }

  performance: {
    e2e_latency_ms: <source-supported type>
    ttft_ms: <source-supported type>
    model_latency_ms: <source-supported type>
    compression_latency_ms: <source-supported type>
    cache_latency_ms: <source-supported type>
    tool_latency_ms: <source-supported type>
  }

  quality: {
    correctness_score: <source-supported type>
    relevance_score: <source-supported type>
    schema_compliance: <SOURCE-UNRESOLVED>
    semantic_preservation: <source-supported type>
    user_task_score: <source-supported type>
    safety_validation: <source-supported type>
  }
}
```

IMPORTANT:

The example above is structural guidance only.

Before writing the corrected schema:

- verify every field against the live `architecture.md §27.1`;
- verify every type against the authoritative corpus;
- do not invent a type merely because the example contains a placeholder.

Where a type is unresolved, record `SOURCE-UNRESOLVED` in the documentation and explain why.

---

# 6. Traceability Header Correction

Update INTF-047 traceability so that it explicitly references the actual ledger sources.

The corrected traceability must include, where supported:

```text
PS §8
AC-005
ARCH §27
ARCH §27.1
CONV §14.1
OBJ-011
```

Do not remove other valid traceability references unless they are proven incorrect.

---

# 7. conventions.md Correction

Update `conventions.md §14.1` only to remove its internal contradiction.

Preserve the requirement that:

> Every request must produce a `CostLedgerEntry` carrying all standard ledger fields defined in `ARCH §27.1`.

Do not weaken that requirement.

Resolve the mismatch between:

- “all standard ledger fields”
- and the narrower explicit Mandatory field lists.

The corrected wording must make it clear that the nine `§27.1` groups collectively constitute the complete ledger contract, while preserving the **“where available”** qualifiers for values whose authoritative source contains those qualifiers.

Do not invent an availability policy that the corpus does not establish.

---

# 8. execution-plan.md Correction

Create the next execution-plan revision, using the repository’s existing versioning convention (expected next revision: `v1.0.6`).

## 8.1 Record the source-contract resolution

Record:

```text
AC-005 contract direction resolved.
INTF-047 must represent the complete §27.1 standard ledger field model.
Canonical representation: nested/category-preserving.
Unsupported field equivalence is prohibited.
Availability semantics are preserved.
Unresolved types are explicitly identified rather than invented.
```

## 8.2 Record the contradiction

Record the frozen-source inconsistency:

```text
INTF-047 does not currently represent the full §27.1 ledger model.
```

Do not describe this merely as an “interface bug.” Identify it as a frozen-source contract inconsistency resolved according to the documented source precedence.

## 8.3 Correct Capability 1 B

The Capability 1 B Definition of Done must explicitly require:

- conformance to the corrected INTF-047 contract;
- representation of all `§27.1` fields;
- distinct representation of the six AC-005 categories;
- preservation of tenant isolation;
- preservation of verified/unverified behavior;
- contract tests proving the canonical ledger structure.

Do not claim these were satisfied historically by `e353dcf`.

The historical execution remains unchanged.

## 8.4 Authorize a new B remediation sequence

Introduce a formally named remediation sequence for the B contract gap.

Do NOT use an already-proposed but unauthorized ID such as:

```text
REM-P0.1.B-01
```

unless that exact ID is formally assigned by the corrected plan.

Use a new ID consistent with the existing remediation naming convention.

Split it into at least:

```text
<NEW-B-REMEDIATION-DESIGN-ID> — Contract / Design Reconciliation
<NEW-B-REMEDIATION-IMPLEMENTATION-ID> — Minimal Implementation Remediation
```

The first unit must produce a design artifact based strictly on the corrected INTF-047 contract.

The second unit must implement only that approved design.

Each unit requires:

```text
Execute <ID>
→ AI Verification
→ Human Approval
```

No automatic chaining.

## 8.5 Gate prerequisites

Preserve:

```text
Capability 1 Gate = BLOCKED
EXE-P0.2.B = BLOCKED
```

until the B remediation sequence is completed and the Capability Gate passes.

---

# 9. P0 Runbook Correction

Synchronize:

```text
docs/execution-plan-p0-steps.md
```

with the new execution-plan version.

Correct:

- stale current-position text;
- stale REM-A status;
- stale “next command” references;
- AC-005 blocker status;
- new B remediation sequence;
- G/H approval evidence state.

Do not assume G/H approvals.

---

# 10. CLAUDE.md Status

Synchronize only the relevant implementation-status section.

It must state, using facts actually supported by the repository:

```text
Capability 1 Gate: BLOCKED
AC-005: contract direction resolved; implementation remediation pending
B remediation: authorized by execution-plan v1.0.6
P0.2.B: blocked
```

Do not add implementation claims that have not occurred.

---

# 11. Historical Execution Preservation

Do NOT rewrite historical commits.

Do NOT modify:

```text
a345742
e353dcf
5d8640e
e1730ce
3987957
95a1267
0c18174
93c1ba3
5e12148
a764689
```

The historical implementation remains historical evidence.

The new correction must be recorded as current-state documentation.

---

# 12. Procedural Audit Status

Preserve these findings.

## G/H Approval Evidence

The repository does not prove that:

```text
Approve EXE-P0.1.G
Approve EXE-P0.1.H
```

were actually issued.

Do not assume them.

Record them as requiring explicit human confirmation.

## Out-of-band .vscode Commit

`a764689` remains:

- non-implementation;
- editor configuration only;
- excluded from P0 execution counts.

## Repository State

Confirm the repository is clean before and after the documentation correction.

## Unauthorized B Remediation

Confirm no unauthorized B remediation implementation exists.

---

# 13. Required Validation Before Commit

Before creating any documentation commit:

1. Verify every `§27.1` field appears exactly once in the canonical nested structure.
2. Verify all nine groups exist.
3. Verify no `§27.1` field has been silently omitted.
4. Verify no unsupported alias/equivalence was introduced.
5. Verify existing INTF-047 fields remain explicitly represented.
6. Verify `lower_snake_case`.
7. Verify source-defined token, monetary, and timestamp types.
8. Verify unresolved types are explicitly marked.
9. Verify availability semantics were not fabricated.
10. Verify AC-005 traceability.
11. Verify execution-plan sequencing.
12. Verify the B remediation unit has a unique authorized ID.
13. Verify no source code changed.
14. Verify no tests changed.
15. Verify only authorized documentation files changed.

---

# 14. Commit Rule

This is a documentation correction.

If the repository’s established process requires a commit:

- create exactly one documentation correction commit;
- include only the authorized documentation files;
- do not mix source code;
- do not execute the remediation;
- do not push unless the normal correction workflow explicitly requires it.

---

# 15. Required Output

Return:

1. Files changed
2. Exact documentation changes by file
3. Canonical nested ledger structure
4. `§27.1` field completeness verification
5. Unsupported-equivalence verification
6. D-1 / D-2 / D-3 / D-4 application verification
7. New B remediation ID(s)
8. Updated execution sequence
9. G/H approval evidence status
10. Git diff summary
11. Commit hash if a documentation-only commit was required
12. Remaining blockers
13. Exact next human command

---

# 16. Hard Stop

After the documentation correction:

DO NOT:

- implement the B remediation;
- modify Java code;
- run `EXE-P0.1.B`;
- approve the Capability 1 Gate;
- execute `EXE-P0.2.B`.

The task ends after documentation correction and verification.

Final status must be:

```text
SOURCE/PLAN CORRECTION COMPLETE — AWAITING HUMAN APPROVAL TO EXECUTE THE AUTHORIZED B REMEDIATION DESIGN UNIT
```

Then provide exactly one next human command:

```text
Execute <NEW-B-REMEDIATION-DESIGN-ID>
```
