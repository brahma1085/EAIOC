# T1.1 Sanitizer — Contract & Design Note (EXE-P0.3.A)

**Status:** Design note. No Java code and no new schema are introduced by this sub-phase.
**Governing decision:** HD-CG-P0.3-01 (human decision, 2026-10-05, Option 3 — split T1.1 normalization from CIS). Evidence for the decision: the operator's instruction and the corrected plan text in `docs/execution-plan.md` §18.6 and §29; the other options were not recorded. This decision resolves a source/plan contradiction and is not approval of `EXE-P0.3.A`.
**Documentation synchronization (2026-10-05, HD-CG-P0.3-08/-09/-10/-11):** this note was committed at `e4519d7` as the A unit's design record, and the text below is preserved as written. The following statements are **superseded** by `docs/interfaces.md` §44 (INTF-072, v1.3.0) and `docs/optimization-catalog.md` `TECH-001`: that interfaces.md defines no T1.1 interface (§2 and §8.1 below); that `TECH-001` cites INTF-001 (§2 and §8.2); and that the INTF-001 gap is not registered (§8.4). Superseded markers are placed beside each statement. The T1.1 input is `ControlPlaneRequest.user_input`; null input is decided by HD-CG-P0.3-10 (`SOURCE-GAP-EXECPLAN-18`); the EC-014 P0 applicability gap is recorded as documented under HD-CG-P0.3-11; the counter identity values were decided by HD-CG-P0.3-09 (2026-10-05); the numeric validation error code for null input remains open under `SOURCE-GAP-EXECPLAN-18`.

**Source of the row:** `docs/execution-plan.md` §18.6, Sub-phase A row ("Cite T1.1's normalization/token-count contract as-is; No new schema invented; design note").

## 1. What T1.1 is

T1.1 Sanitizer normalizes raw input before downstream processing (`docs/architecture.md` §11.1, "Purpose").

Its capabilities, as stated in §11.1:
- normalize whitespace and formatting;
- remove irrelevant formatting;
- remove duplicated content within the input;
- strip known UI noise (timestamps, progress bars, decorative separators);
- normalize equivalent representations (Unicode normalization);
- **preserve** code, structured data, identifiers, URLs, and user-intent information.

Its outputs, as stated in §11.1:
- the sanitized input;
- the baseline token count (pre-sanitization);
- the post-sanitization token count.

§11.1's caution is binding: the sanitizer "must be content-aware and must **not** blindly remove syntax that is meaningful to the task."

T1.1 is **normalization and token counting**. It is not a security-screening component.

## 2. The contract this sub-phase cites (as-is)

The A row asks to cite the normalization/token-count contract as-is. The frozen sources that define it:

| Source | What it supplies |
|---|---|
| `architecture.md` §11.1 | Purpose, capabilities, outputs, and the content-awareness caution (above) |
| `optimization-catalog.md` `TECH-001` | Governing component `T1.1-SANITIZER`; applicability; fallback; quality risks; observability names (`tokens.raw_input`, `tokens.sanitized`, `tokens.removed_by_sanitizer`, `sanitizer.false_removal_rate`) |

No field, type, enum, or schema is defined here beyond what those sources state. In particular this note adds no input or output type, no failure code, and no runtime behaviour.

**Source gap (documented, non-blocking for A; SUPERSEDED 2026-10-05 by `interfaces.md` §44 / INTF-072 — original wording preserved):** `interfaces.md` defined no T1.1-specific interface at the time of the A unit. `TECH-001` names `INTF-001` as its interface, but `INTF-001` is `ControlPlaneRequest` (`interfaces.md` §2), not a T1.1 contract. `interfaces.md` §37.1 has no T1.1/Sanitizer row. INTF-001 is consumed by several stages (for example the T1.x Context Pruner, line ~3280), so it is a request-carrier reference, not a T1.1-specific contract. Consequence: the T1.1 input and output types are defined only as §11.1's prose list above. This is recorded here and not resolved by invention. Sub-phase B (minimal implementation) needs concrete types, so it will require a source decision before it can start. That is a future gate, not a blocker for A.

## 3. Failure behaviour (fail-open, RESTORE_ORIGINAL)

Frozen sources:
- `architecture.md` §30 fallback table: "Sanitization fails → Use original input".
- `architecture.md` (optimization fallback table, ~line 1957): "If sanitization fails: use original input verbatim".
- `conventions.md` §16.1: "Sanitization fails → Use original input verbatim".
- `conventions.md` §16.2: "T1.x — Context ops → Fail-open: RESTORE_ORIGINAL".
- `optimization-catalog.md` `TECH-001` Fallback: "If sanitization fails: use original input verbatim (PS §11; CONV §16.1)". Its Security/Authorization row separately states "Sanitization is fail-open per stage (CONV §16.2)".

The quotations above are close paraphrases in the arrow form the frozen tables use, not verbatim copies of each table cell.

So a T1.1 **normalization** failure returns the original input verbatim. It must not return an altered or partially normalized output. [Added 2026-10-06, HD-CG-P0.3-18: this rule covers normalization failure only. A token-counter failure is not RESTORE_ORIGINAL; at the T1.1 contract boundary it produces no `SanitizerOutput` and no fabricated, nullable, stale or fallback count. The pipeline then applies EC-114 stage-level fallback and continues with the pre-stage representation without rejecting the request (HD-CG-P0.3-19). See `docs/interfaces.md` §44.4.]

### 3.1 T0 / T1 boundary (no conflict)

`conventions.md` §16.2 also has a row "T0.x — Normalization (always required) → Fail-closed: REJECT", and `interfaces.md` §22.2 has "T0.x — Request normalization → REJECT". Those rows cover request canonicalization (`ControlPlaneRequest`, `interfaces.md` §2). T1.1 content normalization is a different stage. Basis: `architecture.md` §8 and §11.1 place "Normalize raw input" under T1, and §10 defines T0 with no content normalizer. The `conventions.md` header gives `architecture.md` precedence over `conventions.md`. So the T0 REJECT row is not applied to T1.1.

## 4. Code-content case (`EC-014`)

[SUPERSEDED for P0 by `interfaces.md` §44.4 and HD-CG-P0.3-11/-13: EC-014 content-type routing is an open gap with no P0 carrier, out of scope for B. The text below describes EC-014 itself.] `edge-cases.md` `EC-014` ("Pruner Removes Python Indentation") applies directly to T1.1 for code content:
- route `content_type = CODE` items through code-aware sanitization, never prose whitespace normalization;
- if code-aware sanitization fails, pass the original code verbatim (`architecture.md` §30);
- observability: `sanitizer.content_type_routed` and `sanitizer.code_sanitizer_fallback_count`.

The earlier A-row statement "None found directly" is withdrawn. `EC-014` applies directly.

`EC-014` itself says "original code passed verbatim". The token RESTORE_ORIGINAL is `conventions.md` §16.2's term; applying it to `EC-014` is an equivalence, not a quotation.

## 5. What is NOT T1.1: Content Integrity Screen (CIS)

CIS is a separate, source-defined governance component:
- `architecture.md` §47.2.5: screens externally-sourced content (RAG chunks, search results, tool/MCP results, sub-agent handoffs) for prompt injection and integrity violations;
- `SEC-016`: content is untrusted by default until CIS returns `PASS`;
- `ScreeningResult` = `PASS | REJECT | QUARANTINE | SCREENING_UNAVAILABLE`;
- `SCREENING_UNAVAILABLE` is fail-closed: content is rejected or quarantined, never silently admitted (`architecture.md` §47.2.5, "Failure Behavior");
- `interfaces.md` §43.5 defines `ContentIntegrityScreen` (INTF-067).

CIS is **not** merged into T1.1. Its implementation is **not** part of EXE-P0.3, and nothing in this note implements or implies it.

## 6. Capability 3 scope boundary

[SUPERSEDED for P0 by HD-CG-P0.3-13: the code-content routing case is OUT OF SCOPE for B.] In scope for EXE-P0.3: T1.1 normalization and token counting, its RESTORE_ORIGINAL fallback, and the code-content case.

Out of scope for EXE-P0.3: CIS screening, and any fail-closed screening behaviour.

## 7. Traceability

| Item | Reference |
|---|---|
| T1.1 component | `architecture.md` §11.1 |
| Fail-open fallback | `architecture.md` §30; `conventions.md` §16.1, §16.2; `optimization-catalog.md` `TECH-001` |
| Code-content case | `edge-cases.md` `EC-014` |
| CIS (separate, fail-closed) | `architecture.md` §47.2.5; `SEC-016`; `interfaces.md` §43.5 (INTF-067) |
| Plan disposition | `docs/execution-plan.md` §18.6 (corrected under HD-CG-P0.3-01) |

## 8. Open items (none blocks this design note)

1. Sub-phase B needs concrete T1.1 input and output types. No interface defines them. (SUPERSEDED 2026-10-05: `interfaces.md` §44 defines the shapes; the counter identity values were decided by HD-CG-P0.3-09 (`JTOKKIT_CL100K_BASE`, `1.1.0`) on 2026-10-05.)
2. `TECH-001`'s INTF-001 citation is incorrect (INTF-001 is `ControlPlaneRequest`). (SUPERSEDED 2026-10-05: corrected in `optimization-catalog.md` `TECH-001` under `SOURCE-GAP-EXECPLAN-17`.)
3. No dedicated `SCN-*` scenario isolates T1.1 (recorded as `SOURCE-GAP-EXECPLAN-03`, unchanged).
4. `TECH-001` cites INTF-001 as T1.1's interface. INTF-001 is not a T1.1-specific contract. The gap is not registered under any `SOURCE-GAP`/`CONTRA` ID; registration needs a human decision before Sub-phase B. (SUPERSEDED 2026-10-05: registered as `SOURCE-GAP-EXECPLAN-17`.)
5. `architecture.md` §10.1 labels "T0.1 — Model Router", while `conventions.md` uses "T0.1-NORMALIZER". The fail-closed T0.x normalization row (`conventions.md` §16.2) sits next to the fail-open T1.1 row. `TECH-001`'s Security row already draws the distinction: T1.1 "sits upstream of, and never substitutes for, T0.x normalization's fail-closed schema/tenant validation". This note does not restate it, and the ID collision is unregistered.
