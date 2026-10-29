---
paths:
  - "features/*/ui-*/**"
  - "core/designsystem/**"
  - "core/ui/**"
  - "app/src/**"
---

# UI modules

## Screen

- `<Name>Screen` gets its ViewModel with `hiltViewModel()`, collects `uiState` with `collectAsStateWithLifecycle()`, and passes it to a stateless `<Name>ScreenContent(state, callbacks)`. Previews use the content composable.
- Navigation leaves a screen as callbacks, such as `onNavigateToCheckout: () -> Unit`. Routes and the back stack live in `:app`.
- Composables render state and forward user actions: no business logic, no validation, no long-running work.

## ViewModel

- A `@HiltViewModel` injects only use cases and UI mappers. Never a repository, a `Context` or a Firebase class.
- It exposes one `uiState: StateFlow<<Name>UiState>`, derived from the use-case flows and the screen's input with `combine` and `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), <initial state>)`. Input the user types or selects lives in a private `MutableStateFlow` combined into it.
- Public functions say what happens (`updateSearchQuery`, `submitOrder`), not which gesture triggered it (`onClick`). Composable parameters keep the `onX` names.
- A failing flow is caught with `catch` and becomes an error state.

## UI state

- Immutable. A sealed interface `<Name>UiState` when the screen has distinct phases (`Loading`, `Ready`, `Error`), with `data object` for a phase without data.
- One-off outcomes, such as navigating or showing a message, are state too, as Google recommends. A nullable field, for example `navigation: CheckoutNavigation?`, is handled by the screen in a `LaunchedEffect`, which then clears it with `viewModel.navigationHandled()`. No `SharedFlow` or `Channel` of events.
- Display models are named `<Noun>DisplayModel` and carry text ready to show. Prices are formatted with `CurrencyFormatter`.
- An error in the state is a type, such as an enum or a sealed interface. The screen picks its text from `strings.xml`.

## Design system

- Build screens from `:core:designsystem`: the `Ds*` components, `AppColors` and `AppTypography`. No hard-coded colors or text styles.
- A component that a second screen needs moves to `:core:designsystem`. It stays stateless: state in, callbacks out, no ViewModel and no navigation.
- Screens work on phone and tablet (`isWideLayout()`), in light and dark mode.
- Each screen state has a preview: `@PreviewPhoneTablet`, wrapped in `LazyPizzaThemePreview`, with fake display models. Network images go through `rememberImagePainterPreviewAware`.

## Accessibility

- An icon or image that means something has a `contentDescription` from `strings.xml` that says what it does ("Log out", "Back"), not what it is. A decorative one gets `null`.
- Touch targets are at least 48dp. Don't shrink an `IconButton` below that; a custom clickable gets `Modifier.minimumInteractiveComponentSize()`.
- Text sizes come from `AppTypography`, in `sp`. A screen still works at the largest font size: no fixed height around text.
- A clickable row or card is one element, so TalkBack reads it once. A toggle uses `toggleable` or `selectable` with its `Role`.

## Code not to copy

- ViewModels start collecting in `_uiState.onStart { viewModelScope.launch { ... } }`. Each time the screen comes back after 5 seconds in the background, this adds another collector that never stops.
- `CheckoutViewModel` and `PhoneAuthViewModel` send events through a `SharedFlow`, which drops them during a rotation.
- `PhoneVerificationManager` and `PhoneAuthScreen` call `FirebaseAuth` from the UI.
- `CartToOrderMapper` and `CheckoutUiStateFactory` hold business rules (order number, pickup slots) and read the clock directly.
- `OrdersViewModel` and `CheckoutViewModel` put English text or `e.message` in the state, and `OrdersViewModel` logs with `Log.d`.
- Checkout names its display models `...UiModel` and `...Ui`.
- `toFormattedCurrency()` in `PriceFormatter.kt` duplicates `CurrencyFormatter`.
- `DsTopBar` labels its account and back buttons "Phone Number Icon" and shrinks the account button to 32dp. `CartScreen`, `OrdersScreen` and `DsTopBar` hard-code their content descriptions in English.
