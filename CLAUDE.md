# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
Subproject specifics live in `frontend/CLAUDE.md` and `service/CLAUDE.md`.

## Project Overview

**Beticon** is a betting game initially developed for the FIFA World Cup 2026, now maintained and updated for future events. It's a Gradle monorepo: `frontend/` (Angular), `service/` (Spring Boot), plus `mysql/` (database config) and `proxy/` (nginx reverse proxy).

- **Live Demo**: https://beticon-dev.no1hardy.ch

## Branches & PRs

- Work branches are named `<type>/<issue>-<slug>`, where `<type>` mirrors the conventional commit types.
- This rules also apply to auto-generated Claude Code branches.
- **PR titles** are plain descriptive text prefixed with the emoji of the branch type e.g. `✨ Implement the API gateway skeleton` — no issue reference requirement.
- **Feature branches always target `develop`**; `main` or `release` is reserved for releases.
- When creating a PR, always link the issues referenced in the PR commits.

## Local Development

`docker-compose up` starts everything: frontend dev server (http://localhost:4200), backend (http://localhost:8080), MySQL (3306) and the nginx proxy (http://localhost:80).

## CI/CD

GitHub Actions run on every push (`.github/workflows/frontend.yml`, `service.yml`): lint/tests via Gradle, then Docker build. Images are pushed to the private registry `docker.no1hardy.ch/beticon/...` only from the `develop` branch.

## graphify

This project has a knowledge graph at graphify-out/ with god nodes, community structure, and cross-file relationships.

Rules:
- For codebase questions, first run `graphify query "<question>"` when graphify-out/graph.json exists. Use `graphify path "<A>" "<B>"` for relationships and `graphify explain "<concept>"` for focused concepts. These return a scoped subgraph, usually much smaller than GRAPH_REPORT.md or raw grep output.
- If graphify-out/wiki/index.md exists, use it for broad navigation instead of raw source browsing.
- Read graphify-out/GRAPH_REPORT.md only for broad architecture review or when query/path/explain do not surface enough context.
- After modifying code, run `graphify update .` to keep the graph current (AST-only, no API cost).

## Agent skills

### Issue tracker

Issues live in GitHub Issues for `NoOneHardy/beticon` (via the `gh` CLI), with gitmoji-prefixed titles and labels chosen from the existing set. See `docs/agents/issue-tracker.md`.

### Domain docs

Single-context: one `GLOSSARY.md` + `docs/adr/` at the repo root. See `docs/agents/domain.md`.
