---
name: spec
description: Write a feature spec together with the developer, then create it as a GitHub issue, with the refactor issues its build needs first. Use when the developer wants to specify a new feature or a user-visible change before it is implemented.
argument-hint: "[feature idea, or the name of a draft to continue]"
disable-model-invocation: true
---

# /spec

Write, with the developer, a complete spec for one feature, then create it as a GitHub issue. The spec gives a developer everything they need to build the feature, except the technical choices: the implementation makes those.

Then create the refactor issues the feature needs first (step 8), so `/implement` can build it straight through once they're closed. They are separate issues: nothing technical goes into the spec.

Feature idea: $ARGUMENTS

## What goes in a spec

If a user or the business could notice it, it goes in the spec. If only the code can see it, it stays out.

- In: what the user sees and can do, business rules, every failure and edge case, exact texts, what happens offline, accessibility, analytics events.
- Out: modules, classes, layers, libraries, data schemas, caching, retry strategies, threading, API design.

Write behavior, not mechanism. "When offline, the menu shows the last loaded pizzas and the banner `You're offline`" belongs in a spec. "Cache the menu in Room and retry with backoff" does not.

Write the spec in English. UI texts are exact, as the user will read them.

## Steps

### 1. Prepare

- Run `gh auth status`. If it fails, stop and ask the developer to run `gh auth login`.
- Run `gh --version`. Attaching sketches to the issue needs 2.99.0 or later. If it's older, stop and ask the developer to upgrade `gh`.
- If `.claude/spec-drafts/` already holds drafts, and one matches the feature idea or the developer named it, offer to continue that draft instead of starting over. When its folder has an `issue.md`, the issue already exists: continue from step 8 with the issue it names.
- If the feature idea is empty, ask what the feature is.

### 2. Read the code first

Before asking anything, learn how the app behaves today in the area the feature touches: the screens, the ViewModels and their UI states, the domain models and use cases, and the existing texts in `strings.xml`. Look for related specs with `gh issue list --state all --search "<keywords>"`.

Never ask the developer something the code already answers. When you mention current behavior in a question, say what you found.

### 3. Ask, one question at a time

Use `AskUserQuestion` with exactly one question per call:

- Give 2 to 4 options. Put your recommendation first, mark it `(Recommended)`, and say why in its description.
- The developer can always type their own answer instead. Never set the `preview` field on an option: a question with previews loses its free-answer row. To compare layouts, draw a sketch (below) and name the options like it.
- Ask only what the developer must decide: the goal, scope, rules, edge-case behavior, texts, constraints. Never ask a technical question. The implementation decides those.
- Go roughly in section order: Why, Scope, Business rules, Scenarios, UI, Constraints, Dependencies.
- After each answer, update the draft (step 4) before asking the next question.

Stop asking when every section can be filled. Something the developer can't answer yet goes into Open questions.

#### Layout sketches

Draw a sketch only when the feature adds a screen or changes where things sit on one. A new text or a new state on an existing layout doesn't need one.

- Draw it as an SVG file in the draft folder, one per screen: `.claude/spec-drafts/<feature-slug>/<screen-slug>.svg`. Give it a `viewBox` and a `width`, so it shows at a readable size.
- Keep it a wireframe: grey boxes and lines, the exact texts from the spec, no fonts or design-system styling, and no color except one accent color that outlines what's new. Draw the parts that exist today as the code shows them.
- When there is a layout choice, draw the options side by side in the same file, labeled A, B, ..., each with one line on its trade-off. Name the question's options the same way.
- Open the file with `open <path>` before asking, so the developer sees it in their browser while the question waits in the terminal.
- Once the developer has chosen, redraw the file with only the chosen layout, and reference it in the screen's UI block, with the same path as the file (see the template).

The sketch only settles the layout. The real screens are approved later, as Compose screenshots, when the feature is implemented.

### 4. Keep a local draft

Write the draft to `.claude/spec-drafts/<feature-slug>/spec.md`, following [template.md](template.md). The slug uses only lowercase letters, digits and hyphens, for example `order-again`. The folder is gitignored and never committed.

- The first line is `# <title>`: a short name for the feature, as the issue title.
- Business rules are numbered `R1`, `R2`, ... Scenarios are numbered `1`, `2`, ... and each one lists the rules it covers.
- Business rules and Scenarios each fold into one `<details>` block, and each UI screen into its own, as in the template, so the issue stays short and a reader opens only what they need. The summary gives the count, for example `5 rules`. Keep a blank line after `</summary>` and before `</details>`, or GitHub doesn't render the Markdown inside.
- Once the issue exists, a scenario is referred to outside the issue as `<issue number>-<scenario number>`, for example `42-3`.
- A section with nothing in it says `None.` explicitly. Only UI, Constraints and Dependencies can be `None.`, when nothing applies. Open questions must end up `None.`

If the spec looks too big for one issue, propose a split into a parent issue and sub-issues, saying what goes in each, and ask the developer to validate it. Each sub-issue then gets its own draft in the same folder, following the same template. The parent keeps Why, Scope and the rules they share.

### 5. Review

Run the `spec-reviewer` subagent, at most 3 rounds. It reports findings and ends with a verdict.

- Round 1: give it the draft's path. It reviews the whole draft.
- Rounds 2 and 3: give it the draft's path, the previous round's findings with how each was handled, and any other change since. It checks only those changes and looks for no new cases.

Handle each finding:

- When the fix is clear, apply it.
- When it needs a decision, and the app already handles the same case elsewhere or only one answer is reasonable, make it yourself. Write it in the draft and add it to `.claude/spec-drafts/<feature-slug>/calls.md`: the decision, where it is in the draft, and why. That file is never part of the issue. When in doubt, ask instead.
- Otherwise ask the developer, one finding per question, like any other question (step 3). Offer the reviewer's fix as the recommended option, and `Not needed` to leave the spec as it is.

After changes, run the next round, until the verdict is `CLEAN`. When round 3 still has findings, handle them the same way, one question at a time, and stop reviewing.

### 6. Ask "Create the issue?"

The spec is done when:

- all 8 sections are filled
- every business rule has at least one scenario
- Open questions is `None.`
- the last review is `CLEAN`, or every finding of round 3 is handled

Before that, keep working on the draft. The developer can stop at any time, and the draft stays in `.claude/spec-drafts/` for a later `/spec`.

When it is done, print the full draft in the conversation, followed by your calls from `calls.md` if there are any, so the developer can change any of them. Then ask with `AskUserQuestion`: "Create the issue?", with the options `Yes` and `No`.

- On `No`, ask what to change and apply it. If the content changed, run one more review of those changes, like rounds 2 and 3. Then ask again.
- On `Yes`, go to step 7.

### 7. Create the issue

Create it once, from the draft. The title comes from the draft's first line, so it is never retyped into the command:

```bash
draft=.claude/spec-drafts/<feature-slug>/spec.md
title="$(head -n 1 "$draft" | sed 's/^# //')"
tail -n +2 "$draft" | gh issue create --title "$title" --body-file -
```

When the draft references sketches, add one `--attach <path>` per sketch to that command, with the same path the draft uses, for example `--attach .claude/spec-drafts/order-again/order-history.svg`. `gh` uploads each file and points the draft's image reference at it.

For sub-issues, create the parent first, then each sub-issue the same way, then link each one to the parent:

```bash
gh api repos/{owner}/{repo}/issues/<parent number>/sub_issues -X POST -F sub_issue_id="$(gh api repos/{owner}/{repo}/issues/<sub-issue number> --jq .id)"
```

Then:

- If a command fails, check whether the issue was created anyway before you run it again, again taking the title from the draft: `gh issue list --search "$(head -n 1 "$draft" | sed 's/^# //')"`.
- Check the issue body with `gh issue view <number>`: it must not contain a `.claude/spec-drafts/` path. If it does, a sketch didn't upload: keep the draft folder and tell the developer which sketch is missing.
- Give the developer the issue URL.
- Write the issue's URL, and each sub-issue's, in `.claude/spec-drafts/<feature-slug>/issue.md`. A later `/spec` that continues this draft then goes to step 8 with that issue, instead of creating it again.

### 8. Refactors first

Find what the feature needs before it can be built, as CLAUDE.md's "Refactors before a feature" says. This is the only technical step of `/spec`, and its only question is the one below: how each refactor is done stays with `/implement`.

1. Read the code the feature will change, beyond what step 2 read: the build files of the modules it touches, the data modules' `src/testFixtures/`, and the "Code not to copy" lists in `.claude/rules/`. List the refactors the feature needs. Look for open refactor issues that already cover one with `gh issue list --state open --search "Refactor in:title <keywords>"`, and reuse each one you find instead of writing it again.
2. When the feature needs none, say so and go to step 9. Otherwise print the list, one issue per refactor, each small enough to review on its own, with why the feature needs it, for example "Fakes and `OrderTestData` for `:features:order:data`, so the Reorder acceptance tests can run the real chain". Ask with `AskUserQuestion`: "Create these refactor issues?", with the options `Yes` and `No`. On `No`, go to step 9: `/implement` will find them. When the developer answers something else, such as dropping one, follow it.
3. Write each new one as a draft, `.claude/spec-drafts/<feature-slug>/refactors/<refactor-slug>.md`, following `.github/ISSUE_TEMPLATE/refactor.md` without its frontmatter. The first line is `# Refactor: <what changes>`, and each comment is replaced by its section's content.
4. Create each one the same way as the spec in step 7, from its draft. Then mark each one, new or reused, as blocking the feature. For a spec with sub-issues, the feature is the parent issue.

   ```bash
   gh api repos/{owner}/{repo}/issues/<feature issue>/dependencies/blocked_by -X POST -F issue_id="$(gh api repos/{owner}/{repo}/issues/<refactor issue> --jq .id)"
   ```

5. Give the developer each refactor issue's URL. Each one is built with `/implement <refactor issue>`, and the feature with `/implement <feature issue>` once they're all closed.

### 9. Clean up

Delete the draft folder. The issues are now the only copy of the spec and its refactors. When step 7 found a sketch that didn't upload, keep the folder instead: it holds the only copy of that sketch. Tell the developer again which sketch is missing.
