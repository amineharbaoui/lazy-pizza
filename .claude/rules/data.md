---
paths:
  - "features/**/data/**"
  - "core/network/**"
---

# Data Layer Rules

## Boundaries

- Implement domain repository interfaces; map data → domain models here.
- Never leak framework types (Room entities/DAOs, Firebase snapshots, SDK types) outside the module.
- Depend only on this feature's own domain and core modules, never on another feature's domain or data. If storage seems to need another feature (e.g. the signed-in user), the decision belongs in a domain use case that passes the result in as a parameter.

## Room

- Any schema change requires a version bump **and** an explicit migration (`@AutoMigration` or custom `Migration`).
- Migrations must be backward-compatible: users upgrade from any shipped version.

## Firebase

- Wrap every Firebase API in a `*DataSource`.
- Design reads/writes for production traffic: paginate, avoid redundant reads, batch writes where possible.

## Threading

- The data layer controls threading and dispatchers internally; callers never pick a dispatcher.
- Any blocking call (non-suspend Room/SDK API) runs inside `withContext`.

## Errors

- Catch framework exceptions (Room, Firebase, network) here and map them to typed domain errors.
- Mark whether an error is retryable so the UI can render it predictably.
- Retries use backoff; degrade gracefully when offline.
