---
paths:
  - "features/*/domain/**"
  - "features/*/api/**"
  - "core/model/**"
---

# Domain modules

- Business rules live here. Never in a ViewModel, a mapper, a repository or a data source.
- A rule about one model's own data is a property or function of that model, such as `Cart.subtotal`. A rule that needs several models or a `Clock` is a domain service: a pure class in `service/`, named for what it decides (`PickupSlotCalculator`), injected into the use cases that need it.
- Checking user input is a business rule too: a phone number's format, a comment's length, whether an order can be placed. A model function, a domain service or the use case's sealed result (`InvalidPhoneNumber`) decides it. The ViewModel only turns the answer into state.
- Every operation the UI needs is a use case, even when it only forwards to a repository.
- A user action is one use case. It runs every step in order and decides what happens when a step fails after an earlier one succeeded, for example placing the order, then clearing the cart.
- A use case is one class, `<Verb><Noun>UseCase`, with an `@Inject constructor` and a single `operator fun invoke`. It returns a `Flow` when the caller observes something (`Observe...`), and is `suspend` for a one-shot action.
- A use case other features call is split in two: its interface, `<Verb><Noun>UseCase` with the same single `operator fun invoke`, in the feature's `:api`, and its implementation, `internal class <Verb><Noun>UseCaseImpl`, here in `usecase/`. A Hilt module in `di/`, installed in `SingletonComponent`, binds each one without a scope: `@Binds internal abstract fun bindObserveCart(impl: ObserveCartUseCaseImpl): ObserveCartUseCase`. It binds nothing else: repositories are bound by `:data`.
- A model or sealed result that appears in an `:api` signature lives in the `:api`'s `model/`, not here.
- A use case injects its own feature's repositories, domain services and use cases, and other features' use cases, through their `:api`. Never another feature's repository: a feature's use cases are what other features see of it. A use case calls another use case to reuse what it does, such as observing the cart, never to share a rule.
- Repository interfaces live in `repository/`, are named `<Noun>Repository`, and use only domain types.
- Domain models are immutable data classes or sealed interfaces, with no Firebase, Room or serialization annotations.
- An amount of money is a `Money` from `:core:model`: whole cents inside, always two decimals outside (`12.30`). Never a `Double` or a `Float`. A percentage, such as a discount or a tax, is rounded to the cent, half up, once, where it's computed.
- Use cases are main-safe. They pick no dispatcher for I/O: the data layer's suspend and `Flow` APIs already are. A use case that does heavy CPU work, such as sorting thousands of items, moves it with `withContext` or `flowOn` on an injected `@DefaultDispatcher CoroutineDispatcher`, never `Dispatchers.Default` directly, so tests can replace it. The first one to need it adds the qualifier to `:core:common`.

## Failures

- An expected failure, such as offline, signed out, rejected, not found or invalid input, is a value declared in `model/`. A one-shot operation returns a sealed result, for example `sealed interface PlaceOrderResult { Success, Offline, Rejected, Failed }`. A `Flow` emits it as one of its values, for example `sealed interface OrdersState { Loaded, SignedOut, Offline }`. The data layer maps framework exceptions to these values, and the UI handles them with an exhaustive `when`.
- Only a bug is an exception. Nothing catches it after a one-shot call: it crashes the app and Crashlytics reports it, since a bug shown as a generic error is a bug nobody fixes. A failing `Flow` is caught in the ViewModel, as `.claude/rules/ui.md` says.
- Never use `runCatching` or catch `Exception` or `Throwable` around a suspend call: they also catch `CancellationException`, which must propagate.

## Code not to copy

- `PlaceOrderUseCase` and `SignInWithSmsCodeUseCase` return `Result<T>` carrying raw Firebase exceptions.
- `Money` doesn't exist yet: prices in the cart, menu and order models are `Double`. Adding `Money` to `:core:model` and moving the prices to it is a refactor of its own.
- `ObserveCartUseCase`, `AddCartItemUseCase`, `UpdateCartItemQuantityUseCase`, `RemoveCartItemUseCase`, `ClearCartUseCase` and `MergeItemsIntoCartUseCase` inject auth's `SessionRepository` instead of its use cases. `SessionCartOwner.kt` holds the cart owner rule as an extension of that repository, in `usecase/`.
- `ReorderUseCase` in `:features:order:domain` injects cart's `UnavailableItemsRepository` instead of a cart use case.
- `OrderStatus` and `ProductCategory` live in `:core:model`, but belong to `:features:order:domain` and `:features:menu:domain`.
