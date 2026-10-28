# <Title: a short name for the feature>

## Why

<The problem this solves for the user or the business, and the outcome we expect.>

## Scope

**In scope**
- <What this feature delivers.>

**Out of scope**
- <What it deliberately does not do, so nobody builds it by accident.>

## Business rules

<details>
<summary>{Number of rules} rules</summary>

- **R1**: <One rule per line, testable, with concrete values: limits, amounts, conditions.>

</details>

## Scenarios

<details>
<summary>{Number of scenarios} scenarios</summary>

### 1. <Short title>

Covers: R1

- **Given** <the starting situation>
- **When** <what the user does, or what happens>
- **Then** <what the user sees, or what the business gets>

<Add one scenario per behavior, including failures and edge cases: errors, empty states, offline, limits, signed-out users.>

</details>

## UI

<details>
<summary>{Screen name}</summary>

- **Entry**: <how the user gets here>
- **States**: <each state the screen can show: loading, content, empty, error, offline, and so on>
- **Texts**: <every text, exactly as the user reads it, per state>
- **Actions**: <what the user can do, and where each action leads>
- **Sketch**: ![{Screen name} sketch](.claude/spec-drafts/<feature-slug>/<screen-slug>.svg)

</details>

<Add one folded block per screen. Keep the Sketch line only when the layout is new or changes. Write `None.` when the feature has no UI change.>

## Constraints

- **Offline**: <what works and what the user sees without network>
- **Accessibility**: <content descriptions, touch targets, anything specific to this feature>
- **Analytics**: <events the business wants to track, if any>

<Write `None.` when nothing applies.>

## Dependencies

- <Other features, specs (#issue), backend data or configuration this feature needs.>

<Write `None.` when nothing applies.>

## Open questions

None.
