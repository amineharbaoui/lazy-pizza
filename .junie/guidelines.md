# 🍕 LazyPizza – Project Development Guidelines

This document defines **project-specific rules and practices** for building, testing, and extending LazyPizza.
It is written for **experienced Android / Kotlin developers** and **AI agents** contributing to the codebase.

These rules are **not optional**.

---

## 1. Project Overview

LazyPizza is a **multi-module Android application** built with:

- Kotlin
- Jetpack Compose (Material 3 + Adaptive)
- Hilt for dependency injection
- Room for local persistence
- Firebase Auth & Firestore
- AndroidX Navigation 3 (typed destinations)
- Gradle Version Catalog
- KSP for annotation processing
- Typesafe Project Accessors

The architecture is **feature-based**, **state-driven**, and **strictly layered**.

---

## 2. Module Structure

Defined in `settings.gradle.kts`.

### Core Modules

- `:app`
- `:core:designsystem`
- `:core:ui`

### Feature Modules (pattern)

:features::

- domain
- data
- ui
- di
  Examples:
- `:features:menu:{domain,data,ui-home,ui-pizza-detail,di}`
- `:features:cart:{domain,data,ui-cart,di}` + aggregator `:features:cart`
- `:features:auth:{domain,data,ui,di}` + aggregator `:features:auth`
- `:features:history`

---

## 3. Layer Responsibilities (Strict)

### Domain

- Pure Kotlin only
- No Android, Compose, Firebase, Room, or framework dependencies (Only Hilt allowed to be used for dependency injection )
- Contains:
    - Business models
    - Use cases
    - Business rules
    - Repository interfaces
- Domain is the **single source of truth for behavior**

### Data

- Implements domain interfaces
- Handles:
    - Room (DAOs, entities, migrations)
    - Firebase / network / SDK integrations
    - Data → domain mapping
- May depend on Android APIs
- Must **never leak framework types outside the module**

### UI

- Jetpack Compose only
- Renders state and forwards user intents
- No business logic
- No direct access to data layer
- Communicates only via:
    - ViewModels
    - Use cases
    - UI State / UI Events

### DI

- Hilt modules only
- Provides bindings for domain and data
- No logic

---

## 4. Dependency Rules (Hard Constraints)

Allowed:

- UI → Domain
- Data → Domain

Forbidden:

- UI → Data
- Domain → UI
- Domain → Data
- Feature-data → Feature-data

Allowed cross-feature dependency:

- Feature-data → other feature’s **domain only**

Shared logic must live in **core modules**.

---

## 5. Build & Configuration

### Toolchain

- Kotlin: **2.2.20**
- AGP: **8.13.0**
- Gradle JVM: **17+ required**
- Kotlin `jvmTarget = 11`
- Java source/target = 11

SDK:

- compileSdk = 36
- minSdk = 26
- targetSdk = 36

---

### Version Management

- All versions are defined in `gradle/libs.versions.toml`
- Always use `libs.*` aliases
- Prefer BOMs (Compose, Firebase)

Typesafe Project Accessors:

- Enabled via `enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")`
- Reference modules using `projects.*`

---

### Compose

- Enabled explicitly per UI module
- Dependencies aligned via Compose BOM
- Adaptive UI uses:
    - `material3-adaptive`
    - `material3-adaptive-navigation-suite`

---

### Hilt

- App module applies Hilt plugin
- `@HiltAndroidApp` Application is mandatory
- KSP is required in any module using Hilt
- Use constructor injection by default
- ViewModels obtained via `hiltViewModel()`

---

### Room

- Used in data modules only
- Any schema change requires:
    - Version bump
    - Explicit migration (`@AutoMigration` or custom `Migration`)
- DAOs and entities must never be accessed outside data modules

---

### Firebase

- Firebase BOM is used
- `google-services.json` must exist in `:app` for runtime
- Compilation may succeed without it, runtime will not
- Firebase APIs must be wrapped in data sources

---

### R8 / Proguard

- Release builds use shrinking
- Add rules if DI / Room / Firebase reflection breaks
- Library modules should define `consumer-rules.pro` when needed

---

## 6. ViewModel Rules

- ViewModels must:
    - Never receive Activity, Fragment, View, or Context
    - Communicate only with use cases
    - Own all screen logic and validation
- ViewModels expose:
    - One `StateFlow` for UI state
    - One `SharedFlow` for UI events
- ViewModels coordinate:
    - Auth flows
    - Cart/session transitions
    - Error handling

---

## 7. UI State & Events

- UI state:
    - Immutable
    - Explicit
    - Represented via sealed interfaces/classes
- UI must:
    - Render state only
    - Never derive business logic
    - Never validate inputs
- UI events:
    - One-shot
    - Emitted only by ViewModel
    - Collected via `LaunchedEffect`

---

## 8. Design System Rules

- All reusable UI components live in `:core:designsystem`
- Components must:
    - Be stateless
    - Receive state via parameters
    - Emit callbacks only
- No navigation, ViewModel, or business logic allowed
- Components should be **restricted and opinionated**, not generic

---

## 9. Navigation (Navigation 3)

- Use `androidx.navigation3`
- Use typed destinations
- Scope ViewModels correctly
- Do not use legacy `navigation-compose`

---

## 10. Cart & Session Rules

- Guest users use a local cart
- Authenticated users use a session-scoped cart
- Cart transfer must:
    - Be explicit
    - Be atomic
    - Be triggered via a domain use case
- Signing out must clear session-scoped data
- UI must not decide cart persistence rules

---

## 11. Performance & Stability

- Use `LazyColumn` / `LazyRow` for large lists
- Avoid runtime resource reflection where possible
- Avoid heavy recomposition logic
- Memoize expensive computations
- Move non-UI work out of composables

---

## 12. Code Quality Rules

- Single responsibility per class
- Explicit naming over abstraction
- Clarity over cleverness
- No shortcuts that violate layer boundaries
- Avoid vague names like Utils, Helper, Manager unless clearly scoped and justified
- Prefer “one public class per file” (except small sealed hierarchies)
- When uncertain, choose stricter separation

---

## 13. Unidirectional Data Flow (UDF)

LazyPizza follows strict UDF:
-Events/Intents go up: UI → ViewModel → UseCase → Repository
-State flows down: Repository/UseCase → ViewModel → UI

Rules:
-UI must not mutate domain/data state directly
-Data layer must not push UI-specific behavior
-No bidirectional state ownership

---

## 14. Error Modeling Rules

-Domain errors must be modeled explicitly (sealed types or structured results)
-Framework exceptions (Room/Firebase/network) must not reach UI directly
-UI must not interpret raw exceptions or build business meaning from strings
-Mapping to user-facing error messages happens in ViewModel/UI mapping layer

---

## 14. Coroutines & Threading Rules

-ViewModels use viewModelScope
-UI must not launch lon-running work
-Domain must not hardcode dispatchers
-Data layer controls threading and dispatchers internally
-No blocking calls in composables or ViewModels

---

## 15. Simplicity & Readability Rules (Important)

- Prefer the simplest working solution over the most reusable/abstract one.
- Avoid “framework-building” (base classes, generic engines, complex DSLs) unless a real need exists.
- If a pattern adds indirection, it must pay for itself immediately in clarity or reuse.

---

## 16. API & Naming Conventions

- Names must reflect intent, not implementation.
- Avoid ambiguous verbs: handle, process, manage, doThing.
- Use consistent suffixes:
- *UiState, *UiEvent, *UiAction
- *UseCase
- *Repository, *DataSource
- Booleans should read like a sentence: isUserSignedIn, isPrimaryEnabled.

---

## 17. State Modeling Rules

- UI State must be either:
- sealed UI state (Loading / Content / Error), or
- Content state + separate one-shot events, but never a “kitchen sink” state.
- Do not mix:
- screen content + transient flags + error strings in one bag unless you explicitly model them.
- If something is optional/nullable, it must be intentional and documented by the type (sealed types > nullable).

---

## 18. Navigation & Event Rules

- ViewModel emits navigation events only after business rules succeed.
- No “double events”:
- Navigation and snackbars must be one-shot and not replay on rotation.

---

## 19. Error UX Rules

- Every error shown to the user must map from a typed domain error (or a typed data error mapped to domain).
- Avoid showing raw backend/exception strings.
- Error UI must be predictable:
- retryable vs non-retryable should be explicit.

___

## 20. ViewModel APIs represent **actions / intents**, not UI callbacks

Public ViewModel functions must describe **what happens**, not **how the UI triggered it**.

- ❌ Avoid UI-mechanic names:
    - `onClick`
    - `onChange`
    - `onSelect`
    - `onTextChanged`

- ✅ Prefer intent/action verbs:
    - `updateSearchQuery`
    - `selectPickupOption`
    - `confirmSchedule`
    - `submitOrder`

> **Rationale**
> ViewModels expose business intent. UI components expose callbacks.

---

### 2. UI callbacks keep the `onX` naming

Composable parameters should use `onX` to represent user interaction callbacks.

```kotlin
SearchField(
    value = state.query,
    onValueChange = viewModel::updateSearchQuery
)
```

---

### 2. Testing

## Tech stack (must use)
- Kotlin + JUnit Jupiter (JUnit 6)
- MockK in **BDD style**:
    - every { ... }  -> given { ... }
    - coEvery { ... } -> coGiven { ... }
    - verify { ... } -> then { ... }
    - coVerify { ... } -> coThen { ... }
- Turbine for Flow testing when needed
- AssertJ for assertions (no JUnit assertions unless unavoidable)

## Test style (must follow)
- Use **BDD** with clear **Given / When / Then** sections (as comments).
- Use a **single concern per test** (1 outcome / 1 behavior).
- Avoid **multiple Then** in the same test: split into separate test methods.
- Keep tests **isolated** (mock dependencies, no network/db/filesystem).
- Keep tests **fast, reliable, deterministic**:
    - no real time delays
    - no random values
    - no shared state between tests
- Avoid **if/for/while/switch** in test methods.
- Avoid **creating complex data inside test methods**:
    - use helper builders / fixtures for setup
    - test method should mainly read like a scenario + assertion
- Avoid **magic strings / magic numbers**:
    - use constants or named helper functions
- Prefer testing **observable behavior** (state/events/output), not implementation details.

## Naming convention (strict)
Use:
methodUnderTest_whenStateOrEvent_thenExpectedBehavior

Examples:
submitOrder_whenUserIsNotSignedIn_thenEmitsNavigateToAuthEvent
submitOrder_whenOrderSucceeds_thenEmitsOrderPlacedState
observePizza_whenPizzaNotFound_thenEmitsErrorState
updateSearchQuery_whenQueryMatchesItems_thenFiltersMenu
addCartItem_whenItemAlreadyExists_thenIncrementsQuantity

## Coroutine rules
- Use kotlinx.coroutines.test:
    - Important: Don't use runTest { ... }. With Junit 6 now you can just use `suspend` as we do in the implementation (example : `suspend fun addCartItem_whenItemAlreadyExists_thenIncrementsQuantity () { ... } `)
    - MainDispatcherRule (JUnit5 extension) to set Dispatchers.Main to a TestDispatcher
- Do not use GlobalScope.
- If code uses Flow:
    - Use Turbine (`flow.test { ... }`)
    - Cancel collection properly (`cancelAndIgnoreRemainingEvents()`)

## Output requirements
When I paste code:
1) Generate the test class in Kotlin (ready to copy-paste).
2) Include required imports.
3) Include a minimal, clean test setup:
    - mocks
    - system under test creation
    - helpers/fixtures
4) Use AssertJ for assertions:
    - assertThat(...)
    - assertThatThrownBy(...) if needed
5) Use MockK BDD aliases (given/coGiven/then/coThen).
6) If something is ambiguous, make a reasonable assumption and document it in a short comment at the top (do not ask questions unless absolutely blocking).

## Extra best practices to apply (add these if relevant)
- Use `@BeforeEach` for common setup, `@AfterEach` only if needed.
- Prefer `relaxed = true` for mocks only when it improves readability and does not hide behavior.
- Verify only what matters for the single concern (avoid over-verifying).
- For sealed UI states/events: assert exact type and key fields.
- For error handling: assert the modeled error (not raw exceptions) unless the code truly throws.

