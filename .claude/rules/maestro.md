---
paths:
  - ".maestro/**"
  - "scripts/e2e.sh"
---

# E2E flows (Maestro)

- A Maestro flow walks one journey through the real app, as a user would: real navigation, the real Hilt graph, Firebase, and data that survives a restart. Flows run last, after the build, on an emulator with `scripts/e2e.sh`. Not in CI.
- One journey per flow, in `.maestro/<feature>/<journey>.yaml`, named by what the user does: `cart/add-pizza-to-cart.yaml`. Rules and edge cases stay in the acceptance tests. A flow is slower and flakier, so it never repeats them.
- A flow is tagged with its feature folder, plus `smoke` for the few journeys every change must keep working. `scripts/e2e.sh cart,smoke` runs the flows that have either tag.
- Each flow starts with `launchApp: clearState: true`, so it never depends on another flow or on a previous run.
- Select an element by the text the user reads, or for an icon by the content description TalkBack reads. Add a `testTag` only to an element that has neither, such as an input without a label. When an icon's description is wrong, fix the description instead of adding a tag. Tags are snake_case: `<screen>_<element>` (`login_code_input`) at the call site, `<component>_<part>` inside `:core:designsystem`. `MainActivity` exposes them to Maestro as resource ids.
- Assert the texts the spec shows the user. A text with a price or a count in it is matched with a regex: `"Add to Cart for .*"`.
- Steps that several flows share live in `.maestro/subflows/` and run with `runFlow`. `subflows/sign-in.yaml` signs in with the test phone number.
- Flows run against the real Firebase project, signed in with a Firebase test phone number. They rely on the real menu, and each run of an order flow places a real order for the test account.
- The test phone number and its code live only in `.maestro/.env.local`, which git ignores. A flow reads them as `${TEST_PHONE_NUMBER}`. Never write them in a flow or a commit. Maestro's own logs, in `build/maestro/` and `~/.maestro/`, can show them as typed, so never paste those lines in a PR or a chat.
- A flow never depends on the time of day. It picks the first pickup slot shown, never a specific time, and asserts nothing time-based. Time rules, such as pickup slots, the 15-minute margin or cart expiry, stay in the acceptance tests, which control the `Clock`.
- A flow records the journey it tests, for its PR's description: `- startRecording: <feature>-<journey>`, named after its folder and file so no two videos share a name, right after sign-in, or right after `launchApp` when it doesn't sign in, and `- stopRecording` as its last step. Never record sign-in: the video goes into a public PR, and would show the test phone number and its code. Adding these two steps to an existing flow changes nothing it checks: do it when a PR changes that flow.
- Wait for anything that loads from the network with `extendedWaitUntil` and a timeout, never a fixed delay.
- Never use `hideKeyboard`: on Android it presses Back, which can leave the screen. Tap the next button instead.
- A flow is written from the spec, before the code: the journey's steps and the texts the spec shows. When a spec changes an existing journey, its flow is updated first in the same way.
- Until a new or updated flow first passes, only its mechanics may change: a selector, a wait or a scroll. What it checks never changes: the journey's steps and the texts it asserts.
- After its first pass, a flow changes only when a spec changes its journey. When it fails, read its screenshot and view hierarchy in `build/maestro/` first, and fix the app. When it is flaky, fix the wait, never the assertion.
- A refactor never changes a flow. A change that needs one isn't a refactor.
