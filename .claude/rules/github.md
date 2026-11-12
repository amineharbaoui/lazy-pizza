---
paths:
  - ".github/ISSUE_TEMPLATE/**"
  - ".github/pull_request_template.md"
  - ".claude/spec-drafts/**"
  - ".claude/implement-drafts/**"
---

# Issues and pull requests

## Labels

Every issue has one `size:` label and one `priority:` label. Each value is defined by its label's description on GitHub: read them with `gh label list --search "size:"` and `gh label list --search "priority:"`.

- The size measures the change the issue becomes: how many modules and layers it touches, not how long the issue is. Pick it once you've read the code.
- The priority of a feature or a bug is the developer's call. A refactor issue is at least as urgent as the most urgent issue it blocks: give it that priority when you create it, and raise it when it comes to block a more urgent one, with `gh issue edit <number> --remove-label "priority: <old>" --add-label "priority: <new>"`.

## Alerts

GitHub's alerts draw the eye to what a reader must not miss:

```markdown
> [!WARNING]
> R3 and R5 decide the price the customer pays.
```

- An alert sits only where the issue or PR template puts one, and says what that slot asks for, in one or two sentences. Never use one to decorate, or to repeat a heading.
- At most one alert of each type in an issue or PR. Each type means one thing:
  - `[!NOTE]`: what the reader gets at a glance, or a fact they need to read the rest.
  - `[!TIP]`: how to review or read it more easily.
  - `[!IMPORTANT]`: what has to happen first, or in order, for it to ship.
  - `[!WARNING]`: the part that needs the closest look, because getting it wrong costs users money or data.
  - `[!CAUTION]`: what can't be undone once it's merged or released, such as a database migration.
- Keep an alert at the top level of the body, never inside a `<details>` block, a list or a table: GitHub shows it there as a plain quote. Never put two alerts in a row.
