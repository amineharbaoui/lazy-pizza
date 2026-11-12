---
paths:
  - ".github/ISSUE_TEMPLATE/**"
  - ".github/pull_request_template.md"
  - ".claude/spec-drafts/**"
  - ".claude/implement-drafts/**"
---

# Issues and pull requests

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
