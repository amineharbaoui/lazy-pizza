---
paths:
  - "**/src/test/**"
  - "**/src/testFixtures/**"
  - "core/testing/**"
  - ".maestro/**"
  - "scripts/e2e.sh"
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
- Given presets data in the fakes, starts collecting `uiState` with Turbine (`uiState.test { ... }`) and waits for the loaded state. A `WhileSubscribed` state only updates while something collects it, and a ViewModel may ignore input until it has loaded. When calls a ViewModel function inside that block, and Then asserts the next state.

## Screenshot tests

- In a module with `custom.screenshot.testing`, every `@Preview` is a screenshot test, generated at build time. Never write one by hand.
- Its reference images are committed in the module's `src/test/screenshots/`. After a UI change, run `./gradlew :<module>:recordRoborazziDebug`, look at every image that changed, and commit the ones that show the intended change.
- The generated tests are JUnit 4 and run on the JUnit Platform through the Vintage engine. Hand-written tests stay JUnit 6.
- A compare passes when at most 0.01% of the pixels differ: references are recorded on macOS, and Linux CI anti-aliases a few pixels differently. PR Check runs `verifyRoborazziDebug` and uploads the diffs when it fails.

## E2E flows (Maestro)

- A Maestro flow walks one journey through the real app, as a user would: real navigation, the real Hilt graph, Firebase, and data that survives a restart. Flows run last, after the build, on an emulator with `scripts/e2e.sh`. Not in CI.
- One journey per flow, in `.maestro/<feature>/<journey>.yaml`, named by what the user does: `cart/add-pizza-to-cart.yaml`. Rules and edge cases stay in the acceptance tests. A flow is slower and flakier, so it never repeats them.
- A flow is tagged with its feature folder, plus `smoke` for the few journeys every change must keep working. `scripts/e2e.sh cart,smoke` runs the flows that have either tag.
- Each flow starts with `launchApp: clearState: true`, so it never depends on another flow or on a previous run.
- Select an element by the text the user reads, or for an icon by the content description TalkBack reads. Add a `testTag` only to an element that has neither, such as an input without a label. When an icon's description is wrong, fix the description instead of adding a tag. Tags are snake_case: `<screen>_<element>` (`login_code_input`) at the call site, `<component>_<part>` inside `:core:designsystem`. `MainActivity` exposes them to Maestro as resource ids.
- Assert the texts the spec shows the user. A text with a price or a count in it is matched with a regex: `"Add to Cart for .*"`.
- Steps that several flows share live in `.maestro/subflows/` and run with `runFlow`. `subflows/sign-in.yaml` signs in with the test phone number.
- Flows run against the real Firebase project, signed in with a Firebase test phone number. They rely on the real menu, and each run of an order flow places a real order for the test account.
- The test phone number and its code live only in `.maestro/.env.local`, which git ignores. A flow reads them as `${TEST_PHONE_NUMBER}`. Never write them in a flow or a commit. Maestro's own logs, in `build/maestro/` and `~/.maestro/`, can show them as typed, so never paste those lines in a PR or a chat.
- A flow never depends on the time of day. It picks the first pickup slot shown, never a specific time, and asserts nothing time-based. Time rules, such as pickup slots, the 15-minute margin or cart expiry, stay in the acceptance tests, which control the `Clock`.
- Wait for anything that loads from the network with `extendedWaitUntil` and a timeout, never a fixed delay.
- Never use `hideKeyboard`: on Android it presses Back, which can leave the screen. Tap the next button instead.
- When a flow fails, read its screenshot and view hierarchy in `build/maestro/` first. Fix the app when the journey is broken. Change the flow only when the spec changed the journey.

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
