# LazyPizza

Android pizza ordering app: Kotlin, Jetpack Compose (Material 3), Hilt, Room, Firebase Auth & Firestore, Navigation 3. Multi-module and feature-based: `:core:*` and `:features:<name>:{domain,data,ui-*}`, listed in `settings.gradle.kts`.

## Workflow

- A user-visible change starts with `/spec`, which writes its spec with the developer and creates it as a GitHub issue.
- Specs are GitHub issues. Never edit one without asking the developer first.

## Git

- Push only your own branch, by name (`git push -u origin <branch>`). Never push to `main`, never force-push.
