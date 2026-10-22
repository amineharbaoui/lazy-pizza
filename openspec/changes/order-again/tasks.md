# Tasks

## 1. Contract

- [x] 1.1 Extract interfaces `ProductRemoteDataSource`, `CartLocalDataSource` (plus new `addItems`), `CartTransferLocalDataSource`, `OrdersRemoteDataSource` and `PhoneAuthDataSource`. Rename the classes to `*Impl`, bind them in each module's Hilt module, and stub `CartLocalDataSourceImpl.addItems` with `TODO()`. Verify: `./gradlew :features:menu:data:testDebugUnitTest :features:cart:data:testDebugUnitTest :features:order:data:testDebugUnitTest :features:auth:data:testDebugUnitTest` green (existing tests unchanged).
- [x] 1.2 Enable Android `testFixtures` (Kotlin support) for menu, cart, order and auth data modules, and add `testImplementation` of those data modules, their `testFixtures` and `:core:testing` to `:features:order:ui-past-orders`. Verify: `./gradlew :features:order:ui-past-orders:compileDebugUnitTestKotlin` succeeds.
- [x] 1.3 Add the domain stubs from design.md Contract with `TODO()` bodies: `MenuSnapshot`, `MenuSnapshotResult`, `MenuRepository.getMenuSnapshot`, `GetMenuSnapshotUseCase`; `CartWriteResult`, `CartRepository.addItems`, `AddCartItemsUseCase`; `ReorderResult`, `SkippedReorderItem`, `ReorderError`, `ReorderUseCase`. Add `:features:menu:domain` and `:features:cart:domain` to `:features:order:domain`. Verify: affected modules compile.
- [x] 1.4 Add the UI contract: `OrderUiState.Ready.orderAgain`, `OrderAgainUiState`, `SkippedItemUi`, `OrdersUiEvent`, `OrderAgainErrorUi`, `OrderUi.orderNumber`, `OrdersViewModel.events` / `orderAgain` / `goToCartFromSkippedItems` / `dismissOrderAgainDialog` (`TODO()` bodies), and `OrdersScreen(onNavigateToCart)` wired in `AppNavigation` to `CartRoute`. Verify: `./gradlew assembleDebug` succeeds.
- [x] 1.5 Add Roborazzi (version catalog + `:features:order:ui-past-orders` test setup, Robolectric). Verify: `./gradlew :features:order:ui-past-orders:recordRoborazziDebug` runs on an empty test class.

## 2. Red (test-writer)

- [x] 2.1 Write every test and support file in test-plan.md. Verify: `./gradlew :features:order:ui-past-orders:testDebugUnitTest` compiles, and every new test fails on an assertion or `TODO()`/`NotImplementedError`, never on compilation or setup.
- [ ] 2.2 Commit `test(order-again): red` and write its sha into test-plan.md `LOCK:`. Verify: `git log -1` shows the commit, and test-plan.md ends with `LOCK: <sha>`.

## 3. Green: Order again adds available items to the cart at today's prices

- [ ] 3.1 `MenuRepositoryImpl.getMenuSnapshot`: `combine` of category flows + toppings, `first()`, `withTimeout`, failures mapped to `Unavailable`. Verify: `./gradlew :features:menu:data:testDebugUnitTest` green.
- [ ] 3.2 `CartLocalDataSourceImpl.addItems` in one `db.withTransaction`, and `CartRepositoryImpl.addItems` maps exceptions to `CartWriteResult.Failed`. Verify: `./gradlew :features:cart:data:testDebugUnitTest` green.
- [ ] 3.3 `AddCartItemsUseCase`: merge non-pizza quantities with the current cart, then `addItems`. Verify: `./gradlew :features:cart:domain:test` green.
- [ ] 3.4 `ReorderUseCase`: match by product id, reprice from the menu, build cart lines. `OrdersViewModel.orderAgain` with the `InProgress` guard and `NavigateToCart`. Verify: `./gradlew :features:order:ui-past-orders:testDebugUnitTest`; rows ORDER-03 to ORDER-08 🟢.

## 3. Green: Order again skips what is no longer on the menu and tells the user

- [ ] 3.5 `ReorderUseCase`: skip products not on the menu and drop toppings not in today's topping list, returning `Added(skipped)` / `NothingAvailable`. ViewModel maps to `SomeSkipped` / `NothingAvailable`, plus `goToCartFromSkippedItems` and `dismissOrderAgainDialog`. Verify: `./gradlew :features:order:ui-past-orders:testDebugUnitTest`; rows ORDER-09 to ORDER-14 🟢.

## 3. Green: Order again fails safely

- [ ] 3.6 `ReorderUseCase` maps `Unavailable` and an empty menu to `MenuUnavailable`, and a cart failure to `CartUpdateFailed`. The ViewModel emits `ShowOrderAgainError` and returns to `Idle`. Verify: `./gradlew :features:order:ui-past-orders:testDebugUnitTest`; rows ORDER-17 to ORDER-21 🟢.

## 4. UI

- [ ] 4.1 `OrderCard` "Order again" button (disabled while `InProgress`); skipped and nothing-available dialogs with "Go to cart" / "Close" and back dismissal; `SnackbarHost` for errors; strings in `strings.xml`; test tags for Maestro ids. Verify: the app builds.
- [ ] 4.2 Previews `OrdersScreenReadyMixedStatusesPreview`, `OrdersScreenNotLoggedInPreview`, `OrdersScreenSomeSkippedDialogPreview`, `OrdersScreenNothingAvailableDialogPreview`. Verify: `./gradlew :features:order:ui-past-orders:recordRoborazziDebug`, open every new PNG and check it against ORDER-01, -02, -15 and -16; rows 🟢.

## 5. Gate

- [ ] 5.1 Run `./gradlew ktlintFormat`, then `./gradlew ktlintCheck unitTestAll assembleDebug`. Verify: green, output kept for verify.md.

## 6. Journey

- [ ] 6.1 Add `.maestro/config.yaml`, `.maestro/common/login.yaml` and `.maestro/common/place-order.yaml` (based on `feature/integrate-ai`), and `.maestro/flows/order_again.yaml`. Run it on `device_for_test` with retries off. Verify: the flow passes; row ORDER-22 🟢.

## 7. Verify and PR

- [ ] 7.1 Commit all work, then the reviewer subagent writes verify.md. Verify: DECISION is PASS or PASS_WITH_WARNINGS.
- [ ] 7.2 Push `feature/order-again` and open the PR. Verify: PR link.
