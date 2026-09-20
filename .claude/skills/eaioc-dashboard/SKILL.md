---
name: eaioc-dashboard
description: >
  Adds a graphical, at-a-glance dashboard section to any EAIOC project report artifact — in
  particular every artifact produced from the `eaioc-guide` subagent's grounded findings. Use
  right after grounding an EAIOC Q&A/research answer and right before finalizing the HTML report's
  page structure, whenever the answer's material contains real quantitative content (ID-series
  counts, status/verdict distributions, coverage or reconciliation percentages, maturity levels).
  Skip it when the material is purely conceptual with nothing genuine to chart.
---

# EAIOC dashboard layer

This project (`D:\GenAI\Practice\Tok_Agent`, EAIOC) answers the user's doubts and research
questions about itself through the `eaioc-guide` subagent (`.claude/agents/eaioc-guide.md`), which
always delivers its answer as a published HTML artifact rather than chat text. This skill adds one
more standing requirement on top of that: **wherever the underlying material supports it, the
published artifact gets a small graphical dashboard near the top, not just prose and tables.**

## Order of operations

1. `eaioc-guide` (or whichever agent is grounding the answer) reads the docs and produces the
   grounded findings, as normal.
2. Load `artifact-design` for the page contract and this project's established report identity
   (IBM Plex Sans/Mono, cool slate palette, teal-blue accent, verdict-pill semantics — see the
   "EAIOC Stack Audit" / "EAIOC Optimization Audit" precedent).
3. Load **this skill** to decide whether a dashboard section applies to this specific answer, and
   if so, what to put in it.
4. If genuine chart marks (bars, donuts, sparklines) are going to be drawn, load `dataviz` for
   color/axis/mark-quality rules before drawing them.
5. Build the page: dashboard section first (directly under the header/lede, above the first prose
   section), then the detailed written report exactly as `eaioc-guide` already structures it.

Because `eaioc-guide` itself has no `Write` tool (only `Read`, `Grep`, `Glob`, `WebFetch`,
`WebSearch`, `Skill`, `Artifact`), it typically hands grounded findings back to whichever agent
performs the actual publish step. That agent is the one that applies this skill and builds the
HTML file — but `eaioc-guide` should still flag, in its hand-back, which figures/IDs are
dashboard-worthy (see below) so the publishing step doesn't have to re-derive them.

## What counts as dashboard-worthy content

Only pull a number or status onto the dashboard if it traces to a specific doc/ID the grounded
answer already cites — never invent one to fill a tile. Look for:

- **ID-series counts** the answer touches — e.g. "20 `TECH-NNN` techniques," "7 `CACHE-NNN`
  types," "25 `DA-NNN` modules," "263 scenarios across 30 domains," "138 traced requirement IDs."
  Render these as stat tiles (big number + label + source doc).
- **Status/verdict distributions** the material naturally has — Maturity Level per technique,
  `PROPOSED` vs. `ACCEPTED` ADRs, `DIRECT`/`PARTIAL`/`NOT COVERED`/`DUPLICATE` scenario
  reconciliation counts, `SOURCE-GAP` vs. resolved counts, the "verified vs. re-measurement-target"
  split this project's reports already draw. Render as a small stacked bar, donut, or meter using
  this project's existing verdict-pill semantics (green = confirmed/keep, amber = needs
  adjustment/unverified, red = gap/risk, blue = recommended addition) so the dashboard's colors
  mean the same thing the pills already do in prose.
- **Coverage/traceability percentages** — e.g. "N of 138 requirement IDs traced," "177 DIRECT / 35
  PARTIAL / 0 NOT COVERED" reconciliation ratios. Render as a progress meter or simple horizontal
  stacked bar, labeled with the actual counts, not just a percentage.
- **Range/estimate figures already in the answer** (e.g. the TECH-NNN catalog's claimed token
  reduction ranges). Render as a simple range strip or sparkline, but visually mark it the same way
  the prose does — amber/hatched, tagged "re-measurement target" — never presented as if it were a
  measured result. This is the same rule CLAUDE.md rule 7 enforces in text; the dashboard must not
  quietly launder an unverified figure into something that reads as fact just because it's now a
  chart.

If a question is a single conceptual clarification with no counts, statuses, or percentages behind
it (e.g. "why does the CIS component exist?"), there is nothing honest to chart. Skip the dashboard
section entirely rather than manufacturing a decorative one, and don't call this out as a gap — an
absent dashboard is the correct outcome for that kind of answer, not a shortfall.

## Building it

- Prefer hand-drawn inline SVG or plain HTML/CSS (stat tiles, meters, small bar/donut charts) over
  pulling in a charting library — per `dataviz`'s own guidance, most dashboards this size don't
  need one. Reach for a library only if a single answer genuinely needs many comparable series at
  once.
- Follow `dataviz`'s form and color rules for any actual chart marks: one scale places ticks and
  labels, every label names a value the chart reaches, chart text takes its color from the page's
  theme tokens (so it reads correctly in both light and dark), and SVG viewBoxes leave room for
  outermost labels.
- Keep the dashboard compact — a row of stat tiles plus at most one or two small charts. It is an
  executive-summary layer sitting above the report's own detail (the same relationship
  `observability.md`'s OBS-001 Executive tier has to the Engineering/Product-Operations tiers
  beneath it), not a second report.
- Cite the source doc/section for every tile and chart the same way the prose below it does — a
  dashboard number without a citation is exactly the "fabricated savings" pattern CLAUDE.md rule 5
  prohibits, just moved into a different part of the page.
