---
paths:
  - "**/src/test/**"
  - "**/src/androidTest/**"
  - "core/testing/**"
---

# Testing Rules

### Tech stack

- Kotlin + JUnit Jupiter (JUnit 5)
- MockK in **BDD style**:
    - `every { }` → `given { }`
    - `coEvery { }` → `coGiven { }`
    - `verify { }` → `then { }`
    - `coVerify { }` → `coThen { }`
- Turbine for Flow testing when needed
- AssertJ for assertions (no JUnit assertions unless unavoidable)

### Test style

- Use **BDD** with clear **Given / When / Then** sections (as comments).
- Use a **single concern per test** (1 outcome / 1 behavior).
- Avoid **multiple Then** in the same test — split into separate test methods.
- Keep tests **isolated** (mock dependencies, no network/db/filesystem).
- Keep tests **fast, reliable, deterministic**:
    - no real time delays
    - no random values
    - no shared state between tests
- Avoid `if/for/while/switch` in test methods.
- Avoid creating complex data inside test methods — use helper builders / fixtures.
- Avoid magic strings / magic numbers — use constants or named helper functions.
- Prefer testing **observable behavior** (state/events/output), not implementation details.

### Naming convention

Use Kotlin backtick BDD style:

```
`methodUnderTest when stateOrEvent then expectedBehavior`
```

Rules:
- Use backticks around the test name
- Write in natural language
- No underscores `_`
- No punctuation (`. , ? !`)
- Start with the method name
- Use consistent verbs: `returns`, `emits`, `updates`, `shows`, `navigates`, `throws`
- Keep it concise but readable

Examples:

```
`submitOrder when user is not signed in then emits navigate to auth event`
`submitOrder when order succeeds then emits order placed state`
`observePizza when pizza not found then emits error state`
`updateSearchQuery when query matches items then filters menu`
`addCartItem when item already exists then increments quantity`
```

### Coroutine rules

- Use `kotlinx.coroutines.test`:
    - Do not use `runTest { }`. With JUnit 5 use `suspend` directly (e.g. `suspend fun myTest() { }`)
    - Use `MainDispatcherExtension` (JUnit 5 extension) to set `Dispatchers.Main` to a `TestDispatcher`
- Do not use `GlobalScope`.
- If code uses Flow:
    - Use Turbine (`flow.test { }`)
    - Cancel collection properly (`cancelAndIgnoreRemainingEvents()`)

### Output requirements

When generating tests:

1. Generate the test class in Kotlin (ready to copy-paste).
2. Include required imports.
3. Include a minimal, clean test setup:
    - mocks
    - system under test creation
    - helpers/fixtures
4. Use AssertJ for assertions (`assertThat(...)`, `assertThatThrownBy(...)` if needed).
5. Use MockK BDD aliases (`given`/`coGiven`/`then`/`coThen`).
6. If something is ambiguous, make a reasonable assumption and document it in a short comment at the top.

### Extra best practices

- Use `@BeforeEach` for common setup; `@AfterEach` only if needed.
- Prefer `relaxed = true` for mocks only when it improves readability and does not hide behavior.
- Verify only what matters for the single concern (avoid over-verifying).
- For sealed UI states/events: assert exact type and key fields.
- For error handling: assert the modeled error (not raw exceptions) unless the code truly throws.
