#!/usr/bin/env bash
# Runs the Maestro flows in .maestro/ on an Android emulator: the last layer of checks,
# after the unit tests, the screenshot tests and the build.
#
#   scripts/e2e.sh               every flow
#   scripts/e2e.sh cart,smoke    the flows tagged cart OR smoke
#
# Needs the Maestro CLI, the Android SDK and .maestro/.env.local (copy .maestro/.env.example;
# a worktree uses the main checkout's copy). With no device connected, it starts the AVD named
# by E2E_AVD (default device_for_test) without a window and leaves it running.
# Results go to build/maestro/: report.xml, and for a failing flow its screenshot,
# view hierarchy and logs.
set -euo pipefail

cd "$(dirname "$0")/.."

tags="${1:-}"
avd="${E2E_AVD:-device_for_test}"
env_file=".maestro/.env.local"
out_dir="build/maestro"

fail() {
  echo "e2e: $*" >&2
  exit 1
}

# A git worktree has none of the untracked files, so it falls back to the main checkout's copies.
main_checkout="$(git worktree list --porcelain | sed -n '1s/^worktree //p')"
[ -f "$env_file" ] || [ ! -f "$main_checkout/$env_file" ] || env_file="$main_checkout/$env_file"
sdk="${ANDROID_HOME:-$(sed -n 's/^sdk\.dir=//p' local.properties "$main_checkout/local.properties" 2> /dev/null | head -1)}"

command -v maestro > /dev/null || fail "Maestro CLI not found. Install it: curl -fsSL https://get.maestro.mobile.dev | bash"
[ -n "$sdk" ] || fail "Android SDK not found. Set ANDROID_HOME, or open the project once in Android Studio."
[ -f "$env_file" ] || fail "$env_file is missing. Copy .maestro/.env.example and fill in the Firebase test phone number."
adb="$sdk/platform-tools/adb"

mkdir -p "$out_dir"

device_count() {
  "$adb" devices | awk 'NR > 1 && $2 == "device"' | wc -l | tr -d ' '
}

if [ "$(device_count)" -eq 0 ]; then
  echo "e2e: no device connected, starting the $avd emulator"
  nohup "$sdk/emulator/emulator" -avd "$avd" -no-window -no-audio -no-boot-anim \
    > "$out_dir/emulator.log" 2>&1 &
  emulator_pid=$!
  for _ in $(seq 1 90); do
    [ "$("$adb" shell getprop sys.boot_completed 2> /dev/null | tr -d '\r')" = "1" ] && break
    kill -0 "$emulator_pid" 2> /dev/null || fail "the $avd emulator stopped, see $out_dir/emulator.log"
    sleep 2
  done
  [ "$("$adb" shell getprop sys.boot_completed 2> /dev/null | tr -d '\r')" = "1" ] ||
    fail "the $avd emulator did not boot in 3 minutes, see $out_dir/emulator.log"
fi

[ "$(device_count)" -eq 1 ] || [ -n "${ANDROID_SERIAL:-}" ] ||
  fail "several devices are connected. Pick one with ANDROID_SERIAL=<serial>, from 'adb devices'."

serial="${ANDROID_SERIAL:-$("$adb" devices | awk 'NR > 1 && $2 == "device" { print $1; exit }')}"

# Animations make taps land on moving elements. Turn them off on emulators only,
# never on a developer's own phone.
if [[ "$serial" == emulator-* ]]; then
  for setting in window_animation_scale transition_animation_scale animator_duration_scale; do
    "$adb" -s "$serial" shell settings put global "$setting" 0
  done
fi

ANDROID_SERIAL="$serial" ./gradlew :app:installDebug --quiet

env_args=()
while IFS='=' read -r key value; do
  [[ -z "$key" || "$key" == \#* ]] && continue
  value="${value%\"}"
  value="${value#\"}"
  env_args+=(-e "$key=$value")
done < "$env_file"

tag_args=()
[ -n "$tags" ] && tag_args+=(--include-tags "$tags")

maestro --device "$serial" test .maestro \
  "${tag_args[@]+"${tag_args[@]}"}" \
  "${env_args[@]+"${env_args[@]}"}" \
  --format junit --output "$out_dir/report.xml" \
  --test-output-dir "$out_dir"
