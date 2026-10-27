# LazyPizza – Project Development Guidelines

Multi-module Android app: Kotlin, Jetpack Compose (Material 3 + Adaptive), Hilt, Room, Firebase Auth & Firestore, Navigation 3. Feature-based, state-driven, strictly layered. Toolchain and SDK versions live in `gradle/libs.versions.toml`.

## Project Purpose & Scale

LazyPizza simulates an **enterprise project at a big tech company**: a very large multi-module app, production-ready, serving millions of users, and developed in parallel by multiple squads. It is also a showcase of engineering skill and a playground for experimenting with AI-assisted development and new tooling.

**Never treat it as a small project.** The current module count is not the constraint. Every decision must hold at enterprise scale:

- Never justify a shortcut with "the project is small" or "it's only N modules".
- Open for scale: favor module isolation, clear ownership boundaries, and stable public APIs between modules so squads can work independently.
- Easy to maintain: small reviewable diffs, no shared mutable global state, consistent conventions across features.
- Build/CI must scale: affected-module builds and tests, build caching, parallelism.
- Production-ready: offline resilience, retries with backoff, pagination, efficient Firestore reads/writes, graceful degradation, observability, crash-free rate, startup time, and backward-compatible migrations are first-class.

Scale justifies boundaries and robustness, not speculative abstractions (see Simplicity & Naming).

## Modules

Listed in `settings.gradle.kts`.

- Core: `:app`, `:core:{designsystem,ui,model,common,network,testing}`
- Features: `:features:<name>:{domain,data,ui-*}`

## Commands

- All unit tests: `./gradlew unitTestAll`
- One module: `./gradlew :features:cart:domain:test` (JVM) or `:features:cart:data:testDebugUnitTest` (Android)
- Lint: `./gradlew ktlintCheck` / auto-fix: `./gradlew ktlintFormat`
- Build: `./gradlew assembleDebug`
- Coverage: `./gradlew koverXmlReport`

Run `ktlintFormat` and the affected module tests before declaring work done.

## Change Workflow

- **Gate**: `./gradlew ktlintCheck unitTestAll assembleDebug`. Never claim work is done without a fresh green run of it.
- **Behavior changes** (anything a user can observe) go through OpenSpec with the `lazypizza` schema: `/opsx:explore` when the idea is still fuzzy, then `/opsx:propose`. The developer approves the specs in the conversation, and that approval is also the go to apply, verify and open the PR without asking again.
- **Fast lane**, no OpenSpec: refactors, dependency bumps, tooling and docs need only the gate. A small bug fix needs one test that fails because of the bug, the fix, then the gate.
- Never edit a spec the developer approved. Stop and ask once, with a proposed diff.
- Run `/opsx:archive` only when the change's verify.md says `DECISION: PASS` or `PASS_WITH_WARNINGS` and the developer has approved the PR. Otherwise stop before moving anything.
- **Git**: push only your own branch, by name (`git push -u origin <branch>`). Never push to `main`, never force-push.

## Layers & Dependencies (Hard Constraints)

- **Domain**: pure Kotlin (Hilt annotations allowed, nothing else from Android/Compose/Firebase/Room). Holds business models, use cases, business rules, repository interfaces. Single source of truth for behavior.
- **Data**: implements domain interfaces; owns Room, Firebase, network, and data → domain mapping. Framework types never leave the module.
- **UI**: Compose only. Renders state, forwards intents to ViewModels. No business logic.
- **DI**: Hilt modules only, bindings only, no logic. Constructor injection by default.

Allowed: UI → any feature's domain, Data → its own feature's domain, Domain → another feature's domain.
Forbidden: UI → Data, Domain → UI, Domain → Data, Data → another feature's domain or data.

A feature's domain module is its public API: **cross-feature dependencies go domain → domain only**, so cross-feature business rules live in a domain, never in a data layer. The feature dependency graph must stay acyclic; if two features need each other, move the shared concept into a core module.

Flow is strictly unidirectional: intents go up (UI → ViewModel → UseCase → Repository), state flows down. No bidirectional state ownership.

## Error Modeling

- Domain errors are typed (sealed types or structured results), and mark retryable vs. non-retryable explicitly.
- Framework exceptions are mapped to domain errors in the data layer and never reach the UI.
- User-facing messages are mapped from typed errors in the ViewModel / UI mapping layer. Never show raw backend or exception strings.

## Coroutines

- Domain never hardcodes dispatchers; the data layer owns threading.
- ViewModels use `viewModelScope`. No blocking calls in ViewModels or composables.

## Simplicity & Naming

- Prefer the simplest working solution. No base classes, generic engines, or DSLs without a real, present need; indirection must pay for itself immediately.
- When uncertain about boundaries, choose stricter separation.
- Names reflect intent, not implementation. Avoid vague verbs (handle, process, manage) and vague classes (Utils, Helper, Manager).
- Suffixes: `*UiState`, `*UiEvent`, `*UiAction`, `*UseCase`, `*Repository`, `*DataSource`.
- Booleans read like a sentence: `isUserSignedIn`, `isPrimaryEnabled`.
- One public class per file (small sealed hierarchies excepted).

## Scoped Rules

These load automatically when you work on matching files:

- `.claude/rules/ui.md`: ViewModels, UI state & events, design system, navigation
- `.claude/rules/data.md`: Room, Firebase, threading, error mapping
- `.claude/rules/gradle.md`: version catalog, build config, R8
- `.claude/rules/cart-session.md`: guest vs. signed-in cart, session lifecycle
- `.claude/rules/testing.md`: test stack and conventions. Read it before writing tests.

Before reviewing code, read `docs/agents/review-checklist.md`.
