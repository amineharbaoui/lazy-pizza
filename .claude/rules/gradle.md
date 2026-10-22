---
paths:
  - "**/*.gradle.kts"
  - "gradle/**"
  - "build-logic/**"
  - "**/*.pro"
---

# Build & Gradle Rules

- All versions live in `gradle/libs.versions.toml` (the source of truth for toolchain and SDK versions). Always use `libs.*` aliases.
- Prefer BOMs (Compose, Firebase).
- Reference modules with typesafe accessors: `projects.*`.
- Use `implementation` by default; `api` only when a type is part of the module's public API.
- KSP is required in every module that uses Hilt or Room.
- Release builds use R8 shrinking. Keep `proguardFiles` on release; library modules ship `consumer-rules.pro` when DI / Room / Firebase reflection needs it.
- `google-services.json` is committed in `:app`. The Google Services plugin fails the build without it.
