---
paths:
  - "**/build.gradle.kts"
  - "settings.gradle.kts"
  - "gradle.properties"
  - "gradle/libs.versions.toml"
  - "build-logic/**"
---

# Gradle

## Dependencies

- Every version lives in `gradle/libs.versions.toml` and is used through a `libs.*` alias. Never a version in a build file. Compose and Firebase come through their BOMs.
- A module dependency uses the type-safe accessor: `projects.features.cart.domain`.
- A module depends on another feature only through its `:api`: never its `:domain`, its `:ui-*`, or, outside tests, its `:data`. A `:domain` depends on its own `:api` with `api(...)`, since its public signatures use the `:api`'s types.
- `implementation` by default. `api` only when this module's public signatures use the other module's types.
- Prefer a library the catalog already has. A new one gets its latest stable version, and the PR says why it's needed.
- After changing dependencies, run `./gradlew buildHealth` and apply its advice for the modules you touched. It only warns, so read its output.

## Convention plugins

- Build setup lives in the `custom.*` convention plugins in `build-logic/`. A module's build file only applies plugins, sets its `namespace` and lists its dependencies. Setup that a second module needs moves into a convention plugin.
- Plugins by module type:
  - `:domain`: `custom.kotlin.module`, `custom.testing`, and `custom.hilt.kotlin` when the feature has an `:api`
  - `:api`: `custom.kotlin.module`
  - `:data`: `custom.android.module`, `custom.hilt`, `custom.testing`, and `custom.room` when it uses Room
  - `:ui-<screen>`: `custom.android.module`, `custom.compose`, `custom.hilt`, `custom.testing`, `custom.screenshot.testing`
  - `:core:*`: `custom.kotlin.module`, or `custom.android.module` when it needs Android. `:core:designsystem` also applies `custom.screenshot.testing`.
  - `:core:screenshot-testing`: test-only, the screenshot tester used by `custom.screenshot.testing`.

## New module

1. Create `features/<feature>/<layer>/` and add `include(":features:<feature>:<layer>")` to `settings.gradle.kts`.
2. Apply the plugins for its type. The namespace is `com.example.<feature>.<layer>`, or `com.example.<feature>.ui.<screen>` for a screen, without dashes (`com.example.menu.ui.pizzadetail`). Packages start with the namespace.
3. Add a new `:data` or `:ui-<screen>` module to `:app`'s dependencies. A new screen also gets its route in `:app`.

## Test fixtures

- A data module with fakes enables them with `android { testFixtures { enable = true } }`.
- A `:domain` with an `:api` applies `java-test-fixtures`, for `<Feature>UseCases`. Its data module's fixtures expose it with `testFixturesApi(testFixtures(projects.features.cart.domain))`.
- A `:ui-<screen>` module's acceptance tests depend on what they wire: `testImplementation(projects.features.cart.data)`, `testImplementation(testFixtures(projects.features.cart.data))` and `testImplementation(projects.core.testing)`.

## Code not to copy

- Most modules don't apply `custom.testing` yet, including every `:ui-*` module except `:ui-login`, `:ui-past-orders`, `:ui-cart`, `:ui-pizza-detail` and `:ui-home`. Add it with the module's first hand-written test: the generated screenshot tests don't need it.
- `:features:cart:data` depends on Firebase Firestore without using it.
- `custom.hilt.kotlin` doesn't exist yet: `custom.hilt` applies `hilt-android` and the Android Hilt plugin, so it only fits Android modules. The JVM one applies KSP with `hilt-core` and `ksp(hilt-compiler)`, and comes with the first `:api`.
- No feature has an `:api` yet. These modules depend on another feature's `:domain`: `:app`, `:auth:ui-login`, `:menu:ui-home`, `:menu:ui-pizza-detail` and `:order:ui-checkout` on cart's; `:cart:domain`, `:menu:ui-home`, `:order:ui-checkout` and `:order:ui-past-orders` on auth's; `:cart:ui-cart` on menu's. Auth and cart already use each other, through `:auth:ui-login` and `:cart:domain`: see `PhoneAuthViewModel` in `.claude/rules/ui.md`.
