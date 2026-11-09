<!-- The top stays short, so a reviewer sees on one screen what to check. Everything else folds into a <details> block, whose summary gives its count. Leave out a folded section with nothing in it. A PR does one thing: a refactor gets its own PR, merged before the feature that needs it. -->

<!-- First line: "Closes #<issue>" for the spec issue, and one more line for each of its sub-issues. A refactor: "Closes #<issue>", then "Unblocks #<issue>" for each issue it blocks. In a stack: "Part <n> of <m> of #<issue>", and only the top part closes the issue. A change without an issue: what it fixes or improves. -->

<!-- What and why, in two or three lines: what a user can now do or see. For a change no user notices, what it changes in the code and why. -->

### Review this first

<!-- At most 3 points, each a link to the lines it means and what to check there: the riskiest logic, a choice a reviewer might question, anything hard to undo, such as a schema change, a new module or a new dependency. -->

### Screens

<!-- One row per preview whose images this PR adds or changes: | Before | After |, with the light phone variant when there are several. Images and links point at fixed commits, for example <img src="https://github.com/<owner>/<repo>/blob/<sha>/<path>?raw=true" width="240">. Leave this section out when no image changes. -->

Tests changed after they were written: <!-- none, or how many -->. Spec changes: <!-- none, or how many -->.

<details><summary>Scenarios and tests (<!-- count -->)</summary>

<!-- One row per scenario of the spec: | Scenario | Tests |, for example | 73-4 Cart already has items | `OrdersViewModelTest` > `reorder when cart has items then merges drinks and keeps pizzas separate` |. A scenario no test can check says "not automated", and why. -->

</details>

<details><summary>Technical choices (<!-- count -->)</summary>

<!-- The choices a reviewer might question, each with its reason: new modules, new dependencies, schema changes, patterns. -->

</details>

<details><summary>Tests changed after they were written (<!-- count -->)</summary>

<!-- Each test changed after it was first written, and why. -->

</details>

<details><summary>Spec changes (<!-- count -->)</summary>

<!-- Each change made to the spec issue during the work, approved by the developer. -->

</details>

<details><summary>Changed screenshots (<!-- count -->)</summary>

<!-- Every reference image this PR adds, changes or deletes, grouped by screen. An image of a screen the spec doesn't name needs a reason. -->

</details>

<details><summary>Follow-ups (<!-- count -->)</summary>

<!-- Problems seen but left for later, such as Code not to copy or a refactor this PR didn't need. -->

</details>

<details><summary>Verification</summary>

<!-- The commands run and the end of their output. -->

</details>
