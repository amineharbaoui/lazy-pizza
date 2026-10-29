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
- Each data module's `src/testFixtures/` has a public `<Feature>TestData`, written in domain types: it builds the module's repositories on its fakes, presets data and forces failures. A test never sees a DTO or an entity, and a constructor change is fixed in one place.
- Given presets data in the fakes. When calls a ViewModel function. Then reads `uiState` with Turbine (`uiState.test { ... }`), because a `WhileSubscribed` state only updates while something collects it.

## Style

- Name: `` `methodUnderTest when state or event then expected behavior` ``, in backticks, plain words, no underscores or punctuation. Verbs: returns, emits, shows, updates, navigates, throws. Example: `` `submitOrder when offline then shows offline error` ``.
- `// Given`, `// When` and `// Then` comments. One behavior per test, one Then.
- No `if`, `for`, `while` or `when` in a test. Several inputs for one rule make a `@ParameterizedTest`.
- Test data comes from fixture functions with default parameters (`cartItem(quantity = 2)`) and named constants. No magic strings or numbers in a test body.
- No shared state between tests, no random values.
- For a sealed state, assert the exact type and the fields that matter.

## Code not to copy

- Every existing test mocks its collaborators with MockK and checks calls, for example `ClearCartUseCaseTest` with `coThen { repository.clear(...) }`. They stay until a screen's acceptance tests cover the same behavior.
- `PhoneAuthViewModelTest` mocks the use cases instead of running the real chain.
- Existing tests are plain `suspend fun` tests without `runTest`.
