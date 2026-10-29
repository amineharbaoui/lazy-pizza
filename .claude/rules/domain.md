---
paths:
  - "features/*/domain/**"
---

# Domain modules

- Business rules live here, in use cases or domain models. Never in a ViewModel, a UI mapper or a data source.
- Every operation the UI needs is a use case, even when it only forwards to a repository.
- A use case is one class, `<Verb><Noun>UseCase`, with an `@Inject constructor` and a single `operator fun invoke`. It returns a `Flow` when the caller observes something (`Observe...`), and is `suspend` for a one-shot action.
- Repository interfaces live in `repository/`, are named `<Noun>Repository`, and use only domain types.
- Domain models are immutable data classes or sealed interfaces, with no Firebase, Room or serialization annotations.
- An amount of money is a `Money` from `:core:model`: whole cents inside, always two decimals outside (`12.30`). Never a `Double` or a `Float`. A percentage, such as a discount or a tax, is rounded to the cent, half up, once, where it's computed.
- No dispatchers here. The data layer picks the thread.

## Failures

- An expected failure, such as offline, signed out, rejected, not found or invalid input, is a value declared in `model/`. A one-shot operation returns a sealed result, for example `sealed interface PlaceOrderResult { Success, Offline, Rejected }`. A `Flow` emits it as one of its values, for example `sealed interface OrdersState { Loaded, SignedOut, Offline }`. The data layer maps framework exceptions to these values, and the UI handles them with an exhaustive `when`.
- Only a bug or an unexpected failure is an exception: a suspend call throws, a `Flow` fails. The ViewModel catches it and shows its generic error.
- Never use `runCatching` or catch `Exception` or `Throwable` around a suspend call: they also catch `CancellationException`, which must propagate.

## Code not to copy

- `PlaceOrderUseCase` and `SignInWithSmsCodeUseCase` return `Result<T>` carrying raw Firebase exceptions.
- `ObserveOrdersUseCase` calls `Instant.now()` instead of reading an injected `Clock`.
- Prices in the cart, menu and order models are still `Double`. Moving them to `Money` is a separate task.
