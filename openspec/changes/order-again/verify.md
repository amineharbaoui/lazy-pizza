# Verify

Reviewed at HEAD `f532a1e` on `feature/order-again` against `main`.

## Tasks
- [x] Every box in tasks groups 1 to 6 is checked (1.1 to 1.5, 2.1 to 2.2, 3.1 to 3.6, 4.1 to 4.2, 5.1, 6.1 all `[x]`)
- [x] `git status --porcelain` shows nothing but verify.md: the only entry is ` M .claude/settings.json`, the developer's local Claude permission settings. It is outside the change and is not product code, tests or specs, so the diff review is complete.
- Open tasks: 7.1 and 7.2 only (this verify, then the PR). Expected at this point.

## Ledger
- [x] Every test-plan.md row exists as a real test. All 44 non-journey rows matched by name against the JUnit XML in `features/order/ui-past-orders/build/test-results/testDebugUnitTest/` from my gate run: 40 acceptance tests in `OrderAgainAcceptanceTest`, `OrderAgainSkippedItemsAcceptanceTest` and `OrderAgainFailureAcceptanceTest`, plus 4 `OrdersScreenScreenshotTest` captures. The module ran exactly 44 tests. ORDER-22 maps to `.maestro/flows/order_again.yaml`.
- [x] Every row is 🟢 green: 44/44 passed, 0 failed, 0 skipped, and ORDER-22 passed (see Journeys).

## Lock integrity (blocking)
- Tests and support files vs `LOCK:` sha:
  `git diff --stat cb54a226fc3fa3011e60b81d7c130a21874b62b2 HEAD -- features/order/ui-past-orders/src/test/java/com/example/checkout/order/ui/pastorders features/menu/data/src/testFixtures features/cart/data/src/testFixtures features/order/data/src/testFixtures features/auth/data/src/testFixtures core/testing/src/main/java/com/example/core/testing/MainDispatcherExtension.kt`
  → **no diff**. All 11 locked files (5 fakes, `OrderAgainFixtures.kt`, `OrderAgainTestWiring.kt`, 3 acceptance classes, `OrdersScreenScreenshotTest.kt`) exist at the LOCK commit and are unchanged.
  Test-side additions after LOCK (`git diff --stat <LOCK> HEAD -- '*/src/test*' '*/testFixtures/*'`): the 4 Roborazzi baseline PNGs (recorded in group 4, as planned) and `features/order/ui-past-orders/src/test/resources/robolectric.properties` (`qualifiers=w411dp-h1200dp-xxhdpi`). The properties file is new, is not a listed test or support file, and changes no assertion. It only sets the Robolectric display size, which affects the screenshot captures and not the plain-JVM JUnit 5 acceptance tests. The baselines were recorded with it in place. It does not weaken the lock. The `.maestro/` flows and subflows were added in group 6, as test-plan.md says ("added during apply").
- Specs vs `SPECS-APPROVED` sha:
  `git diff --stat 5b44e3718d0477d91dfc23b609957542063c8fab HEAD -- openspec/changes/order-again/specs` → **no diff**.

## Gate
- Command: `JAVA_HOME=$(/usr/libexec/java_home -v 17) ./gradlew ktlintCheck unitTestAll assembleDebug --rerun-tasks` (with `--rerun-tasks` so no task came from cache)
- Result: `BUILD SUCCESSFUL in 29s`, `806 actionable tasks: 806 executed`, exit 0. Unit tests, counted from the fresh JUnit XML: **111 passed, 0 failed, 0 skipped** (auth:domain 6, auth:data 17, auth:ui-login 29, cart:domain 11, cart:data 4, order:ui-past-orders 44). Roborazzi, in verify mode (`roborazzi.test.verify=true`), wrote `results-summary.json`: total 4, unchanged 4, changed 0, added 0, recorded 0. ktlint ran on the changed source sets, including the `testFixtures` sets. The SKIPPED ktlint tasks are empty source sets.

## Journeys
- ORDER-22 `.maestro/flows/order_again.yaml` → **PASS**. I installed the APK from my gate run (HEAD tree) on `emulator-5554` (`device_for_test`) with `adb install -r`, then ran `~/.maestro/bin/maestro test .maestro/flows/order_again.yaml` with retries off. Exit 0, and every step COMPLETED: login with the test phone, Margherita order placed, Orders, `order_again_button` index 0, `cart_item_Margherita` visible. An earlier run against the previously installed build also passed.

## Screenshots
All 4 baselines are new and opened and checked against their scenarios:
- `...captures OrdersScreenReadyMixedStatusesPreview.png` → ORDER-01: Completed, In Progress and Canceled cards each show an enabled "Order again" button.
- `...captures OrdersScreenNotLoggedInPreview.png` → ORDER-02: sign-in prompt, no "Order again".
- `...captures OrdersScreenSomeSkippedDialogPreview.png` → ORDER-15: title "Some items are no longer available", lists "Lemonade" and "Truffle on Margherita", "Close" and "Go to cart".
- `...captures OrdersScreenNothingAvailableDialogPreview.png` → ORDER-16: title "None of these items are on the menu anymore", lists "Lemonade", "Close" only.
- New screen state for the developer to look at: these are the first Roborazzi baselines in the repo. In both dialog captures the background "Order again" buttons render disabled, because `orderAgain != Idle`. That matches ORDER-08's rule, but no scenario asks for it. The dialog previews use the inline `DsDialog.ConfirmContent` with a drawn scrim instead of a real `Dialog` window. They prove the content, not the window behavior.
- No existing baseline changed.

## Architecture
OK.
- `:features:order:ui-past-orders` main sources depend only on domains (order, menu, cart, auth). Data modules and their `testFixtures` are `testImplementation` only. No production file outside a data module imports `com.example.*.data.*`.
- New domain → domain edges: `order:domain → menu:domain, cart:domain`. Graph stays acyclic: menu:domain depends on nothing, cart:domain depends on auth:domain, and none depend on order:domain.
- Domain is pure Kotlin. `ReorderUseCase` uses only `java.util.UUID`. No dispatchers are hardcoded in domain.
- Framework exceptions are mapped in data: `MenuRepositoryImpl.getMenuSnapshot` maps to `MenuSnapshotResult.Unavailable` (with a 10 s timeout) and `CartRepositoryImpl.addItems` maps to `CartWriteResult.Failed`. Both rethrow `CancellationException`. Typed `ReorderError` has `isRetryable`. The UI gets the `OrderAgainErrorUi` enum, mapped to string resources, so no raw text can reach the screen.
- No new data → other-feature-domain edges. The known cart-data → auth-domain debt is pre-existing and untouched.
- The ViewModel exposes one `StateFlow` and one `SharedFlow`, and its intents are named for what happens (`orderAgain`, `goToCartFromSkippedItems`, `dismissOrderAgainDialog`).
- Commit `518dbae` contains only ktlint formatting, removes unused imports and the empty package-only `ui-checkout/Navigation.kt`, and commits the CartDatabase v3 schema export. I checked `git show 518dbae -w`: no behavior change.

## Findings
Required behavior that no automated test proves:
1. ORDER-12 "presses back": only the `dismissOrderAgainDialog()` intent is tested. The wiring of back to `onDismissRequest` in `OrdersScreen.kt` (`DsDialog.Confirm(onDismissRequest = onDismiss)`) is correct when read but has no test, as test-plan.md acknowledges.
2. ORDER-17/18/19 message text: the tests assert the typed `ShowOrderAgainError(OrderAgainErrorUi.*)` event. No test checks the enum → snackbar string mapping. I checked by review: `strings.xml` holds "Couldn't load the menu. Please try again." and "Couldn't update your cart. Please try again." verbatim, and `OrdersScreen` maps each enum value to the right one.
3. The real data paths behind the fakes have no tests of their own. These are the atomic `CartLocalDataSourceImpl.addItems` (`db.withTransaction`) and the `MenuRepositoryImpl.getMenuSnapshot` timeout and empty cases. `:features:menu:data:testDebugUnitTest` is NO-SOURCE. The fakes model the contract (all-or-nothing `addItems`), so the acceptance tests prove the domain and UI behavior, not the Room or Firestore implementations. ORDER-22 covers the happy path on device.

Robustness warnings (non-blocking):
4. `OrdersViewModel._events` is a `MutableSharedFlow()` with no replay or buffer. If no collector is active when a reorder finishes, for example because the user switched tabs during the menu load (up to the 10 s timeout), `NavigateToCart` or the error snackbar is dropped. The cart is still written, so no data is lost. Not replaying on rotation is correct per `ui.md`, but a dropped event is silent.
5. `AddCartItemsUseCase` reads the current cart (`observeCart().first()`) and then writes merged quantities in a separate `addItems` transaction. A concurrent quantity change between the two can be overwritten. The window is small and only within one device.

Behavior no scenario asked for (justified, listed for the PR):
6. Test infrastructure for the journey: `testTag`s on the nav bar, auth, menu, pizza detail, cart, checkout, confirmation, orders and the `DsDialog` buttons, plus `testTagsAsResourceId = true` wrapping `MainScreen` in `MainActivity`. No user-visible change.
7. `DsDialog.Confirm` was refactored to delegate to a new public `DsDialog.ConfirmContent`, for inline previews. Behavior is the same: the secondary button still calls `onDismissRequest`.
8. `ui-past-orders` test config sets `unitTests.isReturnDefaultValues = true`, because `OrdersViewModel` still calls `android.util.Log.d` (pre-existing). Any other Android stub call in that module's JVM tests now returns a default value silently instead of failing.

DECISION: PASS_WITH_WARNINGS
