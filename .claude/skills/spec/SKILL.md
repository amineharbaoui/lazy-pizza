---
name: spec
description: Write a feature spec together with the developer, then create it as a GitHub issue. Use when the developer wants to specify a new feature or a user-visible change before it is implemented.
argument-hint: "[feature idea, or the name of a draft to continue]"
disable-model-invocation: true
---

# /spec

Write, with the developer, a complete spec for one feature, then create it as a GitHub issue. The spec gives a developer everything they need to build the feature, except the technical choices: the implementation makes those.

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
- If `.claude/spec-drafts/` already holds drafts, and one matches the feature idea or the developer named it, offer to continue that draft instead of starting over.
- If the feature idea is empty, ask what the feature is.

### 2. Read the code first

Before asking anything, learn how the app behaves today in the area the feature touches: the screens, the ViewModels and their UI states, the domain models and use cases, and the existing texts in `strings.xml`. Look for related specs with `gh issue list --state all --search "<keywords>"`.

Never ask the developer something the code already answers. When you mention current behavior in a question, say what you found.

### 3. Ask, one question at a time

Use `AskUserQuestion` with exactly one question per call:

- Give 2 to 4 options. Put your recommendation first, mark it `(Recommended)`, and say why in its description.
- The developer can always type their own answer instead. Never set the `preview` field on an option: a question with previews loses its free-answer row. To compare layouts, print the mockups in your message just before the question, label them like the options, and keep the options short.
- Ask only what the developer must decide: the goal, scope, rules, edge-case behavior, texts, constraints. Never ask a technical question. The implementation decides those.
- Go roughly in section order: Why, Scope, Business rules, Scenarios, UI, Constraints, Dependencies.
- After each answer, update the draft (step 4) before asking the next question.

Stop asking when every section can be filled. Something the developer can't answer yet goes into Open questions.

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

For sub-issues, create the parent first, then each sub-issue the same way, then link each one to the parent:

```bash
gh api repos/{owner}/{repo}/issues/<parent number>/sub_issues -X POST -F sub_issue_id="$(gh api repos/{owner}/{repo}/issues/<sub-issue number> --jq .id)"
```

Then:

- Give the developer the issue URL.
- Delete the draft folder. The issue is now the only copy of the spec.
