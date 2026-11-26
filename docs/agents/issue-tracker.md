# Issue tracker: GitHub

Issues and specs for this repo live as GitHub issues. Use the `gh` CLI for all operations.

## Conventions

- **Create an issue**: `gh issue create --title "..." --body "..." --type <type> --project "WM Tippspiel 2026"`, then set its board fields (see **Issue type** and **Project board** below). Use a heredoc for multi-line bodies.
- **Read an issue**: `gh issue view <number> --json number,title,body,labels,comments`.
- **List issues**: `gh issue list --state open --json number,title,body,labels,comments --jq '[.[] | {number, title, body, labels: [.labels[].name], comments: [.comments[].body]}]'` with appropriate `--label` and `--state` filters.
- **Make an issue a sub-issue of a parent**: `gh issue create --parent <parent> ...`, or `gh issue edit <parent> --add-sub-issue <child>` afterwards (`gh` 2.94+). Older `gh`: `gh api --method POST repos/<owner>/<repo>/issues/<parent>/sub_issues -F sub_issue_id=<child-db-id>` (database id, as in **Blocking** below). Without sub-issues, put `Part of #<parent>` at the top of the child body.
- **Blocking**: GitHub's **native issue dependencies**, the canonical, UI-visible representation. Add an edge with `gh issue create --blocked-by <n>,<n> ...` or `gh issue edit <child> --add-blocked-by <blocker>` (`gh` 2.94+). Older `gh`: `gh api --method POST repos/<owner>/<repo>/issues/<child>/dependencies/blocked_by -F issue_id=<blocker-db-id>`, where `<blocker-db-id>` is the blocker's numeric **database id** (`gh api repos/<owner>/<repo>/issues/<n> --jq .id`, _not_ the `#number` or `node_id`). Where dependencies aren't available, fall back to a `Blocked by: #<n>, #<n>` line at the top of the child body. A ticket is unblocked when every blocker is closed.
- **Count open blockers**: `gh api repos/<owner>/<repo>/issues/<n> --jq .issue_dependencies_summary.blocked_by` (open blockers only; 0 means startable). `gh issue view <n> --json blockedBy` also lists closed blockers, so filter it: `--jq '[.blockedBy.nodes[] | select(.state == "OPEN")] | length'`.
- **Comment on an issue**: `gh issue comment <number> --body "..."`
- **Apply / remove labels**: `gh issue edit <number> --add-label "..."` / `--remove-label "..."`
- **Close** an issue that no merged PR resolves: post the explanation with `gh issue comment <number> --body "..."`, then `gh issue close <number>`. On an already-closed issue, `gh issue close --comment` drops the comment and still exits 0. An issue a merged PR resolves is finished as in **Work finished** below, without a comment.

Infer the repo from `git remote -v`; `gh` does this automatically when run inside a clone.

File issues only into the user's own repo. Never file into `mattpocock/skills` (the repo these skills come from) unless the user owns it. In a fork, `gh` can default to the upstream parent, so before the first `gh issue create` of a session, check the target with `gh repo view --json nameWithOwner` and confirm it with the user if it is not their repo.

## Issue title and labels (project convention)

Applies to every issue created or edited through this tracker. For manual issue creation the `github-issue` skill implements the same rules.

**Title**: `<gitmoji> <few words>`

- Start with the gitmoji for the issue type (reference: https://gitmoji.dev), followed by a short noun phrase of roughly 2-6 words. Never a full sentence: no trailing period, no "We should...". No type word ("Feature:", "Bug:"); the emoji already says it.
- Examples: `✨ Shopping list sharing`, `🐛 Batch expiry off by one`, `♻️ Extract inventory validators`.
- Common mappings: ✨ feature, 🐛 bug, 🚑️ critical bug, ♻️ refactor, 📝 docs, ✅ tests, 🔧 config/build/tooling, 🧑‍💻 dev environment, ⚡️ performance, 💄 UI/styling, 🔒️ security, 👷 CI, ⬆️/➕/➖ dependencies, 🗃️ database, 🔥 removal, ♿️ accessibility.

**Labels**: use the repo's existing labels only, never create new ones.

1. Read the current set every time: `gh label list --json name,description --limit 100`.
2. Apply every label that genuinely fits: usually the type label (`bug`, `feature`, `documentation`, `refactoring`, `test`, `ci`, ...) plus any topical one (`UX`, `architecture`, ...). Use the exact name, including case.
3. If none fits, apply none; don't stretch a label to fit.
4. Skip status-style labels (`wontfix`, `duplicate`, ...) unless explicitly asked.
5. This rule also covers the `wayfinder:*` labels below: apply them only if they already exist, otherwise note it and proceed without.

## Issue type

Every new issue gets a GitHub issue type with `--type` (`gh issue edit <n> --type <type>` for an existing one): ✨ → `Feature`, 🐛 / 🚑️ → `Bug`, every other gitmoji → `Task`.

## Project board and issue status

Every issue sits on the user project **WM Tippspiel 2026** (`gh project ... 6 --owner NoOneHardy`). Two statuses track it, and both move together: the issue's own **status** (a public-preview GitHub feature, set through GraphQL) and the project's **Status** field.

IDs (look them up again if a call rejects one: `gh project field-list 6 --owner NoOneHardy --format json`, and `planningStatuses` on the repository in GraphQL):

| What | ID |
|---|---|
| Project | `PVT_kwHOBuyVMs4Al764` |
| Project Status field | `PVTSSF_lAHOBuyVMs4Al764zgd6ap4`: Backlog `f75ad846`, In Progress `47fc9ee4`, Done `98236657` |
| Project Priority field | `PVTSSF_lAHOBuyVMs4Al764zgkCBwo`: High `8272228c`, Medium `32f9d244`, Low `4dc7daeb` |
| Project Start Date field | `PVTF_lAHOBuyVMs4Al764zgkCBhs` |
| Project End Date field | `PVTF_lAHOBuyVMs4Al764zgkCBic` |
| Issue status | Backlog `IS_kgAC`, In progress `IS_kgAE`, In review `IS_kgAI`, Done `IS_kgAF` |

Commands:

- **Project item id** of issue `<n>`: `gh api graphql -f query='{repository(owner:"NoOneHardy",name:"beticon"){issue(number:<n>){id projectItems(first:5){nodes{id project{number}}}}}}'` (the node with project number 6; the issue `id` is the GraphQL id the status mutation takes).
- **Set a project field**: `gh project item-edit --project-id PVT_kwHOBuyVMs4Al764 --id <item-id> --field-id <field-id>` plus `--single-select-option-id <option-id>` or `--date <YYYY-MM-DD>`.
- **Set the issue status**: `gh api graphql -f query='mutation{setIssueStatus(input:{issueId:"<issue-id>",statusId:"<status-id>"}){issue{state status{name}}}}'`.

When each field is set:

- **Created**: project Status Backlog, issue status Backlog, and Priority High, Medium or Low as the issue's urgency reads (Medium when nothing says otherwise). The dates stay empty.
- **Work starts** (an implementer, or you, begins on the ticket): project Status In Progress, issue status In progress, Start Date today.
- **Work finished** (the PR that resolves it is merged; a merged PR moves the issue to In review on its own): issue status Done, project Status Done, End Date today. Check `state` is `CLOSED` afterwards and run `gh issue close <n>` if it is not.

## Pull requests as a triage surface

**PRs as a request surface: no.** _(Set to `yes` if this repo treats external PRs as feature requests; `/triage` reads this flag.)_

When set to `yes`, PRs run through the same labels and states as issues, using the `gh pr` equivalents:

- **Read a PR**: `gh pr view <number>`, then `gh pr view <number> --comments` (off a TTY, `--comments` prints only the comments, not the body), and `gh pr diff <number>` for the diff.
- **List external PRs for triage**: `gh api --paginate 'repos/{owner}/{repo}/pulls?state=open' --jq '.[] | select(.author_association | IN("OWNER","MEMBER","COLLABORATOR") | not) | {number, title, author: .user.login, author_association, labels: [.labels[].name]}'`.
- **Comment / label / close**: `gh pr comment`, `gh pr edit --add-label`/`--remove-label`, `gh pr close`.

GitHub shares one number space across issues and PRs, so a bare `#42` may be either: resolve with `gh pr view 42` and fall back to `gh issue view 42`.

## When a skill says "publish to the issue tracker"

Create a GitHub issue.

## When a skill says "fetch the relevant ticket"

Read it as in **Read an issue** above.

## Wayfinding operations

Used by `/wayfinder`. The **map** is a single issue with **child** issues as tickets.

- **Map**: a single issue labelled `wayfinder:map`, holding the Notes / Decisions-so-far / Fog body. `gh issue create --label wayfinder:map`.
- **Child ticket**: an issue linked to the map as a GitHub sub-issue (see **Make an issue a sub-issue of a parent**). Where sub-issues aren't enabled, add the child to a task list in the map body and put `Part of #<map>` at the top of the child body. Labels: `wayfinder:<type>` (`research`/`prototype`/`grilling`/`task`). Once claimed, the ticket is assigned to the driving dev.
- **Blocking**: see **Blocking** under Conventions.
- **Frontier query**: list the map's open children (`gh issue list --state open`, scoped to the map's sub-issues / task list), drop any with an open blocker (see **Count open blockers**, or an open issue in the `Blocked by` line) or an assignee; first in map order wins.
- **Claim**: `gh issue edit <n> --add-assignee @me`, the session's first write.
- **Resolve**: `gh issue comment <n> --body "<answer>"`, then `gh issue close <n>`, then append a context pointer (gist + link) to the map's Decisions-so-far.
