<!-- One template for every kind of PR: a feature, a bug fix, a refactor, or another change such as tooling, build, CI or dependencies. A reviewer reads the visible part in a few minutes. Everything else goes in the collapsed Details.

Each section's comment says whether it's required. Leave out an optional section that doesn't apply, title and all. Give each section you keep its title.

A PR does one thing: a refactor gets its own PR, merged before the feature that needs it. -->

## Issue

<!-- Required.
- Feature: "Closes #<spec issue>", and one more line for each of its sub-issues.
- Bug fix: "Closes #<bug issue>".
- Refactor: "Closes #<issue>", then "Unblocks #<issue>" for each issue it blocks.
- Stacked PRs: "Part <n> of <m> of #<issue>". Only the top part closes the issue.
- No issue: "No issue:" and what the PR fixes or improves. -->

## What changed

<!-- Required. First why, in two to four lines:
- Feature: the problem it solves for the user and the business.
- Bug fix: what the user saw, and its cause.
- Refactor: what the feature it unblocks can't do cleanly without it.
- Other: what it makes easier or safer.

Then the architecture decisions, as bullets, each with its reason: a new module, where a rule lives, how data flows between layers, a schema change, a new dependency. Leave this part out when the change follows the rules as written, as most bug fixes do.

Optional: a diagram, when it helps more than the bullets: the modules and their dependencies, the data flow across layers, or the screens when navigation changes. Draw only what the change touches. Collapse it in its own <details><summary>Diagram: what it shows</summary>, with a blank line after </summary> and before </details>, around a ```mermaid block. -->

## Screenshots

<!-- Optional: when the PR adds or changes reference images. A refactor never does.
One row per preview: | Screen | Before | After |, with the light phone variant when there are several. Point images at fixed commits: <img src="https://github.com/<owner>/<repo>/blob/<sha>/<path>?raw=true" width="240">. -->

## Screen recordings

<!-- Optional: when the PR adds or changes a Maestro flow. A refactor never does.
One video per flow, under a one-line caption saying what it tests. Never a video that shows sign-in: it would show the test phone number. -->

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
