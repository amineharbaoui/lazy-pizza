<!-- A reviewer reads this in a few minutes: what the PR closes, why, the architecture decisions, the screens and recordings. Everything else goes in the collapsed Details. Give each section its ## title. Leave out a section with nothing in it, except Issue and Why. A PR does one thing: a refactor gets its own PR, merged before the feature that needs it. -->

## Issue

<!-- "Closes #<issue>" for the spec issue, and one more line for each of its sub-issues. A refactor: "Closes #<issue>", then "Unblocks #<issue>" for each issue it blocks. In stacked PRs: "Part <n> of <m> of #<issue>", and only the top part closes the issue. A change without an issue: what it fixes or improves. -->

## Why

<!-- Two to four lines: the problem this solves for the user and the business. For a refactor, what the feature it unblocks can't do cleanly without it. -->

## Architecture decisions

<!-- The choices that shape the code, each with its reason: a new module, where a rule lives, how data flows between layers, a schema change, a new dependency. Add a ```mermaid diagram when it helps: the modules and their dependencies, the data flow across layers, or the screens when navigation changes. Draw only what the change touches. Put it under the decisions, collapsed in its own <details><summary>Diagram: what it shows</summary>, with a blank line after </summary> and before </details>. -->

## Screenshots

<!-- One row per preview whose images this PR adds or changes: | Screen | Before | After |, with the light phone variant when there are several. Point images at fixed commits: <img src="https://github.com/<owner>/<repo>/blob/<sha>/<path>?raw=true" width="240">. -->

## Screen recordings

<!-- One video per Maestro flow this PR adds or changes, under a one-line caption saying what it tests. Never a video that shows sign-in: it would show the test phone number. -->

## Details

<details><summary>Scenarios, tests, verification and follow-ups</summary>

### Scenarios and tests

<!-- One row per scenario of the spec: | Scenario | Tests |, for example | 73-4 Cart already has items | `OrdersViewModelTest` > `reorder when cart has items then merges drinks and keeps pizzas separate` |. A scenario no test can check says "not automated", and why. -->

### Tests changed after they were written

<!-- Each test changed after it was first written, and why. "None." when none. -->

### Spec changes

<!-- Each change made to the spec issue during the work, approved by the developer. "None." when none. -->

### Changed screenshots

<!-- Every reference image this PR adds, changes or deletes, grouped by screen. An image of a screen the spec doesn't name needs a reason. -->

### Follow-ups

<!-- Problems seen but left for later, such as Code not to copy or a refactor this PR didn't need. -->

### Verification

<!-- The commands run and the end of their output. -->

</details>
