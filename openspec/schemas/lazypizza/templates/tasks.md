# Tasks

## 1. Contract

- [ ] 1.1 <!-- stub UiState / UiAction / interfaces from design.md; verify: affected modules compile -->

## 2. Red (test-writer)

- [ ] 2.1 <!-- write every test in test-plan.md; verify: each fails on an assertion or TODO(), not a compile error -->
- [ ] 2.2 Commit `test(<change-name>): red` and write its sha into test-plan.md `LOCK:`

## 3. Green: <!-- requirement name -->

- [ ] 3.1 <!-- domain / data / ViewModel step; verify: module test task, rows turned green -->

## 4. UI

- [ ] 4.1 <!-- screen + one @Preview per UiState; verify: screenshots recorded and checked -->

## 5. Gate

- [ ] 5.1 Run `./gradlew ktlintCheck unitTestAll assembleDebug`; verify: green

## 6. Journey

- [ ] 6.1 <!-- only for journey scenarios: run the Maestro flow on device_for_test; verify: passes, retries off -->

## 7. Verify and PR

- [ ] 7.1 Commit all work, then the reviewer subagent writes verify.md; verify: DECISION is PASS or PASS_WITH_WARNINGS
- [ ] 7.2 Push `feature/<change-name>` and open the PR; verify: PR link
