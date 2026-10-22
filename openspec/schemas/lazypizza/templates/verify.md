# Verify

## Tasks
- [ ] Every box in tasks groups 1 to 6 is checked
- [ ] `git status --porcelain` shows nothing but verify.md
- Open tasks: <!-- list with reason, or "none" -->

## Ledger
- [ ] Every test-plan.md row exists as a real test
- [ ] Every row is 🟢 green

## Lock integrity (blocking)
- Tests and support files vs `LOCK:` sha: <!-- command + "no diff" or explained changes -->
- Specs vs `SPECS-APPROVED` sha: <!-- command + "no diff" -->

## Gate
- Command: <!-- e.g. ./gradlew ktlintCheck unitTestAll assembleDebug -->
- Result: <!-- e.g. 214 passed, 0 failed, 0 skipped -->

## Journeys
<!-- flow → result, or "none in scope" -->

## Screenshots
<!-- added or changed baseline → scenario that justifies it; new screens flagged for the developer -->

## Architecture
<!-- CLAUDE.md layer rules in the diff: OK, or the violation -->

## Findings
<!-- required behavior no test proves; behavior no scenario asked for -->

DECISION: <!-- PASS | PASS_WITH_WARNINGS | FAIL -->
