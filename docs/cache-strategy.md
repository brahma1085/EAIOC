# EAIOC-CACHE-001 — Cache Strategy: Mechanics, Key Schemas, TTL Policy, Economics, Invalidation, and Poisoning Defense

**Status:** PRE-IMPLEMENTATION — downstream elaboration of `architecture.md` §11.4, §11.5, §13.3, §15.3, §16; `interfaces.md` §6; `conventions.md` §9.
**Authoritative sources (9, priority order):** Problem Statement > Engineering Specification > `architecture.md` > `interfaces.md` > `conventions.md` > `edge-cases.md` > `scenario-matrix.md` > `optimization-catalog.md` > `provider-matrix.md`.
**Scope:** concrete cache-key schemas, TTL policy defaults, cache-economics estimation methodology, cache-fragmentation remediation, dependency-aware invalidation cascade design, poisoning defenses, and tenant-isolation enforcement — exactly the content `optimization-catalog.md`'s own Scope Boundary and `provider-matrix.md` §14 defer here. This document does not redefine `CacheStore`, SRP, CL-003, CAR, CIS, TMG, DA-003/022/024, or `TECH-009/010/011/012` — it cites them and supplies the concrete policy data they consume.

---

## 0. Critical Principle

EAIOC is a stateful execution control plane, not a stateless request/response cache. A cache hit — of any of the seven types below — is **a candidate for reuse, never proof that reuse is valid**. Every candidate must be reconciled against current tenant, authorization, policy, freshness, dependency, provider/model, and (where applicable) content-integrity state before being served. This principle governs every section that follows; it is restated, not re-derived, wherever a specific cache type's policy would otherwise appear to permit a shortcut around it.

**Provider-neutrality note:** naming a real provider's cache pricing/behavior anywhere in this document (via citation to `provider-matrix.md`) is not a violation of `CLAUDE.md` rule 1 — it is this document consuming the neutral `ModelProfile` data that rule requires. No provider-specific conditional logic is proposed here; every policy below is expressed against the provider-neutral `CacheStore`/`CacheEntry` schema (`interfaces.md` §6.1–6.4).

---

## 1. The `CacheStore` Interface Contract (restated, not redefined)

`interfaces.md` §6.1 defines the provider-neutral cache abstraction this entire document supplies policy for:

```
interface CacheStore {
  lookup(request: CacheLookupRequest)           -> CacheLookupResult
  write(entry: CacheWriteRequest)               -> CacheWriteResult
  invalidate(request: CacheInvalidationRequest) -> CacheInvalidationResult
  refresh(key: string, namespace: string)       -> CacheRefreshResult
  warm(entries: CacheWarmRequest[])             -> CacheWarmResult
  inspect(key: string, namespace: string)       -> CacheEntry | null
  explain_miss(request: CacheLookupRequest)     -> CacheMissExplanation
}
```

| Method | Policy this document supplies |
|---|---|
| `lookup` | Which checks run and in what order per cache type (§6), similarity-threshold governance (§7), tenant-key structure (§4) |
| `write` | `write_policy` guidance per type (§8.1), economics gate (§9), PII/integrity gates (§13–14) |
| `invalidate` | The full dependency-aware cascade (§10) |
| `refresh` | When a refresh-in-place is appropriate vs. a full invalidate+recompute (per cache type, §17) |
| `warm` | Pre-warming eligibility policy (§12) |
| `inspect` | Provenance fields that must be inspectable (§19) |
| `explain_miss` | Diagnosis logic per `CacheMissReason` (§18) |

The schema types this document's policy populates — `CacheLookupRequest`, `CacheLookupResult`, `CacheMissReason`, `CacheEntry`, `CacheWriteRequest`, `CacheInvalidationRequest` — are reproduced verbatim from `interfaces.md` §6.2–6.4 in Appendix A. No field name in this document conflicts with or extends that schema; where a logical concept in this document's own analysis has no backing field, it is marked `SOURCE-GAP` (§31) rather than invented.

---

## 2. Cache Object Model — Logical Concepts Grounded in the Real Schema

| Logical concept | Backed by (real field) | Note |
|---|---|---|
| CacheKey | `CacheEntry.key` + `CacheEntry.namespace` (tenant-prefixed, §4) | Constructed, not a stored field itself |
| CacheValue | `CacheEntry.content` / `content_type` / `token_count` | — |
| CacheMetadata | `CacheEntry.{version, created_at, expires_at, last_accessed_at, access_count}` | — |
| CachePolicy | Derived from cache_type + `conventions.md` §9.2's per-type table; not a single interface field | Logical only — see §6 |
| CacheEligibility | Composite of `PIIClassifier.is_safe_to_cache` (CONV §9.3) + CIS pass + economics gate (§9) | `SOURCE-GAP-CACHESTRAT-01`: no single interface field aggregates this decision; it is a policy computed at write time, not stored |
| CacheValidation | `CacheLookupResult.{freshness_valid, auth_check_passed}` | — |
| CacheProvenance | `CacheEntry.{source_version, source_commit, source_branch, data_timestamp}` | — |
| CacheDependency | `CacheEntry.dependency_refs` (CL-003 invalidation triggers) | — |
| CacheVersion | `CacheEntry.version` | Distinct from `source_version` — the former is the cache schema/write version, the latter is the underlying content's version |
| CacheScope | `CacheEntry.{tenant_id, organization_id, namespace}` | — |
| CacheFreshness | `CacheEntry.freshness_score` + `CacheLookupResult.freshness_valid` | Score vs. boolean gate are two distinct fields — see §17 |
| CacheSecurityMetadata | `CacheEntry.security_classification` | — |
| CacheAuthorizationMetadata | Not a stored `CacheEntry` field — authorization is evaluated at lookup time against `CacheLookupRequest.security_context`, not cached alongside the entry | `SOURCE-GAP-CACHESTRAT-02`: whether authorization *context* itself should ever be embedded in the key (vs. re-evaluated live) is addressed per-type in §6; the interface does not mandate either choice |
| CacheInvalidationMetadata | `CacheInvalidationRequest.{reason, cascade}` | — |
| CacheCostMetadata | `CacheEntry.{write_cost, read_cost, reuse_probability}` | — |
| CacheQualityMetadata | Not a stored field | A cache hit carries no quality score of its own — see §0 and CONV §24.2's anti-pattern ("treating a cache hit as a quality guarantee") |

---

## 3. The Seven Canonical Cache Types

`interfaces.md` §6.2's `CacheLookupRequest.cache_type` enum defines exactly seven values, verified against the live file. This document uses these seven, in this order, as its primary structure. No eighth type is introduced; none is renamed or merged.

| # | Type | Owning architecture section |
|---|---|---|
| 1 | `EXACT` | ARCH §11.4 (T1.6 Prompt Cache) |
| 2 | `SEMANTIC` | ARCH §11.5 (T1.7 Semantic Cache) |
| 3 | `TOOL_RESULT` | ARCH §13.3 (T3.3 Tool Result Cache) |
| 4 | `EMBEDDING` | ARCH §16 (Cache Economics and Cache-Aware Assembly), DA-022 |
| 5 | `PROVIDER_NATIVE` | ARCH §25 / `provider-matrix.md` (pricing/capability facts live there, not here) |
| 6 | `APPLICATION` | ARCH §16 |
| 7 | `CONTEXT` | ARCH §15 (CL-001–007), ARCH §16 |

Note: `CacheWriteRequest.cache_type` (`interfaces.md` §6.4) enumerates only five values (`EXACT, SEMANTIC, TOOL_RESULT, EMBEDDING, CONTEXT`) — `PROVIDER_NATIVE` and `APPLICATION` are omitted from the write-request enum in the live interface. This is recorded as `SOURCE-GAP-CACHESTRAT-03` (§31): `PROVIDER_NATIVE` writes are plausibly provider-adapter-owned (never written by core EAIOC code, consistent with CACHE-005's boundary in §6), which would explain its absence; `APPLICATION`'s absence has no equally clean explanation in the source documents and is flagged as a genuine gap for interface-schema reconciliation, not resolved here by assumption.

---

## 4. Cache Key Construction (mandatory, structural — not merely policy)

Per `conventions.md` §9.1 and root `CLAUDE.md` rule 4: **every cache key's namespace begins with `tenant_id`. Cross-tenant cache serving is unconditionally prohibited — structurally, not just by a runtime check.** `SCN-CACHE-003` establishes the precise standard this document must meet: tenant isolation must be enforced so that "Tenant B's lookup never sees Tenant A's entry as a candidate at all" — a namespacing property, not a filter applied after retrieval.

**Canonical key template** (grounded in `CacheEntry`'s real fields, §2):

```
{tenant_id}:{cache_type}:{namespace}:{artifact_identity}[:{model_id}][:{provider_id}]
```

Where `artifact_identity` is type-specific (§6 gives the concrete construction per type) and the bracketed suffixes apply only where `model_id`/`provider_id` are part of `CacheEntry` for that type (both fields exist on every `CacheEntry`, §2, but are only *identity-relevant* for types whose result depends on model/provider — see §6's per-type notes).

**Over-keying vs. under-keying** (`conventions.md` §9.1's four-check gate exists precisely because keys cannot include every dimension): including a dimension in the key namespace reduces reuse (a narrower key matches fewer future requests) but each additional dimension in the key removes a corresponding *runtime check* that would otherwise be needed. EAIOC's convention, reflected throughout §6, is: **structural (tenant, security-classification-relevant) dimensions go in the key; volatile, cheaply-revalidated dimensions (authorization, policy, freshness) are runtime checks against the candidate, not key components** — this is exactly what the four-check gate (§5) formalizes for semantic cache and what this document extends to every other type.

---

## 5. The Four-Check Gate (mandatory for every non-`EXACT` cache type)

Restated exactly from `conventions.md` §9.1 — a semantic-cache match must pass **all four** before serving:

1. Authorization check (`auth_check_passed: true`)
2. Freshness check (`freshness_valid: true`)
3. Tenant isolation check (`entry.tenant_id == request.tenant_id`)
4. Policy check (`cache_allowed: true`)

`SCN-CACHE-002` establishes the standard precisely: a 0.95-similarity candidate that fails even one check is `UNSAFE/INVALID` and served as a miss — **similarity alone is never sufficient**, and high similarity never overrides a failed check. This document extends the same four-check discipline to `TOOL_RESULT`, `EMBEDDING`, `APPLICATION`, and `CONTEXT` (each with the type-appropriate freshness/dependency check substituted where "freshness" as defined for semantic cache doesn't literally apply — see §6). `EXACT` and `PROVIDER_NATIVE` have their own narrower requirement sets per `conventions.md` §9.2 (Auth: Yes/Provider contract; Freshness: Yes/Provider contract) — restated per-type in §6.

**Anti-pattern this gate exists to prevent** (`conventions.md` §24.1/§24.2, cited exactly): "using semantic cache without freshness/authorization checks"; "treating semantic similarity as sufficient proof of cache safety." Every cache-type entry's Anti-Pattern Check field (§6) cites the applicable one(s) explicitly.

---

## 6. The Seven Cache-Type Policy Entries

Each entry uses the following schema. `N/A — not applicable to this entry.` is used where a field genuinely does not apply; no field is left blank.

### CACHE-001 — EXACT

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `EXACT` |
| **Owning Architecture Section(s)** | ARCH §11.4 (T1.6 Prompt Cache) |
| **Interface Contract** | `CacheStore.lookup/write` with `cache_type: EXACT`; `CacheLookupResult.hit_type: EXACT` |
| **Key Construction Template** | `{tenant_id}:EXACT:{namespace}:{content_hash}:{model_id}:{provider_id}` — `content_hash` over the full cacheable prefix (stable system instructions, tool definitions, few-shot examples, stable policy content per ARCH §11.4); `model_id`/`provider_id` included because an exact prefix match under one model is not guaranteed byte-identical in tokenized effect under another |
| **Namespace** | Per-application/per-prompt-template namespace, tenant-prefixed |
| **TTL Policy** | `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`: 24h (1440 min), renewable on access. `conventions.md` §9.2 requires a TTL for exact cache but states no duration — this default is this document's own engineering proposal, not a source-authoritative value |
| **Auth Check** | Yes (CONV §9.2) |
| **Freshness Check** | Yes (CONV §9.2) — via `policy_version` comparison (see TECH-009's SRP gating, `optimization-catalog.md`) |
| **Similarity Threshold** | N/A — not applicable to this entry (exact match only) |
| **Write Policy Guidance** | `IF_ABSENT` — an exact-match key for identical stable content should not need repeated rewriting; `ALWAYS` only when explicit invalidation (§10) has just occurred |
| **Invalidation Triggers** | Policy-version change (EC-027); explicit invalidation on system-instruction update; CL-003 "File changed"/"Permission changed" where the exact-cached prefix embeds file-derived or permission-derived content |
| **Dependency Rules** | `dependency_refs` populated when the cached prefix derives from a file/policy document (CL-003) |
| **Poisoning/Integrity Considerations** | Lower risk than `SEMANTIC` — an exact-match key cannot be similarity-manipulated, but content must still pass CIS (§13) before write if it derives from any non-first-party source |
| **PII/Sensitive-Data Gating** | `PIIClassifier` check mandatory before write (CONV §9.3); `DO_NOT_CACHE` if `is_safe_to_cache: false` |
| **Economics Estimation** | See §9 — for stable, high-repetition prefixes, `Future Reuse Probability` is typically high, making `EXACT` the first cache-strategy choice attempted (ARCH §16.1's decision order) |
| **Fragmentation Risks** | Highest of all seven types — see §11 in full; exact-match cache is uniquely vulnerable to timestamp/random-ID/ordering drift in the "stable" prefix |
| **Provider/Model Dependencies** | Cross-reference `provider-matrix.md` §4/§8/§12 for whether the selected provider/model exposes a provider-native equivalent (`PROVIDER_NATIVE`, CACHE-005) that should be preferred instead — do not restate provider facts here |
| **Composition/Ordering** | First cache-strategy choice attempted per ARCH §16.1's decision order (No cache → short-lived → long-lived → **exact** → semantic → provider-native → application-side); attempted before `SEMANTIC` (CACHE-002) on every lookup |
| **Revalidation** | On every hit, SRP re-checks `policy_version` (per `optimization-catalog.md` TECH-009) — never served without this check regardless of TTL remaining |
| **Observability Metrics** | `cache.prompt.hit_rate`, `cache.prompt.miss_rate`, `cache.prompt.write_cost`, `cache.prompt.read_cost`, `cache.prompt.tokens_reused` (from `optimization-catalog.md` TECH-009, reused verbatim) |
| **Anti-Pattern Check** | Guards against "claiming savings without accounting for optimization overhead" (CONV §24.1) via honest write-cost accounting; guards against cross-tenant collision (EC-028) via §4's structural key |
| **Validated By** | `SCN-CACHE-001` (exact cache hit returns without any model call) |
| **Traceability** | PS §6.7, §17.4; ES §7.7; ARCH §2, §11.4, §16.1; INTF-012 §6.1–6.4; CONV §9.1–9.2; EC-027, EC-028, EC-029 |

### CACHE-002 — SEMANTIC

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `SEMANTIC` |
| **Owning Architecture Section(s)** | ARCH §11.5 (T1.7 Semantic Cache) |
| **Interface Contract** | `CacheStore.lookup` with `cache_type: SEMANTIC`, `query_embedding` populated, `similarity_threshold` set; `CacheLookupResult.hit_type: SEMANTIC`, `similarity_score` populated |
| **Key Construction Template** | `{tenant_id}:SEMANTIC:{namespace}:{embedding_bucket}` — the "key" for lookup purposes is the query embedding compared against stored entries' embeddings within the tenant's namespace, not a deterministic hash; `embedding_bucket` denotes an implementation-level index partition, not a content-derived identity |
| **Namespace** | Per-task-type/per-application namespace, tenant-prefixed |
| **TTL Policy** | `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`: 60 min for general-purpose queries; 0 (disabled) for any task flagged time-sensitive/freshness-critical (EC-030) — the second value is a policy exclusion, not a duration |
| **Auth Check** | Yes (CONV §9.2), part of the four-check gate (§5) |
| **Freshness Check** | Yes (CONV §9.2), part of the four-check gate (§5) |
| **Similarity Threshold** | Global floor **0.92**, tenant-configurable upward only, never below the floor (`conventions.md` §9.2, verified against the live file) |
| **Write Policy Guidance** | `IF_FRESHER` — a new semantic-cache candidate for a similar query region should replace an existing entry only if its `freshness_score` is higher, never unconditionally |
| **Invalidation Triggers** | Explicit invalidation for mutable data (INTF §6.2 note); CL-003 "External data changed"; policy change; authorization/role change for the entry's original writer |
| **Dependency Rules** | `dependency_refs` populated when the underlying answer derives from a freshness-sensitive source (e.g., a retrieved document with its own version) |
| **Poisoning/Integrity Considerations** | **Mandatory, high-severity** — see §14 in full. `EC-078` (adversarial similarity manipulation to force a hit), `EC-079`/`SCN-CACHE-005` (adversarial model output used as the cache key itself), and the compound `EC-208` (malicious tool result poisons the semantic cache) all target this type specifically |
| **PII/Sensitive-Data Gating** | `PIIClassifier` mandatory before write (CONV §9.3); a semantic cache entry additionally carries provenance (§19) so a later-detected compromise can trigger targeted invalidation (EC-208) |
| **Economics Estimation** | Embedding-computation cost is a first-class term in `Cache Cost` for this type specifically — see `SCN-CACHE-004`: when computation cost exceeds expected benefit, the lookup is skipped entirely (`DO_NOT_OPTIMIZE` at the cache layer), not merely attempted and discarded |
| **Fragmentation Risks** | Low relative to `EXACT` — semantic matching tolerates surface-level drift by design; the risk here is the opposite failure (false-positive similarity, addressed by §5's gate, not by fragmentation remediation) |
| **Provider/Model Dependencies** | Embedding model choice is provider-neutral via adapter (root `CLAUDE.md` rule 1); do not assume embeddings from one embedding model are interchangeable with another (a version/model change invalidates comparability, not just freshness) |
| **Composition/Ordering** | Attempted only after an `EXACT` (CACHE-001) miss (ARCH §16.1's ordering; `optimization-catalog.md` TECH-010's Composition field: "semantic caching is only attempted on an exact-cache miss") |
| **Revalidation** | SRP's `freshness_valid` flag plus TTL check on every hit (ARCH §46.2.11); authorization/policy state evaluated **at hit time, not at write time** (SEC-007) — a previously-authorized writer's entry is re-checked against the *current* requester's authorization |
| **Observability Metrics** | `cache.semantic.hit_rate`, `cache.semantic.similarity_score`, `cache.semantic.freshness_check_pass_rate`, `cache.semantic.auth_check_pass_rate` (from `optimization-catalog.md` TECH-010) |
| **Anti-Pattern Check** | Directly guards against "using semantic cache without freshness/authorization checks" and "treating semantic similarity as sufficient proof of cache safety" (CONV §24.1/§24.2) — the four-check gate (§5) is the mechanism |
| **Validated By** | `SCN-CACHE-002` (rejects similar-but-unsafe candidate), `SCN-CACHE-005` (poisoning attempt via adversarial model output as key) |
| **Traceability** | PS §6.8; ES §7.8; ARCH §11.5, §16.1, §46.2.11; INTF-012 §6.1–6.4; CONV §9.1–9.3; EC-030, EC-031, EC-078, EC-079, EC-098, EC-208 |

### CACHE-003 — TOOL_RESULT

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `TOOL_RESULT` |
| **Owning Architecture Section(s)** | ARCH §13.3 (T3.3 Tool Result Cache) |
| **Interface Contract** | `CacheStore.lookup/write` with `cache_type: TOOL_RESULT` |
| **Key Construction Template** | `{tenant_id}:TOOL_RESULT:{namespace}:{tool_id}:{tool_version}:{normalized_params_hash}:{auth_context_hash}` — `normalized_params_hash` requires parameter normalization (ARCH §13.3) so that equivalent calls with reordered/whitespace-varying parameters still hit; `auth_context_hash` is included in the key (not left as a pure runtime check) specifically because `EC-034` demonstrates that a permission *reduction* must invalidate the entry, which is most reliably achieved by keying on the authorization state that produced the result |
| **Namespace** | Per-tool namespace, tenant-prefixed |
| **TTL Policy** | `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`: per-tool policy required (CONV §9.2 — "Yes (per-tool policy)"); no universal default is proposed because ARCH §23/`optimization-catalog.md` TECH-012 explicitly forbid "a single universal TTL across all tools." Illustrative starting points only: read-only reference-data tools (e.g., documentation retrieval) 60 min; volatile-data tools (e.g., database queries against frequently-mutated tables) 1–5 min; both illustrative and to be set per tool by whoever registers it |
| **Auth Check** | Yes (CONV §9.2) |
| **Freshness Check** | Yes (CONV §9.2) |
| **Similarity Threshold** | N/A — not applicable to this entry |
| **Write Policy Guidance** | `IF_ABSENT` for the first call of a given normalized-parameter set; `ALWAYS` when an explicit invalidation (permission change, dependency change) has just fired and the tool is re-called |
| **Invalidation Triggers** | Permission change (`EC-034`, mandatory); tool/MCP schema-version change (SEC-014, extends CL-003); dependency change via DA-024's event subscription; explicit invalidation |
| **Dependency Rules** | `dependency_refs` populated per the tool's declared data dependencies (e.g., a database-query tool's result depends on the queried table/row set, where knowable) |
| **Poisoning/Integrity Considerations** | A tool result must pass TMG's trust/schema-staleness check (independent of cost/ROI, SEC-014) and CIS screening before being cached — never cached "provisionally" pending a later check |
| **PII/Sensitive-Data Gating** | `PIIClassifier` before write (CONV §9.3); tool-specific — a tool known to return regulated data (e.g., audit-log queries) may be flagged non-cacheable entirely by policy regardless of economics |
| **Economics Estimation** | `Tokens Avoided` term is typically the tool's raw-output token count (pre-filtering, per `optimization-catalog.md` TECH-011's boundary: TOOL_RESULT caches the *filtered* result, not raw); `Cache Cost`'s `Freshness Risk` term should weight heavily for tools whose underlying data mutates frequently |
| **Fragmentation Risks** | Parameter-serialization drift (JSON key ordering, whitespace) is the dominant risk — remediated by the `normalized_params_hash` construction above, not by ad hoc fixes |
| **Provider/Model Dependencies** | N/A — not applicable to this entry (tool-result caching is provider-independent; tool/MCP identity is the relevant dependency, not the LLM provider) |
| **Composition/Ordering** | Downstream of TECH-011 (Tool Output Filtering) — the filtered result, not the raw tool output, is what this cache type stores (`optimization-catalog.md` TECH-012's Composition field, restated) |
| **Revalidation** | SRP subscribes to DA-024's dependency-change events (repository/branch/dependency/permission/external-state) rather than each tool implementing ad hoc invalidation; **never blindly replay a side-effecting or non-idempotent tool call from cache** — this cache type applies only to `idempotent: true`, side-effect-free tool calls (per `optimization-catalog.md` TECH-012's "Explicitly Not Applicable When": `idempotent: false` tools are never cached for reuse) |
| **Observability Metrics** | `cache.tool.hit_rate`, `cache.tool.miss_rate`, `cache.tool.calls_avoided`, `cache.tool.freshness_violations` (from `optimization-catalog.md` TECH-012) |
| **Anti-Pattern Check** | Guards against "caching mutable/permission-sensitive data without validation" (CONV §24.2) via the mandatory `auth_context_hash` key component and DA-024 subscription |
| **Validated By** | `SCN-CMP-017` (cached context item conflicts with newly retrieved version); `SCN-FILE-003` (cached file content changes after caching); `SCN-PERM-001` (permission revoked mid-execution invalidates affected entries) |
| **Traceability** | PS §6.22; ES §7.22; ARCH §13.3, §15.3, §46.2.8, §46.2.11, §47.2.3; INTF-012 §6.1–6.4; CONV §9.2, §11.5; EC-032, EC-033, EC-034, EC-035, EC-036, EC-099 |

### CACHE-004 — EMBEDDING

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `EMBEDDING` |
| **Owning Architecture Section(s)** | ARCH §16 (Cache Economics and Cache-Aware Assembly); supports `SEMANTIC` (CACHE-002) and retrieval/ranking generally |
| **Interface Contract** | `CacheStore.lookup/write` with `cache_type: EMBEDDING` |
| **Key Construction Template** | `{tenant_id}:EMBEDDING:{namespace}:{content_hash}:{embedding_model_id}:{embedding_model_version}` — `embedding_model_id`/`version` are mandatory key components, not optional: an embedding is not comparable across embedding-model versions |
| **Namespace** | Per-content-source namespace (e.g., per document collection), tenant-prefixed |
| **TTL Policy** | **Version-invalidated, not TTL-driven** — an embedding for unchanged content under an unchanged embedding model has no natural expiry; it becomes stale only when the source content changes (CL-003 "File changed"/"External data changed") or the embedding model version changes. Where a TTL is still desired as a storage-management backstop, `PROPOSED DEFAULT`: 30 days, purely for storage hygiene, not correctness |
| **Auth Check** | Yes — embeddings of tenant-scoped or permission-scoped content must not be looked up cross-tenant/cross-permission even though the embedding itself is a numeric vector, not raw content |
| **Freshness Check** | Yes — via `dependency_refs`/source-version comparison, not a TTL comparison (see TTL Policy above) |
| **Similarity Threshold** | N/A — not applicable to this entry itself (the embedding cache stores vectors; similarity thresholding is `SEMANTIC`'s (CACHE-002) concern when those vectors are later compared against a query) |
| **Write Policy Guidance** | `IF_ABSENT` — re-embedding unchanged content under the same model version is pure waste; `ALWAYS` only on a source-content or model-version change |
| **Invalidation Triggers** | CL-003 "File changed"/"Symbol changed"/"External data changed"; embedding-model migration (a full re-embed, not an incremental invalidation) |
| **Dependency Rules** | `dependency_refs` mandatory — every embedding entry must reference the exact source content/version it was computed from |
| **Poisoning/Integrity Considerations** | An embedding computed from unscreened externally-sourced content inherits that content's integrity risk when later used for semantic matching (§5's gate on the *consuming* `SEMANTIC` lookup partially mitigates this, but the embedding write itself should still pass CIS if its source is untrusted) |
| **PII/Sensitive-Data Gating** | `PIIClassifier` on the *source content* before embedding + write, not only on the resulting vector — a vector representation of PII is still sensitive derived data |
| **Economics Estimation** | `Write Cost` is dominated by the embedding-model inference cost itself; `Future Reuse Probability` is typically high for embeddings feeding `SEMANTIC`/retrieval (many future queries may compare against the same stored vector) |
| **Fragmentation Risks** | N/A — not applicable to this entry (fragmentation, as defined in §11, concerns cacheable *prompt prefixes*; embedding cache keys are content-hash-derived and immune to prompt-assembly-level fragmentation) |
| **Provider/Model Dependencies** | Embedding model is a provider capability; cross-reference `provider-matrix.md` for which providers/models expose an embedding endpoint — do not restate that fact here |
| **Composition/Ordering** | Upstream of `SEMANTIC` (CACHE-002) — an embedding-cache hit avoids re-computing the query/document vector; it does not by itself constitute an answer cache hit |
| **Revalidation** | Dependency-version check on lookup, not a freshness-score comparison (this type has no "staleness gradient" — content either matches its recorded version or it doesn't) |
| **Observability Metrics** | `cache.embedding.hit_rate`, `cache.embedding.miss_rate`, `cache.embedding.recompute_count`, `cache.embedding.model_migration_count` — `PROPOSED` metric names (no source-authoritative names exist for this specific type beyond the generic `CacheStore` accounting) |
| **Anti-Pattern Check** | Guards against assuming embeddings from one model/version are interchangeable with another (§6 CACHE-004 note) — a variant of "using provider-specific behavior as a universal architectural assumption" (CONV §24.2) applied to embedding models specifically |
| **Validated By** | No dedicated `SCN-CACHE-*` scenario currently isolates `EMBEDDING` specifically — it is exercised only implicitly as a precondition of `SCN-CACHE-002`/`SCN-CACHE-005`. Recorded as a thin-coverage note in §22, not fabricated as directly validated |
| **Traceability** | ARCH §15.2 (CL-002 Context Freshness Scoring), §15.3 (CL-003), §16; INTF-012 §6.1–6.3; CONV §9; EC-030 (by extension, freshness) |

### CACHE-005 — PROVIDER_NATIVE

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `PROVIDER_NATIVE` |
| **Owning Architecture Section(s)** | ARCH §25 (Provider/Model Optimization Profiles); `provider-matrix.md` §4/§8/§12 (owns all pricing/capability facts for this type) |
| **Interface Contract** | `CacheStore.lookup` with `cache_type: PROVIDER_NATIVE`; **absent from `CacheWriteRequest`'s enum** (INTF §6.4) — see `SOURCE-GAP-CACHESTRAT-03` (§31): writes for this type are plausibly provider-adapter-internal, never issued by core EAIOC write-path code, consistent with the boundary this entry states below |
| **Key Construction Template** | N/A — not applicable to this entry in the sense of an EAIOC-constructed key: `interfaces.md` §6.5's Cache Safety Requirements table states explicitly "Provider adapter manages; core treats as opaque." EAIOC's own responsibility is limited to *detecting* whether the provider exposes this capability (via `provider-matrix.md`'s per-model facts) and *negotiating* its use through the adapter — never constructing or managing the underlying key itself |
| **Namespace** | Provider-adapter-internal; not an EAIOC namespace |
| **TTL Policy** | **Provider-controlled, not an EAIOC policy default** (CONV §9.2: "Provider TTL"). `EC-029` is the canonical failure this boundary must respect: a provider's own cache TTL may differ from EAIOC's organizational policy TTL for the *logical* content, and the provider's cache is opaque to the Control Plane during that window — EAIOC's mitigation is at the *application-cache* layer (do not rely on provider-native TTL alone for security-sensitive content; apply an EAIOC-owned freshness check on top, per SEC-010) |
| **Auth Check** | Provider contract (CONV §9.2) — not independently verifiable by EAIOC core |
| **Freshness Check** | Provider contract (CONV §9.2) |
| **Similarity Threshold** | N/A — not applicable to this entry |
| **Write Policy Guidance** | N/A — not applicable to this entry (provider-adapter-managed, per Key Construction Template above) |
| **Invalidation Triggers** | Not directly controllable by EAIOC; EAIOC's own mitigation is to avoid relying on provider-native cache alone for content requiring immediate invalidation (e.g., a just-updated security policy, EC-027's failure mode) — use `EXACT`/`SEMANTIC` (EAIOC-owned) invalidation as the authoritative control, treating provider-native caching as a pure performance layer underneath it |
| **Dependency Rules** | N/A — not applicable to this entry |
| **Poisoning/Integrity Considerations** | Out of EAIOC's direct control; the mitigating control is that content admitted to *any* prompt (which the provider may then cache natively) has already passed EAIOC's own CIS screening (§13) before dispatch — the provider-native cache cannot contain content that bypassed that gate |
| **PII/Sensitive-Data Gating** | Same upstream gate as above — `PIIClassifier`/CIS run before dispatch, not after, so provider-native caching of already-admitted content does not itself require a separate PII check at this layer |
| **Economics Estimation** | `Effective Input Price` for provider-native cache reads/writes is a `provider-matrix.md` fact (per-model, VERIFIED/ILLUSTRATIVE-tagged there) — cite it, do not restate a number here |
| **Fragmentation Risks** | Identical mechanism to `EXACT` (§11) — provider-native prefix caching is fragmented by the same dynamic-timestamp/random-ID/ordering issues; the remediation playbook in §11 applies equally here since it operates on the same cache-aware prompt-assembly output (§12) that feeds both `EXACT` and `PROVIDER_NATIVE` |
| **Provider/Model Dependencies** | **This entire cache type is defined by provider dependency** — cross-reference `provider-matrix.md` for exactly which providers/models expose it, their pricing, and their TTL; never assume every provider or every model supports it (`provider-matrix.md`'s own Provider Accessibility Model applies: exposure is a capability fact, not a universal assumption) |
| **Composition/Ordering** | ARCH §16.1 lists "provider-native cache" as a distinct strategy choice, attempted per the same net-value comparison as `EXACT`/`SEMANTIC`/`APPLICATION` — not automatically preferred merely because it is provider-managed |
| **Revalidation** | N/A — not applicable to this entry (provider-managed) |
| **Observability Metrics** | Whatever the provider adapter surfaces (opaque to core, per INTF §6.5) — EAIOC's own measurement obligation is limited to *not conflating* a provider-native cache saving with an EAIOC application-cache saving (ARCH §47.9, AC-036) — see §20 |
| **Anti-Pattern Check** | Guards against "provider-specific caching silently weakening organizational security requirements" (SEC-010, EC-029) and against conflating provider-native savings with control-plane savings in measurement (AC-036) |
| **Validated By** | No dedicated `SCN-CACHE-*` scenario currently isolates `PROVIDER_NATIVE` directly; `EC-029` is the closest validating edge case. Recorded as thin coverage in §22 |
| **Traceability** | PS §41 CE; ARCH §11.4 note, §25, §47.9; INTF-012 §6.5; CONV §9.2; `provider-matrix.md` §4/§8/§12/§14; EC-029 |

### CACHE-006 — APPLICATION

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `APPLICATION` |
| **Owning Architecture Section(s)** | ARCH §16 (Cache Economics and Cache-Aware Assembly) — "application-side cache" is explicitly one of ARCH §16.1's seven strategy choices |
| **Interface Contract** | `CacheStore.lookup/write` with `cache_type: APPLICATION` |
| **Key Construction Template** | `{tenant_id}:APPLICATION:{namespace}:{artifact_identity}:{dependency_version}` — `artifact_identity` covers normalized application data, derived results, reusable task artifacts, and domain computations that are not themselves direct model responses |
| **Namespace** | Per-artifact-class namespace, tenant-prefixed |
| **TTL Policy** | `PROPOSED DEFAULT — VALIDATE LOCALLY BEFORE PRODUCTION USE`: 60 min, or version-invalidated where the artifact has a natural dependency version (preferred over TTL where available, consistent with CACHE-004's pattern) |
| **Auth Check** | Yes — treated identically to `TOOL_RESULT`/`SEMANTIC`'s requirement, applied to this type by this document since `conventions.md` §9.2's table does not enumerate `APPLICATION` explicitly; this is this document's own extension of the four-check discipline (§5), not a restated source fact — recorded as `PROPOSED METHODOLOGY` |
| **Freshness Check** | Yes, same basis as Auth Check above |
| **Similarity Threshold** | N/A — not applicable to this entry |
| **Write Policy Guidance** | `IF_FRESHER` where a dependency-version comparison is available; `IF_ABSENT` otherwise |
| **Invalidation Triggers** | CL-003's full trigger set, applied generically since `APPLICATION` artifacts may depend on files, permissions, or external data depending on what produced them |
| **Dependency Rules** | `dependency_refs` mandatory whenever the artifact was derived from a versioned source |
| **Poisoning/Integrity Considerations** | Same discipline as `SEMANTIC` (§14) if the artifact incorporates any externally-sourced or tool-derived content |
| **PII/Sensitive-Data Gating** | `PIIClassifier` before write (CONV §9.3), identical requirement to every other type |
| **Economics Estimation** | Same formula as §9; `Tokens Avoided` may be zero for a non-token artifact (e.g., a cached domain computation) — in that case substitute the equivalent compute/latency cost avoided, evidence-tagged `PROPOSED METHODOLOGY` since the source formula is expressed in token terms and this is an extension |
| **Fragmentation Risks** | Depends on the artifact's own serialization — apply §11's remediation playbook if the artifact is itself embedded in a later cacheable prompt prefix |
| **Provider/Model Dependencies** | Typically none — this is the one cache type most likely to be fully provider-independent, since it caches application-level, not model-invocation-level, artifacts |
| **Composition/Ordering** | Last of the four EAIOC-owned "content" strategy choices in ARCH §16.1's list (after exact, semantic, provider-native) — attempted when the artifact is not itself a direct model response |
| **Revalidation** | Dependency-version check preferred; TTL as backstop, per TTL Policy above |
| **Observability Metrics** | `cache.application.hit_rate`, `cache.application.miss_rate`, `cache.application.recompute_count` — `PROPOSED` metric names, no source-authoritative names exist for this generic type |
| **Anti-Pattern Check** | Guards against "treating a cache hit as a quality guarantee" (CONV §24.2) — an application artifact's cached correctness is only as good as its last dependency-version validation |
| **Validated By** | No dedicated `SCN-CACHE-*` scenario currently isolates `APPLICATION` specifically. Recorded as thin coverage in §22 — this is the least source-elaborated of the seven types, consistent with `SOURCE-GAP-CACHESTRAT-03`'s observation that it is also absent from `CacheWriteRequest`'s enum |
| **Traceability** | ARCH §16.1; INTF-012 §6.2 (lookup enum only, per §3's note); CONV §9 (by extension, §5 above) |

### CACHE-007 — CONTEXT

| Field | Value |
|---|---|
| **Cache Type (INTF §6.2 enum value)** | `CONTEXT` |
| **Owning Architecture Section(s)** | ARCH §15 (CL-001 Freshness Metadata Graph, CL-002 Context Freshness Scoring, CL-003 Dependency-Aware Cache Invalidation); ARCH §16.2 (Cache-Aware Context Construction) |
| **Interface Contract** | `CacheStore.lookup/write` with `cache_type: CONTEXT` |
| **Key Construction Template** | `{tenant_id}:CONTEXT:{namespace}:{context_fragment_id}:{context_version}` — covers context fragments, summaries, stable-context sections, repository maps, project rules, retrieval artifacts, and compressed context (per the V3 prompt's own enumeration of this type's coverage) |
| **Namespace** | Per-repository/per-project namespace where applicable, tenant-prefixed |
| **TTL Policy** | **Version/dependency-invalidated, not TTL-driven** — this type's freshness is governed by `context_version`/CL-003 dependency triggers, not a duration. Where a TTL backstop is still wanted for storage hygiene: `PROPOSED DEFAULT`: 7 days |
| **Auth Check** | Yes — a context fragment may embed permission-scoped content (e.g., a repository section the current identity may no longer be authorized to see) |
| **Freshness Check** | Yes — via CL-002's freshness metadata (Source, Version, Commit, Branch, Timestamp, Last verification time, Freshness status — restated verbatim from ARCH §15.2) |
| **Similarity Threshold** | N/A — not applicable to this entry |
| **Write Policy Guidance** | `IF_FRESHER`, compared on `context_version`/CL-002's freshness status |
| **Invalidation Triggers** | CL-003's full trigger table (§10) applies most directly to this type of any of the seven — file/symbol/branch/permission/external-data changes are all context-cache invalidation triggers by design |
| **Dependency Rules** | `dependency_refs` mandatory; this is also the type CL-004 (Reversible Optimization) most directly governs — a destructively-reduced context item should retain a recovery pointer per CL-004, which this cache type is a natural home for |
| **Poisoning/Integrity Considerations** | Same discipline as `SEMANTIC`/`APPLICATION` for any externally-sourced fragment |
| **PII/Sensitive-Data Gating** | `PIIClassifier` before write (CONV §9.3); additionally, **Tier 0 Mandatory context information (per the context-criticality model referenced in `optimization-catalog.md`) must never be silently evicted via a cache-reuse decision** — this is root `CLAUDE.md` rule 6 applied to the cache layer specifically |
| **Economics Estimation** | Same formula as §9; this is the type where `CE-002`'s cache-aware prefix-construction guidance (§12) most directly determines `Future Reuse Probability` — a well-constructed stable prefix raises this type's reuse probability across the board |
| **Fragmentation Risks** | High — subject to the same §11 remediation playbook as `EXACT`, since context fragments frequently compose into the cacheable prefix `EXACT`/`PROVIDER_NATIVE` ultimately serve from |
| **Provider/Model Dependencies** | Indirect — context-cache hit rate interacts with `EXACT`/`PROVIDER_NATIVE` cache-breakpoint placement (§12), which is provider-dependent (cite `provider-matrix.md`, do not restate) |
| **Composition/Ordering** | Feeds `EXACT`/`SEMANTIC`/`PROVIDER_NATIVE` as an upstream input (a context-cache hit reduces the work needed to *assemble* the prompt that those types then look up/write) — not a terminal cache-strategy choice on its own |
| **Revalidation** | CL-002's freshness metadata is checked on every lookup, not only at write time; DA-024 (for developer-agent contexts specifically) is the invalidation authority (§17) |
| **Observability Metrics** | `cache.context.hit_rate`, `cache.context.miss_rate`, `cache.context.dependency_invalidation_count`, `cache.context.recovery_pointer_used_count` — `PROPOSED` names for the generic type; developer-agent-specific metrics are DA-003/DA-022's (`optimization-catalog.md`), reused, not restated |
| **Anti-Pattern Check** | Guards against "removing context without provenance or recovery information where recovery is required" (CONV §24.2) via the CL-004 cross-reference above, and against "assuming larger context windows eliminate context-management problems" (CONV §24.2) by keeping this type's freshness/dependency discipline independent of context-window size |
| **Validated By** | `SCN-CACHE-004` (by extension — the economics-skip decision applies to this type's lookup attempts too); `SCN-CODE-002` (repository map, a `CONTEXT`-type artifact via DA-003, stale after branch switch) |
| **Traceability** | PS §34; ARCH §15.1–15.4, §16.2; INTF-012 §6.1–6.4; CONV §8.3, §9; EC-013, EC-014, EC-021, EC-022, EC-073 |

---

## 7. Semantic Cache Similarity Threshold Governance

The global floor is **0.92** (`conventions.md` §9.2, verified against the live file — not assumed). This floor exists because a lower threshold would admit similarity matches that are lexically/topically close but materially different in intent or required precision (the exact failure `EC-030`/`EC-078` describe). Per-tenant configurability may only raise the threshold, never lower it below 0.92 — a tenant wanting stricter cache safety may opt in; no tenant configuration can opt out of the floor.

**Similarity ≠ semantic equivalence ≠ authorization ≠ freshness ≠ policy validity.** These are five independent properties. A candidate clearing the similarity floor still must independently clear all four checks in §5 — the threshold governs whether a candidate is even considered, not whether it is served.

---

## 8. Cache Admission and Write Policy

### 8.1 Write Policy Enum Guidance (`CacheWriteRequest.write_policy`)

| Value | When to use |
|---|---|
| `IF_ABSENT` | First occurrence of a stable, low-volatility artifact (`EXACT`, `EMBEDDING`, first `TOOL_RESULT` call) — avoids redundant writes for content that shouldn't change without an explicit invalidation event |
| `ALWAYS` | Immediately following an explicit invalidation (§10), where the caller has already determined a fresh write is required and unconditional overwrite is safe |
| `IF_FRESHER` | Any type with a freshness/version comparison available (`SEMANTIC`, `APPLICATION`, `CONTEXT`) — prevents a stale concurrent write from clobbering a fresher existing entry |

### 8.2 Cache Eligibility Pipeline (order matters)

```
Tenant
  ↓
Authorization
  ↓
Policy
  ↓
Data Governance (PII/classification, §13)
  ↓
Security/Integrity (CIS, §13)
  ↓
Freshness model (per type, §6/§17)
  ↓
Cache type selection (ARCH §16.1's ordering)
  ↓
Expected Reuse Probability
  ↓
Economics evaluation (§9)
  ↓
Write decision
```

A result is never written to cache merely because it is technically reusable — every stage above must pass, in this order, with the governance stages (tenant → security/integrity) always preceding the economics stages, never the reverse. This ordering is the cache-layer instance of root `CLAUDE.md` rule 2's precedence: an optimization-layer (economics) decision never substitutes for or precedes a governance decision.

---

## 9. Cache Economics — Estimation Methodology, Not Fabricated Constants

Restated exactly from `architecture.md` §16.1 / `conventions.md` §9.4:

```
Expected Cache Benefit = Future Reuse Probability × Tokens Avoided × Effective Input Price
Cache Cost = Write Cost + Storage Cost + Invalidation Cost + Freshness Risk + Cache Miss/Break Risk
```

A cache write executes only when `Expected Cache Benefit > Cache Cost`. This document proposes the following estimation methods; every one is `PROPOSED METHODOLOGY` unless otherwise cited, not a source-authoritative formula:

| Term | Proposed estimation method |
|---|---|
| Future Reuse Probability | Historical hit-rate tracking per key-pattern/task-type bucket, decayed over a rolling window; cold-start default derived from the cache type's typical reuse profile (e.g., `EXACT` prefixes for system instructions default higher than `TOOL_RESULT` for a highly parameterized query) |
| Tokens Avoided | `CacheEntry.token_count` of the candidate entry, measured, not estimated |
| Effective Input Price | Sourced from `provider-matrix.md`'s per-model `ModelProfile` data — never re-derived or hard-coded here |
| Write Cost | Measured: the actual compute/latency cost of the write operation (embedding computation for `SEMANTIC`/`EMBEDDING`; serialization/storage call for others) |
| Storage Cost | Function of entry size × retention duration × storage-tier unit cost — a deployment-specific constant, `SOURCE-GAP` at the EAIOC-policy level (deployment infrastructure determines this, not this document) |
| Invalidation Cost | Cascade breadth (§10) — the more dependents a cascade must invalidate, the higher this term; estimable from `dependency_refs` graph fan-out |
| Freshness Risk | Function of the source's known mutation rate (e.g., a database-query tool result's risk scales with the queried table's write frequency, where knowable) — `PROPOSED METHODOLOGY`, no source-authoritative model exists |
| Cache Miss/Break Risk | Historical false-negative rate (candidate existed but was incorrectly rejected) plus false-positive rate (candidate was incorrectly served, caught downstream) |

**Cache lookup overhead is itself a cost.** `SCN-CACHE-004` establishes that when the lookup's own cost (e.g., embedding computation for a `SEMANTIC` attempt) exceeds the expected benefit, the correct decision is `DO_NOT_OPTIMIZE` at the cache layer — skip the lookup attempt entirely, record it as a `SkippedStage` with reason `COST_EXCEEDS_BENEFIT`, and proceed directly to the full pipeline. This is not a fallback-after-failure; it is a decision made *before* attempting the lookup.

**Negative-optimization outcomes this document supports:** `DO_NOT_CACHE`, `DO_NOT_REUSE`, `BYPASS_CACHE`, `REVALIDATE` — used whenever the economics comparison, the four-check gate, or a governance check (§8.2) fails. None of these is a fallback failure state; each is a correct, expected outcome of the admission pipeline.

---

## 10. Dependency-Aware Cache Invalidation (CL-003)

Restated exactly from `architecture.md` §15.3:

| Trigger | Action |
|---|---|
| File changed | Invalidate symbol summaries |
| Symbol changed | Invalidate dependent context |
| Branch changed | Invalidate branch-specific cache |
| Permission changed | Invalidate affected cache entries |
| External data changed | Invalidate freshness-sensitive semantic cache |

**Full cascade schema**, extending the table above into `CacheInvalidationRequest`'s real fields (`interfaces.md` §6.4):

| `CacheInvalidationRequest.reason` | Typical trigger | `cascade` | Cache types affected |
|---|---|---|---|
| `EXPLICIT` | Manual/API-triggered invalidation | Caller-specified | Any |
| `STALENESS` | TTL expiry or freshness-check failure | Usually `false` (self-contained) | `EXACT`, `SEMANTIC`, `TOOL_RESULT`, `APPLICATION` |
| `PERMISSION_CHANGE` | Role/permission revocation (`EC-034`) | `true` (cascades to all cache types keyed on the affected authorization state) | All seven |
| `DEPENDENCY_CHANGE` | CL-003 "Symbol changed"/"External data changed" | `true` | `CONTEXT`, `TOOL_RESULT`, `EMBEDDING`, `CACHE-004`/`007` most directly |
| `BRANCH_CHANGE` | CL-003 "Branch changed" | `true` for repository-scoped entries | `CONTEXT` (repository maps, DA-003), `TOOL_RESULT` (repository-metadata tools) |
| `POLICY_CHANGE` | Policy-version update (`EC-027`) | `true` | `EXACT` (cached system prompts), `SEMANTIC` |

This document supplies the concrete cascade policy DA-024 (Context Invalidation Engine, `optimization-catalog.md`) executes — it does not redefine DA-024's own algorithm. `DA-024`'s stated fallback ("on invalidation-propagation failure, the conservative choice is to over-invalidate... never to under-invalidate") is the correctness principle this cascade must honor: a missed invalidation (`EC-023`) is a correctness bug, never an acceptable trade-off.

**Canonical example this cascade must prevent:** `EC-034` (Cached Tool Result Returned After Permission Change) — a `PERMISSION_CHANGE` invalidation must reach every cache entry keyed (directly or via `dependency_refs`) on the revoked authorization state, not only the entry the triggering request happens to touch.

---

## 11. Cache Fragmentation — Detection and Remediation

Restated from `architecture.md` §16.3 / `conventions.md` §9.5, each with a concrete remediation:

| Prohibited pattern | Remediation |
|---|---|
| Dynamic timestamps in system instructions | Move timestamps to the volatile region (§12); never interpolate a current-time value into the stable prefix |
| Random identifiers in tool schemas | Use deterministic, content-derived IDs (e.g., a hash of the tool's own schema) instead of a per-request UUID in schema-level fields |
| Tool ordering changes | Sort tool definitions deterministically (e.g., alphabetically by `tool_id`) before serialization — never rely on registration/discovery order |
| Serialization changes | Use a canonical serializer (stable key ordering, consistent number formatting) for any structure that enters the stable prefix |
| Whitespace/format changes | Normalize whitespace in stable-prefix content at construction time, not left to the underlying template engine's default output |
| Dynamic metadata in stable prefixes | Move per-request metadata to the volatile region; the stable prefix carries only content stable across the metadata's expected variation |
| Changing system instructions | Version system instructions explicitly (`policy_version`) rather than mutating in place — a version bump is a deliberate, observable invalidation trigger (§10's `POLICY_CHANGE`), not silent fragmentation |
| Changing tool definitions | Version tool schemas explicitly (SEC-014's schema-staleness check) |
| Unstable context ordering | Apply a deterministic ordering rule (e.g., CL-002's freshness/priority ordering) to any context fragment set entering the stable prefix |

**Estimating "recoverable cache value"** (ARCH §16.3's phrase): compare the historical hit rate immediately before vs. after a suspected fragmentation event for the same logical prefix — a sharp, otherwise-unexplained hit-rate drop coinciding with a code/config change is the diagnostic signal; the recoverable value is the estimated benefit (§9) that would be restored by fixing the fragmentation source. `PROPOSED METHODOLOGY`.

**Boundary:** do not "fix" fragmentation by removing content that is semantically meaningful merely because it varies — only genuinely incidental variation (formatting, ordering, non-semantic identifiers) is a fragmentation target. Removing a timestamp that is actually load-bearing for the task is a correctness bug, not a fragmentation fix.

---

## 12. Cache-Aware Context Construction and Breakpoint Optimization

Restated from `architecture.md` §16.2 / `conventions.md` §8.3's three-region model:

| Region | Contents |
|---|---|
| **STABLE / CACHEABLE PREFIX** | System instructions, stable developer policies, stable tool schemas, stable project rules, stable examples, stable repository metadata |
| **SEMI-STABLE REGION** | Project documentation, memory, slowly changing context |
| **VOLATILE REGION** | Current user request, current files, current errors, current tool results, current task state |

The optimizer's mandate (ARCH §16.2) is to minimize unnecessary changes to the stable prefix — every remediation in §11 serves this mandate directly. Placement guidance: assemble the prompt so the stable region is contiguous and always appears first (a requirement of provider prefix-caching mechanics generally, per `provider-matrix.md`'s cache-pricing notes for the providers that expose `PROVIDER_NATIVE` caching); never interleave volatile content within what would otherwise be a stable block, since that fragments the entire prefix rather than only the volatile portion.

**Pre-warming (CE-004):** pre-warm a high-value stable prefix only when (a) the provider supports it (`provider-matrix.md` per-model fact, never assumed), (b) expected first-request latency/cost reduction justifies the pre-warm cost (§9's economics comparison, applied to the pre-warm operation itself), and (c) governance requirements (§8.2) are already satisfied for the content being warmed. Do not indiscriminately pre-warm every cache.

**Breakpoint optimization (CE-005):** where a provider's semantics permit multiple cache boundaries (a `provider-matrix.md` capability fact — do not assume universal support), choose breakpoints by context stability and expected reuse frequency: place a breakpoint immediately after the most stable, least-frequently-changing content block, and another after the next-most-stable block, rather than a single breakpoint at the very end of the stable region.

---

## 13. Cache Poisoning Defenses and PII Gating

### 13.1 Content-Integrity Ordering (mandatory)

```
Content Integrity Screening (CIS)
  → Cache Admission
```

This document does not define CIS's own mechanism (deferred to `security.md`, not yet generated) — it states only the ordering invariant. **If CIS screening fails, the outcome is `DO_NOT_CACHE`, unconditionally.** A favorable economics calculation (§9) never overrides a failed integrity, authorization, policy, or data-governance result — this is root `CLAUDE.md` rule 2 applied at the cache-write boundary specifically.

### 13.2 Cache Key Poisoning

A cache key must never be derived directly from unscreened model output or untrusted content (`SCN-CACHE-005`: adversarial model output used as the cache key itself is a documented attack). Separate **trusted identity inputs** (tenant ID, tool ID, normalized parameters, content hashes of already-screened content) from **untrusted content** (raw model output, raw tool results before filtering) when constructing any key template in §6 — every key template in this document is built from the former category only.

### 13.3 Semantic Cache Poisoning (the highest-severity case)

`EC-208` (Malicious Tool Result Poisons the Semantic Cache, CRITICAL, compound) is the canonical worst case: content that evades *both* TMG's trust check and CIS screening, then gets written into `SEMANTIC` (CACHE-002), extends a single-request compromise into every future request the cache serves until the entry expires or is invalidated. The required defense, restated from `edge-cases.md` EC-208's Expected Behavior:

1. Every `SEMANTIC` (and, by extension, `APPLICATION`/`CONTEXT`) cache entry derived from tool/MCP-origin content carries provenance metadata (§19) linking back to the originating tool call and its trust/screening result.
2. If the originating tool is later quarantined, or the content is later flagged by post-hoc detection, **all cache entries derived from it are invalidated as part of that response** — not left to expire naturally on TTL.
3. This extends DGE's deletion-propagation traversal model to security-driven cache invalidation, not only privacy-driven deletion (§15).

### 13.4 PII and Sensitive Data (restated exactly, `conventions.md` §9.3)

Every cache entry, of every type, must be checked by `PIIClassifier` before writing. If `is_safe_to_cache: false`, the entry is not cached — regardless of §9's economics result. Do not unnecessarily duplicate sensitive content into cache *metadata* (provenance fields, observability logs) even when the primary content is legitimately cacheable.

---

## 14. Semantic Cache Poisoning — Cross-Reference Summary

(Consolidated for traceability; the substantive treatment is §13.3 and CACHE-002's entry in §6.)

| Edge Case | Failure mode | Defense |
|---|---|---|
| `EC-078` | Adversarial similarity manipulation to force a semantic-cache hit | §5's four-check gate — similarity alone is never sufficient |
| `EC-079` | Poisoned semantic-cache key from adversarial model output | §13.2's trusted-identity-inputs discipline |
| `EC-030` | Stale result served to a time-sensitive query | §5 freshness check + explicit time-sensitivity exclusion (CACHE-002's TTL Policy) |
| `EC-031` | Semantic cache authorization bypass across users | §5 authorization check, re-evaluated at hit time (SEC-007) |
| `EC-208` | Compound: malicious tool result evades TMG+CIS, poisons semantic cache | §13.3's provenance-linked, security-driven invalidation |

---

## 15. Deletion / Erasure Propagation

Deleting a source does not automatically delete all derived cache representations — this must be an explicit propagation, not an assumption. Derived artifacts requiring propagation: `SEMANTIC` entries, `EMBEDDING` entries, summaries stored as `CONTEXT`, compressed context, `TOOL_RESULT` entries, repository maps (DA-003, a `CONTEXT`-type artifact), and any `APPLICATION` artifact derived from the deleted source. This document extends DGE's deletion-propagation traversal model (`edge-cases.md` EC-167, cited by EC-208's own Expected Behavior as the model to extend) to cache invalidation generally: a deletion request must walk `dependency_refs` the same way a `DEPENDENCY_CHANGE` invalidation (§10) does, not merely delete the one entry the deletion request names directly.

---

## 16. Cache Consistency Models

Not every cache type warrants the same consistency guarantee:

| Cache type | Consistency model |
|---|---|
| `EXACT` | Strongly validated (SRP checks on every hit) |
| `SEMANTIC` | Strongly validated (four-check gate on every hit) |
| `TOOL_RESULT` | Event-invalidated (DA-024 subscription) + TTL backstop |
| `EMBEDDING` | Version-consistent (dependency-version match required) |
| `PROVIDER_NATIVE` | Provider-contract-consistent (opaque to EAIOC) |
| `APPLICATION` | Version-consistent where available, else eventually consistent with a TTL backstop |
| `CONTEXT` | Version-consistent (CL-002 freshness metadata) |

Imposing a single uniform consistency model (e.g., "everything is TTL-based") across all seven types would either under-protect the strongly-validated types or over-invalidate the version-consistent ones — this table's differentiation is intentional, not an oversight.

---

## 17. Stale Result Protection (SRP) — Data This Document Supplies

SRP's own algorithm (`architecture.md` §46.2.11) is not redefined here. Per type, the cache-layer data SRP consumes:

| Cache type | SRP input |
|---|---|
| `EXACT`/`PROVIDER_NATIVE` | `policy_version` comparison on every hit |
| `SEMANTIC` | `freshness_valid` flag + TTL check |
| `TOOL_RESULT` | Dependency-change event subscription (via DA-024) |
| `EMBEDDING`/`CONTEXT` | Dependency/source-version comparison |
| `APPLICATION` | Whichever of the above applies per the artifact's own dependency structure |

**Never serve a stale result silently.** On a stale detection, SRP's possible actions — `REVALIDATE`, `REFETCH`, `RECOMPUTE`, `BYPASS`, `INVALIDATE` — are SRP's to choose; this document's obligation is only to ensure the cache layer emits the `StaleResultEvent` SRP needs to make that choice, and to never suppress a stale signal to preserve an apparent hit rate.

---

## 18. Cache Miss Diagnosis (`explain_miss`)

`CacheMissReason.reason` enum, restated exactly from `interfaces.md` §6.2: `NOT_FOUND`, `EXPIRED`, `STALE`, `AUTH_FAILED`, `POLICY_PROHIBITED`, `SIMILARITY_BELOW_THRESHOLD`, `MUTABLE_DATA`, `PROVIDER_UNAVAILABLE`.

| Reason | Diagnosis logic |
|---|---|
| `NOT_FOUND` | No key/embedding match in the tenant's namespace at all |
| `EXPIRED` | TTL elapsed (types with a TTL policy per §6) |
| `STALE` | Freshness/dependency-version check failed (§5, §17) |
| `AUTH_FAILED` | Authorization check failed (§5) — the explanation must not reveal *why* authorization failed if that itself would leak protected information (e.g., must not say "the entry belongs to a higher-privilege role" in a way that discloses role structure to an unauthorized caller) |
| `POLICY_PROHIBITED` | Policy check failed (§5), or the content class is policy-excluded from caching entirely (e.g., a PII-classified item, §13.4) |
| `SIMILARITY_BELOW_THRESHOLD` | `SEMANTIC` only — candidate similarity score below the governed floor (§7) |
| `MUTABLE_DATA` | The underlying data category is flagged mutable without an applicable freshness policy (INTF §6.5's Cache Safety Requirements table) |
| `PROVIDER_UNAVAILABLE` | `PROVIDER_NATIVE` only — the provider's own cache infrastructure is unreachable/degraded (a CAR-adjacent condition, not redefined here) |

`explain_miss()`'s output must never expose protected information beyond identifying which of these eight categories applied — no entry content, no other tenant's existence, no internal authorization-model detail.

---

## 19. Cache Provenance

For every materially-reused cache artifact (any hit that actually influences a served result, not merely inspected), sufficient provenance must be retrievable to determine: origin, creation time, tenant, authorization state at write time, policy version at write time, `context_version`/`workflow_version` where applicable, provider/model (where relevant per §6), originating tool/source, repository/version (developer-agent contexts), applied transformations, `dependency_refs`, last validation time, and the reuse decision itself (served / rejected-and-why). This is the data §13.3's security-driven invalidation and §15's deletion propagation both depend on. Do not log sensitive payloads into provenance metadata unnecessarily — provenance identifies *what produced* an entry, not a duplicate copy of potentially sensitive *content*.

---

## 20. Provider-Native vs. EAIOC-Owned Cache — Measurement Separation

Restated from `architecture.md` §47.9 (Ownership Boundaries, H18): cache optimization is "owned and implemented directly, built on provider-exposed cache primitives where available." **A measurement that attributes a provider-native cache saving (CACHE-005) to a control-plane cache saving (CACHE-001–004, 006–007), or vice versa, is invalid per `AC-036`.** Every observability metric in §6's per-type entries is scoped to exactly one cache type for this reason — do not aggregate `cache.prompt.*` (EAIOC-facing accounting of `EXACT`) with whatever the provider adapter reports for `PROVIDER_NATIVE` into a single combined "cache savings" figure without explicitly labeling the two components.

---

## 21. Agent Memory vs. Cache

**Agent may own working memory, but the Control Plane owns execution truth** (restated from the Dynamic Execution amendment, `architecture.md` §46). Agent memory (scratch state, temporary plans, transient observations, intermediate reasoning) is never automatically treated as authoritative cache state, and is never substituted for a `CacheEntry`'s own authorization/policy/freshness metadata. An agent's belief that a result is still valid does not override this document's checks (§5, §17) — current authorization, policy, workflow, and external state always override stale or conflicting agent memory, exactly as they override a stale cache entry.

---

## 22. Developer-Agent, Multi-Agent, and Tool/MCP Cache Coverage

### 22.1 Developer-Agent Cache (DA-003, DA-022, DA-024 — cited, not redefined)

This document supplies the concrete key schema and TTL/invalidation policy for artifacts `optimization-catalog.md`'s DA-003/DA-022 define at the technique level:

- **Repository map (DA-003):** `{tenant_id}:CONTEXT:{repo_id}:repo_map:{commit_sha}` — invalidated on new commit (CL-003 "File changed") and on branch switch/force-push (`EC-073`, `SCN-CMP-037`), never silently reused across an incompatible working-tree state.
- **Symbol indexes, AST-derived information, file summaries:** same `{repo_id}:{commit_sha}` scoping pattern, `CONTEXT` type.
- **Git diffs, search results, compiler/test/linter output, terminal output:** `TOOL_RESULT` type, keyed additionally by the invoking command/tool version.
- **Sub-agent results, patch-impact analysis:** `APPLICATION` type, keyed by the originating task/workflow version.

A repository-map cache entry from commit A is never automatically valid for an incompatible working-tree state B — this is enforced structurally by including `commit_sha` in the key, not by a post-hoc validity check.

### 22.2 Multi-Agent / Sub-Agent Cache

A sub-agent result cached under `APPLICATION` is reusable by a parent or sibling task only when its recorded assumptions — tenant, authorization, `context_version`, `workflow_version`, tool dependencies, provider/model, freshness — remain valid for the *new* consuming context, checked via the same §5/§8.2 pipeline as any other cache candidate. A sub-agent's own cache write must never bypass tenant isolation to become visible to a different tenant's agent, even within the same multi-agent workflow (root `CLAUDE.md` rule 4, absolute).

### 22.3 Tool/MCP Cache — Capability Metadata vs. Results vs. Side Effects

Three distinct sub-cases within `TOOL_RESULT` (§6, CACHE-003) and its adjacent capability data:

1. **Capability metadata** (which tools/MCP servers exist, their schemas) — potentially longer-lived than any individual result, but subject to TMG's version/availability/authorization checks (SEC-014) before being trusted, independent of this document's cache-freshness mechanics.
2. **Read-only tool results** — cacheable per CACHE-003's full policy.
3. **Side-effecting operations** — **never cached for reuse.** A cached result must never be interpreted as permission to replay a side effect (payment, email, deployment, database mutation, ticket creation, infrastructure change, file mutation). This is CACHE-003's "Explicitly Not Applicable When" field, restated with emphasis: cache reuse and side-effect replay are categorically different operations, and this document's entire `TOOL_RESULT` policy applies only to the former.

---

## 23. Recovery / Checkpoint Interaction

**Resume != replay.** A cache artifact may assist recovery (e.g., a still-valid `TOOL_RESULT` avoids re-executing a tool during resume) but must be reconciled against current state via the same protocol RCO already runs (PRV → DPE → CAR → WVM consult → SRP → RE, per `conventions.md` §22.4) before being trusted — this document does not add a cache-specific bypass of that protocol. Specifically: a stale checkpoint referencing a cache entry that has since been invalidated (§10) must not be treated as still valid merely because the checkpoint itself is otherwise intact; a changed permission, workflow, repository, or external state between checkpoint and resume invalidates the *cache entries* the checkpoint refers to exactly as it would invalidate them for a live request. Non-idempotent side effects recorded in a checkpoint's `completed_actions` are never replayed from a `TOOL_RESULT` cache entry — §22.3's rule applies identically during recovery.

---

## 24. Cache Stampede Protection

Where multiple concurrent requests would independently attempt the same expensive cache-miss recomputation (a "hot key"), apply request coalescing / single-flight execution: the first request performs the recompute and write; concurrent identical requests wait on that in-flight operation rather than each independently recomputing. This must not weaken tenant isolation — coalescing occurs only within a single tenant's own namespace (consistent with §4's structural key design and the scenario-matrix's own note that "the concurrency control needed is only within each tenant's own namespace, not across them"). Stale-while-revalidate (serving the previous value while a refresh runs in the background) is permitted only for cache types whose consistency model (§16) already tolerates eventual consistency — never for `EXACT`/`SEMANTIC`'s strongly-validated types.

---

## 25. Cache Failure Modes

| Failure | Handling |
|---|---|
| Cache store unavailable/timeout | Fall back to the unoptimized/full-pipeline path — an optimization-stage failure, fail-open per root `CLAUDE.md` rule 2, provided the request remains authorized/policy-compliant |
| Corrupted entry | Treat as `NOT_FOUND`/miss; do not attempt partial recovery of a corrupted entry |
| Storage exhaustion | Evict per the storage tier's own policy (deployment-specific, `SOURCE-GAP` at this document's level); never silently fail writes without surfacing the condition to observability |
| Semantic-search/index failure | Fall back to `EXACT`-only lookup (skip the `SEMANTIC` attempt) rather than blocking the request |
| Invalidation failure | Per DA-024's own fallback: over-invalidate rather than under-invalidate |
| Authorization/policy service unavailable (needed for §5's checks) | **This is a governance-check failure, not an optimization-stage failure — fail closed** (root `CLAUDE.md` rule 2's distinction): if the four-check gate cannot be evaluated, the candidate is treated as a miss, never served on the assumption that it would have passed |
| Cache poisoning detected | §13.3's provenance-linked invalidation; never "fail open" on a detected poisoning event |

**Governing principle:** non-critical cache failure (store unavailable, index failure) does not automatically become task failure — the request proceeds unoptimized. Security-critical cache *validation* failure (the checks in §5 cannot be evaluated) requires fail-closed behavior at the cache layer specifically, even though the broader request may still proceed via the full, uncached pipeline if authorization/policy can be established through the normal (non-cache) path.

---

## 26. Cache Observability

Metrics beyond the per-type lists in §6: `cache.lookups_total`, `cache.hits_total`, `cache.misses_total`, `cache.partial_hits_total`, `cache.stale_hits_rejected_total`, `cache.invalidations_total` (broken out by `CacheInvalidationRequest.reason`), `cache.revalidations_total`, `cache.bypasses_total` (broken out by reason — `COST_EXCEEDS_BENEFIT`, governance failure, etc.), `cache.poisoning_detections_total`, `cache.auth_failures_total`, `cache.policy_failures_total`, `cache.tenant_mismatches_total` (expected always 0, per §4's structural guarantee — a nonzero value here indicates a key-construction defect), `cache.lookup_latency_ms`, `cache.validation_latency_ms`, `cache.tokens_avoided_total`, `cache.model_calls_avoided_total`, `cache.net_optimization_value` (per §9's formula, honestly computed, never fabricated per root `CLAUDE.md` rule 5). **A cache hit is not the same measurement as a successful optimization** — the latter requires the full §9 net-value accounting, not merely a `hit: true` flag.

---

## 27. Implementation-Neutral Pseudocode (illustrative only — not an API specification)

```text
function lookup(request):
    entry_candidates = index.find_candidates(request.tenant_id, request.cache_type, request.key or request.query_embedding)
    if entry_candidates is empty:
        return miss(NOT_FOUND)
    candidate = select_best(entry_candidates, request.similarity_threshold)   # SEMANTIC only
    if not passes_four_check_gate(candidate, request):                        # §5
        return miss(appropriate_reason)
    return hit(candidate)

function write(request):
    if not pii_classifier.is_safe_to_cache(request.entry):                    # §13.4
        return reject(DO_NOT_CACHE)
    if not cis.screen(request.entry).PASS:                                    # §13.1
        return reject(DO_NOT_CACHE)
    if not eligibility_pipeline_passes(request):                              # §8.2
        return reject(governance_reason)
    if expected_cache_benefit(request) <= cache_cost(request):                # §9
        return reject(COST_EXCEEDS_BENEFIT)
    return store.write(request, policy=write_policy_for_type(request.cache_type))

function invalidate(trigger):
    affected = dependency_graph.resolve(trigger)                              # §10, CL-003
    for entry in affected:
        store.invalidate(entry)
    emit(InvalidationEvent, trigger, affected.count)
```

This pseudocode is illustrative of the ordering this document specifies; it introduces no field or method not already present in `interfaces.md` §6.

---

## 28. Test Strategy

| Category | Representative tests |
|---|---|
| Functional | hit, miss, partial hit (semantic near-miss failing one check), stale, expiry, invalidation, refresh — per cache type (§6) |
| Security | tenant mismatch (`SCN-CACHE-003`), authorization revocation mid-session (`EC-034`), policy change (`EC-027`), PII rejection (§13.4), poisoning (`SCN-CACHE-005`, `EC-208`), prompt injection surviving into a cached artifact |
| State | `context_version`/`workflow_version` change, repository/branch change (`EC-073`), external-dependency change |
| Provider | model/provider switch mid-cascade invalidating `PROVIDER_NATIVE`/`EXACT` assumptions (cross-reference `provider-matrix.md`'s CAR-failover treatment — not redefined here), provider-native cache unavailable |
| Agents | multi-step agent reusing a sub-agent result across steps, developer-agent repository-map staleness after branch switch |
| Recovery | checkpoint resume with a since-invalidated cache entry (§23), superseded execution, non-idempotent tool call never replayed from cache |
| Performance | cache stampede/hot-key coalescing (§24), storage exhaustion, invalidation-cascade breadth stress test |

---

## 29. Scope Boundary

| Content | Lives In |
|---|---|
| Problem definition | `Ent_Agent_LLM_Inference_Opt_Control_Plane_problemstatement.txt` |
| Architecture | `architecture.md` |
| Interfaces | `interfaces.md` |
| Conventions | `conventions.md` |
| Edge cases | `edge-cases.md` |
| Scenario validation | `scenario-matrix.md` |
| Optimization technique/DA module definitions | `optimization-catalog.md` |
| Provider/model capability facts, pricing | `provider-matrix.md` |
| **Cache-key schemas, TTL policy, economics tuning, invalidation cascade, poisoning defenses** | **`cache-strategy.md` (this document)** |
| Agent-loop-level optimization detail | `agent-optimization.md` (not yet generated) |
| Inference-infrastructure mechanism (KV-cache internals, etc.) | `inference-optimization.md` (not yet generated) |
| CIS content-integrity screening mechanism | `security.md` (not yet generated) |
| Quality-gate thresholds/methodology | `quality-gates.md` (not yet generated) |

This document does not duplicate any of the above; every cross-reference cites rather than restates.

---

## 30. Cross-Document Coverage Matrix

| Source document | Coverage in this document | Note |
|---|---|---|
| Problem Statement | Moderate (§6.7, §6.8, §6.21, §6.22, §17.4, §25, §34, §35, §41) | Cache-specific PS sections are well-covered; broader PS sections are out of this document's scope by design |
| Engineering Specification | Moderate (§7.7, §7.8, §7.21, §7.22, §10.1) | Mirrors ARCH's own cache sections at the ES elaboration level |
| Architecture | Strong (§11.4, §11.5, §13.2, §13.3, §15.1–15.7, §16.1–16.5, §25, §46.2.8, §46.2.11, §47.2.3, §47.9) | This is the document's primary architectural grounding |
| Interfaces | Strong (§6.1–6.5 fully restated in Appendix A; §21.3 TenantIsolationContext) | One schema gap noted (`SOURCE-GAP-CACHESTRAT-03`, §31) |
| Conventions | Strong (§8.3, §9.1–9.5, §11.3, §11.5, §13.1–13.2, §24.1–24.2) | The primary convention-level grounding for this document's mandatory rules |
| Edge Cases | Strong for `EC-027–036` (Domains D/E/I directly); moderate for `EC-013/014/021/022/023/073/098/099` (cited where materially relevant); `EC-208` (compound, CACHE-002-relevant) | Not every one of the 213 edge cases is cache-specific — see §22's classification discipline |
| Scenario Matrix | Strong for `SCN-CACHE-001–005`; supporting citations to `SCN-TEN-001`, `SCN-PERM-001`, `SCN-PERM-003`, `SCN-FILE-003`, `SCN-CMP-006/017/037`, `SCN-CODE-002`, `SCN-POL-003` | Not every one of the 263 scenarios is cache-specific |
| `optimization-catalog.md` | Strong (`TECH-009/010/011/012`, `DA-003/022/024` all cited by ID) | This document supplies exactly what those entries' schemas deferred here; no new `TECH-NNN`/`DA-NNN` created |
| `provider-matrix.md` | Moderate (§4/§8/§12/§14 cited for `PROVIDER_NATIVE` pricing/capability facts) | Deliberately thin — this document cites rather than restates provider-matrix content |

---

## 31. SOURCE GAPS DISCOVERED

| Gap ID | Affected entry | Missing information | Why it matters | Blocking? |
|---|---|---|---|---|
| `SOURCE-GAP-CACHESTRAT-01` | Cache Eligibility (§2, §8.2) | No single `interfaces.md` field aggregates a write-time "is this candidate eligible to be cached" decision — it is a computed policy outcome, not a stored field | An implementation needs to know this is a computed value, not something to look up on an existing schema field | No — this document's §8.2 pipeline supplies the needed computation without inventing a field |
| `SOURCE-GAP-CACHESTRAT-02` | Cache Authorization Metadata (§2) | Whether authorization context should ever be embedded in a cache key (vs. always re-evaluated live) is not settled by the interface for every type | `TOOL_RESULT` (§6, CACHE-003) makes a specific choice (embed `auth_context_hash`) that other types do not — this asymmetry is a deliberate per-type judgment call in this document, not a resolved architectural rule | No — non-blocking; each type's entry states its own choice explicitly |
| `SOURCE-GAP-CACHESTRAT-03` | `PROVIDER_NATIVE`/`APPLICATION` (§3, CACHE-005/006) | `CacheWriteRequest.cache_type`'s enum (`interfaces.md` §6.4) omits both `PROVIDER_NATIVE` and `APPLICATION`, while `CacheLookupRequest.cache_type`'s enum (§6.2) includes all seven | `PROVIDER_NATIVE`'s omission has a clean explanation (provider-adapter-managed, never core-written); `APPLICATION`'s omission does not have an equally clean explanation in the source documents | No — non-blocking for this document (both types' *lookup* behavior is fully specified); should be resolved via an `interfaces.md` schema reconciliation before implementation, not invented here |
| `SOURCE-GAP-CACHESTRAT-04` | Storage Cost estimation (§9) | No source document specifies a storage-cost model; it is deployment-infrastructure-specific | The economics formula's `Storage Cost` term cannot be given a concrete estimation method without deployment context | No — deployment-specific, consistent with how `provider-matrix.md`/`optimization-catalog.md` treat similarly deployment-specific gaps |
| `SOURCE-GAP-CACHESTRAT-05` | `EMBEDDING`/`APPLICATION`/`PROVIDER_NATIVE` scenario coverage (§6, §30) | No dedicated `SCN-CACHE-*` scenario currently isolates these three types individually | Thin validation coverage for three of the seven canonical types | No — non-blocking; recorded honestly rather than fabricating a validating scenario reference |

No gap above is artificially manufactured to appear thorough, and none is silently resolved by inventing source-authoritative content.

---

## 32. SOURCE CONTRADICTIONS

No new source contradictions were identified between the Problem Statement, Engineering Specification, `architecture.md`, `interfaces.md`, `conventions.md`, `edge-cases.md`, `scenario-matrix.md`, `optimization-catalog.md`, or `provider-matrix.md` regarding cache mechanics, key construction, TTL policy, economics, or invalidation. The one apparent tension investigated — `interfaces.md` §6.2 vs. §6.4's differing `cache_type` enum membership (§3, `SOURCE-GAP-CACHESTRAT-03`) — is recorded as a source gap, not a contradiction, since the two enums are not making conflicting claims about the same fact; §6.4 simply enumerates a narrower set for write purposes, and no source document asserts they must be identical.

Previously-resolved contradictions from upstream documents (`CONTRA-001`, `CONTRA-OPTCAT-01/02/03`) are not reintroduced here; none concerned cache mechanics specifically.

---

## 33. Anti-Pattern Coverage

Every entry in §6 carries an Anti-Pattern Check field citing `conventions.md` §24's actual anti-patterns. Consolidated cross-reference:

| `conventions.md` §24 anti-pattern | Guarded by |
|---|---|
| "Using semantic cache without freshness/authorization checks" (§24.1) | §5 (four-check gate), CACHE-002 |
| "Caching mutable/permission-sensitive data without validation" (§24.2) | CACHE-003 (auth-context-hash key), §10 (`PERMISSION_CHANGE` cascade) |
| "Treating semantic similarity as sufficient proof of cache safety" (§24.2) | §5, §7, CACHE-002 |
| "Treating a cache hit as a quality guarantee" (§24.2) | §2 (CacheQualityMetadata note), §26, CACHE-004/007 |
| "Claiming savings without accounting for optimization overhead" (§24.1) | §9 (lookup overhead as a cost term), §26 |
| "Provider-specific caching silently weakening organizational security requirements" (SEC-010, §24.1-adjacent) | CACHE-005, `EC-029` |
| "Removing context without provenance or recovery information where recovery is required" (§24.2) | CACHE-007 (CL-004 cross-reference) |
| "Assuming larger context windows eliminate context-management problems" (§24.2) | CACHE-007 |
| "Using provider-specific behavior as a universal architectural assumption" (§24.2) | CACHE-004 (embedding-model interchangeability), CACHE-005 |

No anti-pattern from `conventions.md` §24 that materially applies to caching is left unaddressed.

---

## 34. Requirement Traceability

| Requirement | Cache-strategy relevance |
|---|---|
| `OBJ-005` (Reduce unnecessary model calls) | CACHE-001 (`SCN-CACHE-001`) |
| `OBJ-007` (Reuse prompts/context/tool results safely) | CACHE-001/002/003 collectively |
| `SEC-003` (Tenant-aware cache keys) | §4, CACHE-003 (`SCN-CACHE-003`, `EC-028`) |
| `SEC-004` (PII/data-retention in cache) | §13.4 |
| `SEC-006` (Tool-result filtering preserves audit fields) | Upstream of CACHE-003 (TECH-011's boundary, not redefined here) |
| `SEC-007` (Semantic cache verifies auth + freshness) | §5, CACHE-002 (`SCN-CACHE-002`) |
| `SEC-010` (Provider caching must not weaken security requirements) | CACHE-005 (`EC-029`) |
| `SEC-014` (Tool/MCP trust independent of ROI) | CACHE-003 |
| `AC-006` (Safe exact caching) | CACHE-001 |
| `AC-007` (Semantic caching with freshness/auth validation) | CACHE-002 |
| `AC-019` (Skip optimization when cost exceeds benefit) | §9, `SCN-CACHE-004` |
| `AC-036` (Measurement-category separation) | §20 |

No requirement above is force-mapped where the actual relevance is only incidental.

---

## 35. Final Report

**Cache-type entries:** 7 (`CACHE-001`–`007`), matching the seven canonical `cache_type` values in `interfaces.md` §6.2 exactly. No eighth type introduced; none renamed.

**TTL-value evidence distribution:** 5 of 7 types carry a `PROPOSED DEFAULT` numeric TTL (`EXACT` 24h, `SEMANTIC` 60min/disabled-for-time-sensitive, `TOOL_RESULT` illustrative 1–60min per-tool, `APPLICATION` 60min, `EMBEDDING`/`CONTEXT` storage-hygiene-only backstops of 30/7 days); 2 types (`EMBEDDING`, `CONTEXT`) are primarily version/dependency-invalidated rather than TTL-driven, stated explicitly per §19's own instruction to do so; `PROVIDER_NATIVE`'s TTL is provider-controlled, cited not proposed. Zero numeric TTL/economics values are presented as authoritative fact — every one carries a `PROPOSED DEFAULT`/`PROPOSED METHODOLOGY` tag or an explicit citation to a source document.

**Similarity-threshold governance:** the 0.92 global floor is verified against the live `conventions.md` §9.2, stated once (§7) and referenced consistently thereafter — not redefined per entry.

**Economics methodology:** full estimation-method table for all eight terms of the Expected-Cache-Benefit/Cache-Cost formula (§9); no universal probability/cost constant is asserted.

**Invalidation coverage:** CL-003's full trigger table restated exactly (§10), extended into a complete `CacheInvalidationRequest.reason`-keyed cascade table with per-type applicability.

**Poisoning-defense coverage:** `EC-078`, `EC-079`, `EC-208`, and `SCN-CACHE-005` addressed explicitly (§13–14), with the CIS-precedes-admission ordering invariant stated without redefining CIS's own mechanism.

**Tenant-isolation coverage:** structural (namespace-level, §4), not merely a runtime check — verified against `SCN-CACHE-003`'s exact standard.

**Provider-native boundary:** CACHE-005 cites `provider-matrix.md` for every concrete pricing/capability fact; no provider fact is restated or re-derived in this document.

**Developer-agent / multi-agent / recovery coverage:** §22–23, citing DA-003/022/024 by ID without redefining their applicability; `TECH-009/010/011/012` cited by ID throughout §6 without redefinition.

**Scenario/edge-case coverage:** `SCN-CACHE-001`–`005` fully covered (one per type where a dedicated scenario exists; `EMBEDDING`/`APPLICATION`/`PROVIDER_NATIVE` honestly recorded as thin, `SOURCE-GAP-CACHESTRAT-05`); `EC-027`–`036` fully covered; not every one of the 213 edge cases or 263 scenarios is claimed to be cache-specific.

**Optimization-catalog traceability:** zero new `TECH-NNN`/`DA-NNN` entries created; `TECH-009/010/011/012`, `DA-003/022/024` all cited by ID and not redefined.

**Source gaps:** 5 (`SOURCE-GAP-CACHESTRAT-01`–`05`), all non-blocking. **Contradictions:** 0 new (one apparent tension resolved as a gap, not a contradiction, §30).

**Unresolved limitations:** (1) three of the seven cache types (`EMBEDDING`, `APPLICATION`, `PROVIDER_NATIVE`) lack a dedicated validating scenario and are honestly marked thin rather than force-mapped to an existing one; (2) `APPLICATION`'s absence from `CacheWriteRequest`'s enum is flagged but not resolved, since resolving it would require modifying `interfaces.md`, which this document does not do; (3) storage-cost estimation is deployment-specific and left as a gap rather than given an invented universal model.

---

## 36. Appendix A — Interface Schema Reference (verbatim from `interfaces.md` §6.1–6.4, reproduced for this document's self-containment only; `interfaces.md` remains the authoritative source)

```
interface CacheStore {
  lookup(request: CacheLookupRequest)           -> CacheLookupResult
  write(entry: CacheWriteRequest)               -> CacheWriteResult
  invalidate(request: CacheInvalidationRequest) -> CacheInvalidationResult
  refresh(key: string, namespace: string)       -> CacheRefreshResult
  warm(entries: CacheWarmRequest[])             -> CacheWarmResult
  inspect(key: string, namespace: string)       -> CacheEntry | null
  explain_miss(request: CacheLookupRequest)     -> CacheMissExplanation
}

CacheLookupRequest {
  cache_type:            enum { EXACT, SEMANTIC, TOOL_RESULT, EMBEDDING,
                                PROVIDER_NATIVE, APPLICATION, CONTEXT }
  key:                   string | null
  query_embedding:       float[] | null
  namespace:             string
  tenant_id:             string        // REQUIRED; isolation
  model_id:              string
  provider_id:           string
  security_context:      SecurityContext
  freshness_requirement: FreshnessRequirements | null
  similarity_threshold:  float | null
}

CacheLookupResult {
  hit:               boolean
  hit_type:          enum { EXACT, SEMANTIC, NONE }
  entry:             CacheEntry | null
  similarity_score:  float | null
  freshness_valid:   boolean
  auth_check_passed: boolean
  miss_reason:       CacheMissReason | null
  latency_ms:        integer
}

CacheMissReason {
  reason:      enum { NOT_FOUND, EXPIRED, STALE, AUTH_FAILED, POLICY_PROHIBITED,
                      SIMILARITY_BELOW_THRESHOLD, MUTABLE_DATA, PROVIDER_UNAVAILABLE }
  explanation: string
}

CacheEntry {
  key:                     string
  namespace:               string
  tenant_id:               string
  organization_id:         string
  version:                 string
  content:                 any
  content_type:            string
  token_count:             integer
  security_classification: SecurityClassification

  ttl_seconds:       integer
  created_at:        ISO8601 string
  expires_at:        ISO8601 string
  last_accessed_at:  ISO8601 string
  access_count:      integer
  reuse_probability: float
  write_cost:        float
  read_cost:         float

  freshness_score:  float
  source_version:   string | null
  source_commit:    string | null
  source_branch:    string | null
  data_timestamp:   ISO8601 string | null

  dependency_refs: string[]    // CL-003: invalidation triggers
  model_id:        string
  provider_id:     string
}

CacheWriteRequest {
  cache_type:      enum { EXACT, SEMANTIC, TOOL_RESULT, EMBEDDING, CONTEXT }
  key:             string
  namespace:       string
  tenant_id:       string
  entry:           CacheEntry
  idempotency_key: string
  write_policy:    enum { IF_ABSENT, ALWAYS, IF_FRESHER }
}

CacheInvalidationRequest {
  keys:      string[] | null
  namespace: string
  tenant_id: string
  pattern:   string | null
  reason:    enum { EXPLICIT, STALENESS, PERMISSION_CHANGE,
                    DEPENDENCY_CHANGE, BRANCH_CHANGE, POLICY_CHANGE }
  cascade:   boolean    // Invalidate dependents via CL-003
}
```

**Cache Safety Requirements** (`interfaces.md` §6.5, verbatim):

| Data Category | Cache Policy |
|---|---|
| Mutable data (live DB results) | Cache only with explicit freshness policy; TTL required |
| Permission-sensitive data | Cache key must include permission context hash |
| Tenant-specific data | Cache namespace must be tenant-scoped; no cross-tenant lookup |
| Branch-specific coding context | Cache key must include branch and commit |
| Provider-native cache | Provider adapter manages; core treats as opaque |
| PII-containing data | Cache prohibited unless explicitly permitted by tenant policy |
| Security instructions | Never serve from cache without auth check |

---

## Cache Strategy Readiness

All seven canonical cache types have complete policy entries using the full schema, with tenant-scoped key construction explicit and structurally enforced (§4), every concrete TTL/similarity-threshold/economics value evidence-tagged (§6, §7, §9), the CL-003 invalidation cascade fully specified (§10), cache-fragmentation remediation concretely playbooked (§11), poisoning defenses explicit for both content (§13.1, §13.3) and key material (§13.2), PII/data-governance gating stated once and applied consistently (§13.4), the provider-native/EAIOC-cache boundary explicit and citing rather than duplicating `provider-matrix.md` (§20, CACHE-005), developer-agent/multi-agent/tool-MCP caching covered without redefining DA-003/022/024 (§22), recovery/checkpoint interaction addressed without weakening Resume != Replay (§23), and no new optimization technique or DA module invented anywhere in this document. Every cited interface, scenario, edge-case, and requirement ID was verified against the live source files during drafting. Remaining source gaps (5) and contradictions (0 new) are documented, bounded, and non-blocking — none prevents correct interpretation, safe behavior, traceability, or implementation of this document's schema by a real `CacheStore` deployment.

`READY FOR NEXT DOCUMENTATION PHASE`
