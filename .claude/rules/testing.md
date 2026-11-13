---
paths:
  - "**/src/test/**"
  - "**/src/testFixtures/**"
  - "core/testing/**"
---

# Tests

## Stack

- JUnit 6 (Jupiter), AssertJ (`assertThat`, `assertThatThrownBy`) and Turbine for flows. No JUnit assertions.
- A coroutine test is `fun ... = runTest { }`, which gives it a `TestScope` and virtual time. Move time with `advanceUntilIdle()` or `runCurrent()`. Never a real `delay` or `Thread.sleep`.
- A ViewModel test also registers `MainDispatcherExtension` from `:core:testing`. `runTest` shares its scheduler, so `advanceUntilIdle()` drives the ViewModel too.
- Time comes from a `Clock.fixed(...)`.

## Fakes, not mocks

- Acceptance tests use only the fakes from the data modules' `src/testFixtures/`, through each module's `<Feature>TestData`.
- MockK only for a class that can't be faked, such as a Firebase class in a data source test. Then use the BDD aliases: `given`, `coGiven`, `then`, `coThen`.
- Assert an outcome: the UI state, a returned value, or what a fake stored. Never only that a method was called.

## Acceptance tests

- One class per ViewModel, `<Name>ViewModelTest`, in the screen module's `src/test/`. A `@Nested inner class` per ViewModel function.
- Build the real chain by hand: the data modules' fakes and repositories, the real use cases, then the ViewModel. No Hilt in tests: the build already checks the Hilt graph.
- Each data module's `src/testFixtures/` has a public `<Feature>TestData`, written in domain types: it builds the module's repositories on its fakes, presets data and forces failures. When the feature has an `:api`, it also builds the feature's public use cases on those fakes, through `<Feature>UseCases` in `:domain`'s `src/testFixtures/`, which can reach the `internal` implementations: another feature's acceptance tests then run the real chain without depending on its `:domain`. A test never sees a DTO or an entity, and a constructor change is fixed in one place.
- Given presets data in the fakes, starts collecting `uiState` with Turbine (`uiState.test { ... }`) and waits for the loaded state. A `WhileSubscribed` state only updates while something collects it, and a ViewModel may ignore input until it has loaded. When calls a ViewModel function inside that block, and Then asserts the next state.
- An event is checked the same way on `events`: Given starts collecting with `events.test { ... }`, When calls the ViewModel function, and Then asserts the next event with `awaitItem()`.

## Screenshot tests

- In a module with `custom.screenshot.testing`, every `@Preview` is a screenshot test, generated at build time. Never write one by hand.
- Its reference images are committed in the module's `src/test/screenshots/`. After a UI change, run `./gradlew :<module>:recordRoborazziDebug`, look at every image that changed, and commit the ones that show the intended change.
- A PR lists every image it adds, changes or deletes under Changed screenshots. An image of a screen its spec doesn't name needs a reason there: it is often a change that leaked from shared code.
- Renaming or deleting a preview leaves its old images behind. Delete them in the same PR.
- The generated tests are JUnit 4 and run on the JUnit Platform through the Vintage engine. Hand-written tests stay JUnit 6.
- A compare passes when at most 0.01% of the pixels differ: references are recorded on macOS, and Linux CI anti-aliases a few pixels differently. PR Check runs `verifyRoborazziDebug` and uploads the diffs when it fails.

## Style

- Name: `` `methodUnderTest when state or event then expected behavior` ``, in backticks, plain words, no underscores or punctuation. Verbs: returns, emits, shows, updates, navigates, throws. Example: `` `submitOrder when offline then shows offline error` ``.
- `// Given`, `// When` and `// Then` comments. One behavior per test, one Then.
- No `if`, `for`, `while` or `when` in a test. Several inputs for one rule make a `@ParameterizedTest`.
- Test data comes from fixture functions with default parameters (`cartItem(quantity = 2)`) and named constants. No magic strings or numbers in a test body.
- No shared state between tests, no random values.
- For a sealed state, assert the exact type and the fields that matter.

## Code not to copy

- The use-case tests in `:features:cart:domain` and `:features:auth:domain`, and `PhoneAuthRepositoryImplTest`, mock their collaborators with MockK and check calls, for example `ClearCartUseCaseTest` with `coThen { repository.clear(...) }`. They stay until a screen's acceptance tests cover the same behavior.
- Those tests and `PhoneAuthRemoteDataSourceImplTest` are plain `suspend fun` tests without `runTest`.
