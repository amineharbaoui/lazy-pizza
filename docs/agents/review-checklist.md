# Code Review Checklist

Read this before reviewing changes. Prioritize:

- **Layer boundaries**: any violation of the dependency rules in `CLAUDE.md`.
- **Compose**: unconditional `remember*`/`produceState`/`LaunchedEffect` calls, unstable parameters causing recomposition, missing `remember` on lambdas in hot paths, `derivedStateOf` misuse.
- **Coroutines**: broken structured concurrency, wrong dispatcher, leaked scopes, main-thread blocking, Flows collected outside lifecycle scope, `GlobalScope`.
- **Hilt**: wrong scope (`@Singleton` vs `@ViewModelScoped` vs `@ActivityRetainedScoped`), leaking `Context`.
- **Gradle**: dependencies outside `libs.versions.toml`, wrong configuration (`api` vs `implementation`), missing release `proguardFiles`, Kover exclusion patterns that don't match.
- **Room**: missing migrations, blocking calls outside `withContext`, entities leaking out of data modules.
- **Security**: secrets in CI, cleartext traffic, logging PII.

Do not report: style nitpicks already covered by ktlint, missing KDoc, or "extract this into a function" without a clear benefit.
