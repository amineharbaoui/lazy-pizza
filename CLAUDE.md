# LazyPizza

Android pizza ordering app: Kotlin, Jetpack Compose (Material 3), Hilt, Room, Firebase Auth & Firestore, Navigation 3. Multi-module and feature-based: `:core:*` and `:features:<name>:{domain,data,ui-*}`, listed in `settings.gradle.kts`.

## How to build here

LazyPizza is a showcase that mirrors a very large multi-module production app. The app is small today, but every change is built as if it were that app: open to scale-up and easy to maintain.

- Several squads work on the project, and any squad can change any part of it. Follow the project's conventions over your own preferences, so every module reads the same to whoever opens it next.
- Scale is a reason for clear module boundaries, not for abstractions with no present use.
- Scale shapes how code is built, never what a feature does. That comes from the spec.
- Existing code that breaks a rule is listed under "Code not to copy" in `.claude/rules/`. Don't imitate it. Fix it only where your change already rewrites that code, and list anything bigger in the PR description as a follow-up.

## Commands

- Lint: CI runs ktlint only on the Kotlin files a PR changes. `./gradlew ktlintCheck` also reports old violations in about ten modules: fix the ones in files you change, leave the rest, and never reformat a file you don't otherwise change.
- All unit tests: `./gradlew unitTestAll`
- One module: `./gradlew :features:cart:domain:test` (pure Kotlin module) or `./gradlew :features:cart:data:testDebugUnitTest` (Android module)
- One test class: add `--tests "com.example.cart.domain.usecase.AddCartItemUseCaseTest"`
- Build: `./gradlew assembleDebug`
- Dependency health: `./gradlew buildHealth`

## Workflow

- A user-visible change starts with `/spec`, which writes its spec with the developer and creates it as a GitHub issue.
- Specs are GitHub issues. Never edit one without asking the developer first.

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
- Add a unit test only where an acceptance test is the wrong tool: logic with many input cases, such as prices or pickup slots, or shared code no screen owns, such as `:core:*`. Don't repeat in a unit test what an acceptance test already covers.
- A bug fix starts with a test that fails because of the bug. Then fix it and watch the test pass.

## Done

A change to Kotlin or Gradle code is done when:
- it has its tests, and it follows this file and `.claude/rules/`
- the Kotlin files it changes pass ktlint
- `./gradlew unitTestAll assembleDebug` passes. Show the command and the end of its output.

## Git

- Push only your own branch, by name (`git push -u origin <branch>`). Never push to `main`, never force-push.
