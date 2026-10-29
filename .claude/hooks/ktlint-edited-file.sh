#!/bin/bash
# PostToolUse hook on Edit and Write: lints the Kotlin file Claude just changed,
# like CI does for the files a PR changes. Skipped when the ktlint CLI isn't installed.
# Exit code 2 shows the violations to Claude so it fixes them.

input=$(cat)
file=$(printf '%s' "$input" | grep -oE '"file_path"[[:space:]]*:[[:space:]]*"([^"\\]|\\.)*"' | head -1 | sed -E 's/^"file_path"[[:space:]]*:[[:space:]]*"(.*)"$/\1/')

case "$file" in
  *.kt | *.kts) ;;
  *) exit 0 ;;
esac

command -v ktlint > /dev/null 2>&1 || exit 0
[ -f "$file" ] || exit 0

if ! output=$(cd "${CLAUDE_PROJECT_DIR:-.}" && ktlint --relative "$file" 2>&1); then
  echo "ktlint found violations in $file. Fix them: CI fails on any violation in a file the PR changes." >&2
  echo "$output" >&2
  exit 2
fi

exit 0
