# EAIOC — P0 Implementation Environment Readiness Check

**Based on:** `docs/execution-plan.md` v1.0.3  
**Purpose:** Verify that the local implementation environment is ready before any EAIOC P0 source-code execution begins.  
**Execution mode:** Read-only verification.  
**Scope:** P0 implementation prerequisites only.  
**Important:** This document does not create source code, does not modify the EAIOC architecture, and does not replace `docs/execution-plan.md`.

---

# 1. Objective

Before issuing:

```text
Execute EXE-P0.1.A
```

verify that the implementation environment can support the approved P0 execution plan.

The readiness check must answer exactly one question:

```text
P0 IMPLEMENTATION ENVIRONMENT: READY / NOT READY
```

This is an environment gate, not an implementation capability and not an additional `EXE-P0` unit.

---

# 2. Relationship to the Execution Plan

The approved execution plan defines:

- 6 P0 capabilities
- 8 lifecycle sub-phases A–H per capability
- 48 atomic `EXE-P0.<n>.<letter>` execution units
- 6 separate Capability Gates
- 54 total gated checkpoints

The environment check does **not** change those counts.

The environment check occurs before the first atomic execution unit:

```text
PRE-IMPLEMENTATION ENVIRONMENT READINESS
                ↓
         READY / NOT READY
             /       \
           NOT        READY
           ↓            ↓
       Fix environment  Execute EXE-P0.1.A
```

The execution plan states that Java 25 / Spring Boot 4.1.x / Maven is the selected P0 request-critical baseline, Docker/Compose supports the proving-lab environment, and several enterprise technologies are intentionally deferred from this P0 slice.

---

# 3. Read-Only Rule

This readiness check must be diagnostic only.

## Allowed

- inspect installed versions
- inspect environment variables
- inspect repository state
- inspect directory structure
- inspect required documentation
- run harmless version/help commands
- run a harmless build/tool availability check where it does not modify the repository
- verify network access to dependency repositories
- verify Docker/Compose availability without creating application artifacts
- inspect Git status and current branch

## Forbidden

Do NOT:

- create `control_plane/`
- create Java source files
- create tests
- modify `docs/`
- modify `CLAUDE.md`
- modify any ADR
- modify architecture or interfaces
- install application dependencies into the repository
- accept or modify ADRs
- create Docker images for EAIOC
- create Docker Compose application files
- make Git commits
- change branches
- reset/rebase/merge
- modify tracked files
- create secrets
- configure provider credentials
- call a live LLM provider

The purpose is to verify readiness, not to start implementation.

---

# 4. P0 Technology Baseline

## Required for P0

| Technology / Tool | P0 Requirement | Status |
|---|---|---|
| Java | Java 25 | REQUIRED |
| Spring Boot | 4.1.x baseline | REQUIRED through project dependencies |
| Maven | Build/dependency management | REQUIRED |
| Git | Source control and one-commit-per-sub-phase discipline | REQUIRED |
| Claude Code | Repository execution environment | REQUIRED |
| Docker | Proving-lab environment | REQUIRED before proving-lab work; may not block Contract & Design if not yet needed |
| Docker Compose | Proving-lab packaging | REQUIRED before proving-lab work; not a prerequisite for every atomic unit |

## Not required to start P0

| Technology | Status |
|---|---|
| PostgreSQL | Deferred / not required for P0 |
| pgvector | Deferred / not required for P0 |
| Redis | Deferred / not required for P0 |
| Kafka | Deferred / not required for P0 |
| Python | Deferred for this P0 slice |
| Go | Future extraction candidate |
| Kubernetes | Deferred |
| Helm | Deferred |
| React / TypeScript | Deferred |
| Keycloak | Deferred |
| Live LLM provider credentials | Not required for P0 capability implementation |
| Provider adapters | Not built in this P0 slice |

---

# 5. Readiness Categories

The environment check is divided into:

1. Operating System and Shell
2. Git
3. Java 25
4. Maven
5. Spring Boot Dependency Resolution
6. Repository Structure
7. Claude Code Access
8. Required Documentation
9. Network / Dependency Resolution
10. Docker
11. Docker Compose
12. Disk Space
13. Permissions
14. Environment Variables
15. Secrets and Provider Access
16. Repository Cleanliness
17. Final P0 Readiness Verdict

---

# 6. Operating System and Shell

Verify the operating environment.

### Windows

Check:

```text
winver
```

PowerShell:

```powershell
$PSVersionTable
```

Verify command discovery:

```powershell
Get-Command java
Get-Command mvn
Get-Command git
Get-Command docker
```

The commands may return "not found" for tools intentionally deferred, but required P0 tools must be available.

### Record

```text
OS:
Shell:
Architecture:
User permissions:
```

### Pass criteria

- supported development machine
- command shell usable
- normal user permissions sufficient for repository development
- no obvious execution restriction preventing Java/Maven/Git/Claude Code

---

# 7. Git Readiness

Git is mandatory because the execution model requires one commit per A–H lifecycle sub-phase.

Run:

```bash
git --version
git status --short
git branch --show-current
git rev-parse --show-toplevel
```

Also:

```bash
git log -1 --oneline
```

### Record

```text
Git version:
Repository root:
Current branch:
Latest commit:
Working tree:
```

### Pass criteria

- Git available
- correct EAIOC repository detected
- current branch identified
- repository is accessible
- no unexpected repository corruption

### Important

A dirty working tree is not automatically a failure.

It becomes a blocker when the existing changes would make scope isolation or one-commit-per-sub-phase verification ambiguous.

Record:

```text
WORKING TREE STATUS: CLEAN / DIRTY-BUT-ACCEPTABLE / BLOCKING
```

Do not modify or clean the tree during this readiness check.

---

# 8. Java 25 Readiness

Run:

```bash
java -version
javac -version
```

Check:

```bash
echo $JAVA_HOME
```

Windows PowerShell:

```powershell
$env:JAVA_HOME
```

Verify compiler/runtime consistency.

Example checks:

```powershell
java -version
javac -version
```

### Record

```text
Java runtime:
Java compiler:
JAVA_HOME:
```

### Pass criteria

- Java 25 is available
- `java` resolves correctly
- `javac` resolves correctly
- `JAVA_HOME` is either correctly set or the environment otherwise resolves the intended Java 25 installation
- runtime/compiler mismatch is absent

### Blocker examples

```text
Java 17 only
Java 21 only
Java command unavailable
JAVA_HOME points to an incompatible JDK
java and javac resolve to different major versions
```

Do not install or change Java automatically during this readiness check.

---

# 9. Maven Readiness

Run:

```bash
mvn -version
```

Verify which Java Maven uses.

Record:

```text
Maven version:
Maven Java version:
Maven home:
```

### Pass criteria

- Maven available
- Maven uses Java 25
- local Maven environment is functional

### Important

Do not invent a Maven command that depends on a project structure that does not yet exist.

At this stage, the repository may still be documentation-only and the P0 `control_plane/` tree may not exist.

---

# 10. Spring Boot 4.1.x Dependency Resolution Readiness

The execution plan selects:

```text
Java 25 / Spring Boot 4.1.x / Maven
```

But before implementation, verify that the environment can resolve dependencies when the project is created.

This check should be performed without modifying the repository.

Possible diagnostic approach:

```text
Use an isolated temporary directory outside the EAIOC repository.
```

Verify that Maven can reach the configured repository and resolve a Spring Boot 4.1.x dependency in an isolated test project.

Do not create this test project inside the EAIOC repository.

Record:

```text
Spring Boot dependency resolution:
Repository:
Resolution result:
```

### Pass criteria

- Maven can reach the configured dependency repository
- Spring Boot 4.1.x artifacts can be resolved
- no repository/proxy/certificate issue blocks project creation

### Blocker examples

- Maven Central/repository unreachable
- corporate proxy misconfiguration
- TLS/certificate failure
- dependency resolution consistently fails

---

# 11. Repository Structure Readiness

Inspect without modifying:

```text
.
├── CLAUDE.md
├── docs/
│   ├── implementation-plan.md
│   ├── implementation-readiness-gate.md
│   ├── requirements-traceability.md
│   ├── architecture.md
│   ├── interfaces.md
│   ├── conventions.md
│   ├── edge-cases.md
│   ├── scenario-matrix.md
│   ├── optimization-catalog.md
│   ├── provider-matrix.md
│   ├── cache-strategy.md
│   ├── agent-optimization.md
│   ├── inference-optimization.md
│   ├── quality-gates.md
│   ├── security.md
│   ├── observability.md
│   ├── eval.md
│   ├── SCALING.md
│   ├── implementation-plan.md
│   ├── requirements-traceability.md
│   ├── implementation-readiness-gate.md
│   └── execution-plan.md
```

Also verify:

```text
control_plane/
```

is **not unexpectedly pre-created by this readiness check**.

If it already exists because of legitimate prior work, record the state; do not delete it.

### Pass criteria

- expected repository is open
- required planning documents are available
- no unexpected modifications are introduced
- environment check does not create `control_plane/`

---

# 12. Claude Code Readiness

Verify that Claude Code is installed and can access the EAIOC repository.

The exact command depends on the installed Claude Code setup.

Verify:

```text
Claude Code executable available
Claude Code opens the correct repository
Claude Code can read CLAUDE.md
Claude Code can read docs/execution-plan.md
```

### Pass criteria

Claude Code can:

- access the repository
- inspect files
- read the approved execution plan
- run local shell commands
- use Git
- later create source files when explicitly instructed

### Important

Do not execute:

```text
Execute EXE-P0.1.A
```

during this readiness test.

That belongs to Mode B implementation execution after the environment gate passes.

---

# 13. Required Documentation Readiness

Before implementation, confirm that the documents required by the execution plan are present and readable.

At minimum:

```text
CLAUDE.md
docs/execution-plan.md
docs/implementation-plan.md
docs/implementation-readiness-gate.md
docs/requirements-traceability.md
docs/architecture.md
docs/interfaces.md
docs/conventions.md
docs/edge-cases.md
docs/security.md
docs/observability.md
docs/quality-gates.md
docs/eval.md
docs/SCALING.md
```

Also confirm the ADR corpus is available:

```text
docs/adr/0000-index.md
docs/adr/0001-...
docs/adr/0002-...
docs/adr/0003-...
docs/adr/0004-...
docs/adr/0005-...
docs/adr/0006-...
```

### Pass criteria

- files exist
- files are readable
- no file is unexpectedly modified as part of this check

Do not "fix" documentation during the environment readiness check.

---

# 14. Network Readiness

P0 implementation itself does not require a live LLM provider.

However, dependency resolution may require internet/repository access.

Verify:

```text
Maven dependency repository reachable
Git remote reachable if repository policy requires it
```

Do not require OpenAI/Anthropic/Gemini/etc. provider access for this P0 readiness gate.

### Record

```text
Dependency repository access: PASS / FAIL
Git remote access: PASS / FAIL / NOT REQUIRED
LLM provider access: NOT REQUIRED FOR P0
```

---

# 15. Docker Readiness

Docker is part of the selected proving-lab packaging.

Run:

```bash
docker --version
docker info
```

The exact behavior of `docker info` depends on whether Docker Engine/Desktop is running.

### Pass criteria

- Docker executable available
- Docker daemon accessible when proving-lab work begins

If Docker is installed but the daemon is not running, record:

```text
Docker installation: READY
Docker daemon: NOT READY
```

Do not automatically start services unless the operator intentionally chooses to do so.

---

# 16. Docker Compose Readiness

Run:

```bash
docker compose version
```

Record:

```text
Docker Compose version:
```

### Pass criteria

- Compose command available
- Compose can be invoked successfully

The execution plan uses Docker Compose for the reference proving environment; it is not a prerequisite for every design-only sub-phase.

---

# 17. Disk Space

Because Maven caches dependencies and later Docker work may consume storage, verify available disk space.

On Windows PowerShell:

```powershell
Get-PSDrive -PSProvider FileSystem
```

Record the available space for:

```text
Repository drive:
Maven local repository drive:
Docker storage drive:
```

### Pass criteria

There is sufficient free space for:

- Java/Maven dependencies
- test artifacts
- future Docker images/containers
- Git operations

No fixed numeric threshold is asserted by the EAIOC execution plan; record the actual environment rather than inventing a project-specific number.

---

# 18. File and Directory Permissions

Verify the current user can:

```text
read repository files
write to repository files
execute Java/Maven/Git commands
create files later inside the repository
```

Do not actually create EAIOC implementation files during this gate.

A harmless temporary write/read test may be performed outside the repository if necessary.

Record:

```text
Repository read access:
Repository write access:
Command execution access:
```

---

# 19. Environment Variables

Inspect relevant variables:

```text
JAVA_HOME
PATH
MAVEN_HOME (if configured)
```

Do not print secret-bearing environment variables.

### Important

Do not dump the entire environment using commands that may expose credentials.

Never include passwords, access tokens, API keys, certificates, or private keys in the readiness report.

---

# 20. Secret and Provider Readiness

P0 capability implementation does not require live LLM provider credentials.

Therefore:

```text
OPENAI_API_KEY           NOT REQUIRED FOR P0
ANTHROPIC_API_KEY        NOT REQUIRED FOR P0
GEMINI_API_KEY           NOT REQUIRED FOR P0
AZURE OPENAI CREDENTIALS NOT REQUIRED FOR P0
```

Do not create or request provider secrets simply to pass this readiness gate.

Provider-specific adapters are outside this P0 implementation slice.

---

# 21. Enterprise Components — Explicitly Deferred

Do not fail the P0 environment gate because the following are not installed:

```text
PostgreSQL
pgvector
Redis
Kafka
Python
Go
Kubernetes
Helm
Keycloak
React
```

These are classified as deferred/not required for this P0 execution slice.

The execution plan uses in-memory/reference state for the current P0 work and defers durable enterprise infrastructure decisions behind the applicable ADRs.

---

# 22. Git Safety Before First Execution

Before implementation starts, verify:

```bash
git status --short
git branch --show-current
```

Capture the baseline state.

Recommended operator record:

```text
BASELINE GIT CHECK
==================
Date:
Branch:
HEAD:
Working tree:
Untracked files:
Modified files:
```

Do not create a commit during the environment readiness check.

After readiness passes, the first implementation unit will create the first P0 implementation commit.

---

# 23. P0 Runtime Classification Check

The execution plan allocates all six P0 capabilities to the Java 25 / Spring Boot 4.1.x core.

Verify the environment supports this classification:

```text
Capability 1 — Java core
Capability 2 — Java core
Capability 3 — Java core
Capability 4 — Java core
Capability 5 — Java core
Capability 6 — Java core
```

Python and Go are not prerequisites for P0.

---

# 24. Environment Readiness Checklist

Use this checklist during the verification.

## Operating Environment

```text
[ ] OS identified
[ ] Shell identified
[ ] User permissions sufficient
```

## Git

```text
[ ] Git installed
[ ] Correct repository detected
[ ] Current branch identified
[ ] HEAD recorded
[ ] Working tree reviewed
```

## Java

```text
[ ] Java 25 runtime available
[ ] Java 25 compiler available
[ ] JAVA_HOME correct or equivalent Java resolution confirmed
[ ] java/javac major versions match
```

## Maven

```text
[ ] Maven installed
[ ] Maven resolves Java 25
[ ] Maven environment functional
```

## Spring Boot

```text
[ ] Spring Boot 4.1.x dependency resolution verified
[ ] Maven repository reachable
```

## Repository

```text
[ ] CLAUDE.md available
[ ] docs/ available
[ ] execution-plan.md v1.0.3 available
[ ] required upstream docs available
[ ] ADR corpus available
[ ] No implementation files created by this readiness check
```

## Claude Code

```text
[ ] Claude Code available
[ ] Correct repository opened
[ ] CLAUDE.md readable
[ ] execution-plan.md readable
[ ] Git accessible
[ ] Shell commands accessible
```

## Docker

```text
[ ] Docker executable available
[ ] Docker daemon accessible when needed
[ ] Docker Compose available
```

## Network

```text
[ ] Maven dependency repository reachable
[ ] Git remote access checked where required
[ ] LLM provider access not required for P0
```

## Storage / Permissions

```text
[ ] Disk space reviewed
[ ] Repository read access
[ ] Repository write access
[ ] Command execution access
```

## Secrets

```text
[ ] No unnecessary provider credentials requested
[ ] No secrets exposed in readiness report
```

---

# 25. Readiness Result Matrix

Complete this table before beginning implementation.

| Category | Result | Notes |
|---|---|---|
| Operating Environment | PASS / FAIL | |
| Git | PASS / FAIL | |
| Java 25 | PASS / FAIL | |
| Maven | PASS / FAIL | |
| Spring Boot 4.1.x Resolution | PASS / FAIL | |
| Repository | PASS / FAIL | |
| Claude Code | PASS / FAIL | |
| Documentation | PASS / FAIL | |
| Network / Dependency Access | PASS / FAIL | |
| Docker | PASS / FAIL / DEFERRED UNTIL LAB | |
| Docker Compose | PASS / FAIL / DEFERRED UNTIL LAB | |
| Disk Space | PASS / FAIL | |
| Permissions | PASS / FAIL | |
| Secrets / Provider Access | PASS / NOT REQUIRED | |

---

# 26. Blocking Rules

The environment is **NOT READY** if any required condition below fails:

```text
Java 25 unavailable
Maven unavailable
Git unavailable
Correct repository unavailable
Required EAIOC documentation unavailable
Claude Code cannot access the repository
Maven dependency resolution is blocked
Repository permissions prevent implementation
```

The environment may still be **READY FOR P0.1.A** when:

```text
Docker daemon is not currently running
Docker Compose is not currently running
PostgreSQL is not installed
Redis is not installed
Kafka is not installed
Python is not installed
Go is not installed
Kubernetes is not installed
LLM provider credentials are absent
```

provided those components are not needed for the specific unit being executed and the execution plan continues to classify them as deferred.

---

# 27. Final Environment Gate

After all checks are completed, produce one of these results.

## READY

Use:

```text
P0 IMPLEMENTATION ENVIRONMENT: READY
```

Then record:

```text
READY FOR:
Execute EXE-P0.1.A
```

No implementation should have been performed during this gate.

## NOT READY

Use:

```text
P0 IMPLEMENTATION ENVIRONMENT: NOT READY
```

Then record:

```text
BLOCKERS:
1.
2.
3.

REMEDIATION REQUIRED:
1.
2.
3.
```

Do not execute `EXE-P0.1.A` until the blockers are resolved.

---

# 28. Recommended Claude Code Environment-Check Prompt

After opening the EAIOC repository in Claude Code, use the following prompt before starting implementation:

```text
Perform the EAIOC P0 Implementation Environment Readiness Check.

Read:
- CLAUDE.md
- docs/execution-plan.md
- docs/implementation-plan.md
- docs/implementation-readiness-gate.md

Purpose:
Verify that the local environment is ready for the approved P0 Mode B execution.

Check only:
- OS/shell
- Git
- Java 25 runtime/compiler
- JAVA_HOME
- Maven
- Maven's Java version
- Spring Boot 4.1.x dependency-resolution capability using an isolated temporary location outside the repository if required
- EAIOC repository structure
- required documentation availability
- Claude Code repository access
- Maven/dependency repository connectivity
- Docker installation
- Docker daemon availability
- Docker Compose availability
- disk space
- repository read/write permissions
- Git working-tree state

Important constraints:
- READ-ONLY ENVIRONMENT CHECK
- DO NOT create control_plane/
- DO NOT create source code
- DO NOT create tests
- DO NOT modify docs/
- DO NOT modify CLAUDE.md
- DO NOT modify architecture or interfaces
- DO NOT modify any ADR
- DO NOT commit anything
- DO NOT change branches
- DO NOT clean/reset the working tree
- DO NOT request or create LLM provider credentials
- DO NOT call a live LLM provider
- PostgreSQL, Redis, Kafka, Python, Go, Kubernetes, Helm, Keycloak and React are NOT P0 prerequisites unless the current execution plan explicitly says otherwise

Use the approved technology classification from docs/execution-plan.md v1.0.3:
- Java 25 / Spring Boot 4.1.x / Maven = P0 selected baseline
- Docker / Docker Compose = proving-lab tooling
- PostgreSQL / Redis / Kafka / Python / Go / Kubernetes / UI = deferred for this P0 slice

Do not begin implementation.

At the end report exactly:

P0 IMPLEMENTATION ENVIRONMENT: READY

or

P0 IMPLEMENTATION ENVIRONMENT: NOT READY

If NOT READY, list every blocker and its evidence.

If READY, state:

READY FOR:
Execute EXE-P0.1.A

Stop after the report.
```

---

# 29. After the Environment Gate Passes

The sequence becomes:

```text
1. Perform environment readiness check
2. Obtain:
   P0 IMPLEMENTATION ENVIRONMENT: READY
3. Confirm the Git baseline
4. Review the readiness evidence
5. Start the first implementation unit

Execute EXE-P0.1.A
```

At this point and not earlier, Mode B implementation begins.

---

# 30. Critical Boundary

The environment readiness result:

```text
P0 IMPLEMENTATION ENVIRONMENT: READY
```

does not mean:

```text
CODE COMPLETE
TEST COMPLETE
BENCHMARK VALIDATED
PRODUCTION READY
ENTERPRISE SCALE READY
```

It means only:

```text
The local environment is sufficiently prepared to begin the first approved P0 atomic execution unit.
```

---

# 31. Final Operator Summary

```text
ENVIRONMENT GATE
      ↓
READY?
  ┌───┴───┐
 NO      YES
 ↓        ↓
Fix      Execute
env      EXE-P0.1.A
          ↓
        Verify
          ↓
        Commit
          ↓
        Approve
          ↓
        Execute next explicit unit
```

The environment gate exists to prevent implementation from starting with an incomplete or inconsistent development setup while preserving the 48 atomic-unit + 6 Capability-Gate execution model.
