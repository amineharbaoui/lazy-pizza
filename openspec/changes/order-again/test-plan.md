SPECS-APPROVED: 5b44e3718d0477d91dfc23b609957542063c8fab

# Test Plan

<!-- Every scenario in specs/ mapped to at least one named test. -->
<!-- LIVE LEDGER: apply flips each row from 🔴 red to 🟢 green as its test passes. verify blocks on any row left red. -->

Paths below abbreviate `features/order/ui-past-orders/src/test/java/com/example/checkout/order/ui/pastorders/` as `<pastorders-test>/`.

| Scenario | Level | Test File | Test Name | State |
|----------|-------|-----------|-----------|-------|
| ORDER-01 Order again button on every past order | screenshot | `<pastorders-test>/screenshot/OrdersScreenScreenshotTest.kt` (preview in `OrdersScreen.kt`) | `OrdersScreenReadyMixedStatusesPreview` | 🟢 green |
| ORDER-02 No Order again when signed out | screenshot | `<pastorders-test>/screenshot/OrdersScreenScreenshotTest.kt` (preview in `OrdersScreen.kt`) | `OrdersScreenNotLoggedInPreview` | 🟢 green |
| ORDER-03 Whole order still on the menu | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when whole order is on the menu then cart holds every item with its quantities and toppings` | 🟢 green |
| ORDER-03 Whole order still on the menu | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when whole order is on the menu then emits navigate to cart event` | 🟢 green |
| ORDER-03 Whole order still on the menu | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when whole order is on the menu then shows no dialog` | 🟢 green |
| ORDER-04 Existing cart items are kept | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when cart already holds other items then keeps them alongside the reordered items` | 🟢 green |
| ORDER-05 Same drink adds to its quantity | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when same drink is already in the cart then updates its quantity on a single line` | 🟢 green |
| ORDER-06 Same pizza gets its own line | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when same pizza is already in the cart then adds a separate pizza line` | 🟢 green |
| ORDER-07 Today's prices apply per unit and per topping | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when menu prices changed then cart line uses todays unit and topping prices` | 🟢 green |
| ORDER-08 Buttons are disabled while Order again runs | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when tapped while a reorder is in progress then shows every order again button disabled` | 🟢 green |
| ORDER-08 Buttons are disabled while Order again runs | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when in progress reorder finishes then shows order again buttons enabled` | 🟢 green |
| ORDER-08 Buttons are disabled while Order again runs | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when tapped twice before the first finishes then cart holds the order once` | 🟢 green |
| ORDER-08 Buttons are disabled while Order again runs | acceptance | `<pastorders-test>/acceptance/OrderAgainAcceptanceTest.kt` | `orderAgain when tapped twice before the first finishes then emits navigate to cart once` | 🟢 green |
| ORDER-09 Unavailable item skipped and named | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when a product is off the menu then cart holds only the available items` | 🟢 green |
| ORDER-09 Unavailable item skipped and named | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when a product is off the menu then shows some skipped dialog naming the product` | 🟢 green |
| ORDER-10 Unavailable topping dropped and named | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when a topping is off the menu then cart holds the pizza without that topping` | 🟢 green |
| ORDER-10 Unavailable topping dropped and named | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when a topping is off the menu then shows some skipped dialog naming the topping on its pizza` | 🟢 green |
| ORDER-11 Go to cart from the dialog | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `goToCartFromSkippedItems when some skipped dialog is showing then closes the dialog` | 🟢 green |
| ORDER-11 Go to cart from the dialog | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `goToCartFromSkippedItems when some skipped dialog is showing then emits navigate to cart event` | 🟢 green |
| ORDER-11 Go to cart from the dialog | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `goToCartFromSkippedItems when some skipped dialog is showing then cart holds the available items` | 🟢 green |
| ORDER-12 Close the dialog after a partial reorder | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `dismissOrderAgainDialog when some skipped dialog is showing then closes the dialog` | 🟢 green |
| ORDER-12 Close the dialog after a partial reorder | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `dismissOrderAgainDialog when some skipped dialog is showing then emits no navigation event` | 🟢 green |
| ORDER-12 Close the dialog after a partial reorder | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `dismissOrderAgainDialog when some skipped dialog is showing then cart holds the available items` | 🟢 green |
| ORDER-13 Nothing in the order is still on the menu | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when nothing in the order is on the menu then shows nothing available dialog naming the products` | 🟢 green |
| ORDER-13 Nothing in the order is still on the menu | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `orderAgain when nothing in the order is on the menu then leaves the cart unchanged` | 🟢 green |
| ORDER-14 Close after nothing was available | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `dismissOrderAgainDialog when nothing available dialog is showing then closes the dialog` | 🟢 green |
| ORDER-14 Close after nothing was available | acceptance | `<pastorders-test>/acceptance/OrderAgainSkippedItemsAcceptanceTest.kt` | `dismissOrderAgainDialog when nothing available dialog is showing then emits no navigation event` | 🟢 green |
| ORDER-15 Skipped items dialog | screenshot | `<pastorders-test>/screenshot/OrdersScreenScreenshotTest.kt` (preview in `OrdersScreen.kt`) | `OrdersScreenSomeSkippedDialogPreview` | 🟢 green |
| ORDER-16 Nothing available dialog | screenshot | `<pastorders-test>/screenshot/OrdersScreenScreenshotTest.kt` (preview in `OrdersScreen.kt`) | `OrdersScreenNothingAvailableDialogPreview` | 🟢 green |
| ORDER-17 Menu cannot be loaded | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu fails to load then emits menu unavailable error event` | 🟢 green |
| ORDER-17 Menu cannot be loaded | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu fails to load then leaves the cart unchanged` | 🟢 green |
| ORDER-17 Menu cannot be loaded | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu fails to load then emits no navigation event` | 🟢 green |
| ORDER-17 Menu cannot be loaded | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu fails to load then shows order again buttons enabled` | 🟢 green |
| ORDER-18 Menu comes back empty | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu comes back empty then emits menu unavailable error event` | 🟢 green |
| ORDER-18 Menu comes back empty | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu comes back empty then shows no dialog` | 🟢 green |
| ORDER-18 Menu comes back empty | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when menu comes back empty then leaves the cart unchanged` | 🟢 green |
| ORDER-19 Cart cannot be updated | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when cart fails to save then emits cart update failed error event` | 🟢 green |
| ORDER-19 Cart cannot be updated | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when cart fails to save then leaves the cart unchanged` | 🟢 green |
| ORDER-19 Cart cannot be updated | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when cart fails to save then emits no navigation event` | 🟢 green |
| ORDER-19 Cart cannot be updated | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when cart fails to save then shows order again buttons enabled` | 🟢 green |
| ORDER-20 Retry after the menu failed | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when retried after menu failure then cart holds the order` | 🟢 green |
| ORDER-20 Retry after the menu failed | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when retried after menu failure then emits navigate to cart event` | 🟢 green |
| ORDER-21 Retry after the cart failed | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when retried after cart failure then cart holds the order` | 🟢 green |
| ORDER-21 Retry after the cart failed | acceptance | `<pastorders-test>/acceptance/OrderAgainFailureAcceptanceTest.kt` | `orderAgain when retried after cart failure then emits navigate to cart event` | 🟢 green |
| ORDER-22 Reorder from history to cart | journey | `.maestro/flows/order_again.yaml` | `order_again.yaml` | 🔴 red |

## Support files

Fakes (data-module testFixtures, plain scriptable Kotlin, no mocks):

- `features/menu/data/src/testFixtures/java/com/example/menu/data/datasource/FakeProductRemoteDataSource.kt`: implements `ProductRemoteDataSource`. Settable products per `ProductCategory` (toppings included, so "today's topping list" is scripted here), an empty-menu setting, and a failure switch that makes the flows throw. The switch can be turned off between calls (ORDER-20).
- `features/cart/data/src/testFixtures/java/com/example/cart/data/datasource/FakeCartLocalDataSource.kt`: implements `CartLocalDataSource`. In-memory lines per `ownerKey`, keyed by `lineId` with REPLACE semantics like the Room DAO (an insert with an existing `lineId` overwrites the line, it never sums), so a missing quantity merge fails ORDER-05. `addItems` is all-or-nothing. Failure switch `failWritesAfterLines(n)`: single-line writes succeed until `n` lines are written and then throw, and `addItems` throws and writes nothing when the batch would go past `n`. A per-line, non-atomic implementation therefore leaves a partial cart and fails ORDER-19, which is why that scenario uses a two-item order. The switch can be turned off between calls (ORDER-21). It also has a hold gate on `addItems` (suspends until released) so ORDER-08 can observe the in-progress state without timing tricks.
- `features/cart/data/src/testFixtures/java/com/example/cart/data/datasource/FakeCartTransferLocalDataSource.kt`: implements `CartTransferLocalDataSource`. Records transfers and does nothing else (not exercised by these scenarios, needed to build `CartRepositoryImpl` / `SessionRepositoryImpl`).
- `features/order/data/src/testFixtures/java/com/example/order/data/datasource/FakeOrdersRemoteDataSource.kt`: implements `OrdersRemoteDataSource`. Settable `OrderDto` list per user id, `createOrder` appends.
- `features/auth/data/src/testFixtures/java/com/example/auth/data/datasource/FakePhoneAuthDataSource.kt`: implements `PhoneAuthDataSource`. Settable signed-in uid (null means signed out), backing `userIdFlow`, `isSignedIn` and `getCurrentUser()`.

Acceptance package helpers (`<pastorders-test>/acceptance/`):

- `OrderAgainFixtures.kt`: named menu products and prices (Margherita, Pepperoni, Cola, Extra cheese, plus Lemonade and Truffle that only exist in past orders), past-order `OrderDto` builders, existing-cart line builders, and expected price constants (ORDER-07: $9.00, $1.50, total $24.00).
- `OrderAgainTestWiring.kt`: builds the real graph by hand. Fakes, then `MenuRepositoryImpl`, `CartRepositoryImpl`, `OrdersRepositoryImpl`, `SessionRepositoryImpl`, then `GetMenuSnapshotUseCase`, `AddCartItemsUseCase`, `ReorderUseCase`, `ObserveOrdersUseCase`, `GetCurrentUserUidUseCase`, then `OrdersViewModel`. It exposes the fakes for scripting and `ObserveCartUseCase` (real `CartRepositoryImpl` over the fake) for cart assertions.
- Reused: `core/testing/src/main/java/com/example/core/testing/MainDispatcherExtension.kt`.

Screenshot and journey:

- `<pastorders-test>/screenshot/OrdersScreenScreenshotTest.kt`: Roborazzi capture of the previews named above. Roborazzi is set up in the Contract group.
- `.maestro/common/login.yaml` (signs in the Firebase test phone) and an order-placement subflow under `.maestro/common/` (adds 1 x Margherita and checks out). Both are added during apply.

## Coverage notes

- Acceptance tests assert only on `OrderUiState`, `OrdersUiEvent` and the cart as read through the real `ObserveCartUseCase`. Data sources are never inspected directly.
- The spec's several THEN/AND clauses are split one per test (testing rules: a single concern per test), so most scenarios have more than one row.
- "Stays on the Orders screen" is asserted as "no `NavigateToCart` event emitted". "Able to tap Order again again" is asserted as `orderAgain` back to `Idle` (buttons enabled). The retry scenarios (ORDER-20, ORDER-21) prove it end to end.
- Dialog titles and buttons follow from the state type (`SomeSkipped` means "Go to cart" + "Close", `NothingAvailable` means "Close" only). The acceptance tests assert the type and `skippedLabels` ("Lemonade", "Truffle on Margherita"). The ORDER-15 and ORDER-16 screenshots prove the rendered title text and buttons.
- Error messages are asserted as the typed `ShowOrderAgainError(OrderAgainErrorUi.*)` event. The exact strings ("Couldn't load the menu. Please try again.", "Couldn't update your cart. Please try again.") are string resources checked at review. The enum payload also makes it impossible for raw exception text to reach the UI.
- ORDER-12 "presses back" maps to the same `dismissOrderAgainDialog()` intent as "Close" (Contract). The acceptance test covers the intent. The back-to-intent wiring is the dialog's `onDismissRequest` in `OrdersScreen.kt` and is not covered by a JVM test.
- The fake cart and menu failures throw plain `Exception` subclasses, because Room/Firestore types are not available on the JVM. `CartRepositoryImpl` and `MenuRepositoryImpl` must therefore map any non-cancellation exception to their typed failure.
- ORDER-03 is also exercised on a device by ORDER-22.
- Extra preview `OrdersScreenOrderAgainInProgressPreview` (all buttons disabled) is recommended for ORDER-08's visual state. It needs no row.

LOCK: cb54a226fc3fa3011e60b81d7c130a21874b62b2
