# Design

## Context

See proposal.md for the motivation. These facts about the current code shape the approach:

- `OrdersViewModel` (`:features:order:ui-past-orders`) maps domain `Order`s straight to `OrderUi` and drops them. `OrderUiState` is `Loading | NotLoggedIn | Ready(orders) | Error(message)`. There is no one-shot event flow and no snackbar host.
- `Order.items` stores `productId`, `quantity` and toppings (`id`, `quantity`). Those are the keys used to look items up on today's menu. The stored prices are ignored.
- `MenuRepository` only has Firestore snapshot-listener flows. `observeMenuSections()` combines one flow per category, and `observeToppings()` is a separate flow.
- Cart lines are written with `OnConflictStrategy.REPLACE`. Each pizza gets a new UUID `lineId`. A non-pizza line uses `lineId = productId`, and the menu screen writes the new total quantity. Writing a past order's Cola line as-is would therefore overwrite a Cola already in the cart instead of adding to it.
- `CartRepository.addItem` writes one line at a time, so a failure halfway would leave a partial cart.
- Every data source is a concrete class (`ProductRemoteDataSource`, `CartLocalDataSource`, `CartTransferLocalDataSource`, `OrdersRemoteDataSource`, `PhoneAuthDataSource`). No module has `testFixtures` yet.

## Goals / Non-Goals

**Goals:**
- Put the reorder rule (match against the menu, reprice, drop missing toppings, report what was skipped) in one domain use case that the ViewModel calls once.
- Write the cart atomically: every line is added, or none is.
- Keep the non-pizza quantity rule ("adding increments") in the cart domain, next to the cart.
- Let acceptance tests drive the real ViewModel, use cases and repositories with every data source faked.

**Non-Goals:**
- Migrating `MenuRepository` / `OrdersRepository` streams to typed errors. Only the new one-shot calls return typed results.
- A shared app-level snackbar host. The Orders screen gets its own.

## Contract

### Domain: `:features:order:domain` (new deps: `:features:menu:domain`, `:features:cart:domain`)

```kotlin
// com.example.order.domain.model.ReorderResult
sealed interface ReorderResult {
    /** At least one item was added to the cart. `skipped` is empty when everything was added. */
    data class Added(val skipped: List<SkippedReorderItem>) : ReorderResult

    /** Nothing in the order is on the menu anymore. The cart is unchanged. */
    data class NothingAvailable(val skipped: List<SkippedReorderItem>) : ReorderResult

    data class Failed(val error: ReorderError) : ReorderResult
}

sealed interface SkippedReorderItem {
    data class Product(val name: String) : SkippedReorderItem
    data class Topping(val toppingName: String, val pizzaName: String) : SkippedReorderItem
}

sealed interface ReorderError {
    val isRetryable: Boolean

    data object MenuUnavailable : ReorderError { override val isRetryable = true }
    data object CartUpdateFailed : ReorderError { override val isRetryable = true }
}

// com.example.order.domain.usecase.ReorderUseCase
class ReorderUseCase @Inject constructor(
    getMenuSnapshotUseCase: GetMenuSnapshotUseCase,
    addCartItemsUseCase: AddCartItemsUseCase,
) {
    suspend operator fun invoke(order: Order): ReorderResult
}
```

### Domain: `:features:menu:domain`

```kotlin
// com.example.menu.domain.model.MenuSnapshot
data class MenuSnapshot(
    val items: List<MenuItem>,
    val toppings: List<Topping>,
)

// com.example.menu.domain.model.MenuSnapshotResult
sealed interface MenuSnapshotResult {
    data class Success(val snapshot: MenuSnapshot) : MenuSnapshotResult
    data object Unavailable : MenuSnapshotResult   // retryable: network, timeout, backend error
}

// MenuRepository: one new member
suspend fun getMenuSnapshot(): MenuSnapshotResult

// com.example.menu.domain.usecase.GetMenuSnapshotUseCase
class GetMenuSnapshotUseCase @Inject constructor(repository: MenuRepository) {
    suspend operator fun invoke(): MenuSnapshotResult
}
```

### Domain: `:features:cart:domain`

```kotlin
// com.example.cart.domain.model.CartWriteResult
sealed interface CartWriteResult {
    data object Success : CartWriteResult
    data object Failed : CartWriteResult   // retryable: storage error, nothing was written
}

// CartRepository: one new member. All lines are written in one transaction, or none are.
suspend fun addItems(owner: CartOwner, items: List<CartItem>): CartWriteResult

// com.example.cart.domain.usecase.AddCartItemsUseCase
// Adds the lines to the session's cart. A non-pizza line whose product is already in the
// cart adds its quantity to that line instead of replacing it. Pizzas always get a new line.
class AddCartItemsUseCase @Inject constructor(
    repository: CartRepository,
    sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(items: List<CartItem>): CartWriteResult
}
```

### Data-source interfaces (impl renamed `*Impl`, bound in each module's Hilt module)

```kotlin
// :features:menu:data
interface ProductRemoteDataSource {
    fun observeProductsByCategory(category: ProductCategory): Flow<List<ProductDto>>
    fun observeProductById(id: String): Flow<ProductDto?>
}

// :features:cart:data
interface CartLocalDataSource {
    fun observeCart(ownerKey: String): Flow<Cart>
    suspend fun addItem(ownerKey: String, item: CartItem)
    suspend fun addItems(ownerKey: String, items: List<CartItem>)   // new, single Room transaction
    suspend fun updateItemQuantity(ownerKey: String, lineId: String, quantity: Int)
    suspend fun removeItem(ownerKey: String, lineId: String)
    suspend fun clear(ownerKey: String)
}
interface CartTransferLocalDataSource {
    suspend fun transferCart(fromOwnerKey: String, toOwnerKey: String)
}

// :features:order:data
interface OrdersRemoteDataSource {
    suspend fun createOrder(order: OrderDto): Result<Unit>
    fun observeOrdersByUser(userId: String): Flow<List<OrderDto>>
}

// :features:auth:data (same members as the current class)
interface PhoneAuthDataSource {
    val userIdFlow: Flow<String?>
    val isSignedIn: Flow<Boolean>
    fun getCurrentUser(): RemoteUser?
    fun signOut()
    suspend fun signInWithCode(verificationId: String, smsCode: String): RemoteUser
}
```

Fakes in each data module's `src/testFixtures`: `FakeProductRemoteDataSource` (settable products per category, failure switch), `FakeCartLocalDataSource` (in-memory lines, failure switch on `addItems`), `FakeCartTransferLocalDataSource`, `FakeOrdersRemoteDataSource` (settable orders), `FakePhoneAuthDataSource` (settable signed-in uid).

### UI: `:features:order:ui-past-orders`

```kotlin
sealed interface OrderUiState {
    data object Loading : OrderUiState
    data object NotLoggedIn : OrderUiState
    data class Ready(
        val orders: List<OrderUi>,
        val orderAgain: OrderAgainUiState,
    ) : OrderUiState
    data class Error(val message: String) : OrderUiState   // unchanged
}

// OrderUi gains: val orderNumber: String  (the raw id, passed back in the intent)

sealed interface OrderAgainUiState {
    data object Idle : OrderAgainUiState
    data object InProgress : OrderAgainUiState               // every Order again button disabled
    data class SomeSkipped(val skippedLabels: List<String>) : OrderAgainUiState   // dialog: "Go to cart" + "Close"
    data class NothingAvailable(val skippedLabels: List<String>) : OrderAgainUiState  // dialog: "Close" only
}

sealed interface OrdersUiEvent {
    data object NavigateToCart : OrdersUiEvent
    data class ShowOrderAgainError(val error: OrderAgainErrorUi) : OrdersUiEvent
}

enum class OrderAgainErrorUi { MENU_UNAVAILABLE, CART_UPDATE_FAILED }   // mapped to string resources

class OrdersViewModel {
    val uiState: StateFlow<OrderUiState>
    val events: SharedFlow<OrdersUiEvent>

    fun orderAgain(orderNumber: String)
    fun goToCartFromSkippedItems()   // closes dialog, emits NavigateToCart
    fun dismissOrderAgainDialog()    // "Close" or back: closes dialog, stays on Orders
}

@Composable
fun OrdersScreen(
    innerPadding: PaddingValues,
    onSignInClick: () -> Unit,
    onNavigateToCart: () -> Unit,   // new; AppNavigation wires it to backStack.add(CartRoute)
    modifier: Modifier = Modifier,
    viewModel: OrdersViewModel = hiltViewModel(),
)
```

Skipped labels: a product is shown by its name ("Lemonade"), a topping as "Truffle on Margherita". The strings come from resources.

## Decisions

1. **The reorder rule lives in `:features:order:domain`.** It needs orders, the menu and the cart. Domain → domain is the only allowed cross-feature edge. Order → menu and order → cart add no cycle, because neither menu nor cart depends on order. *Alternative:* a use case in the ViewModel's module. Rejected: that is business logic in UI.
2. **One-shot `getMenuSnapshot()`, built on the existing flows with `first()`.** `combine` of the per-category flows plus the toppings flow, `first()`, with failures caught in `MenuRepositoryImpl` and mapped to `MenuSnapshotResult.Unavailable`, so Firestore exceptions stay in data. No new data-source method, and offline cache behavior stays the same as the menu screen's. *Alternative:* new Firestore `get()` calls. Rejected: they duplicate the query code for no behavior gain.
3. **An empty menu is a failed load.** `ReorderUseCase` treats a `Success` snapshot with no items as `MenuUnavailable` (ORDER-18). This is a business rule, so it lives in domain, not in the repository. It covers Firestore returning an empty snapshot from a cold cache while offline.
4. **Menu lookup by product id, prices from the menu.** A past item matches when its `productId` is in `MenuSnapshot.items`. Each topping matches when its `id` is in `MenuSnapshot.toppings`. The cart line uses the menu's name, image and price, and the past quantities. A `PizzaItem` match becomes `CartItem.Pizza` with a new UUID `lineId`. Anything else becomes `CartItem.Other` with `lineId = productId`, the same as the menu screen.
5. **`AddCartItemsUseCase` merges non-pizza quantities. `CartRepository.addItems` writes atomically.** The use case reads the current cart once, sets each `Other` line's quantity to the existing quantity plus the added quantity, and passes the whole list to `addItems`. `CartLocalDataSourceImpl.addItems` runs every insert in one `db.withTransaction`. *Alternative:* merging inside the data layer. Rejected: "adding increments" is a business rule, and the data layer must not own it. The read-then-write gap is the same as today's menu screen.
6. **Typed results from the repository up.** The repositories catch Firestore/Room exceptions and return `MenuSnapshotResult` / `CartWriteResult`. `kotlin.Result` is not used here because it carries the framework exception into domain. `ReorderUseCase` maps them to `ReorderError.MenuUnavailable` / `CartUpdateFailed`. Both are retryable by the user tapping again. No automatic backoff, because this is a user-triggered action with visible feedback. The ViewModel maps those to `OrderAgainErrorUi`, then to string resources. Raw exception text never reaches the UI.
7. **The skipped-items dialog is state, navigation is an event.** The dialog survives rotation until the user acts. `NavigateToCart` and errors are one-shot `SharedFlow` events, collected in `LaunchedEffect`, so they do not replay. The error shows in a `SnackbarHost` on the Orders screen.
8. **Double-tap guard in the ViewModel.** `orderAgain` returns at once while the state is `InProgress`, and every button renders disabled in that state. ORDER-08 checks both the state and the cart.
9. **Interfaces for the five data sources the acceptance tests reach.** This is required by the project's acceptance-test rule (every data source faked, fakes in `testFixtures`). The classes keep their behavior and get an `Impl` suffix. Existing data-source tests move with the class.

## Risks / Trade-offs

- [Android `testFixtures` with Kotlin sources needs `android.experimental.enableTestFixturesKotlinSupport=true` on AGP 8.13] → set it in `gradle.properties` in Contract task 1. If it fails, fall back to a `:core:testing`-style JVM fixtures module per feature, and record that in tasks.
- [`first()` on a Firestore listener with no cache and no network can hang instead of failing] → wrap `getMenuSnapshot()` in `withTimeout` (10 s) in the repository, and treat a timeout as a failure.
- [A product renamed on the menu shows its new name in the cart] → this is intended: the cart reflects today's menu.
- [The read-then-write gap in `AddCartItemsUseCase` could lose an increment if the user adds the same drink on another screen during the call] → this is the same window the menu screen already has. Accepted.
- [Old orders whose `productId` is empty, from before ids were stored] → they never match and are reported as skipped by name. That is the safe outcome.

## Migration Plan

No data migration. Room schema is unchanged: `addItems` reuses the existing DAO inserts. Firestore documents are unchanged. Rollback is reverting the PR.
