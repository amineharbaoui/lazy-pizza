# <Title: a short name for the feature>

## Why

<The problem this solves for the user or the business, and the outcome we expect.>

## Scope

**In scope**
- <What this feature delivers.>

**Out of scope**
- <What it deliberately does not do, so nobody builds it by accident.>

## Business rules

- **R1**: <One rule per line, testable, with concrete values: limits, amounts, conditions.>

## Scenarios

<details>
<summary>1. {Short title} · Covers R1</summary>

- **Given** <the starting situation>
- **When** <what the user does, or what happens>
- **Then** <what the user sees, or what the business gets>

</details>

<Add one folded block per behavior, including failures and edge cases: errors, empty states, offline, limits, signed-out users.>

## UI

<details>
<summary>{Screen name}</summary>

- **Entry**: <how the user gets here>
- **States**: <each state the screen can show: loading, content, empty, error, offline, and so on>
- **Texts**: <every text, exactly as the user reads it, per state>
- **Actions**: <what the user can do, and where each action leads>

</details>

<Add one folded block per screen. Write `None.` when the feature has no UI change.>

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
