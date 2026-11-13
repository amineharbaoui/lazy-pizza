---
paths:
  - "features/*/data/**"
  - "core/network/**"
---

# Data modules

- `<Noun>RepositoryImpl` in `repository/` implements the domain interface. Bind it in `di/` with `@Binds @Singleton`, in a module installed in `SingletonComponent`.
- A repository injects only its own module's data sources and mappers, and `:core` infrastructure such as a dispatcher. Never another repository, a use case or the session: what it needs to know about the user, such as the cart's owner, comes as a parameter from the use case. It decides where the data comes from, local or remote, and maps exceptions to domain values.
- Everything in a data module is `internal` except its Hilt module: repository implementations, data sources, DAOs, entities, DTOs and mappers. The Hilt module binds them with `@Binds internal abstract fun`. Other modules see only the domain's interfaces, and tests go through `<Feature>TestData` in `src/testFixtures/`.
- Each Firebase or Room access sits behind a data source in `datasource/`: an interface, `<Noun>RemoteDataSource` for Firebase or `<Noun>LocalDataSource` for Room, implemented by `<Interface>Impl`.
- Each data source interface has a `Fake<Interface>` in the module's `src/testFixtures/`, for acceptance tests. A fake works in memory like the real thing: it stores what it receives and emits on change. It also lets a test preset data and force a failure.
- Data sources stay thin: they call Firebase or Room and convert DTOs or entities, nothing else. Any logic goes in the repository, or in the domain when it's a business rule, so that acceptance tests cover it.
- Firebase and Room types (DTOs, entities, DAOs, exceptions) never leave the module. `<From>To<To>Mapper` classes turn DTOs and entities into domain models.
- The repository maps framework exceptions to the domain's sealed results with `try`/`catch` on the specific types, for example a `FirebaseFirestoreException` with code `UNAVAILABLE` becomes `Offline`. After the specific cases, it catches the base types its sources throw, `FirebaseException` and `IOException`, as the result's failure value, such as `Failed`: a backend or network failure never leaves the data layer as an exception. Never `runCatching`, and never catch `Exception` or `Throwable`: they swallow `CancellationException`.
- Money crosses the boundary once, in the mapper: a Firestore number becomes `Money` cents with `(price * 100).roundToLong()`.
- Use the suspend and `Flow` APIs of Room and Firebase. Never block a thread, and never use `Dispatchers.IO` directly: inject the dispatcher, so tests can replace it.

## Room

- A schema change bumps the database version, adds a migration (`@AutoMigration` or a `Migration`), and commits the exported schema in the module's `schemas/`.
- Never use `fallbackToDestructiveMigration`.

## Code not to copy

- `OrdersRemoteDataSource.createOrder` wraps a suspend call in `runCatching`, and `PhoneAuthRepositoryImpl` catches `Exception`.
