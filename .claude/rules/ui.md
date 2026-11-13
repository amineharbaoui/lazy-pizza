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
- A screen opened with arguments gets them as a `<Name>Args` type, with ids and values only, never a domain model. Its ViewModel receives them in its constructor, through Hilt's assisted injection: `@HiltViewModel(assistedFactory = <Name>ViewModel.Factory::class)` on the class, `@Assisted args: <Name>Args` in its `@AssistedInject constructor`, and `hiltViewModel<<Name>ViewModel, <Name>ViewModel.Factory> { it.create(args) }` in `<Name>Screen`. No `initialize(args)` function, and no state for args not received yet.
- `:app` shows the back stack with Navigation 3's `NavDisplay`, with `rememberViewModelStoreNavEntryDecorator()` in its `entryDecorators`, so each entry has its own ViewModel store: two entries of the same screen get two ViewModels, each with its own args, cleared when the entry leaves the back stack.

## ViewModel

- A `@HiltViewModel` injects only use cases and UI mappers. Never a repository, a data source, a `:core` service such as `NetworkMonitor`, a `Context` or a Firebase class: what it needs from them comes through a use case.
- A user action calls one use case, as `.claude/rules/domain.md` says. The ViewModel never chains use cases into a business sequence, such as placing an order then clearing the cart. It may combine several `Observe` use cases to build its state.
- It exposes one `uiState: StateFlow<<Name>UiState>`, derived from the use-case flows and the screen's input with `combine` and `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5.seconds), <initial state>)`. Input the user types or selects lives in a private `MutableStateFlow` combined into it.
- Public functions say what happens (`updateSearchQuery`, `submitOrder`), not which gesture triggered it (`onClick`). Composable parameters keep the `onX` names.
- A failing flow is caught with `catch` and becomes an error state.
- A one-shot use case call has no `try`/`catch`: its sealed result says how it went, and the ViewModel turns it into state with an exhaustive `when`. Any exception left is a bug, which crashes and is reported, as `.claude/rules/domain.md` says.

## UI state

- Immutable. A sealed interface `<Name>UiState` when the screen has distinct phases (`Loading`, `Ready`, `Error`), with `data object` for a phase without data.
- Domain types stop at the ViewModel and its UI mappers. The UI state, display models and composables never hold a domain model, a domain result or a use case.
- A one-off outcome, such as navigating or showing a message, is an event, not state: a sealed interface `<Name>Event`. The ViewModel sends it from `viewModelScope` into a private `Channel<<Name>Event>(Channel.BUFFERED)`, and exposes it as `events: Flow<<Name>Event>` with `receiveAsFlow()`. What the screen shows stays in `uiState`.
- `<Name>Screen` collects `events` only with `ObserveEvents(viewModel.events) { event -> ... }` from `:core:ui`, once per screen. It collects while the screen is started, on `Dispatchers.Main.immediate`: a collector cancelled between receiving an event and handling it would lose it. Never collect `events` by hand, and never use a `SharedFlow` for events: it drops what it emits while nothing collects.
- Display models are named `<Noun>DisplayModel` and carry values ready to show: names, dates, and prices formatted with `CurrencyFormatter`. A text that wraps them, such as `Order #%1$s`, is a `strings.xml` template the composable fills with `stringResource`.
- An error in the state is a type, such as an enum or a sealed interface. The screen picks its text from `strings.xml`.

## Design system

- Build screens from `:core:designsystem`: the `Ds*` components, `AppColors` and `AppTypography`. No hard-coded colors or text styles.
- A component that a second screen needs moves to `:core:designsystem`. It stays stateless: state in, callbacks out, no ViewModel and no navigation.
- Screens work on phone and tablet (`isWideLayout()`), in light and dark mode.
- Each screen state has a preview: `@PreviewPhoneTablet`, wrapped in `LazyPizzaThemePreview`, with fake display models. Network images go through `rememberImagePainterPreviewAware`.
- A preview draws the same image on every machine and every run, because it is a screenshot test. Its display models carry their texts already formatted, so no time zone or locale changes them. It never reads the current time or a random value.

## Accessibility

- An icon or image that means something has a `contentDescription` from `strings.xml` that says what it does ("Log out", "Back"), not what it is. A decorative one gets `null`.
- Touch targets are at least 48dp. Don't shrink an `IconButton` below that; a custom clickable gets `Modifier.minimumInteractiveComponentSize()`.
- Text sizes come from `AppTypography`, in `sp`. A screen still works at the largest font size: no fixed height around text.
- A clickable row or card is one element, so TalkBack reads it once. A toggle uses `toggleable` or `selectable` with its `Role`.

## Code not to copy

- `CheckoutViewModel` and `PhoneAuthViewModel` send events through a `SharedFlow`, which drops them during a rotation, and their screens collect them by hand in a `LaunchedEffect(Unit)`.
- `ObserveEvents` doesn't exist in `:core:ui` yet. It comes in a refactor of its own, before the first screen that sends an event.
- `PhoneVerificationManager` and `PhoneAuthScreen` call `FirebaseAuth` from the UI.
- `CheckoutViewModel` puts English text or `e.message` in the state.
- Checkout names its display models `...UiModel` and `...Ui`.
- `toFormattedCurrency()` in `PriceFormatter.kt` duplicates `CurrencyFormatter`.
- `DsTopBar` labels its account and back buttons "Phone Number Icon" and shrinks the account button to 32dp. `CartScreen` and `DsTopBar` hard-code their content descriptions in English.
- `CheckoutViewModel.submitOrder` chains the steps of placing an order itself: get the user, read the cart, build the order, place it, clear the cart. `PhoneAuthViewModel` calls `TransferGuestCartToUserUseCase` after sign-in. Moving that call into auth's sign-in use case would close a cycle, since cart's domain already uses auth's: fixing it needs one feature to declare an interface the other implements, decided with the developer.
- `MainViewModel` injects `NetworkMonitor` directly.
- `FormattingModule` in `:app` binds `CurrencyFormatter`, whose implementation lives in `:core:ui`.
- `PizzaDetailViewModel` gets its args through `initialize(args, sessionId)`, called from a `LaunchedEffect` with a saved `sessionId`, instead of assisted injection: a workaround from when every ViewModel lived in the activity's store.
