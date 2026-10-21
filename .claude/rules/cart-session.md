---
paths:
  - "features/cart/**"
  - "features/auth/**"
  - "features/order/**"
---

# Cart & Session Rules

- Guest users use a local cart.
- Authenticated users use a session-scoped cart.
- Cart transfer (guest → signed-in) must be explicit, atomic, and triggered via a domain use case.
- Signing out must clear all session-scoped data.
- The UI never decides cart persistence rules.
