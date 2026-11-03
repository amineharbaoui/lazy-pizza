---
name: implement
description: Build a feature from its spec issue, from approved screens to an open pull request. Use when the developer runs /implement with the number of a spec issue written by /spec.
argument-hint: "<issue number>"
disable-model-invocation: true
---

# /implement

Build the feature a spec issue describes, and open its pull request. The spec, written with `/spec`, says what to build. You make the technical choices, guided by CLAUDE.md and `.claude/rules/`.

The developer approves the screens once, near the start. That approval is their go for everything after it: tests, code, checks, review and the pull request follow without another question, except in the cases below.

Issue: $ARGUMENTS

## When to ask the developer

Ask with `AskUserQuestion`, one question per call. Give 2 to 4 options, put your recommendation first, mark it `(Recommended)`, and say why. Ask only:

- before opening prerequisite PRs (step 3)
- at the UI checkpoint (step 4)
- when the spec is wrong, ambiguous or incomplete (see "Spec gaps")
- when you hesitate between technical choices that are hard to undo, such as a new module, a new dependency or a schema change
- when the same check fails 3 times for the same reason

Everything else is your call. Write down each choice a reviewer might question in the plan's Calls. They go in the PR, where the developer reviews them.

## Working notes

Keep your notes in `.claude/implement-drafts/<issue>/`. The folder is gitignored and never committed.

- `spec.md`: the issue body as you last read it, followed by its sub-issues' bodies
- `plan.md`: the technical plan (step 2), with a Progress list of the steps done, so a later run can resume
- `review/`: the files the reviewer reads (step 8)
- `pr.md`: the PR description (step 9)

## Steps

### 1. Start

- Run `gh auth status`. If it fails, stop and ask the developer to run `gh auth login`.
- If the issue number is missing, ask for it.
- Read the issue with `gh issue view <issue> --json number,title,body,state,comments`, and its sub-issues: `gh api repos/{owner}/{repo}/issues/<issue>/sub_issues --jq '.[].number'`, then each one the same way. Save them in `spec.md`.
- Stop and say why when the issue is closed, isn't a spec (it has no Business rules and Scenarios), or its Open questions isn't `None.`. A spec is fixed with `/spec` and the developer, not here.
- If the issue has a parent issue, say so and ask whether to build the parent instead. `/implement <parent>` builds the parent and all its sub-issues in one PR, which closes each of them.

Then resume, or start fresh:

- If `.claude/implement-drafts/<issue>/plan.md` exists, or a branch `feature/<issue>-*` exists locally or on `origin`, resume. Switch to the branch if there is one, read `plan.md`, and continue from the first step not done. If the branch has an open PR, see "After the PR".
- Otherwise the working tree must be clean. If `git status --porcelain` shows changes, stop and ask the developer to commit or stash them. Then run `git fetch origin main`.

### 2. Plan

Before deciding anything, read the code the spec touches: the screens, ViewModels and UI states, use cases, repositories and data sources, `strings.xml`, and the navigation in `:app`. Read CLAUDE.md and the rules of each layer you'll change.

Write `plan.md` with these sections:

- **Modules**: each module you change or add, and why. A new screen gets its own `:ui-<screen>` module.
- **Domain**: models, sealed results, use cases, repository interfaces.
- **Data**: repositories, data sources, Firestore and Room changes.
- **UI**: per screen, its UI state, display models, and one preview per state the spec lists.
- **Navigation**: routes and back-stack changes in `:app`.
- **Scenarios and tests**: every scenario, with the test class and test name that will check it. A scenario no test can check, such as a back-stack behavior that lives in `:app`, is marked `not automated`, with the reason.
- **Prerequisites**: see step 3.
- **Calls**: the technical choices a reviewer might question, each with its reason.
- **Follow-ups**: Code not to copy you saw but won't fix, because the change doesn't rewrite it.
- **Test changes**: empty for now (step 6).
- **Progress**: the steps done.

The developer doesn't approve the plan: they review your technical choices in the PR. You show it to them at the UI checkpoint, so they can redirect you there.

### 3. Prerequisites

Check that the tests the spec needs can be written the way `.claude/rules/testing.md` requires:

- Each data module the acceptance tests run through has fakes and a `<Feature>TestData` in `src/testFixtures/`.
- Each screen module the spec changes applies `custom.screenshot.testing`, and its packages start with its namespace. Otherwise its previews silently get no screenshot tests.

Never work around a missing prerequisite with MockK, a skipped test or a missing preview. When one is missing:

1. List the prerequisites, one PR each, each small enough to review on its own, for example "Fakes and `OrderTestData` for `:features:order:data`". Ask "Open these prerequisite PRs?", with the options `Yes` and `No`. On `No`, stop.
2. For each one, create `refactor/<slug>` from the latest `origin/main` and make the change, following the rules. Remove the lines it fixes from the "Code not to copy" lists. Write what it changes and why in `.claude/implement-drafts/<issue>/prereqs/<slug>.md`. Run step 7, then step 8 with that file in place of `spec.md` and `plan.md` and without a test checkpoint. Then open its PR as step 9 does, without the fold. Its template sections that don't apply say `None.`.
3. Write the PR links in `plan.md` and stop. Tell the developer to run `/implement <issue>` again once they're merged.

On the next run, check with `gh pr view <number> --json state` that each one is merged. If one isn't, say which and stop. Then `git fetch origin main`, re-read the code they changed, and update the plan.

When nothing is missing, create the feature branch from the latest main: `git switch -c feature/<issue>-<slug> origin/main`. The slug is a few lowercase words from the issue title, joined by hyphens, for example `feature/73-reorder-past-order`.

### 4. UI checkpoint

When the spec's UI section is `None.`, go to step 5.

1. Build each screen the spec names, following `.claude/rules/ui.md`: its UI state, display models, the texts in `strings.xml`, and the stateless `<Name>ScreenContent` with its callbacks. Add one preview per state the spec lists, with fake display models and the spec's exact texts. A new `:ui-<screen>` module is created now, as `.claude/rules/gradle.md` says. Write no ViewModel logic, use case or data change yet: the `<Name>Screen` may pass empty callbacks until step 6.
2. Record every module you touched, in one command: `./gradlew :<module>:recordRoborazziDebug :<other module>:recordRoborazziDebug`.
3. List the images that changed with `git status --porcelain -uall -- '*/src/test/screenshots/*'`: `??` is new, `M` is changed. Look at each one yourself first with Read. Fix any text that differs from the spec, any clipped text and any broken layout, then record again.
4. Copy the previous version of each changed image to `.claude/implement-drafts/<issue>/before/`, with `git show HEAD:<path>`. Open all the images, new ones and previous ones, with a single `open <paths>` call.
5. Print the plan, then a table of the images: the image, its screen, the spec state it shows, and whether it's new or changed. An image of a screen the spec doesn't name is flagged, with its cause.
6. Ask "Approve the screens?", with the options `Approve` and `Change something`. Until the developer approves, apply what they ask, record again and show again.
7. On `Approve`, commit the screens with their images, for example `feat: add the Reorder screens for #73`. Mark step 4 done in `plan.md`.

### 5. Tests first

1. Write the tests the plan lists, following `.claude/rules/testing.md`. Each acceptance test runs the real chain, built from the data modules' `<Feature>TestData`, and checks one behavior. A scenario's Then becomes its test's assertions, with the spec's exact values and texts. Test names describe the behavior, never a scenario number: the PR maps scenarios to tests.
2. Add only the stubs the tests need to compile: new ViewModel functions, use case and repository signatures, UI state fields. A stub does nothing or returns a fixed value, never the behavior and never `TODO()`.
3. Run the new test classes: `./gradlew :<module>:testDebugUnitTest --tests "<class>"`. Every new test must fail on an assertion. A test that already passes doesn't check the new behavior: fix it. A test that fails with an exception: fix the stub or the test setup.
4. Commit the tests and stubs as a local checkpoint, never pushed: `test: checkpoint for #<issue>`. Write its hash in `plan.md`.

### 6. Build

Build from the inside out: domain, data, then the ViewModel and the screen's wiring, then the navigation in `:app`. Run the module's tests as you go. Don't commit before step 9.

- Follow CLAUDE.md and `.claude/rules/`. Fix Code not to copy only where your change rewrites that code, and remove its line from the rule file's list. Add anything bigger to the plan's Follow-ups.
- When a test fails, decide whether the code or the test is wrong. A test may change to follow a rename or to fix a wrong fixture. It never loses an assertion, gets a looser expected value, or gets deleted or disabled to pass. Write every test change and its reason in the plan's Test changes.
- When the same check fails 3 times for the same reason, stop and tell the developer what you tried.
- When the spec turns out wrong, ambiguous or incomplete, see "Spec gaps".

### 7. Verify

Run the checks in as few commands as possible, so the developer isn't asked again and again:

1. List the changed Kotlin files: `git diff --merge-base origin/main --name-only --diff-filter=d -- '*.kt' '*.kts'`. `--merge-base` compares with the commit the branch started from, including uncommitted changes, even after `main` moved on.
2. Lint them and run the build, in one command: `ktlint --relative <files> && ./gradlew unitTestAll verifyRoborazziDebug assembleDebug`. Leave out the `ktlint` part when no Kotlin file changed: without files, it lints the whole project. If `ktlint` isn't installed, ask the developer to install the version `.github/workflows/ktlint.yml` uses.
3. When a build file or `gradle/libs.versions.toml` changed, also run `./gradlew buildHealth` and apply its advice for the modules you touched.

Then check the screenshots against the spec:

- List every image the branch adds, changes or deletes: `git diff --merge-base origin/main --name-status -- '*/src/test/screenshots/*'`.
- Each image belongs to a screen, through its module and preview name. An image of a screen the spec doesn't name is a leak, for example a design-system change that moved another screen. Fix the code, or keep the image and give the reason in the PR.
- A renamed or deleted preview leaves its old images behind. Delete them.

### 8. Review

Write the reviewer's inputs to `.claude/implement-drafts/<issue>/review/`:

- `changes.diff`: `git diff --merge-base origin/main -- . ':(exclude)*.png'`, everything the PR changes except the images
- `tests-since-checkpoint.diff`: `git diff <checkpoint> -- '*/src/test/*' '*/src/testFixtures/*' ':(exclude)*.png'`, every test change since the checkpoint
- `screenshots.md`: the image list from step 7, with each image's screen, and the reason for any outside the spec's screens

Run the `implement-reviewer` subagent, at most 3 rounds. It reports findings and ends with a verdict.

- Round 1: give it the paths of `spec.md`, `plan.md` and the three review files. It reviews everything.
- Rounds 2 and 3: give it the same paths, the previous round's findings, and how each was handled. It checks only those changes.

Handle each finding:

- When the fix is clear, apply it.
- When it changes what the user sees, or a business rule, ask the developer, one finding per question. Offer the reviewer's fix as the recommended option, and `Not needed` to leave it.

After changes, run step 7's checks again, rewrite the review files, and run the next round, until the verdict is `CLEAN`. When round 3 still has findings, handle them the same way and stop reviewing.

### 9. Pull request

1. Fold the test checkpoint into the rest of the work, so every pushed commit compiles and passes: `git reset --soft <checkpoint>~1`, then `git add -A` and commit, for example `feat: reorder a past order (#73)`.
2. Push the branch by name: `git push -u origin feature/<issue>-<slug>`. Never push `main`, never force-push.
3. Write `pr.md` from `.github/pull_request_template.md`, filling every section. A section that doesn't apply says `None.`. Paste the end of step 7's output under Verification.
4. Open the PR: `gh pr create --title "<issue title>" --body-file .claude/implement-drafts/<issue>/pr.md`.
5. Give the developer the PR link, and point them to the Calls and any screenshot outside the spec's screens.

Keep the notes folder until the PR is merged: a later run needs it.

## Spec gaps

When building shows the spec is wrong, ambiguous or incomplete, for example a scenario that contradicts a rule or a state with no text:

- Never guess a behavior the user would notice. Ask the developer, with your suggested fix as the recommended option.
- Never edit the issue without the developer's yes. On yes, read the issue again, apply only that change, update it with `gh issue edit <issue> --body-file <file>`, and update `spec.md`. List the change in the PR's Spec changes.
- A technical detail never goes into the spec.

## After the PR

When the developer asks you to address the PR's review comments, in the same session or by running `/implement <issue>` again:

- Read them with `gh pr view <number> --comments` and `gh api repos/{owner}/{repo}/pulls/<number>/comments`.
- Read bot reviews, such as CodeRabbit or Sourcery, critically. Fix what's clearly right, reply with your reason when you disagree, and ask the developer when it's only a matter of taste.
- Push each fix as a new commit, after step 7's checks pass. Never force-push.
- Once the PR is merged, delete `.claude/implement-drafts/<issue>/`.
