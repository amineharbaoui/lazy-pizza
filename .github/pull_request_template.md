<!-- One template for every kind of PR: a feature, a bug fix, a refactor, or another change such as tooling, build, CI or dependencies. A reviewer reads the visible part in a few minutes. Everything else goes in the collapsed Details.

Each section's comment says whether it's required, and where an alert goes. Alerts follow `.claude/rules/github.md`. Leave out an optional section that doesn't apply, title and all. Give each section you keep its title.

A PR does one thing: a refactor gets its own PR, merged before the feature that needs it. -->

## Issue

<!-- Required.
- Feature: "Closes #<spec issue>", and a "Closes #<sub-issue>" line for each of its sub-issues.
- Bug fix: "Closes #<bug issue>".
- Refactor: "Closes #<issue>", then "Unblocks #<issue>" for each issue it blocks.
- Stacked PRs: a [!IMPORTANT] alert, "Part <n> of <m> of #<issue>. Merge the parts in order, from part 1." Only the top part closes the issue.
- No issue: "No issue:" and what the PR fixes or improves. -->

## What changed

<!-- Required. First why, in two to four lines:
- Feature: the problem it solves for the user and the business.
- Bug fix: what the user saw, and its cause.
- Refactor: what the feature it unblocks can't do cleanly without it.
- Other: what it makes easier or safer.

Then the architecture decisions, as bullets, each with its reason: a new module, where a rule lives, how data flows between layers, a schema change, a new dependency. Leave this part out when the change follows the rules as written, as most bug fixes do.

Optional, right after the why: one risk alert, saying where the risk is and what to check. [!CAUTION] when the PR holds something that can't be undone once released, such as a database migration or a change to stored data. Otherwise [!WARNING] for the part that needs the reviewer's closest look, such as a price rule, sign-in or deleting data.

When the PR has several commits, end the section with a [!TIP] alert: "Review it commit by commit: each one builds and passes on its own."

Optional: a diagram, when it helps more than the bullets: the modules and their dependencies, the data flow across layers, or the screens when navigation changes. Draw only what the change touches. Collapse it in its own <details><summary>Diagram: what it shows</summary>, with a blank line after </summary> and before </details>, around a ```mermaid block.

Color each part by what the PR does to it. In a flowchart, end the block with these lines, then `class <ids> new`, `class <ids> changed` and so on, and draw a new dependency as a thick arrow, ==>:
  classDef new fill:#2da44e33,stroke:#2da44e,stroke-width:2px
  classDef changed fill:#d4a72c33,stroke:#d4a72c,stroke-width:2px
  classDef removed fill:#cf222e1a,stroke:#cf222e,stroke-dasharray:4 3
  classDef unchanged fill:#8c959f1a,stroke:#8c959f,stroke-dasharray:4 3
In a sequenceDiagram, wrap the new steps in `rect rgba(46, 160, 67, 0.2)` ... `end`, and the changed ones in `rect rgba(212, 167, 44, 0.2)` ... `end`. These see-through colors read in GitHub's light and dark themes. Under the diagram, one line names the colors it uses, such as "Green: new. Amber: changed. Grey: unchanged." -->

## Screenshots

<!-- Optional: when the PR adds or changes reference images. A refactor never does.
One row per preview: | Screen | Before | After |, with the light phone variant when there are several. Point images at fixed commits: <img src="https://github.com/<owner>/<repo>/blob/<sha>/<path>?raw=true" width="240">. -->

## Screen recordings

<!-- Optional: when the PR adds or changes a Maestro flow. A refactor never does.
One video per flow, under a one-line caption saying what it tests. A video over 10 MB is left out, with a [!NOTE] alert under its caption saying so. -->

## Details

<details><summary>Tests, verification and follow-ups</summary>

### Scenarios and tests

<!-- Required for a feature or a bug fix. Optional for a refactor or another change, when it adds tests.
- Feature: one row per scenario of the spec: | Scenario | Tests |, for example | 73-4 Cart already has items | `OrdersViewModelTest` > `reorder when cart has items then merges drinks and keeps pizzas separate` |. A scenario no test can check says "not automated", and why.
- Bug fix: the test that failed because of the bug, and passes with the fix.
- Refactor or another change: the tests it adds, such as for new test setup. -->

### Tests changed after they were written

<!-- Required for a feature, a bug fix or a refactor: each test changed after it was first written, and why. "None." when none. -->

### Spec changes

<!-- Required for a feature: each change made to the spec issue during the work, approved by the developer. "None." when none. -->

### Changed screenshots

<!-- Required when the PR has Screenshots: every reference image it adds, changes or deletes, grouped by screen. An image of a screen the spec doesn't name needs a reason. -->

### Follow-ups

<!-- Optional: problems seen but left for later, such as Code not to copy or a refactor this PR didn't need. -->

### Verification

<!-- Required: the commands run and the end of their output. -->

</details>
