---
paths:
  - "features/**/ui-*/**"
  - "core/ui/**"
  - "core/designsystem/**"
  - "app/src/main/**"
---

# UI Rules (Compose, ViewModels, Navigation)

## ViewModels

- Never receive Activity, Fragment, View, or Context.
- Talk only to use cases. Own all screen logic and input validation.
- Expose exactly one `StateFlow` (UI state) and one `SharedFlow` (one-shot events). Never `LiveData`.
- Coordinate auth flows, cart/session transitions, and error handling.
- Use `viewModelScope`; no blocking calls.
- Obtain via `hiltViewModel()`.
- Emit navigation events only after business rules succeed.

### Public functions are intents, not UI callbacks

Name ViewModel functions for **what happens**, not how the UI triggered it.

- Avoid: `onClick`, `onChange`, `onSelect`, `onTextChanged`
- Prefer: `updateSearchQuery`, `selectPickupOption`, `confirmSchedule`, `submitOrder`

Composable parameters keep `onX` naming:

```kotlin
SearchField(
    value = state.query,
    onValueChange = viewModel::updateSearchQuery
)
```

## UI State

State is immutable and takes one of two shapes:

- **Sealed state** (`Loading` / `Content` / `Error`) when the screen has distinct phases, or
- **Content state + separate one-shot events** when the screen is always showing content.

Never a "kitchen sink" class mixing content, transient flags, and error strings. Optional values are expressed by the type (sealed > nullable).

## UI Events

- One-shot, emitted only by the ViewModel, collected via `LaunchedEffect`.
- Navigation and snackbars must not replay on rotation.

## Composables

- Render state and forward intents only: no business logic, no validation, no long-running work.
- Keep non-UI work out of composables.

## Design System (`:core:designsystem`)

- All reusable components live here.
- Stateless: state in via parameters, callbacks out.
- No navigation, ViewModel, or business logic.
- Restricted and opinionated, not generic.

## Navigation

- `androidx.navigation3` with typed destinations.
- Never legacy `navigation-compose`.

## Compose Build Setup

- Compose is enabled explicitly per UI module; versions aligned via the Compose BOM.
- Adaptive UI uses `material3-adaptive` and `material3-adaptive-navigation-suite`.
