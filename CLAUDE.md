# LazyPizza

Android pizza ordering app: Kotlin, Jetpack Compose (Material 3), Hilt, Room, Firebase Auth & Firestore, Navigation 3. Multi-module and feature-based: `:core:*` and `:features:<name>:{domain,data,ui-*}`, listed in `settings.gradle.kts`.

## How to build here

LazyPizza is a showcase that mirrors a very large multi-module production app. The app is small today, but every change is built as if it were that app: open to scale-up and easy to maintain.

- Several squads work on the project, and any squad can change any part of it. Follow the project's conventions over your own preferences, so every module reads the same to whoever opens it next.
- Scale is a reason for clear module boundaries, not for abstractions with no present use.
- Scale shapes how code is built, never what a feature does. That comes from the spec.
- Existing code that breaks a rule is listed under "Code not to copy" in `.claude/rules/`. Don't imitate it. When your change has to change or build on it, fix it first in a refactor PR of its own. List the rest you see in the PR description as a follow-up.

## Commands

- Lint: CI runs ktlint only on the Kotlin files a PR changes. `./gradlew ktlintCheck` also reports old violations in about ten modules: fix the ones in files you change, leave the rest, and never reformat a file you don't otherwise change.
- All unit tests: `./gradlew unitTestAll`
- One module: `./gradlew :features:cart:domain:test` (pure Kotlin module) or `./gradlew :features:cart:data:testDebugUnitTest` (Android module)
- One test class: add `--tests "com.example.cart.domain.usecase.AddCartItemUseCaseTest"`
- Screenshot tests: `./gradlew verifyRoborazziDebug` fails when a preview no longer matches its reference image; `./gradlew recordRoborazziDebug` re-records them
- Build: `./gradlew assembleDebug`
- E2E flows (Maestro, on a local emulator): `scripts/e2e.sh` runs every flow, `scripts/e2e.sh cart,smoke` the flows tagged `cart` or `smoke`. Needs `.maestro/.env.local`, copied from `.maestro/.env.example`.
- Dependency health: `./gradlew buildHealth`

## Workflow

- A user-visible change starts with `/spec`, which writes its spec with the developer and creates it as a GitHub issue, then the refactor issues the feature needs first.
- Specs are GitHub issues. Never edit one without asking the developer first.
- `/implement <issue>` builds a spec once the refactor issues blocking it are closed: the developer approves the screens, then Claude writes the tests and the code, verifies and reviews them, and leaves them on a local branch, in small commits a reviewer reads one at a time. At the end, it asks whether to open the pull request now. `/implement` builds a refactor issue the same way, without the screens.
- `/open-pr <issue>` opens the pull request for that branch, as stacked PRs when it's too big to review in one sitting, with a short description: what changed and why, screenshots and recordings.

## Refactors before a feature

A feature's PR holds only the feature. What the feature needs first is a refactor, with its own issue from `.github/ISSUE_TEMPLATE/refactor.md`, marked as blocking the feature's issue. A feature needs one when it lacks:

- The test setup its tests need: fakes and a `<Feature>TestData` in the `src/testFixtures/` of each data module its acceptance tests run through, and `custom.screenshot.testing` in each screen module it changes, with packages that start with the module's namespace. Never work around missing test setup with MockK, a skipped test or a missing preview.
- Code not to copy fixed, where the feature has to change or build on it.
- Existing code reshaped so the feature fits, for example a component another screen will reuse, moved into the design system.

A refactor the feature doesn't need goes in its Follow-ups.

## Modules and dependencies

The project follows Clean Architecture strictly: dependencies point toward `:domain`. When a boundary is unclear, choose the stricter separation.

- `:domain` is pure Kotlin: business models and rules, use cases, repository interfaces. No Android, Compose, Firebase or Room.
- `:data` implements its own feature's repository interfaces with Firebase or Room. It depends only on its own `:domain` and on `:core:*`.
- `:ui-<screen>` holds one screen: composables, ViewModel, UI state. A new screen gets its own `:ui-<screen>` module. It may depend on any feature's `:domain`, never on a `:data` or on another `:ui-*`.
- A feature uses another feature only through that feature's `:domain`. Dependencies between features never form a cycle.
- Only `:app` depends on `:data` modules, so Hilt sees their bindings. `:app` also owns navigation: the routes and the back stack.
- `:core:*` modules never depend on a feature.
- One exception, in tests only: a `:ui-<screen>` module may depend on the `:data` modules it uses and on their test fixtures, to run its acceptance tests.

## In every layer

- Read the time from an injected `java.time.Clock`, never `System.currentTimeMillis()`, `Instant.now()` or `LocalDate.now()`, so time-based logic can be tested.
- Every text a user reads lives in a `strings.xml`, never in Kotlin.
- A user never sees an exception message. The UI maps a failure to one of its own texts.

## Tests

- Behavior a user can notice gets an acceptance test in the screen's `:ui-<screen>` module. It runs the real chain, from the ViewModel through the use cases to the repositories, and fakes only the data sources.
- A journey through several screens gets a Maestro flow in `.maestro/<feature>/`, which runs the real app on an emulator. It checks that the screens work together, never the rules the acceptance tests already cover.
- Add a unit test only where an acceptance test is the wrong tool: logic with many input cases, such as prices or pickup slots, or shared code no screen owns, such as `:core:*`. Don't repeat in a unit test what an acceptance test already covers.
- A bug fix starts with a test that fails because of the bug. Then fix it and watch the test pass.

## Done

A change to Kotlin or Gradle code is done when:
- it has its tests, and it follows this file and `.claude/rules/`
- the Kotlin files it changes pass ktlint
- `./gradlew unitTestAll verifyRoborazziDebug assembleDebug` passes. Show the command and the end of its output.
- for a change a user can notice, `scripts/e2e.sh <features>,smoke` passes after that, with the tag of every feature the change touches (`scripts/e2e.sh order,cart,smoke`). Show its output too.

## Git

- Push only your own branch, by name (`git push -u origin <branch>`). Never push to `main`, never force-push.
- A pull request does one thing, so it stays small and easy to review. A refactor, any change to existing code that keeps what the app does, gets its own PR, merged before the feature that needs it.
- Its commits are steps a reviewer checks one at a time: one concern each, inside out. Rebase merge puts each one on main, so each one builds and passes on its own.
- A pull request's description follows `.github/pull_request_template.md`.
