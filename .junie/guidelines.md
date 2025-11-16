# LazyPizza — Project Guidelines and Overview

Last updated: 2025-11-03 01:20 (local)

## 1) Project Overview

LazyPizza is an Android application built with Kotlin and Jetpack Compose. It showcases a pizza ordering experience with a focus on a clean
design system, feature-based modular package structure, and simple navigation. Data for products is currently provided via local sample data
and/or Firebase Firestore integration.

Core characteristics:

- Single Gradle module (`app`)
- UI with Jetpack Compose (Material 3–style components implemented in a local design system)
- Feature-oriented packages (`features/home`, `features/detail`, `features/cart`, `features/history`)
- Simple navigation with a central `AppNavigation` and `Route` definitions
- Dependency injection via DI modules under `di` and feature `data/di` packages
- Optional remote data via Firebase Firestore (see `core/firebase/firestore`)

## 2) Repository Layout (high level)

- app/src/main/java
    - com.example.core.designsystem
        - Reusable UI components (`DsButton`, `DsCardRow`, `DsTextField`, `DsTopBar`, etc.)
        - Theme and typography (`Theme.kt`, `AppColors.kt`, `AppTypography.kt`)
        - Utilities for window size and previews
    - com.example.lazypizza
        - `LazyPizzaApp.kt` — app-level Compose setup
        - `di/AppModule.kt` — app-level DI bindings
        - navigation — `AppNavigation.kt`, `Route.kt`
        - main — `MainActivity.kt`, `MainScreen.kt`
        - features
            - home — domain/data/presentation for the home screen and product listings
            - detail — domain/data/presentation for product detail and toppings
            - cart — domain/data/presentation for cart and recommendations
            - history — presentation for order history
    - core/firebase/firestore — `ProductDatasource.kt` for Firestore access
- app/src/main/res — images, icons, fonts, strings, themes, etc.
- Testing
    - Unit: `app/src/test/...`
    - Instrumented: `app/src/androidTest/...`
- Tooling & config
    - `detekt-config.yml` and `detekt-compose-0.4.26-all.jar`
    - Gradle version catalogs: `gradle/libs.versions.toml`

## 3) Build & Run

- Android Studio: Use “Run” to launch on an emulator/device.
- Gradle (CLI):
    - Assemble debug: `./gradlew :app:assembleDebug`
    - Install & run (device connected): `./gradlew :app:installDebug`

## 4) Tests

Current tests are minimal (sample unit/instrumented tests provided).

- Run unit tests: `./gradlew test`
- Run instrumented tests (device/emulator): `./gradlew connectedAndroidTest`

Guideline for Junie:

- If you make code changes affecting logic or UI behavior, run unit tests. Instrumented tests are optional unless the change impacts Android
  framework behavior or Compose UI on-device specifics.
- For documentation-only or comment-only changes, do not run tests.

## 5) Linting & Static Analysis

- Detekt config is provided via `detekt-config.yml`. If the Gradle plugin is enabled in the project, you can run:
    - `./gradlew detekt`
- A compose-specific Detekt ruleset JAR is included (`detekt-compose-0.4.26-all.jar`). If not wired into Gradle, prefer not to run Detekt
  via CLI JAR directly unless explicitly requested.

## 6) Coding Style

- Kotlin conventions; keep functions and files concise and readable.
- Jetpack Compose best practices:
    - Use stateless, previewable composables where possible.
    - Hoist state to ViewModels/presenters when appropriate.
    - Reuse design system components (`core.designsystem.components`).
- Follow existing package boundaries:
    - `domain` for models/use-cases, `data` for repositories/mappers/datasources, `presentation` for UI and ViewModels.
- Prefer immutable data models for UI state.

## 7) Firebase/Remote Data

- Firestore access lives under `core/firebase/firestore/datasource/ProductDatasource.kt`.
- When a feature needs remote data, prefer repository abstractions (e.g., `domain/repository`) and map DTOs to domain models in
  `data/mapper`.

## 8) Navigation

- Centralize routes in `navigation/Route.kt` and screens wiring in `navigation/AppNavigation.kt`.
- Keep screen-level state in their respective ViewModels under `presentation` where applicable.

## 9) What Junie Should Do Before Submitting Changes

- For code changes:
    - Build debug variant: `./gradlew :app:assembleDebug`
    - Run unit tests: `./gradlew test`
- For resource-only or documentation-only changes:
    - No build/tests required.
- Do not run the full project build if not necessary; prefer targeted tasks.

## 10) Contribution Notes

- Keep changes scoped to the relevant feature package.
- Match existing naming and file structure.
- Add brief KDoc/comments where it improves clarity, but avoid noise.

## 11) Project Contacts & Ownership

- If something is unclear (e.g., DI framework specifics or missing wiring for Detekt), request clarification before introducing new tools or
  large refactors.
